package ru.smartro.inventory.database

import io.realm.RealmObject
import ru.smartro.inventory.Snull

open class ImageRealmEntity(
    var imageBase64: String = Snull,
) : RealmObject()