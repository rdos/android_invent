package ru.smartro.inventory

import ru.smartro.inventory.base.AbstractEntity

data class LoginEntity(
    val email: String,
    val password: String
)  : AbstractEntity()