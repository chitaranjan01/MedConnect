package com.project.medconnect.dto;

import com.project.medconnect.appointment.AppoinmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class AppointmentResponse {
    private Long id;
    private int patientId;
    private int doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppoinmentStatus status;
}
