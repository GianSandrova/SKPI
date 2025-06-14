package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.*;
import com.skpijtk.springboot_boilerplate.service.StudentService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller khusus admin untuk mengelola data mahasiswa,
 * termasuk CRUD data dan pemantauan kehadiran.
 */
@RestController
@RequestMapping("/admin")
public class AdminStudentManagementController {

    private static final Logger logger = LoggerFactory.getLogger(AdminStudentManagementController.class);

    @Autowired
    private StudentService studentService;

    /**
     * Menampilkan daftar mahasiswa lengkap dengan data kehadiran,
     * mendukung filter berdasarkan nama dan rentang tanggal.
     */
    @GetMapping("/list_all_mahasiswa")
    public ResponseEntity<GlobalResponse<PagedStudentAttendanceResponseData>> getListAllMahasiswa(
            @RequestParam(name = "student_name", required = false) String studentName,
            @RequestParam(name = "startdate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "enddate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "nim_asc") String sortBy
    ) {
        logger.info("GET /list_all_mahasiswa with filters: name={}, start={}, end={}, page={}, size={}, sort={}",
                studentName, startDate, endDate, page, size, sortBy);

        Sort sort = "nim_desc".equalsIgnoreCase(sortBy) ?
                Sort.by("nim").descending() :
                Sort.by("nim").ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        PagedStudentAttendanceResponseData data = studentService.getAllStudentsWithAttendance(studentName, startDate, endDate, pageable);

        String responseCode = (data.getData() != null && !data.getData().isEmpty()) ?
                ResponseMessage.T_SUCC_004 : ResponseMessage.T_SUCC_005;

        GlobalResponse<PagedStudentAttendanceResponseData> response = GlobalResponse.success(
                data, responseCode, HttpStatus.OK
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Menghapus data mahasiswa berdasarkan ID.
     */
    @DeleteMapping("/mahasiswa/{id_student}")
    public ResponseEntity<GlobalResponse<DeleteStudentResponseData>> deleteMahasiswa(
            @PathVariable("id_student") Long studentId) {
        logger.info("DELETE /mahasiswa/{}", studentId);

        DeleteStudentResponseData result = studentService.deleteStudent(studentId);
        logger.info("Student deleted: {}", result.getStudentName());

        GlobalResponse<DeleteStudentResponseData> response = GlobalResponse.success(
                result,
                ResponseMessage.T_SUCC_006,
                HttpStatus.OK
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Mengambil detail lengkap mahasiswa beserta histori kehadiran.
     */
    @GetMapping("/mahasiswa/{id_student}")
    public ResponseEntity<GlobalResponse<StudentAttendanceDto>> getDetailMahasiswa(
            @PathVariable("id_student") Long studentId) {

        logger.info("GET /mahasiswa/{}", studentId);

        StudentAttendanceDto data = studentService.getStudentDetailsWithAllAttendance(studentId);

        GlobalResponse<StudentAttendanceDto> response = GlobalResponse.success(
                data,
                ResponseMessage.T_SUCC_004,
                HttpStatus.OK
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Menambahkan data mahasiswa baru.
     */
    @PostMapping("/add-mahasiswa")
    public ResponseEntity<GlobalResponse<StudentAttendanceDto>> createMahasiswa(
            @Valid @RequestBody CreateStudentRequest request) {

        logger.info("POST /add-mahasiswa - email: {}", request.getEmail());

        StudentAttendanceDto data = studentService.createStudent(request);

        GlobalResponse<StudentAttendanceDto> response = GlobalResponse.success(
                data,
                ResponseMessage.T_SUCC_008,
                HttpStatus.CREATED
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Memperbarui data mahasiswa berdasarkan ID.
     */
    @PutMapping("/edit-mahasiswa/{id_student}")
    public ResponseEntity<GlobalResponse<StudentAttendanceDto>> editMahasiswa(
            @PathVariable("id_student") Long studentId,
            @Valid @RequestBody EditStudentRequest request) {

        logger.info("PUT /edit-mahasiswa/{}", studentId);

        StudentAttendanceDto data = studentService.editStudent(studentId, request);

        GlobalResponse<StudentAttendanceDto> response = GlobalResponse.success(
                data,
                ResponseMessage.T_SUCC_008,
                HttpStatus.OK
        );

        return ResponseEntity.ok(response);
    }
}
