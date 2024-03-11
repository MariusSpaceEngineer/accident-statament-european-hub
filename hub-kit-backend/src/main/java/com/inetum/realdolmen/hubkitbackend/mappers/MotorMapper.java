package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.MotorDTO;
import com.inetum.realdolmen.hubkitbackend.models.Motor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MotorMapper {
    @Mapping(target = "id", ignore = true)
    Motor fromDTO(MotorDTO motorDTO);

    MotorDTO toDTO(Motor motor);
}
