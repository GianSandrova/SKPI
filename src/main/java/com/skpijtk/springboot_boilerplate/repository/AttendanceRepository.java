package com.skpijtk.springboot_boilerplate.repository;

import com.skpijtk.springboot_boilerplate.model.Attendance;
import com.skpijtk.springboot_boilerplate.model.CheckInStatus;
import com.skpijtk.springboot_boilerplate.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository untuk mengelola entitas Attendance.
 * Mendukung query custom dan dynamic filtering menggunakan JpaSpecificationExecutor.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {

    /**
     * Menghitung jumlah mahasiswa yang melakukan check-in pada tanggal tertentu.
     */
    long countByAttendanceDateAndCheckInTimeIsNotNull(LocalDate attendanceDate);

    /**
     * Menghitung jumlah mahasiswa berdasarkan status check-in pada tanggal tertentu.
     */
    long countByAttendanceDateAndCheckInStatus(LocalDate attendanceDate, CheckInStatus checkInStatus);

    /**
     * Mengambil data attendance dengan filter opsional berdasarkan:
     * - Nama mahasiswa (dari entitas User)
     * - Rentang tanggal (startDate - endDate)
     * - Hanya yang sudah melakukan check-in
     *
     * JOIN FETCH digunakan agar data student dan user di-fetch dalam satu query (hindari N+1).
     */
    @Query("""
        SELECT a FROM Attendance a
        JOIN FETCH a.student s
        JOIN FETCH s.user u
        WHERE (:studentName IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :studentName, '%')))
          AND (:startDate IS NULL OR a.attendanceDate >= :startDate)
          AND (:endDate IS NULL OR a.attendanceDate <= :endDate)
          AND a.checkInTime IS NOT NULL
        """)
    Page<Attendance> findFilteredAttendanceWithDetails(
            @Param("studentName") String studentName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    /**
     * Mengambil satu data kehadiran berdasarkan mahasiswa dan tanggal.
     * Cocok digunakan untuk validasi kehadiran unik.
     */
    Optional<Attendance> findByStudentAndAttendanceDate(Student student, LocalDate attendanceDate);

    /**
     * Mengambil seluruh kehadiran mahasiswa dalam rentang tanggal tertentu, terurut berdasarkan tanggal.
     */
    List<Attendance> findByStudentAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            Student student, LocalDate startDate, LocalDate endDate);

    /**
     * Mengambil seluruh data attendance untuk mahasiswa tertentu pada tanggal spesifik (should be max 1 karena constraint).
     */
    List<Attendance> findByStudentAndAttendanceDateOrderByCreatedAtAsc(Student student, LocalDate date);

    /**
     * Mengambil seluruh riwayat attendance untuk mahasiswa, urut tanggal naik.
     */
    List<Attendance> findByStudentOrderByAttendanceDateAsc(Student student);

    /**
     * Mengambil data attendance terakhir mahasiswa berdasarkan tanggal kehadiran (desc).
     * Bisa dipakai untuk menampilkan riwayat terbaru.
     */
    Optional<Attendance> findFirstByStudentOrderByAttendanceDateDesc(Student student);
}
