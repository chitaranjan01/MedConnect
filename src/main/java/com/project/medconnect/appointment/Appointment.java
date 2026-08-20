package com.project.medconnect.appointment;

import com.project.medconnect.domain.entity.Doctor;
import com.project.medconnect.domain.entity.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id" , nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    private LocalDateTime startTime;
    private  LocalDateTime endTime;

     private String reason;

     @Enumerated(EnumType.STRING)
    private AppoinmentStatus status;
}
