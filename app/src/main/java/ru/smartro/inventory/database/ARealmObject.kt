package ru.smartro.inventory.database

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import ru.smartro.inventory.Inull
import ru.smartro.inventory.Snull
import java.io.Serializable

private const val LIVE_MSG_A = "Оо.!.Ошибочка"
open class ARealmObject (
    var LiveMsg_App: String = Snull
) : RealmObject(), Serializable
{
    //==is error
    fun isRealmO(): Boolean {
        return !isOnull()
    }
    fun isOnull(): Boolean {
        return LIVE_MSG_A == this.LiveMsg_App
    }
}
