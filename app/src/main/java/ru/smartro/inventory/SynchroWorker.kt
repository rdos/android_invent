package ru.smartro.inventory

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Settings.Secure
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.work.*
import com.google.gson.Gson
import com.yandex.mapkit.location.LocationManagerUtils
import io.realm.Realm
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import ru.smartro.inventory.core.SynchroRequestRPC
import androidx.localbroadcastmanager.content.LocalBroadcastManager




//https://code.luasoftware.com/tutorials/android/coroutineworker-use-kotlin-coroutines-in-work-manager/
class SynchroWorker(
    private val p_context: Context,
    params: WorkerParameters
) : CoroutineWorker(p_context, params) {

    companion object {
        fun run() : LiveData<WorkInfo> {
            val work = OneTimeWorkRequestBuilder<SynchroWorker>().build()
            WorkManager.getInstance().enqueue(work)

            return WorkManager.getInstance().getWorkInfoByIdLiveData(work.id)
        }
    }

    private val TAG = "SynchroWorker--AaA"
    private val mDeviceId = Secure.getString(p_context.contentResolver, Secure.ANDROID_ID)
    private val mMinutesInSec = 30 * 60

    override suspend fun doWork(): Result = coroutineScope {
        Log.w(TAG, "doWork.before thread_id=${Thread.currentThread().id}")

        //showNotification(context, true, "НезакрывайтЕ", "Служба отправки данных работает")
            // Caused by: java.lang.IllegalStateException: Call `Realm.init(Context)` before calling this method.
        val db = RealmRepo(Realm.getDefaultInstance())
        //Caused by: java.lang.IllegalStateException: Call `Realm.init(Context)` before calling this method.

        while (true) {
            synchronizeData(db)
            delayS(16000)
            sendMessage()
        }
        Result.success()
    }

    private suspend fun delayS(timeMillis: Long) {
        Log.d(TAG, "save_-delayS.before start")
        delay(timeMillis)
        Log.d(TAG, "save_-delayS.after stop")
    }

    private suspend fun sendMessage() {
        Log.d("sender", "Broadcasting message")
        val intent = Intent("custom-event-name")
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!")
        LocalBroadcastManager.getInstance(p_context).sendBroadcast(intent)
    }

    private suspend fun synchronizeData(db: RealmRepo) {
        Log.w(TAG, "save_-synchronizeData.before thread_id=${Thread.currentThread().id}")
        val platformEntityS = db.loadPlatformEntitySSynchro()
        for(platformEntity in platformEntityS) {
            Log.i(TAG, "save_-synchronizeData. platformEntity=${platformEntity.uuid}")
            SynchroRequestRPC().callAsyncRPC(platformEntity)
        }


//        con.observe(
//            viewLifecycleOwner,
//            { bool ->
//
//                if (bool){
//                    // TODO: 22.11.2021 !!!
//
//              } else
//               {
//
//                }
//            }
//        )
    }

    private fun showNotification(context: Context, ongoing: Boolean, content: String, title: String) {
        val channelId = "M_CH_ID"
        val fullScreenIntent = Intent(context, Activity::class.java)
        fullScreenIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val fullScreenPendingIntent =
            PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .run {
                setSmallIcon(R.drawable.ic_back)
                setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_back))
                setContentTitle(title)
                setContentText(content)
                setOngoing(ongoing)
                priority = NotificationCompat.PRIORITY_MAX
                setDefaults(NotificationCompat.DEFAULT_ALL)
                setContentIntent(fullScreenPendingIntent)
                setShowWhen(true)
            }
//        <!--<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />-->
        if (!ongoing) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
        }
        val notification: Notification = builder.build()
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "FACT_SERVICE", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1, notification)
    }

    private fun dismissNotification() {
        Log.d(TAG, "dismissNotification.before")
        val notificationManager = NotificationManagerCompat.from(p_context)
        notificationManager.cancel(1)
    }


}

