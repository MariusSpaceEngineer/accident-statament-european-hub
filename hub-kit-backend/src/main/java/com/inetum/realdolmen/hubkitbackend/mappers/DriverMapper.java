package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.DriverDTO;
import com.inetum.realdolmen.hubkitbackend.models.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    @Mapping(target = "id", ignore = true)
    Driver fromDTO(DriverDTO driverDTO);

    DriverDTO toDTO(Driver driver);
}

