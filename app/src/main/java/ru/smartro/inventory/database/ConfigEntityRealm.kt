package ru.smartro.inventory.database


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

//open class WayTaskEntity(
//    @PrimaryKey
//    @SerializedName("id")
//    var id: Int? = null,
//    @SerializedName("accounting")
//    var accounting: Int? = null,
//    @SerializedName("beginned_at")
//    var beginnedAt: String? = null,
//    @SerializedName("finished_at")
//    var finishedAt: String? = null,
//    @SerializedName("name")
//    var name: String? = null,
//    @SerializedName("platforms")
//    var platforms: RealmList<PlatformEntity> = RealmList(),
//    @SerializedName("start")
//    var start: StartEntity? = null,
//    @SerializedName("unload")
//    var unload: UnloadEntity? = null
//) : Serializable, RealmObject()

enum class TaskStatus(val displayName: String) {
    Open("Open"),
    InProgress("In Progress"),
    Complete("Complete"),
}
open class ConfigEntityRealm(
    @PrimaryKey var name: String = "task",
    var value: String = "rNull"
) : RealmObject() {
    @Required
    var status: String = TaskStatus.Open.name
    var statusEnum: TaskStatus
        get() {
            // because status is actually a String and another client could assign an invalid value,
            // default the status to "Open" if the status is unreadable
            return try {
                TaskStatus.valueOf(status)
            } catch (e: IllegalArgumentException) {
                TaskStatus.Open
            }
        }
        set(value) { status = value.name }
}

