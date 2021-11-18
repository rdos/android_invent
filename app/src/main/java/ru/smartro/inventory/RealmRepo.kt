package ru.smartro.inventory

import android.util.Log
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import ru.smartro.inventory.database.*
import java.lang.Exception
import java.util.concurrent.Executors

// find! has
// 4) save load ; get set ; add update;
//;new;edit;delete;clear;; Has? extra?
// TODO: 12.11.2021 "block:() -> Unit "=0
//5) initialYear, initialDay
//src;dest

//    LiveRealmData
class RealmRepo(private val mRealm: Realm) /*: Realm.Transaction*/ {
    private val TAG : String = "RealmRepository--AAA"

    fun saveConfig(configEntity: ConfigEntityRealm) {
        mRealm.executeTransaction { realm ->
            realm.insertOrUpdate(configEntity)
        }
    }

    fun saveOwner(ownerEntityList: OwnerResponse) {
        execInTransaction{ p_realm ->
            p_realm.insertOrUpdate(ownerEntityList.data.organisationRealmEntities)
        }
    }

    private fun execInTransaction(block: (p_realm: Realm) -> Unit) {
        mRealm.executeTransaction { realm ->
            block(realm)
        }
    }

    fun loadConfig(name: String): String {
        mRealm.refresh()
        val configEntity = mRealm.where(ConfigEntityRealm::class.java).findFirst() ?: return Snull
        return configEntity.value
    }

    fun save(block: () -> Unit) {
        mRealm.executeTransaction { realm ->
            block()
        }
    }

    fun insert(realmObject: RealmObject) {
        mRealm.insertOrUpdate(realmObject)
    }

    fun saveSave(platform_id: Int) {
        val exe = Executors.newSingleThreadExecutor()
        Log.e("TAG", "kasta каста ()")
        exe.execute {
            Log.i("TAG", Thread.currentThread().id.toString())
            val onSuccess = Realm.Transaction.OnSuccess {
                Log.e("TAG", "kasta каста ()")
            }
            val backgroundRealm = Realm.getDefaultInstance()
            val realmAsyncTask: Unit = backgroundRealm.executeTransaction {
                val containerEntity = PlatformEntityRealm(platform_id)
                backgroundRealm.insertOrUpdate(containerEntity)
            }
        }
    }

    fun loadPlatformType(): List<PlatformTypeRealm> {
        val realmResults = mRealm.where(PlatformTypeRealm::class.java).findAll()
        val result = mRealm.copyFromRealm(realmResults)
        return result
    }

    fun loadPlatformEntity(): List<PlatformEntityRealm> {
        val realmResults = mRealm.where(PlatformEntityRealm::class.java ).findAll()
        val result = mRealm.copyFromRealm(realmResults)
        return result
    }

    fun createPlatformEntity(): PlatformEntityRealm {
        Log.e("ErrorsE", "kasta каста ()")
        val result = try {
            mRealm.createObject(PlatformEntityRealm::class.java)?: Onull
        } catch (e: Exception) {
            Log.e("ErrorsE", "kasta каста ()")
            Onull
        }
        return result
    }

    /** entityRealm entityRealmS**/
    fun saveRealmEntity(entityRealm: ARealmObject) {
        execInTransaction{ p_realm ->
            p_realm.insertOrUpdate(entityRealm)
        }
    }
    /**? //entityRealmS vs entitySRealm **/
    fun saveRealmEntity(entityRealmS: RealmList<ARealmObject>) {
        execInTransaction{ p_realm ->
            p_realm.insertOrUpdate(entityRealmS)
        }
    }
    /** entitySRealm**/
    fun createContainerEntityS(): List<ContainerEntityRealm> {
        Log.e("ErrorsE", "kasta каста ()")
        val result = try {
            mRealm.createObject(ContainerEntityRealm::class.java)?: O2null
        } catch (e: Exception) {
            Log.e("ErrorsE", "kasta каста ()")
            O2null
        }
        val resMList =  mutableListOf<ContainerEntityRealm>()
        resMList.add(result)
        return resMList
    }


//fun findWayTask(): WayTaskEntity {
//    realm.refresh()
//    // TODO: 25.10.2021 !!!???
//    //  return WayTaskEntity() is fail
//    val wayTaskEntity = realm.where(WayTaskEntity::class.java).findFirst()
//    if (wayTaskEntity != null) {
//    return realm.copyFromRealm(wayTaskEntity)
//    }
//    return WayTaskEntity()
//  }


//    private fun updateTimer(entity: PlatformEntity?) {
//        entity?.updateAt = MyUtil.timeStamp()
//    }

}
