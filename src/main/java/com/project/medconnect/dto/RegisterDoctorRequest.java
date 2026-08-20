package com.project.medconnect.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDoctorRequest {

    @NotNull
    private String username;

    @NotNull
    @Size(min = 8, max = 75)
    private String password;

    @NotNull
    private String email;

    @NotNull
    private String specialty;

    @NotNull
    private String licenseNumber;


}
