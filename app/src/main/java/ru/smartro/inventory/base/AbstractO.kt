package ru.smartro.inventory.base

import io.realm.Realm
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.smartro.inventory.database.RealmRepository

abstract class AbstractO {
    protected val log: Logger = LoggerFactory.getLogger("${this::class.simpleName}")

    protected val db by lazy {
        RealmRepository(Realm.getDefaultInstance())
    }
}