package com.skpijtk.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.dto.*;
import com.skpijtk.springboot_boilerplate.exception.CustomErrorException;
import com.skpijtk.springboot_boilerplate.handler.WebSocketHandler;
import com.skpijtk.springboot_boilerplate.model.*;
import com.skpijtk.springboot_boilerplate.repository.AppSettingsRepository;
import com.skpijtk.springboot_boilerplate.repository.AttendanceRepository;
import com.skpijtk.springboot_boilerplate.repository.StudentRepository;
import com.skpijtk.springboot_boilerplate.repository.UserRepository;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);
    private static final int APP_SETTINGS_ID = 1;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AppSettingsRepository appSettingsRepository;

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Transactional(readOnly = true)
    public TotalMahasiswaResponseData getTotalMahasiswa() {
        try {
            long count = studentRepository.count();
            return new TotalMahasiswaResponseData(count);
        } catch (Exception e) {
            logger.error("Failed to get total mahasiswa: {}", e.getMessage(), e);
            throw new CustomErrorException("T-ERR-006", ResponseMessage.T_ERR_006, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResumeCheckinResponseData getResumeCheckin(LocalDate date) {
        try {
            long totalMahasiswa = studentRepository.count();
            long totalCheckin = attendanceRepository.countByAttendanceDateAndCheckInTimeIsNotNull(date);
            long totalTelatCheckin = attendanceRepository.countByAttendanceDateAndCheckInStatus(date, CheckInStatus.TERLAMBAT);
            long totalBelumCheckin = Math.max(0, totalMahasiswa - totalCheckin);

            return ResumeCheckinResponseData.builder()
                    .totalMahasiswa(totalMahasiswa)
                    .totalCheckin(totalCheckin)
                    .totalBelumCheckin(totalBelumCheckin)
                    .totalTelatCheckin(totalTelatCheckin)
                    .build();
        } catch (Exception e) {
            logger.error("Failed to get resume checkin for {}: {}", date, e.getMessage(), e);
            throw new CustomErrorException("T-ERR-006", ResponseMessage.T_ERR_006, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public PagedStudentAttendanceResponseData getListCheckinMahasiswa(String studentName, LocalDate startDate, LocalDate endDate, String sortBy, Pageable pageable) {
        try {
            Specification<Attendance> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (studentName != null && !studentName.isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("student").get("user").get("name")), "%" + studentName.toLowerCase() + "%"));
                }
                if (startDate != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("attendanceDate"), startDate));
                }
                if (endDate != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("attendanceDate"), endDate));
                }
                predicates.add(cb.isNotNull(root.get("checkInTime")));
                return cb.and(predicates.toArray(new Predicate[0]));
            };

            Page<Attendance> page = attendanceRepository.findAll(spec, pageable);
            List<StudentAttendanceDto> dtoList = page.getContent().stream()
                    .map(this::mapToStudentAttendanceDto)
                    .collect(Collectors.toList());

            return PagedStudentAttendanceResponseData.builder()
                    .data(dtoList)
                    .totalData(page.getTotalElements())
                    .totalPage(page.getTotalPages())
                    .currentPage(page.getNumber())
                    .pageSize(page.getSize())
                    .build();
        } catch (Exception e) {
            logger.error("Failed to get checkin list: {}", e.getMessage(), e);
            throw new CustomErrorException("T-ERR-006", ResponseMessage.T_ERR_006, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public AttendanceResponseDto performCheckIn(Authentication authentication, CheckinRequestDto request) {
        Student student = findStudentByAuthentication(authentication);
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        checkLastAttendanceNotCheckedOut(student, today);

        Optional<Attendance> todayAttendanceOpt = attendanceRepository.findByStudentAndAttendanceDate(student, today);
        validateCheckinNotExists(todayAttendanceOpt);

        AppSettings settings = appSettingsRepository.findById(APP_SETTINGS_ID)
                .orElseThrow(() -> new CustomErrorException("T-ERR-002", "Application settings not configured.", HttpStatus.INTERNAL_SERVER_ERROR));

        LocalDateTime checkinDeadline = today.atTime(settings.getDefaultCheckInTime())
                .plusMinutes(settings.getCheckInLateToleranceMinutes());

        CheckInStatus status = now.isAfter(checkinDeadline) ? CheckInStatus.TERLAMBAT : CheckInStatus.TEPAT_WAKTU;

        Attendance attendance = todayAttendanceOpt.orElseGet(() ->
                Attendance.builder().student(student).attendanceDate(today).build());

        attendance.setCheckInTime(now);
        attendance.setCheckInNotes(request.getNotesCheckin());
        attendance.setCheckInStatus(status);

        Attendance saved = attendanceRepository.save(attendance);
        sendCheckinNotification(saved);

        return mapToAttendanceResponseDto(saved);
    }

    @Transactional
    public AttendanceResponseDto performCheckOut(Authentication authentication, CheckoutRequestDto request) {
        Student student = findStudentByAuthentication(authentication);
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByStudentAndAttendanceDate(student, today)
                .filter(a -> a.getCheckInTime() != null)
                .orElseThrow(() -> new CustomErrorException("T-ERR-010", "You must check in before checking out.", HttpStatus.BAD_REQUEST));

        if (attendance.getCheckOutTime() != null) {
            throw new CustomErrorException("T-ERR-010", "You have already checked out today.", HttpStatus.BAD_REQUEST);
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        attendance.setCheckOutNotes(request.getNotesCheckout());

        Attendance saved = attendanceRepository.save(attendance);
        return mapToAttendanceResponseDto(saved);
    }

    // === PRIVATE METHODS ===

    private Student findStudentByAuthentication(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomErrorException("T-ERR-005", "User not found.", HttpStatus.NOT_FOUND));
        return studentRepository.findByUser(user)
                .orElseThrow(() -> new CustomErrorException("T-ERR-005", "Student profile not found.", HttpStatus.NOT_FOUND));
    }

    private void checkLastAttendanceNotCheckedOut(Student student, LocalDate today) {
        attendanceRepository.findFirstByStudentOrderByAttendanceDateDesc(student).ifPresent(last -> {
            if (!last.getAttendanceDate().isBefore(today) && last.getCheckOutTime() == null) {
                throw new CustomErrorException("T-ERR-010", "You must check out from the previous day before checking in.", HttpStatus.BAD_REQUEST);
            }
        });
    }

    private void validateCheckinNotExists(Optional<Attendance> todayAttendanceOpt) {
        if (todayAttendanceOpt.isPresent() && todayAttendanceOpt.get().getCheckInTime() != null) {
            throw new CustomErrorException("T-ERR-010", "You have already checked in today.", HttpStatus.BAD_REQUEST);
        }
    }

    private void sendCheckinNotification(Attendance attendance) {
        try {
            User user = attendance.getStudent().getUser();
            String message = user.getName() + " telah berhasil check-in.";
            NotificationDto notification = new NotificationDto(
                    user.getName(),
                    attendance.getStudent().getNim(),
                    attendance.getCheckInTime(),
                    message
            );
            webSocketHandler.broadcastNotification(notification);
        } catch (Exception e) {
            logger.error("Failed to send WebSocket notification.", e);
        }
    }

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

    private StudentAttendanceDto mapToStudentAttendanceDto(Attendance attendance) {
        Student student = attendance.getStudent();
        User user = student.getUser();

        AttendanceRecordDto record = AttendanceRecordDto.builder()
                .attendanceId(attendance.getId())
                .checkinTime(attendance.getCheckInTime())
                .checkoutTime(attendance.getCheckOutTime())
                .attendanceDate(attendance.getAttendanceDate())
                .late(attendance.getCheckInStatus() == CheckInStatus.TERLAMBAT)
                .notesCheckin(attendance.getCheckInNotes())
                .notesCheckout(attendance.getCheckOutNotes())
                .status(attendance.getCheckInStatus())
                .build();

        return StudentAttendanceDto.builder()
                .studentId(student.getId())
                .userId(user.getId())
                .studentName(user.getName())
                .nim(student.getNim())
                .email(user.getEmail())
                .attendanceData(List.of(record))
                .build();
    }
}
