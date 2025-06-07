package com.br.sb.project_movie.mapper;

import com.br.sb.project_movie.dto.HistoryDto;
import com.br.sb.project_movie.model.History;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel =  MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, imports = {UUID.class},
        uses = {UserMapper.class, MovieMapper.class})
public interface HistoryMapper {

    HistoryDto toDto(History history);

    History toModel(HistoryDto historyDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    History partialUpdate(HistoryDto historyDTO, @org.mapstruct.MappingTarget History history);
}
