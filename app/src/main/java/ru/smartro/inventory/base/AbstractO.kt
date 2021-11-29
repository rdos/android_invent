package ru.smartro.inventory.base

import io.realm.Realm
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.smartro.inventory.RealmRepo

//ToD LiveEntity
/**  private fun gotoAddPlatform() {
val platformEntity = db().createPlatformEntity()
 **/
//gotoAdd = Add -> create->ПОТОМ->Save->ПОТОМ Show
abstract class AbstractO {
    protected val log: Logger = LoggerFactory.getLogger("${this::class.simpleName}")

    protected val db by lazy {
        db()
    }

    protected fun db(): RealmRepo {
        return RealmRepo(Realm.getDefaultInstance())
    }
}