package com.inetum.realdolmen.hubkitbackend.mappers;


import com.inetum.realdolmen.hubkitbackend.dto.TrailerDTO;
import com.inetum.realdolmen.hubkitbackend.models.Trailer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrailerMapper {
    @Mapping(target = "id", ignore = true)
    Trailer fromDTO(TrailerDTO trailerDTO);

    TrailerDTO toDTO(Trailer trailer);
}