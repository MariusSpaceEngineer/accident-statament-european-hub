package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.MotorDTO;
import com.inetum.realdolmen.hubkitbackend.models.Motor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MotorMapper {
    Motor fromDTO(MotorDTO motorDTO);

    MotorDTO toDTO(Motor motor);

    @Mapping(target = "id", ignore = true)
    Motor updateFromDTO(MotorDTO insuranceAgencyDTO, @MappingTarget Motor insuranceAgency);
}
