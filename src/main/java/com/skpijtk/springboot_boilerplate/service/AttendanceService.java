package com.skpijtk.springboot_boilerplate.service;

// Java Standard Library
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Jakarta Persistence
import jakarta.persistence.criteria.Predicate;

// Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Project-specific DTOs
import com.skpijtk.springboot_boilerplate.dto.AttendanceRecordDto;
import com.skpijtk.springboot_boilerplate.dto.AttendanceResponseDto;
import com.skpijtk.springboot_boilerplate.dto.CheckinRequestDto;
import com.skpijtk.springboot_boilerplate.dto.CheckoutRequestDto;
import com.skpijtk.springboot_boilerplate.dto.PagedStudentAttendanceResponseData;
import com.skpijtk.springboot_boilerplate.dto.ResumeCheckinResponseData;
import com.skpijtk.springboot_boilerplate.dto.StudentAttendanceDto;
import com.skpijtk.springboot_boilerplate.dto.TotalMahasiswaResponseData;

// Project-specific Models
import com.skpijtk.springboot_boilerplate.model.AppSettings;
import com.skpijtk.springboot_boilerplate.model.Attendance;
import com.skpijtk.springboot_boilerplate.model.CheckInStatus;
import com.skpijtk.springboot_boilerplate.model.Student;
import com.skpijtk.springboot_boilerplate.model.User;

// Project-specific Repositories
import com.skpijtk.springboot_boilerplate.repository.AppSettingsRepository;
import com.skpijtk.springboot_boilerplate.repository.AttendanceRepository;
import com.skpijtk.springboot_boilerplate.repository.StudentRepository;
import com.skpijtk.springboot_boilerplate.repository.UserRepository;

// Project-specific Utils & Exceptions
import com.skpijtk.springboot_boilerplate.exception.CustomErrorException;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;

