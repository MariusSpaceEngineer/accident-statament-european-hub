package com.inetum.realdolmen.hubkitbackend.mappers;


import com.inetum.realdolmen.hubkitbackend.dto.WitnessDTO;
import com.inetum.realdolmen.hubkitbackend.models.Driver;
import com.inetum.realdolmen.hubkitbackend.models.Witness;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WitnessMapper {
    @Mapping(target = "id", ignore = true)
    Driver fromDTO(WitnessDTO witnessDTO);

    WitnessDTO toDTO(Witness witness);
}
