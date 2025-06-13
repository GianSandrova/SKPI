package com.skpijtk.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.dto.AttendanceRecordDto;
import com.skpijtk.springboot_boilerplate.dto.DeleteStudentResponseData;
import com.skpijtk.springboot_boilerplate.dto.PagedStudentAttendanceResponseData;
import com.skpijtk.springboot_boilerplate.dto.StudentAttendanceDto;
import com.skpijtk.springboot_boilerplate.exception.CustomErrorException;
import com.skpijtk.springboot_boilerplate.model.Attendance;
import com.skpijtk.springboot_boilerplate.model.CheckInStatus;
import com.skpijtk.springboot_boilerplate.model.Student;
import com.skpijtk.springboot_boilerplate.model.User;
import com.skpijtk.springboot_boilerplate.repository.AttendanceRepository;
import com.skpijtk.springboot_boilerplate.repository.StudentRepository;
import com.skpijtk.springboot_boilerplate.repository.UserRepository;
import com.skpijtk.springboot_boilerplate.dto.EditStudentRequest;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import java.util.Collections;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.skpijtk.springboot_boilerplate.dto.CreateStudentRequest; 
import com.skpijtk.springboot_boilerplate.model.UserRole;        
import org.springframework.security.crypto.password.PasswordEncoder; 
import com.skpijtk.springboot_boilerplate.dto.MahasiswaProfileResponseDto; 
import org.springframework.security.core.Authentication; 

