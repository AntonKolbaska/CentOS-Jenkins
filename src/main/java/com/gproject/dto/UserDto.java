package com.gproject.dto;

import com.gproject.entity.Roles;
import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class UserDto {

    private int id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Roles role;
    private String company;
}
