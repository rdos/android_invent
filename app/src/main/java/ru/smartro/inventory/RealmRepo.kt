package ru.smartro.inventory

import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmList
import io.realm.RealmObject
import ru.smartro.inventory.database.*
import java.util.concurrent.Executors


// find! has
// 4) save

// load ; get ; default и
//add update;

//;new;edit;delete;clear;; Has? extra?
// TODO: 12.11.2021 "block:() -> Unit "=0
//5) initialYear, initialDay
//src;dest

//    LiveRealmData
//SpinnerDataL
class RealmRepo(private val mRealm: Realm) /*: Realm.Transaction*/ {
    private val TAG : String = "RealmRepository--AAA"

    fun saveConfig(configEntity: Config) {
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
          val configEntity = mRealm.where(Config::class.java).equalTo("name", name.uppercase()).findFirst() ?: return Snull
            result = configEntity.value
        } catch (e: Exception) {
            print(e)
        }
        return result
    }

    fun loadConfigSting(name: String): String {
        val result = loadConfig(name) ?: return ""
        if (result == Snull) return ""
        return try {
            result
        } catch (e: Exception) {
            ""
        }
    }

    fun loadConfigBool(name: String): Boolean {
        val result = loadConfig(name) ?: return true
        if (result == Snull) return true
        return try {
            result.toBoolean()
        } catch (e: Exception) {
            true
        }
    }

    fun loadConfigL(name: String): Config {
        return Config(name, loadConfigLong(name))
    }

    private fun loadConfigLong(name: String): String {
        val result = loadConfig(name)
        try {
            if (result != null) {
                result.toLong()
                return result
            }
        } catch (e: Exception) {
            return "0"
        }
        return "0"
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

    fun getImages(uuid: String): List<ImageRealmEntity> {
        var images: List<ImageRealmEntity>? = null
        mRealm.executeTransaction { p_realm ->
            val results = p_realm.where(ImageRealmEntity::class.java).equalTo("uuid", uuid).findAll()
            images = p_realm.copyFromRealm(results)
        }
        return images!!
    }

    fun clearUnusedEntities() {
        mRealm.executeTransaction { p_realm ->
            val emptyPlatformS = p_realm.where(PlatformEntityRealm::class.java).equalTo("wasSaved", false).findAll()
            emptyPlatformS.forEach { platform ->
                platform.containers.forEach { container ->
                    val images = p_realm.where(ImageRealmEntity::class.java).equalTo("uuid", container.uuid).findAll()
                    images.deleteAllFromRealm()
                }

                platform.containers.deleteAllFromRealm()

                val images = p_realm.where(ImageRealmEntity::class.java).equalTo("uuid", platform.uuid).findAll()
                images.deleteAllFromRealm()
                platform.deleteFromRealm()
            }
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
                val containerEntity = PlatformEntityRealm(platformUuid)
                backgroundRealm.insertOrUpdate(containerEntity)
            }
        }
    }

    fun loadPlatformType(): List<PlatformTypeRealm> {
        val realmResults = mRealm.where(PlatformTypeRealm::class.java).findAll().sort("id")
        val result = mRealm.copyFromRealm(realmResults)
        return result
    }

    fun loadContainerType(): List<ContainerTypeRealm> {
        val realmResults = mRealm.where(ContainerTypeRealm::class.java).findAll().sort("id")
        val result = mRealm.copyFromRealm(realmResults)
        return result
    }

    fun loadContainerStatus(): List<ContainerStatusRealm> {
        val realmResults = mRealm.where(ContainerStatusRealm::class.java).findAll().sort("id")
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
        var result: PlatformEntityRealm? = null
        mRealm.executeTransaction { p_realm ->
            var realmResults = mRealm.where(PlatformEntityRealm::class.java).equalTo("uuid", platformUuid).findFirst()
            if(realmResults == null) {
                realmResults = PlatformEntityRealm(platformUuid)
                realmResults.beforeCreate()
                p_realm.insertOrUpdate(realmResults)
                result = realmResults
            } else {
                result = mRealm.copyFromRealm(realmResults)
            }
        }

        return result!!
    }

    fun loadPlatformEntitySSynchro(): List<PlatformEntityRealm> {
        try {
            mRealm.refresh()
        } catch (e: Exception) {
            print(e)
        }
        var result = emptyList<PlatformEntityRealm>()
        try {
            val realmResults = mRealm.where(PlatformEntityRealm::class.java).equalTo("status_id", Inull).equalTo("is_allow_synchro", true).findAll()
            result = mRealm.copyFromRealm(realmResults!!)
        } catch (e: Exception) {
            print(e)
        }
        return result
    }

    fun loadContainerEntity(platformUuid: String, containerUuid: String): ContainerEntityRealm {
        try {
            mRealm.refresh()
        } catch (e: Exception) {
            print(e)
        }
        var result: ContainerEntityRealm? = null
        mRealm.executeTransaction { p_realm ->
            var realmResults = mRealm.where(ContainerEntityRealm::class.java).equalTo("uuid", containerUuid).findFirst()
            if(realmResults == null) {
                realmResults = ContainerEntityRealm(containerUuid)
                p_realm.insertOrUpdate(realmResults)
                result = realmResults
            } else {
                result = mRealm.copyFromRealm(realmResults)
            }
            var platform = mRealm.where(PlatformEntityRealm::class.java).equalTo("uuid", platformUuid).findFirst()
            platform?.containers?.add(realmResults)
        }
        return result!!
    }

    /** entityRealm entityRealmS**/
    fun saveRealmEntity(entityRealm: RealmObject) {
        execInTransaction{ p_realm ->
            p_realm.insertOrUpdate(entityRealm)
        }
    }

    /**? //entityRealmS vs entitySRealm **/
    fun saveFromRealmEntityList(entityRealmS: RealmList<out RealmObject>) {
        execInTransaction{ p_realm ->
            p_realm.insertOrUpdate(entityRealmS)
        }
    }

    fun deleteContainerEntity(platformUuid: String, containerUuid: String) {
        execInTransaction { p_realm ->
            val platform = mRealm.where(PlatformEntityRealm::class.java).equalTo("uuid", platformUuid).findFirst()
            if(platform != null) {
                val container = platform.containers.find { el -> el.uuid == containerUuid }
                if(container != null) {
                    val imagesOfContainer = mRealm.where(ImageRealmEntity::class.java).equalTo("uuid", container.uuid).findAll()
                    imagesOfContainer.deleteAllFromRealm()
                    container.deleteFromRealm()
                }
            }
        }
    }

    fun deletePlatformEntity(platformUuid: String) {
        execInTransaction { p_realm ->
            val platform = mRealm.where(PlatformEntityRealm::class.java).equalTo("uuid", platformUuid).findFirst()
            if(platform != null) {
                platform.containers.forEach { container ->
                    val imagesOfContainer = mRealm.where(ImageRealmEntity::class.java).equalTo("uuid", container.uuid).findAll()
                    imagesOfContainer.deleteAllFromRealm()
                    container.deleteFromRealm()
                }
                val imagesOfPlatform = mRealm.where(ImageRealmEntity::class.java).equalTo("uuid", platform.uuid).findAll()
                imagesOfPlatform.deleteAllFromRealm()
                platform.deleteFromRealm()
            }
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

    /**
     ***Охраняемая Зона R_)OS
     */
    fun deleteData() {
        val realmConfiguration: RealmConfiguration = mRealm.getConfiguration()
        execInTransaction { p_realm ->
            for (clazz in realmConfiguration.realmObjectClasses) {
                if (clazz != Config::class.java) {
                    p_realm.delete(clazz)
                }
            }
        }

        val cntPlatformCreate = this.loadConfigL("cnt_platform__create")
        cntPlatformCreate.value = "0"
        this.saveConfig(cntPlatformCreate)
        val cntPlatformSync = this.loadConfigL("CNT_PLATFORM__SYNC")
        cntPlatformSync.value = "0"
        this.saveConfig(cntPlatformSync)

    }
    /**Охраняемая Зона R_)OS
    Охраняемая Зона R_)OS
    Охраняемая Зона R_)OS
     */

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
