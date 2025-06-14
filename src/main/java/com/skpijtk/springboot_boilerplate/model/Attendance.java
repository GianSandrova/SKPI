package com.skpijtk.springboot_boilerplate.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity yang merepresentasikan data kehadiran mahasiswa.
 * Setiap entri bersifat unik berdasarkan kombinasi student dan tanggal (attendance_date).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "attendance",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_student_attendance_date",
            columnNames = {"student_id", "attendance_date"}
        )
    }
)
public class Attendance {

    /**
     * Primary key (auto increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mahasiswa yang melakukan check-in.
     * Relasi Many-to-One dengan entitas Student.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    private Student student;

    /**
     * Tanggal kehadiran (tanpa jam).
     */
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    /**
     * Waktu check-in (boleh null jika belum absen).
     */
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    /**
     * Status check-in: TEPAT_WAKTU atau TERLAMBAT.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "check_in_status")
    private CheckInStatus checkInStatus;

    /**
     * Catatan opsional saat check-in.
     */
    @Lob
    @Column(name = "check_in_notes", columnDefinition = "TEXT")
    private String checkInNotes;

    /**
     * Waktu check-out (boleh null jika belum absen keluar).
     */
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    /**
     * Catatan opsional saat check-out.
     */
    @Lob
    @Column(name = "check_out_notes", columnDefinition = "TEXT")
    private String checkOutNotes;

    /**
     * Timestamp otomatis saat record dibuat.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp otomatis saat record terakhir diperbarui.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
