package com.gproject.entity;

import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private int id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Roles role;
    private String company;
}
