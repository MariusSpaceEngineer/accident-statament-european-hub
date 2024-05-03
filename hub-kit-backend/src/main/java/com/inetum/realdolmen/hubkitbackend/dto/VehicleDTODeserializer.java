package com.inetum.realdolmen.hubkitbackend.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class VehicleDTODeserializer extends StdDeserializer<VehicleDTO> {
    public VehicleDTODeserializer() {
        this(null);
    }

    public VehicleDTODeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public VehicleDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String licensePlate = null;
        if (node.has("licensePlate")) {
            licensePlate = node.get("licensePlate").asText();
        }
        String countryOfRegistration = null;
        if (node.has("countryOfRegistration")) {
            countryOfRegistration = node.get("countryOfRegistration").asText();
        }
        Integer id = null;
        if (node.has("id")) {
            id = node.get("id").asInt();
        }

        if (node.has("markType")) {
            String markType = node.get("markType").asText();
            return MotorDTO.builder()
                    .id(id)
                    .licensePlate(licensePlate).
                    countryOfRegistration(countryOfRegistration)
                    .markType(markType)
                    .build();
        } else if (node.has("hasRegistration")) {
            Boolean hasRegistration = node.get("hasRegistration").asBoolean();
            TrailerDTO trailerDTO = TrailerDTO.builder().id(id)
                    .licensePlate(licensePlate)
                    .countryOfRegistration(countryOfRegistration)
                    .hasRegistration(hasRegistration).build();
            if (node.has("ofVehicle")) {
                String ofVehicle = node.get("ofVehicle").asText();
                trailerDTO.setOfVehicle(ofVehicle);
            }

            return trailerDTO;
        }
        throw new IllegalArgumentException("Unsupported VehicleDTO type");
    }
}
