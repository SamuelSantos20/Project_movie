package com.br.sb.project_movie.mapper;

import com.br.sb.project_movie.dto.MovieDto;
import com.br.sb.project_movie.dto.MovieOutputDto;
import com.br.sb.project_movie.model.Movie;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS)
public interface MovieMapper {
    @Mapping(target = "id", ignore = true)
    Movie toModel(MovieDto movieDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void partialUpdate(MovieDto movieDTO, @MappingTarget Movie movie);

    @Mapping(target = "id", ignore = true)
    MovieDto toDto(Movie json);

    MovieOutputDto OUTPUT_DTO(Movie movie);
}
