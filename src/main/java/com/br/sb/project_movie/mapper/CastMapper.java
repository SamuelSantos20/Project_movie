package com.br.sb.project_movie.mapper;

import com.br.sb.project_movie.dto.CastDto;
import com.br.sb.project_movie.model.Cast;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CastMapper {

    CastDto toDto(Cast cast);

    Cast toModel(CastDto castDto);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    Cast partialUpdate(CastDto castDto, @org.mapstruct.MappingTarget Cast cast);


}
