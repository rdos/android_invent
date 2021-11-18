package ru.smartro.inventory.database

val Onull = ObjNullRealm()
class ObjNullRealm (
   val msg: String = "Оо..Ошибка.ObjNullRealm"
) : PlatformEntityRealm()


val O2null = O2NullRealm()
class O2NullRealm (
   val msg: String = "Оо..Ошибка2.O2NullRealm"
) : ContainerEntityRealm()