import java.time.LocalDate;
import java.util.ArrayList;
// import java.util.Collections; // Tidak terpakai di versi ini
import java.util.List;
// import java.util.Optional; // Tidak terpakai secara eksplisit di versi ini
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PagedStudentAttendanceResponseData getAllStudentsWithAttendance(
            String studentName, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        try {
            Specification<Student> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (studentName != null && !studentName.isEmpty()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("name")), "%" + studentName.toLowerCase() + "%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            Page<Student> studentPage = studentRepository.findAll(spec, pageable);
            List<StudentAttendanceDto> dtoList = studentPage.getContent().stream().map(student -> {
                User user = student.getUser(); 
                List<AttendanceRecordDto> attendanceRecordsList = new ArrayList<>();
                if (startDate != null && endDate != null) {
                    List<Attendance> attendances = attendanceRepository.findByStudentAndAttendanceDateBetweenOrderByAttendanceDateAsc(student, startDate, endDate);
                    attendanceRecordsList = attendances.stream().map(this::mapAttendanceToRecordDto).collect(Collectors.toList());
                } else if (startDate != null) {
                    List<Attendance> attendances = attendanceRepository.findByStudentAndAttendanceDateOrderByCreatedAtAsc(student, startDate); 
                    attendanceRecordsList = attendances.stream().map(this::mapAttendanceToRecordDto).collect(Collectors.toList());
                } else {
                    List<Attendance> attendances = attendanceRepository.findByStudentAndAttendanceDateOrderByCreatedAtAsc(student, LocalDate.now());
                    attendanceRecordsList = attendances.stream().map(this::mapAttendanceToRecordDto).collect(Collectors.toList());
                }
                return StudentAttendanceDto.builder()
                        .studentId(student.getId())
                        .userId(user.getId())
                        .studentName(user.getName())
                        .nim(student.getNim())
                        .email(user.getEmail())
                        .attendanceData(attendanceRecordsList) // Menggunakan nama field attendanceData
                        .build();
            }).collect(Collectors.toList());
            return PagedStudentAttendanceResponseData.builder()
                    .data(dtoList)
                    .totalData(studentPage.getTotalElements())
                    .totalPage(studentPage.getTotalPages())
                    .currentPage(studentPage.getNumber())
                    .pageSize(studentPage.getSize())
                    .build();
        } catch (Exception e) {
            logger.error("Failed to get list of all mahasiswa: {}", e.getMessage(), e);
            throw new CustomErrorException("T-ERR-006", ResponseMessage.T_ERR_006, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private AttendanceRecordDto mapAttendanceToRecordDto(Attendance attendance) {
        if (attendance == null) return null;
        return AttendanceRecordDto.builder()
                .attendanceId(attendance.getId())
                .checkinTime(attendance.getCheckInTime())
                .checkoutTime(attendance.getCheckOutTime())
                .attendanceDate(attendance.getAttendanceDate())
                .late(attendance.getCheckInStatus() == CheckInStatus.TERLAMBAT)
                .notesCheckin(attendance.getCheckInNotes())
                .notesCheckout(attendance.getCheckOutNotes())
                .status(attendance.getCheckInStatus())
                .build();
    }

    @Transactional
    public DeleteStudentResponseData deleteStudent(Long studentId) {
        logger.info("Attempting to delete student with id: {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.warn("Student not found with id: {}. Cannot delete.", studentId);
                    throw new CustomErrorException("T-ERR-005", ResponseMessage.T_ERR_005, HttpStatus.NOT_FOUND);
                });
        User user = student.getUser();
        String studentName = "Unknown"; 
        if (user != null) {
            studentName = user.getName();
        } else {
             logger.warn("User associated with student id: {} is null. Student name will be 'Unknown'.", studentId);
        }
        try {
            studentRepository.delete(student);
            logger.info("Student record with id: {} deleted.", studentId);
            if (user != null) {
                userRepository.delete(user); 
                logger.info("Associated user record with id: {} for student name: {} deleted.", user.getId(), studentName);
            }
            logger.info("Successfully deleted student data for id: {} and name: {}", studentId, studentName);
            return DeleteStudentResponseData.builder()
                    .studentId(student.getId())
                    .studentName(studentName)
                    .nim(student.getNim())
                    .build();
        } catch (DataIntegrityViolationException e) {
            logger.error("Failed to delete student with id: {} and name: {} due to data integrity violation (e.g., existing attendance records). Error: {}", studentId, studentName, e.getMessage());
            String displayMessage = ResponseMessage.T_ERR_007.replace("{student name}", studentName) + ". Reason: Student has related records (e.g., attendance) that prevent deletion.";
            throw new CustomErrorException("T-ERR-007", displayMessage, HttpStatus.CONFLICT); 
        } catch (Exception e) {
            logger.error("An unexpected error occurred while deleting student with id: {} and name: {}. Error: {}", studentId, studentName, e.getMessage(), e);
            // Menggunakan T-ERR-002 untuk error server yang tidak terduga
            String displayMessage = ResponseMessage.T_ERR_002; 
            throw new CustomErrorException("T-ERR-002", displayMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public StudentAttendanceDto getStudentDetailsWithAllAttendance(Long studentId) {
        logger.info("Attempting to get details for student id: {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.warn("Student not found with id: {}", studentId);
                    throw new CustomErrorException("T-ERR-005", ResponseMessage.T_ERR_005, HttpStatus.NOT_FOUND);
                });

        User user = student.getUser(); // Ini akan memicu pengambilan data User jika LAZY
        if (user == null) { // Pengecekan tambahan jika relasi user bisa null (meskipun di DDL not null)
            logger.error("User data is missing for student id: {}", studentId);
            throw new CustomErrorException("T-ERR-005", "Associated user data not found for student.", HttpStatus.NOT_FOUND);
        }

        List<Attendance> attendances = attendanceRepository.findByStudentOrderByAttendanceDateAsc(student);
        List<AttendanceRecordDto> attendanceRecordDtos = attendances.stream()
                .map(this::mapAttendanceToRecordDto)
                .collect(Collectors.toList());

        return StudentAttendanceDto.builder()
                .studentId(student.getId())
                .userId(user.getId())
                .studentName(user.getName())
                .nim(student.getNim())
                .email(user.getEmail())
                .attendanceData(attendanceRecordDtos) // Menggunakan field attendanceData (list)
                .build();
    }

        @Transactional
    public StudentAttendanceDto createStudent(CreateStudentRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomErrorException("T-ERR-008", ResponseMessage.T_ERR_008, HttpStatus.BAD_REQUEST);
        }

        if (studentRepository.existsByNim(request.getNim())) {
            throw new CustomErrorException("T-ERR-010", "Data Failed to be saved. NIM " + request.getNim() + " already exists.", HttpStatus.BAD_REQUEST);
        }
        
        // Rule #2: Validasi 2 angka pertama NIM sesuai tahun berjalan
        String currentYearPrefix = String.valueOf(LocalDate.now().getYear()).substring(2);
        if (!request.getNim().startsWith(currentYearPrefix)) {
            throw new CustomErrorException("T-ERR-010", "Data Failed to be saved. First 2 digits of NIM must be '" + currentYearPrefix + "' for the current year.", HttpStatus.BAD_REQUEST);
        }

        // 2. Buat dan simpan entitas User
        User newUser = User.builder()
                .name(request.getStudentName().trim()) // trim() untuk memastikan tidak ada spasi di awal/akhir
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.MAHASISWA) // Role untuk mahasiswa
                .build();
        User savedUser = userRepository.save(newUser);

        // 3. Buat dan simpan entitas Student
        Student newStudent = Student.builder()
                .nim(request.getNim())
                .user(savedUser) // Hubungkan dengan User yang baru dibuat
                .build();
        Student savedStudent = studentRepository.save(newStudent);

        // 4. Buat DTO Respons
        return StudentAttendanceDto.builder()
                .studentId(savedStudent.getId())
                .userId(savedUser.getId())
                .studentName(savedUser.getName())
                .nim(savedStudent.getNim())
                .email(savedUser.getEmail())
                .attendanceData(Collections.emptyList())
                .build();
    }

        @Transactional
    public StudentAttendanceDto editStudent(Long studentId, EditStudentRequest request) {
        logger.info("Attempting to edit student with id: {}", studentId);

        // 1. Cari mahasiswa yang akan di-edit
        Student studentToUpdate = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomErrorException("T-ERR-005", ResponseMessage.T_ERR_005, HttpStatus.NOT_FOUND));

        User userToUpdate = studentToUpdate.getUser();
        if (userToUpdate == null) {
            throw new CustomErrorException("T-ERR-010", "Data failed to be saved. Associated user not found.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 2. Validasi Keunikan Data (jika ada perubahan)
        // Cek email, jika berubah dan sudah dipakai user lain
        if (!request.getEmail().equalsIgnoreCase(userToUpdate.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(userToUpdate.getId())) {
                    throw new CustomErrorException("T-ERR-008", ResponseMessage.T_ERR_008, HttpStatus.BAD_REQUEST);
                }
            });
        }

        // Cek NIM, jika berubah dan sudah dipakai student lain
        if (!request.getNim().equals(studentToUpdate.getNim())) {
            studentRepository.findByNim(request.getNim()).ifPresent(existingStudent -> {
                if (!existingStudent.getId().equals(studentId)) {
                    throw new CustomErrorException("T-ERR-010", "Data Failed to be saved. NIM already exists.", HttpStatus.BAD_REQUEST);
                }
            });
        }
        
        // Cek validasi prefix NIM sesuai tahun join mahasiswa
        LocalDate joinDate = studentToUpdate.getCreatedAt().toLocalDate();
        String yearPrefix = String.valueOf(joinDate.getYear()).substring(2);
        if (!request.getNim().startsWith(yearPrefix)) {
             throw new CustomErrorException("T-ERR-010", "Data Failed to be saved. First 2 digits of NIM must be '" + yearPrefix + "' according to join date.", HttpStatus.BAD_REQUEST);
        }

        // 3. Update data di entitas
        userToUpdate.setName(request.getStudentName().trim());
        userToUpdate.setEmail(request.getEmail());
        studentToUpdate.setNim(request.getNim());

        // 4. Simpan perubahan. Karena ada @Transactional, Spring Data JPA akan otomatis
        // menyimpan perubahan pada entitas yang di-manage (studentToUpdate dan userToUpdate).
        // studentRepository.save(studentToUpdate); // Pemanggilan save() eksplisit juga tidak masalah.

        logger.info("Successfully updated data for student with id: {}", studentId);

        // 5. Kembalikan data lengkap sesuai kontrak API
        return getStudentDetailsWithAllAttendance(studentId);
    }

        @Transactional(readOnly = true)
    public MahasiswaProfileResponseDto getMahasiswaProfile(Authentication authentication, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        
        String email = authentication.getName();
        logger.info("Fetching profile and attendance history for mahasiswa with email: {}", email);

        // 1. Ambil User dari email yang terautentikasi
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomErrorException("T-ERR-005", "User data not found for authenticated user.", HttpStatus.NOT_FOUND));

        // 2. Ambil Student dari User
        Student student = studentRepository.findByUser(user)
            .orElseThrow(() -> new CustomErrorException("T-ERR-005", "Student data not found for authenticated user.", HttpStatus.NOT_FOUND));

        // 3. Ambil riwayat attendance yang terpaginasi (logika sama seperti API No. 10)
        Specification<Attendance> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("student"), student));
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("attendanceDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("attendanceDate"), endDate));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Attendance> attendancePage = attendanceRepository.findAll(spec, pageable);
        List<AttendanceRecordDto> attendanceRecords = attendancePage.getContent().stream()
                .map(this::mapAttendanceToRecordDto)
                .collect(Collectors.toList());

        // 4. Bangun DTO respons
        return MahasiswaProfileResponseDto.builder()
                .studentId(student.getId())
                .studentName(user.getName())
                .nim(student.getNim())
                .attendanceRecords(attendanceRecords)
                .totalData(attendancePage.getTotalElements())
                .totalPage(attendancePage.getTotalPages())
                .currentPage(attendancePage.getNumber())
                .pageSize(attendancePage.getSize())
                .build();
    }
}