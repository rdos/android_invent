package ru.smartro.inventory.core

import ru.smartro.inventory.base.AbstractEntity

data class CatalogsRequestEntity(
    var payload: PayLoadCatalogRequest,
    var type: String = "mobile_get_catalogs",
)  : AbstractEntity() {

}

data class PayLoadCatalogRequest (
    val organisation_id: Int = 1,
    var catalogs: MutableList<String> = mutableListOf("container_type", "container_platform_type", "card_status", "container_status")
) : AbstractEntity()

