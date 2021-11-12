package ru.smartro.inventory.database

import android.util.Log
import io.realm.Realm

// find!
// 4) save load ; get set ; add update;
//;new;edit;delete;clear;; Has? extra?
// TODO: 12.11.2021
//5) initialYear, initialDay
//src;dest

//    LiveRealmData
class RealmRepository(private val realm: Realm) {
    private val TAG : String = "RealmRepository--AAA"

    fun saveConfig(configEntity: ConfigEntityRealm) {
        realm.executeTransaction { realm ->
            realm.insertOrUpdate(configEntity)
        }
    }

    fun saveOwner(ownerEntityList: List<OwnerEntityRealm>) {
        realm.executeTransaction { realm ->
            realm.insertOrUpdate(ownerEntityList)
        }
    }

    fun loadConfig(name: String): String {
        realm.refresh()
        val configEntity = realm.where(ConfigEntityRealm::class.java).findFirst() ?: return "rNull"
        return configEntity.value
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
