package ru.smartro.inventory.core

import io.realm.RealmObject
import ru.smartro.inventory.base.AbstractEntity

//TOdo LiveEntity
//!/.)(Рассматривает внимательно изображение/
//Ну знаешь, это как дети которых лечишь, Если они болеют, или растут не в ту сторону
//В каком порядке и согласье
//!>Идет в пространствах ход р.Oбот/
//Все, что находится в запасе
//-В углах вселенной непочатых,
//То тысяча существ крылатых
//Поочередно подает
//Друг другу в золотых ушатах
//И вверх снует и вниз снует.
data class LoginEntity(
    val email: String,
    val password: String
)  : AbstractEntity()