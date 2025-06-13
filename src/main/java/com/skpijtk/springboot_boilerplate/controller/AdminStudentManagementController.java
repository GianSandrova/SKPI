package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.CreateStudentRequest;
import com.skpijtk.springboot_boilerplate.dto.DeleteStudentResponseData;
import com.skpijtk.springboot_boilerplate.dto.EditStudentRequest;
import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.dto.PagedStudentAttendanceResponseData;
import com.skpijtk.springboot_boilerplate.dto.StudentAttendanceDto;
import com.skpijtk.springboot_boilerplate.service.StudentService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; 
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import com.skpijtk.springboot_boilerplate.dto.EditStudentRequest; // IMPORT BARU
import jakarta.validation.Valid; // IMPORT BARU
import org.springframework.web.bind.annotation.PutMapping; // IMPORT BARU


import java.time.LocalDate;

@RestController
@RequestMapping("/admin")
// @PreAuthorize("hasRole('ADMIN')") // Anda bisa menambahkan ini jika semua endpoint di sini khusus admin
public class AdminStudentManagementController {

    private static final Logger logger = LoggerFactory.getLogger(AdminStudentManagementController.class);

    @Autowired
    private StudentService studentService;

    @GetMapping("/list_all_mahasiswa")
    public ResponseEntity<GlobalResponse<PagedStudentAttendanceResponseData>> getListAllMahasiswa(
            @RequestParam(name = "student_name", required = false) String studentName,
            @RequestParam(name = "startdate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "enddate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "nim_asc") String sortBy
    ) {
        logger.info("Request received for /list_all_mahasiswa: studentName={}, startDate={}, endDate={}, page={}, size={}, sortBy={}",
                studentName, startDate, endDate, page, size, sortBy);

        Sort sort;
        if ("nim_desc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by("nim").descending(); // Sorting by student's NIM
        } else { // Default atau "nim_asc" atau sortBy lain yang tidak dikenal
            sort = Sort.by("nim").ascending();  // Sorting by student's NIM
        }
        // Catatan: Sort.by("nim") akan bekerja jika StudentRepository (yang mengembalikan Page<Student>) bisa mengurutkan berdasarkan field "nim" di Student.

        Pageable pageable = PageRequest.of(page, size, sort);

        PagedStudentAttendanceResponseData data = studentService.getAllStudentsWithAttendance(
                studentName, startDate, endDate, pageable);

        String successMessageCode = ResponseMessage.T_SUCC_005; // Default "Data successfully displayed."
        if (data.getData() != null && !data.getData().isEmpty()) {
            successMessageCode = ResponseMessage.T_SUCC_004; // "Student data successfully found."
        }
        logger.info("Successfully retrieved list of all mahasiswa. Code: {}", successMessageCode);

        GlobalResponse<PagedStudentAttendanceResponseData> response = GlobalResponse.success(
                data,
                successMessageCode,
                HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/mahasiswa/{id_student}")
    // @PreAuthorize("hasRole('ADMIN')") // Bisa juga ditaruh di level method
    public ResponseEntity<GlobalResponse<DeleteStudentResponseData>> deleteMahasiswa(
            @PathVariable("id_student") Long studentId) {
        logger.info("Request received to delete student with id: {}", studentId);
        
        DeleteStudentResponseData deletedStudentData = studentService.deleteStudent(studentId);
        
        // Format pesan display untuk logging (tidak dikirim ke client di field message)
        String formattedDisplayMessage = ResponseMessage.T_SUCC_006
                                    .replace("{student name}", deletedStudentData.getStudentName());
        
        logger.info("Delete Mahasiswa Success: {}", formattedDisplayMessage);
        
        // Sesuai kontrak, field 'message' di respons berisi KODE PESAN
        GlobalResponse<DeleteStudentResponseData> response = GlobalResponse.success(
                deletedStudentData,
                ResponseMessage.T_SUCC_006, // Kode pesan sukses delete
                HttpStatus.OK
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mahasiswa/{id_student}") // PERUBAHAN DI SINI
    public ResponseEntity<GlobalResponse<StudentAttendanceDto>> getDetailMahasiswa(
            @PathVariable("id_student") Long studentId) { // PERUBAHAN DI SINI

        logger.info("Request received to get details for student id: {}", studentId);
        StudentAttendanceDto studentData = studentService.getStudentDetailsWithAllAttendance(studentId);
        
        GlobalResponse<StudentAttendanceDto> response = GlobalResponse.success(
                studentData,
                ResponseMessage.T_SUCC_004, // "Student data successfully found."
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }

        @PostMapping("/add-mahasiswa")
    public ResponseEntity<GlobalResponse<StudentAttendanceDto>> createMahasiswa(
            @Valid @RequestBody CreateStudentRequest request) {
        
        logger.info("Request received to create a new student with email: {}", request.getEmail());
        
        StudentAttendanceDto newStudentData = studentService.createStudent(request);
        
        GlobalResponse<StudentAttendanceDto> response = GlobalResponse.success(
                newStudentData,
                ResponseMessage.T_SUCC_008, // "Data successfully saved"
                HttpStatus.CREATED // Status 201 Created
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

        @PutMapping("/edit-mahasiswa/{id_student}")
    public ResponseEntity<GlobalResponse<StudentAttendanceDto>> editMahasiswa(
            @PathVariable("id_student") Long studentId,
            @Valid @RequestBody EditStudentRequest request) {

        logger.info("Request received to edit student with id: {}", studentId);
        
        StudentAttendanceDto updatedStudentData = studentService.editStudent(studentId, request);
        
        GlobalResponse<StudentAttendanceDto> response = GlobalResponse.success(
                updatedStudentData,
                ResponseMessage.T_SUCC_008,  // "Data successfully saved"
                HttpStatus.OK // Menggunakan 200 OK untuk update
        );
        return ResponseEntity.ok(response);
    }

    // Endpoint CRUD mahasiswa lainnya (POST /add-mahasiswa, PUT /edit-mahasiswa/{id_student}, GET /mahasiswa/{id_student})
    // dan GET /list_attendance_mahasiswa (riwayat satu mahasiswa) akan ditambahkan di sini.
}