package com.gproject.services;

import com.gproject.dto.UserDto;
import com.gproject.exception.CustomSQLException;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface UserService {

    <Optional> UserDto findUserById(int id) throws CustomSQLException;

    <Optional>UserDto findUserByUsername(String username)  throws CustomSQLException;

    Collection<UserDto> getAllUsers()  throws CustomSQLException;

//    Collection<UserDto> getAllUsersFromCompany(String company)  throws CustomSQLException;

    Optional<Integer> createUser(final UserDto userDto)  throws CustomSQLException;

    UserDto updateUser(final UserDto userDto)  throws CustomSQLException;

    boolean deleteUser(int id)  throws CustomSQLException;
}
