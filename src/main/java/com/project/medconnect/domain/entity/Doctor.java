package com.project.medconnect.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "doctor")
@Entity
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "user_id")
public class Doctor extends User {
    private String specialty;
    private String licenseNumber;
}
