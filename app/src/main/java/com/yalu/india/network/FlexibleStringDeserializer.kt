package com.yalu.india.network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Robust Gson String deserializer that safely handles string properties
 * whether the backend API returns a raw JSON primitive string ("Category Name")
 * or a JSON object (e.g. multilingual {"en": "Category Name"} or nested object).
 */
class FlexibleStringDeserializer : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String? {
        if (json == null || json.isJsonNull) return null
        if (json.isJsonPrimitive) {
            return json.asString
        }
        if (json.isJsonObject) {
            val obj = json.asJsonObject
            if (obj.has("en") && !obj.get("en").isJsonNull && obj.get("en").isJsonPrimitive) {
                return obj.get("en").asString
            }
            if (obj.has("name") && !obj.get("name").isJsonNull && obj.get("name").isJsonPrimitive) {
                return obj.get("name").asString
            }
            if (obj.has("title") && !obj.get("title").isJsonNull && obj.get("title").isJsonPrimitive) {
                return obj.get("title").asString
            }
            if (obj.has("value") && !obj.get("value").isJsonNull && obj.get("value").isJsonPrimitive) {
                return obj.get("value").asString
            }
            for (entry in obj.entrySet()) {
                if (entry.value.isJsonPrimitive) {
                    return entry.value.asString
                }
            }
        }
        return null
    }
}
