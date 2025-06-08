package com.br.sb.project_movie.mapper;

import com.br.sb.project_movie.dto.MovieDto;
import com.br.sb.project_movie.model.Movie;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS)
public interface MovieMapper {

     @Mapping(target = "id", ignore = true)
     MovieDto toDto(Movie movie);

     @Mapping(target = "id", ignore = true)
     Movie toModel(MovieDto movieDto);

     @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
     Movie partialUpdate(MovieDto movieDTO, @MappingTarget Movie movie);
}
