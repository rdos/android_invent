package ru.smartro.inventory.database

import android.content.Context

private const val DATABASE_NAME = "database.sqlite"

class AppDatabase private constructor(context: Context) {

//    companion object {
//        private var INSTANCE: AppDatabase? = null
//
//        fun initialize(context: Context) {
//            if (INSTANCE == null) {
//                INSTANCE = AppDatabase(context)
//            }
//        }
//
//        fun get(): AppDatabase {
//            return INSTANCE ?:
//            throw IllegalStateException("CatDatabase must be initialized")
//        }
//    }
//
//    private val mDatabase : AppRoomDatabase = Room.databaseBuilder(
//        context.applicationContext,
//        AppRoomDatabase::class.java,
//        DATABASE_NAME
//    ).addMigrations(migration_1_2)
//        .build()
//
//    private val mExecutor = Executors.newSingleThreadExecutor()
//    private val filesDir = context.applicationContext.filesDir
//
////    fun getCats(): LiveData<List<Cat>> = catDao.getCats()
////
////    fun getCats(maxCnt: Int): LiveData<List<Cat>> {
////        return catDao.getCats(maxCnt)
////    }
////
//////
//////    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)
//////
//////    fun updateCrime(crime: Crime) {
//////        executor.execute {
//////            crimeDao.updateCrime(crime)
//////        }
//////    }
////
////    fun addCat(catEntity: Cat) {
////        mExecutor.execute {
////            catDao.addCat(catEntity)
////        }
////    }
////
////    fun deleteCat(idForDelete: Int){
////        mExecutor.execute {
////            catDao.deleteId(idForDelete)
////        }
////    }
////
////    fun getPhotoFile(catEntity: Cat): File = File(filesDir, catEntity.photoFileName)
////
//    fun start(pCatEntity: Setting) {
//    mDatabase.settings().add(pCatEntity)
////        mExecutor.execute {
////            mDatabase.runInTransaction {
////                settingDao.addCat(pCatEntity)
//////                settingDao.deleteAll()
//////                for (catEntity in pCatEntity) {
//////                    settingDao.addCat(pCatEntity)
//////                }
////
////            }
////        }
//    }


}