@Service
public class AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AppSettingsRepository appSettingsRepository; 

    // ... (method getTotalMahasiswa dan getResumeCheckin tetap sama) ...
    @Transactional(readOnly = true)
    public TotalMahasiswaResponseData getTotalMahasiswa() {
        try {
            long count = studentRepository.count();
            return new TotalMahasiswaResponseData(count);
        } catch (Exception e) {
            logger.error("Failed to get total mahasiswa from AttendanceService: {}", e.getMessage(), e);
            throw new CustomErrorException("T-ERR-006", ResponseMessage.T_ERR_006, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResumeCheckinResponseData getResumeCheckin(LocalDate date) {
        try {
            long totalMahasiswa = studentRepository.count();
            long totalCheckin = attendanceRepository.countByAttendanceDateAndCheckInTimeIsNotNull(date);
            long totalTelatCheckin = attendanceRepository.countByAttendanceDateAndCheckInStatus(date, CheckInStatus.TERLAMBAT);
            long totalBelumCheckin = totalMahasiswa - totalCheckin;
            if (totalBelumCheckin < 0) {
                logger.warn("Total belum checkin is negative ({}). Setting to 0. Total Mahasiswa: {}, Total Checkin: {} for date: {}", totalBelumCheckin, totalMahasiswa, totalCheckin, date);
                totalBelumCheckin = 0;
            }
            return ResumeCheckinResponseData.builder().totalMahasiswa(totalMahasiswa).totalCheckin(totalCheckin).totalBelumCheckin(totalBelumCheckin).totalTelatCheckin(totalTelatCheckin).build();
        } catch (Exception e) {
            logger.error("Failed to get resume checkin for date {}: {}", date, e.getMessage(), e);
            throw new CustomErrorException("T-ERR-006", ResponseMessage.T_ERR_006, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public PagedStudentAttendanceResponseData getListCheckinMahasiswa(
            String studentName, LocalDate startDate, LocalDate endDate,
            String sortBy, Pageable pageable) {
        try {
            Specification<Attendance> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (studentName != null && !studentName.isEmpty()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("student").get("user").get("name")), "%" + studentName.toLowerCase() + "%"));
                }
                if (startDate != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("attendanceDate"), startDate));
                }
                if (endDate != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("attendanceDate"), endDate));
                }
                predicates.add(criteriaBuilder.isNotNull(root.get("checkInTime")));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            
            Page<Attendance> attendancePage = attendanceRepository.findAll(spec, pageable);

            List<StudentAttendanceDto> dtoList = attendancePage.getContent().stream()
                    .map(this::mapToStudentAttendanceDto) // Panggil method yang sudah diperbaiki
                    .collect(Collectors.toList());

            return PagedStudentAttendanceResponseData.builder()
                    .data(dtoList)
                    .totalData(attendancePage.getTotalElements())
                    .totalPage(attendancePage.getTotalPages())
                    .currentPage(attendancePage.getNumber())
                    .pageSize(attendancePage.getSize())
                    .build();
        } catch (Exception e) {
            logger.error("Failed to get list checkin mahasiswa: {}", e.getMessage(), e);
            throw new CustomErrorException(
                    "T-ERR-006",
                    ResponseMessage.T_ERR_006,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private StudentAttendanceDto mapToStudentAttendanceDto(Attendance attendance) {
        Student student = attendance.getStudent();
        User user = student.getUser(); 

        // Buat satu objek AttendanceRecordDto dari objek Attendance saat ini
        AttendanceRecordDto attendanceRecordDto = AttendanceRecordDto.builder()
                .attendanceId(attendance.getId())
                .checkinTime(attendance.getCheckInTime())
                .checkoutTime(attendance.getCheckOutTime())
                .attendanceDate(attendance.getAttendanceDate())
                .late(attendance.getCheckInStatus() == CheckInStatus.TERLAMBAT)
                .notesCheckin(attendance.getCheckInNotes())
                .notesCheckout(attendance.getCheckOutNotes())
                .status(attendance.getCheckInStatus())
                .build();

        // Untuk API /admin/list_checkin_mahasiswa, DTO StudentAttendanceDto
        // seharusnya berisi satu attendanceData (bukan list), karena setiap item adalah satu event absensi.
        // Namun, jika Anda telah mengubah StudentAttendanceDto secara global menjadi
        // private List<AttendanceRecordDto> attendanceData; untuk API /admin/list_all_mahasiswa,
        // maka untuk API ini, Anda perlu membuat list yang hanya berisi satu item attendanceRecordDto ini.
        // Atau, gunakan DTO yang berbeda.

        // Asumsi StudentAttendanceDto yang relevan di sini adalah yang memiliki satu attendanceData:
        // Jika StudentAttendanceDto memiliki: private AttendanceRecordDto attendanceData;
        // return StudentAttendanceDto.builder()
        //         .studentId(student.getId())
        //         .userId(user.getId())
        //         .studentName(user.getName())
        //         .nim(student.getNim())
        //         .email(user.getEmail())
        //         .attendanceData(attendanceRecordDto) // <- PERBAIKAN: gunakan objek attendanceRecordDto
        //         .build();

        // JIKA StudentAttendanceDto telah Anda ubah menjadi: private List<AttendanceRecordDto> attendanceData;
        // untuk mengakomodasi API /list_all_mahasiswa, maka untuk API /list_checkin_mahasiswa ini
        // yang per itemnya adalah SATU event absensi, Anda perlu membungkus attendanceRecordDto dalam list:
        List<AttendanceRecordDto> records = new ArrayList<>();
        records.add(attendanceRecordDto);

        return StudentAttendanceDto.builder()
                .studentId(student.getId())
                .userId(user.getId())
                .studentName(user.getName())
                .nim(student.getNim())
                .email(user.getEmail())
                .attendanceData(records) // <- Menggunakan list yang berisi satu DTO
                .build();
    }

    @Transactional
    public AttendanceResponseDto performCheckIn(Authentication authentication, CheckinRequestDto request) {
        Student student = findStudentByAuthentication(authentication);
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // Business Rule: Cek apakah sudah checkout hari sebelumnya
        attendanceRepository.findFirstByStudentOrderByAttendanceDateDesc(student)
            .ifPresent(lastAttendance -> {
                if (!lastAttendance.getAttendanceDate().isBefore(today) && lastAttendance.getCheckOutTime() == null) {
                    // PERUBAHAN DI SINI: Gunakan T-ERR-010
                    throw new CustomErrorException("T-ERR-010", "Data Failed to be saved. Reason: You must check out from the previous day before checking in.", HttpStatus.BAD_REQUEST);
                }
            });

        // Business Rule: Cek apakah sudah check-in hari ini
        Optional<Attendance> todayAttendanceOpt = attendanceRepository.findByStudentAndAttendanceDate(student, today);
        if (todayAttendanceOpt.isPresent() && todayAttendanceOpt.get().getCheckInTime() != null) {
            // PERUBAHAN DI SINI: Gunakan T-ERR-010
            throw new CustomErrorException("T-ERR-010", "Data Failed to be saved. Reason: You have already checked in today.", HttpStatus.BAD_REQUEST);
        }

        // Ambil AppSettings untuk menentukan status terlambat
        AppSettings settings = appSettingsRepository.findById(1)
            .orElseThrow(() -> new CustomErrorException("T-ERR-002", "Application settings not configured.", HttpStatus.INTERNAL_SERVER_ERROR));
        
        LocalDateTime checkinDeadline = today.atTime(settings.getDefaultCheckInTime())
                                             .plusMinutes(settings.getCheckInLateToleranceMinutes());
        
        CheckInStatus status = now.isAfter(checkinDeadline) ? CheckInStatus.TERLAMBAT : CheckInStatus.TEPAT_WAKTU;

        // Buat atau update record attendance
        Attendance attendance = todayAttendanceOpt.orElseGet(() -> 
            Attendance.builder()
                .student(student)
                .attendanceDate(today)
                .build()
        );
        
        attendance.setCheckInTime(now);
        attendance.setCheckInNotes(request.getNotesCheckin());
        attendance.setCheckInStatus(status);
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        
        return mapToAttendanceResponseDto(savedAttendance);
    }

    @Transactional
    public AttendanceResponseDto performCheckOut(Authentication authentication, CheckoutRequestDto request) {
        Student student = findStudentByAuthentication(authentication);
        LocalDate today = LocalDate.now();
        
        // Business Rule: Cari data absensi hari ini, pastikan sudah check-in
        Attendance attendance = attendanceRepository.findByStudentAndAttendanceDate(student, today)
            .filter(att -> att.getCheckInTime() != null)
            .orElseThrow(() -> new CustomErrorException("T-ERR-010", "Data Failed to be saved. Reason: You must check in before checking out.", HttpStatus.BAD_REQUEST)); // PERUBAHAN DI SINI
            
        // Business Rule: Pastikan belum pernah check-out
        if (attendance.getCheckOutTime() != null) {
            // PERUBAHAN DI SINI: Gunakan T-ERR-010
            throw new CustomErrorException("T-ERR-010", "Data Failed to be saved. Reason: You have already checked out today.", HttpStatus.BAD_REQUEST);
        }

        // Update record attendance
        attendance.setCheckOutTime(LocalDateTime.now());
        attendance.setCheckOutNotes(request.getNotesCheckout());
        
        Attendance savedAttendance = attendanceRepository.save(attendance);

        return mapToAttendanceResponseDto(savedAttendance);
    }

    // Helper method untuk mencari student
    private Student findStudentByAuthentication(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomErrorException("T-ERR-005", "User not found.", HttpStatus.NOT_FOUND));
        return studentRepository.findByUser(user).orElseThrow(() -> new CustomErrorException("T-ERR-005", "Student profile not found for this user.", HttpStatus.NOT_FOUND));
    }

    // Helper method untuk mapping ke DTO respons
    private AttendanceResponseDto mapToAttendanceResponseDto(Attendance attendance) {
        Student student = attendance.getStudent();
        User user = student.getUser();
        return AttendanceResponseDto.builder()
                .studentId(student.getId())
                .studentName(user.getName())
                .nim(student.getNim())
                .attendanceId(attendance.getId())
                .checkinTime(attendance.getCheckInTime())
                .checkoutTime(attendance.getCheckOutTime())
                .attendanceDate(attendance.getAttendanceDate())
                .notesCheckin(attendance.getCheckInNotes())
                .notesCheckout(attendance.getCheckOutNotes())
                .statusCheckin(attendance.getCheckInStatus())
                .build();
    }
}