package com.inetum.realdolmen.crashkit.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.inetum.realdolmen.crashkit.dto.MotorDTO
import com.inetum.realdolmen.crashkit.dto.TrailerDTO
import com.inetum.realdolmen.crashkit.dto.Vehicle
import java.lang.reflect.Type

/**
 * `VehicleAdapter` is a class that provides custom serialization and deserialization for `Vehicle` objects.
 * It implements `JsonDeserializer<Vehicle>` and `JsonSerializer<Vehicle>`.
 *
 * @function deserialize This function takes a JsonElement and converts it into a `Vehicle` object.
 * It checks the type of vehicle based on the properties present in the JSON and creates the appropriate `Vehicle` object.
 *
 * @function serialize This function takes a `Vehicle` object and converts it into a JsonElement.
 * It checks the type of the vehicle and adds the appropriate properties to the JSON.
 */
class VehicleAdapter : JsonDeserializer<Vehicle>, JsonSerializer<Vehicle> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Vehicle {
        val jsonObject = json.asJsonObject

        val id = jsonObject.get("id").asInt
        val licensePlate = jsonObject.get("licensePlate").asString
        val countryOfRegistration = jsonObject.get("countryOfRegistration").asString

        return when {
            jsonObject.has("hasRegistration") -> TrailerDTO(
                id,
                licensePlate,
                countryOfRegistration,
                hasRegistration =
                jsonObject.get("hasRegistration").asBoolean,
                null
            )

            jsonObject.has("markType") -> MotorDTO(
                id,
                licensePlate,
                countryOfRegistration, markType =
                jsonObject.get("markType").asString
            )

            else -> throw JsonParseException("Not a Vehicle")
        }
    }

    override fun serialize(
        src: Vehicle,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val jsonObject = JsonObject()

        jsonObject.addProperty("id", src.id)
        jsonObject.addProperty("licensePlate", src.licensePlate)
        jsonObject.addProperty("countryOfRegistration", src.countryOfRegistration)

        when (src) {
            is TrailerDTO -> {
                jsonObject.addProperty("hasRegistration", src.hasRegistration)
            }

            is MotorDTO -> {
                jsonObject.addProperty("markType", src.markType)
            }
        }

        return jsonObject
    }
}
