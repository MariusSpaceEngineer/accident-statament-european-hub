package com.inetum.realdolmen.crashkit.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.inetum.realdolmen.crashkit.dto.MotorDTO
import com.inetum.realdolmen.crashkit.dto.TrailerDTO
import com.inetum.realdolmen.crashkit.dto.Vehicle
import java.lang.reflect.Type


class VehicleDeserializer : JsonDeserializer<Vehicle> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Vehicle {
        val jsonObject = json.asJsonObject

        val licensePlate = jsonObject.get("licensePlate").asString
        val countryOfRegistration = jsonObject.get("countryOfRegistration").asString

        return when {
            jsonObject.has("hasRegistration") -> TrailerDTO(
                licensePlate,
                countryOfRegistration,
                jsonObject.get("hasRegistration").asBoolean
            )
            jsonObject.has("markType") -> MotorDTO(
                licensePlate,
                countryOfRegistration,
                jsonObject.get("markType").asString
            )
            else -> throw JsonParseException("Not a Vehicle")
        }
    }
}
