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
        configEntity.name = configEntity.name.uppercase()
        mRealm.executeTransaction { realm ->
            realm.insertOrUpdate(configEntity)
        }
    }

    fun saveOwnerList(ownerEntityList: OwnerResponse) {
        execInTransaction{ p_realm ->
            p_realm.insertOrUpdate(ownerEntityList.data.organisationRealmEntities)
        }
    }

    private fun execInTransaction(block: (p_realm: Realm) -> Unit) {
        mRealm.executeTransaction { realm ->
            block(realm)
        }
    }

    fun loadConfig(name: String): String? {
        try {
            mRealm.refresh()
        } catch (e: Exception) {
            print(e)
        }
        var result: String? = null
        try {
          val configEntity = mRealm.where(ConfigEntityRealm::class.java).equalTo("name", name.uppercase()).findFirst() ?: return Snull
            result = configEntity.value
        } catch (e: Exception) {
            print(e)
        }
        return result
    }

    fun loadConfigBool(name: String): Boolean {
        val result = loadConfig(name)
        if (result == null) return true
        if (result == Snull) return true
        try {
            return result.toBoolean()
        } catch (e: Exception) {
            return true
        }
    }

    fun loadConfigInt(name: String): Int {
        val result = loadConfig(name)
        if (result == null) return Inull
        if (result == Snull) return Inull
        try {
            return result.toInt()
        } catch (e: Exception) {
            return Inull
        }
    }

    fun save(block: () -> Unit) {
        mRealm.executeTransaction { realm ->
            block()
        }
    }

    fun insert(realmObject: RealmObject) {
        mRealm.insertOrUpdate(realmObject)
    }

//    fun delete(realmObject: RealmObject) {
//        RealmObject.deleteFromRealm(realmObject)
//    }

    fun saveSave(platformUuid: String) {
        val exe = Executors.newSingleThreadExecutor()
        Log.e("TAG", "kasta каста ()")
        exe.execute {
            Log.i("TAG", Thread.currentThread().id.toString())
            val onSuccess = Realm.Transaction.OnSuccess {
                Log.e("TAG", "kasta каста ()")
            }
            val backgroundRealm = Realm.getDefaultInstance()
            val realmAsyncTask: Unit = backgroundRealm.executeTransaction {
                val containerEntity = PlatformEntityRealm(platformUuid, is_synchro_start = false)
                backgroundRealm.insertOrUpdate(containerEntity)
            }
        }
    }

    fun loadPlatformType(): List<PlatformTypeRealm> {
        val realmResults = mRealm.where(PlatformTypeRealm::class.java).findAll()
        val result = mRealm.copyFromRealm(realmResults)
        return result
    }

    fun loadContainerType(): List<ContainerTypeRealm> {
        val realmResults = mRealm.where(ContainerTypeRealm::class.java).findAll()
        val result = mRealm.copyFromRealm(realmResults)
        return result
    }

    fun loadContainerStatus(): List<ContainerStatusRealm> {
        val realmResults = mRealm.where(ContainerStatusRealm::class.java).findAll()
        val result = mRealm.copyFromRealm(realmResults)
        return result
    }

    fun loadPlatformEntityS(): List<PlatformEntityRealm> {
        try {
            mRealm.refresh()
        } catch (e: Exception) {
            print(e)
        }
        val realmResults = mRealm.where(PlatformEntityRealm::class.java).findAll()
        val result = mRealm.copyFromRealm(realmResults)
        return result
    }

    fun loadPlatformEntity(platformUuid: String): PlatformEntityRealm {
        try {
            mRealm.refresh()
        } catch (e: Exception) {
            print(e)
        }
        val realmResults = mRealm.where(PlatformEntityRealm::class.java).equalTo("uuid", platformUuid).findFirst()
        val result = mRealm.copyFromRealm(realmResults!!)
        return result
    }

    fun loadPlatformEntitySSynchro(): List<PlatformEntityRealm> {
        try {
            mRealm.refresh()
        } catch (e: Exception) {
            print(e)
        }
        var result = emptyList<PlatformEntityRealm>()
        try {
            val realmResults = mRealm.where(PlatformEntityRealm::class.java).equalTo("status_id", Inull).notEqualTo("is_synchro_start", true).findAll()
            result = mRealm.copyFromRealm(realmResults!!)
        } catch (e: Exception) {
            print(e)
        }
        return result
    }

    fun loadContainerEntity(containerUuid: String): ContainerEntityRealm {
        try {
            mRealm.refresh()
        } catch (e: Exception) {
            print(e)
        }
        val realmResults = mRealm.where(ContainerEntityRealm::class.java).equalTo("uuid", containerUuid).findFirst()
        val result = mRealm.copyFromRealm(realmResults!!)
        return result
    }

    fun loadPlatformContainers(platformUuid: String): List<ContainerEntityRealm> {
        try {
            mRealm.refresh()
        } catch (e: Exception) {
            print(e)
        }
        val realmResults = mRealm.where(PlatformEntityRealm::class.java).equalTo("uuid", platformUuid).findFirst()
        val result = mRealm.copyFromRealm(realmResults!!.containers)
        return result
    }

    fun createPlatformEntity(platformUuid: String): PlatformEntityRealm {
//        val result =  mRealm.createObject(PlatformEntityRealm::class.java, id)
        val result = PlatformEntityRealm(platformUuid, is_synchro_start = true)
        saveRealmEntity(result)
        return result
    }

    /** entityRealm entityRealmS**/
    fun saveRealmEntity(entityRealm: RealmObject) {
        execInTransaction{ p_realm ->
            p_realm.insertOrUpdate(entityRealm)
        }
    }
    /**? //entityRealmS vs entitySRealm **/
    fun saveRealmEntityList(entityRealmS: RealmList<out RealmObject>) {
        execInTransaction{ p_realm ->
            p_realm.insertOrUpdate(entityRealmS)
        }
    }

    fun deleteContainerEntity(containerUuid: String) {
        execInTransaction { p_realm ->
            val realmResults = mRealm.where(ContainerEntityRealm::class.java).equalTo("uuid", containerUuid).isNull("number", ).findAll()
            realmResults.deleteAllFromRealm()
        }
    }
    fun deletePlatformEntity(platformUuid: String) {
        execInTransaction { p_realm ->
            val realmResults = mRealm.where(PlatformEntityRealm::class.java).equalTo("uuid", platformUuid).isNull("type", ).findAll()
            realmResults.deleteAllFromRealm()
        }
    }

    /** entitySRealm**/
    fun createContainerEntityS(): List<ContainerEntityRealm> {
        val resMList= mutableListOf<ContainerEntityRealm>()

        val result = ContainerEntityRealm("66")
        saveRealmEntity(result)
        resMList.add(result)

//        mRealm.executeTransaction{
//            val result = mRealm.createObject(ContainerEntityRealm::class.java, 66)
//            resMList.add(result)
//        }
        return resMList
    }

    fun clearData() {
        mRealm.deleteAll()
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
