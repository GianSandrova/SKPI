package com.skpijtk.springboot_boilerplate.repository;

import com.skpijtk.springboot_boilerplate.model.Attendance;
import com.skpijtk.springboot_boilerplate.model.CheckInStatus;
import com.skpijtk.springboot_boilerplate.model.Student; // IMPORT BARU
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional; // IMPORT BARU
import java.util.List; // IMPORT BARU

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {

    long countByAttendanceDateAndCheckInTimeIsNotNull(LocalDate attendanceDate);
    long countByAttendanceDateAndCheckInStatus(LocalDate attendanceDate, CheckInStatus checkInStatus);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.student s JOIN FETCH s.user u WHERE " + // Menggunakan JOIN FETCH
           "(:studentName IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :studentName, '%'))) AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) AND " +
           "(a.checkInTime IS NOT NULL)") // Filter tambahan hanya yang sudah checkin
    Page<Attendance> findFilteredAttendanceWithDetails( // Nama method diubah agar lebih jelas
            @Param("studentName") String studentName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    // METHOD BARU untuk mengambil attendance student pada tanggal tertentu
    Optional<Attendance> findByStudentAndAttendanceDate(Student student, LocalDate attendanceDate);

    // METHOD BARU: Mengambil semua attendance untuk student dalam rentang tanggal, diurutkan
    List<Attendance> findByStudentAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            Student student, LocalDate startDate, LocalDate endDate);
            
    // METHOD BARU: Mengambil semua attendance untuk student pada satu tanggal (jika diperlukan sebagai list)
    List<Attendance> findByStudentAndAttendanceDateOrderByCreatedAtAsc( // Urutkan berdasarkan waktu pembuatan jika ada beberapa di hari yang sama (seharusnya tidak terjadi karena unique constraint)
            Student student, LocalDate date);
        // METHOD BARU: Mengambil semua attendance untuk student pada satu tanggal (jika diperlukan sebagai list)
    List<Attendance> findByStudentOrderByAttendanceDateAsc(Student student);

    Optional<Attendance> findFirstByStudentOrderByAttendanceDateDesc(Student student);
}