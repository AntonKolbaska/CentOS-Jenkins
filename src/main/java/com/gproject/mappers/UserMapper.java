package com.gproject.mappers;

import com.gproject.dto.UserDto;
import com.gproject.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper
public interface UserMapper {
    UserDto userToDto(User user);

    User dtoToUser(UserDto userDto);

    Collection<UserDto> userToDtoCollection (Collection<User> userCollection);

    Collection<User> dtoToUserCollection (Collection<UserDto> userDtoCollection);
}
