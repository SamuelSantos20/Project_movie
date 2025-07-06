package com.br.sb.project_movie.mapper;

import com.br.sb.project_movie.dto.MovieDto;
import com.br.sb.project_movie.dto.MovieOutput;
import com.br.sb.project_movie.dto.MovieOutputDto;
import com.br.sb.project_movie.model.Movie;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.util.Base64;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS)
public interface MovieMapper {

    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);


    @Mapping(target = "id", ignore = true)
    Movie toModel(MovieDto movieDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void partialUpdate(MovieDto movieDTO, @MappingTarget Movie movie);

    @Mapping(target = "id", ignore = true)
    MovieDto toDto(Movie json);

    MovieOutputDto OUTPUT_DTO(Movie movie);

    @Mapping(source = "image", target = "image", qualifiedByName = "byteArrayToBase64")
    @Mapping(source = "trailer", target = "trailer", qualifiedByName = "byteArrayToBase64")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "durationToString")
    MovieOutput toOutput(Movie movie);

    @Named("byteArrayToBase64")
    static String byteArrayToBase64(byte[] value) {
        return value != null ? Base64.getEncoder().encodeToString(value) : null;
    }

    @Named("durationToString")
    static String durationToString(Duration duration) {
        return duration != null ? duration.toMinutes() + "min" : null;
    }
}
