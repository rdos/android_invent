package ru.smartro.inventory.core

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import ru.smartro.inventory.Snull
import ru.smartro.inventory.database.*

//data class Responsel(
data class ResponseI(
        @SerializedName("type")
        var type: String = Snull,
        @SerializedName("payload")
        val payload: PayloadCatalog = PayloadCatalog(),
        @SerializedName("error")
        val error: RealmList<ResponseRPCError> = RealmList()

) {

//        companion object {
//                fun from(responseBody: String?, classs: Class<*>): ResponseO<T> {
//                        val body = responseBody ?: Snull
//                        Log.i("ResponseO.from body", "=${body}")
//                        return Gson().fromJson(body, classs::class.java)
//                }
//        }
}



class SpinnerADataRealmL: RealmList<ContainerStatusRealm>() {

        fun containerStatusRealm_ADataO(): ContainerStatusRealm {
                //                                                      mapTOCardStatus_AData
                return ContainerStatusRealm(0, "Выберите Тип")
        }

        //getDEF_AbstractO GetIDatASpinneRrrrr
        fun GETiDatASpinner(): SpinnerADataRealmL {
                val sp = SpinnerADataRealmL()
                //containerStatus_to_AdataSPINNER
                sp.add(this.containerStatusRealm_ADataO())
                return sp
        }
}

open class PayloadCatalog(
        @SerializedName("container_type")
        var container_type: RealmList<ContainerTypeRealm> = RealmList(),
        @SerializedName("container_platform_type")
        var container_platform_type : RealmList<PlatformTypeRealm> = RealmList(),
        @SerializedName("card_status")
        var card_status : RealmList<CardStatusTypeRealm> = RealmList(),
        @SerializedName("container_status")
        var container_status : SpinnerADataRealmL = SpinnerADataRealmL().GETiDatASpinner()

) {

        //fun mapAData(): ContainerTypeRealm {
        fun mapTOPlatform_TypeO(): ContainerTypeRealm {
                // //mApTO_SpinnerAData
                // TODO: 14.12.2021 from O to I
                return ContainerTypeRealm(0, "Выберите Тип")
        }

        fun __TO_SpinnerAData(): PlatformTypeRealm {
                //TODO: 01.10.2220 __TO_SpinnerAData
                return PlatformTypeRealm(0, "Выберите Тип")
        }

        fun mapTOCardStatus_AData(): CardStatusTypeRealm {
                return CardStatusTypeRealm(0, "Выберите Тип")
        }



}