package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.AccidentStatementDTO;
import com.inetum.realdolmen.hubkitbackend.models.AccidentImage;
import com.inetum.realdolmen.hubkitbackend.models.AccidentStatement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AccidentImage.class, DriverMapper.class, WitnessMapper.class, PolicyHolderMapper.class, TrailerMapper.class})
public interface AccidentStatementMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "vehicleAAccidentImages", target = "vehicleAAccidentImages")
    @Mapping(source = "vehicleBAccidentImages", target = "vehicleBAccidentImages")
    @Mapping(source = "sketchOfAccident", target = "sketchOfAccident")
    @Mapping(source = "vehicleACircumstances", target = "vehicleACircumstances")
    @Mapping(source = "vehicleBCircumstances", target = "vehicleBCircumstances")
    @Mapping(source = "vehicleAInitialImpactSketch", target = "vehicleAInitialImpactSketch")
    @Mapping(source = "vehicleBInitialImpactSketch", target = "vehicleBInitialImpactSketch")
    @Mapping(source = "vehicleAVisibleDamageDescription", target = "vehicleAVisibleDamageDescription")
    @Mapping(source = "vehicleBVisibleDamageDescription", target = "vehicleBVisibleDamageDescription")
    @Mapping(source = "vehicleARemark", target = "vehicleARemark")
    @Mapping(source = "vehicleBRemark", target = "vehicleBRemark")
    @Mapping(source = "vehicleASignature", target = "vehicleASignature")
    @Mapping(source = "vehicleBSignature", target = "vehicleBSignature")
    @Mapping(source = "unregisteredTrailers", target = "unregisteredTrailers")
    AccidentStatement fromDTO(AccidentStatementDTO accidentStatementDTO);

    AccidentStatementDTO toDTO(AccidentStatement accidentStatement);

}
