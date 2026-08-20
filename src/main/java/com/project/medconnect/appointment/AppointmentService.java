package com.project.medconnect.appointment;

import com.project.medconnect.domain.entity.Doctor;
import com.project.medconnect.domain.entity.Patient;
import com.project.medconnect.dto.AppointmentRequest;
import com.project.medconnect.dto.AppointmentResponse;
import com.project.medconnect.repository.DoctorRepository;
import com.project.medconnect.repository.PatientRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.medconnect.errorHandling.ConflictException;

import java.time.LocalDateTime;

@Service
public class AppointmentService {

    private static final int DEFAULT_DURATION_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public AppointmentResponse book(String email, AppointmentRequest request) throws BadRequestException {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new BadCredentialsException("Invalid doctor id"));
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("invalid patient"));

        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime() != null
                ? request.getEndTime()
                : start.plusMinutes(DEFAULT_DURATION_MINUTES);

        if (start.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("cannot book an appointment in the past");
        }
        if (!end.isAfter(start)) {
            throw new BadRequestException("end time must be after start time");
        }
        boolean conflict = appointmentRepository.existsOverlapAppointment(doctor.getUserId(), start, end);
        if (conflict) {
            throw new ConflictException("appointment already exists");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setReason(request.getReason());
        appointment.setStatus(AppoinmentStatus.APPOINMENT_PENDING);
        Appointment saved = appointmentRepository.save(appointment);

        AppointmentResponse appointmentResponse = new AppointmentResponse();
        appointmentResponse.setId(saved.getId());
        appointmentResponse.setDoctorId(doctor.getUserId());
        appointmentResponse.setPatientId(patient.getUserId());
        appointmentResponse.setStartTime(saved.getStartTime());
        appointmentResponse.setEndTime(saved.getEndTime());
        appointmentResponse.setStatus(saved.getStatus());
        return appointmentResponse;
    }
}