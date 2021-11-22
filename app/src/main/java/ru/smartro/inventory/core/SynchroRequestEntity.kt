package ru.smartro.inventory.core

import ru.smartro.inventory.Inull
import ru.smartro.inventory.Snull
import ru.smartro.inventory.base.AbstractEntity
import ru.smartro.inventory.database.ContainerEntityRealm
import ru.smartro.inventory.database.PlatformEntityRealm

class SynchroRequestEntity (
    val payload: PayLoadSynchroRequest = PayLoadSynchroRequest(),
    var type: String = "mobile_inventory_synchro_store",
)  : AbstractEntity() {

}

data class PayLoadSynchroRequest (
    // TODO: 22.11.2021  
    var organisation_id: Int = Inull,
    val device: String = Snull,
    val data: MutableList<PlatformEntityRealm> = mutableListOf()
) : AbstractEntity()

