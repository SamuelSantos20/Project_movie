package com.br.sb.project_movie.mapper;

import com.br.sb.project_movie.dto.UserDto;
import com.br.sb.project_movie.model.User;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);


    User toModel(UserDto userDto);


@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
User partialUpdate(UserDto timesheetDTO, @MappingTarget User timesheet);


}
