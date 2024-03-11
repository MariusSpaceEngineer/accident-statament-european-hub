package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.AccidentStatementDTO;
import com.inetum.realdolmen.hubkitbackend.models.AccidentStatement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DriverMapper.class, WitnessMapper.class,
        MotorMapper.class, InsuranceCertificateMapper.class, TrailerMapper.class})
public interface AccidentStatementMapper {
    @Mapping(target = "id", ignore = true)
    AccidentStatement fromDTO(AccidentStatementDTO accidentStatementDTO);

    AccidentStatementDTO toDTO(AccidentStatement accidentStatement);

}
