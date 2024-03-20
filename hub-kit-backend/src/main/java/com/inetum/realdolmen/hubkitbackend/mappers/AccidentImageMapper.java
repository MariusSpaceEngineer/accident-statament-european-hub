package com.inetum.realdolmen.hubkitbackend.mappers;

import com.inetum.realdolmen.hubkitbackend.dto.AccidentImageDTO;
import com.inetum.realdolmen.hubkitbackend.models.AccidentImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccidentImageMapper {
    @Mapping(target = "id", ignore = true)
    AccidentImage fromDTO(AccidentImageDTO accidentImageDTO);
}
