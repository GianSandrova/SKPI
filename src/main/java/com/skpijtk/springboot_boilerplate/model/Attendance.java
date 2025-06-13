package com.skpijtk.springboot_boilerplate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attendance", // Sesuai DDL
    uniqueConstraints = {
        // Nama constraint di DDL: uq_student_attendance_date
        // Kolom di DDL: student_id, attendance_date
        @UniqueConstraint(name = "uq_student_attendance_date", columnNames = {"student_id", "attendance_date"})
    }
)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key ke tabel students
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    private Student student;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate; // DDL: DATE

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime; // DDL: DATETIME

    @Enumerated(EnumType.STRING)
    @Column(name = "check_in_status")
    private CheckInStatus checkInStatus; // DDL: ENUM('TEPAT_WAKTU', 'TERLAMBAT')

    @Lob // Menandakan Large Object
    @Column(name = "check_in_notes", columnDefinition = "TEXT") // Eksplisit mapping ke TEXT MySQL
    private String checkInNotes; // DDL: TEXT

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;// DDL: DATETIME

    @Lob // Menandakan Large Object
    @Column(name = "check_out_notes", columnDefinition = "TEXT") // Eksplisit mapping ke TEXT MySQL
    private String checkOutNotes; // DDL: TEXT

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}