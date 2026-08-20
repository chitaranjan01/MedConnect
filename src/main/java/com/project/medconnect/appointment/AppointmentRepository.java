package com.project.medconnect.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("""
            SELECT COUNT(a) > 0 FROM Appointment a
            WHERE a.doctor.userId = :doctorId
            AND a.status <> 'APPOINMENT_CANCELLED'
            AND a.startTime < :enddateTime
            AND a.endTime > :startdateTime
            """)
    boolean existsOverlapAppointment(
            @Param("doctorId") Integer doctorId,
            @Param("startdateTime") LocalDateTime startdateTime,
            @Param("enddateTime") LocalDateTime enddateTime
    );
}

