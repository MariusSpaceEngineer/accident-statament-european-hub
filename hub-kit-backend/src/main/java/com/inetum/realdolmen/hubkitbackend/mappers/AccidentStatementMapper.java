package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.AccidentStatementDTO;
import com.inetum.realdolmen.hubkitbackend.models.AccidentStatement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DriverMapper.class, WitnessMapper.class,
        MotorMapper.class, PolicyHolderMapper.class, TrailerMapper.class})
public interface AccidentStatementMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "vehicleAAccidentImage", target = "vehicleAAccidentImage")
    @Mapping(source = "vehicleBAccidentImage", target = "vehicleBAccidentImage")
    AccidentStatement fromDTO(AccidentStatementDTO accidentStatementDTO);

    AccidentStatementDTO toDTO(AccidentStatement accidentStatement);

}
