package ru.smartro.inventory.database

import io.realm.RealmObject
import ru.smartro.inventory.base.AbstractEntity

//TOdo LiveEntity
data class LoginEntity(
    val email: String,
    val password: String
)  : AbstractEntity()