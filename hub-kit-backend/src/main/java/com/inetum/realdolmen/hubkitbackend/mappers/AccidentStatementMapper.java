package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.AccidentStatementDTO;
import com.inetum.realdolmen.hubkitbackend.models.AccidentImage;
import com.inetum.realdolmen.hubkitbackend.models.AccidentStatement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AccidentImage.class, DriverMapper.class, WitnessMapper.class,
        MotorMapper.class, PolicyHolderMapper.class, TrailerMapper.class})
public interface AccidentStatementMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "vehicleAAccidentImages", target = "vehicleAAccidentImages")
    @Mapping(source = "vehicleBAccidentImages", target = "vehicleBAccidentImages")
    AccidentStatement fromDTO(AccidentStatementDTO accidentStatementDTO);

    AccidentStatementDTO toDTO(AccidentStatement accidentStatement);

}
