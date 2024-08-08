package com.example.Sales.Dto;

import com.example.Sales.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    private String username;
    private String password;
    private String email;
    private Role role;
}
