package com.fivepartday.alarm.data.model

import org.json.JSONObject

data class AlarmItem(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("hour", hour)
        put("minute", minute)
        put("enabled", enabled)
    }

    companion object {
        fun fromJson(json: JSONObject): AlarmItem = AlarmItem(
            id = json.getInt("id"),
            hour = json.getInt("hour"),
            minute = json.getInt("minute"),
            enabled = json.getBoolean("enabled")
        )
    }
}
