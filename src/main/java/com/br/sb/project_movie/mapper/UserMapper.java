package com.br.sb.project_movie.mapper;

import com.br.sb.project_movie.dto.UserDto;
import com.br.sb.project_movie.model.User;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, imports = {UUID.class})
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toModel(UserDto userDto);


@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
User partialUpdate(UserDto timesheetDTO, @MappingTarget User timesheet);


}
