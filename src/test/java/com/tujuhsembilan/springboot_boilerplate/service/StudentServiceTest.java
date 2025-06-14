package com.tujuhsembilan.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.dto.DeleteStudentResponseData;
import com.skpijtk.springboot_boilerplate.exception.CustomErrorException;
import com.skpijtk.springboot_boilerplate.model.Student;
import com.skpijtk.springboot_boilerplate.model.User;
import com.skpijtk.springboot_boilerplate.repository.StudentRepository;
import com.skpijtk.springboot_boilerplate.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// 2. TAMBAHKAN IMPORT UNTUK StudentService
import com.skpijtk.springboot_boilerplate.service.StudentService;


@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StudentService studentService; 

    // --- TEST CASE 1: Skenario Sukses ---
    @Test
    @DisplayName("Harus mengembalikan DTO dan menghapus student jika student ada dan bisa dihapus")
    void testDeleteStudent_whenStudentExists_shouldDeleteSuccessfully() {
        // Given (Arrange): Siapkan kondisi dan data palsu
        long studentId = 1L;
        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setName("Budi Santoso");

        Student mockStudent = new Student();
        mockStudent.setId(studentId);
        mockStudent.setNim("123456789");
        mockStudent.setUser(mockUser);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(mockStudent));
        doNothing().when(studentRepository).delete(any(Student.class));
        doNothing().when(userRepository).delete(any(User.class));

        // When (Act): Panggil method yang akan diuji
        DeleteStudentResponseData response = studentService.deleteStudent(studentId);

        // Then (Assert): Verifikasi hasilnya
        assertNotNull(response);
        assertEquals(studentId, response.getStudentId());
        assertEquals("Budi Santoso", response.getStudentName());
        assertEquals("123456789", response.getNim());

        verify(studentRepository, times(1)).delete(mockStudent);
        verify(userRepository, times(1)).delete(mockUser);
    }

    // --- TEST CASE 2: Skenario Gagal (Student Tidak Ditemukan) ---
    @Test
    @DisplayName("Harus melempar CustomErrorException (T-ERR-005) jika student tidak ditemukan")
    void testDeleteStudent_whenStudentNotFound_shouldThrowException() {
        // Given (Arrange): Siapkan kondisi
        long nonExistentStudentId = 99L;

        when(studentRepository.findById(nonExistentStudentId)).thenReturn(Optional.empty());

        // When & Then (Act & Assert): Panggil method dan pastikan exception yang benar dilempar
        CustomErrorException exception = assertThrows(CustomErrorException.class, () -> {
            studentService.deleteStudent(nonExistentStudentId);
        });
        
        // Verifikasi detail dari exception
        assertEquals("T-ERR-005", exception.getMessageCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

        // Verifikasi bahwa method delete TIDAK PERNAH dipanggil
        // PERBAIKI DI SINI: ganti any() dengan any(ClassName.class)
        verify(userRepository, never()).delete(any(User.class));
        verify(studentRepository, never()).delete(any(Student.class)); // <-- PERBAIKAN
    }

    // --- TEST CASE 3: Skenario Gagal (Constraint Database) ---
    @Test
    @DisplayName("Harus melempar CustomErrorException (T-ERR-007) jika terjadi DataIntegrityViolationException")
    void testDeleteStudent_whenIntegrityViolation_shouldThrowException() {
        // Given (Arrange): Siapkan kondisi
        long studentId = 2L;
        User mockUser = new User();
        mockUser.setId(11L);
        mockUser.setName("Dewi Lestari");
        
        Student mockStudent = new Student();
        mockStudent.setId(studentId);
        mockStudent.setNim("987654321");
        mockStudent.setUser(mockUser);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(mockStudent));
        
        // Atur perilaku mock: jika studentRepository.delete() dipanggil, lemparkan error constraint
        doThrow(new DataIntegrityViolationException("Constraint violation")).when(studentRepository).delete(any(Student.class));

        // When & Then (Act & Assert): Panggil method dan pastikan exception yang benar dilempar
        CustomErrorException exception = assertThrows(CustomErrorException.class, () -> {
            studentService.deleteStudent(studentId);
        });

        // Verifikasi detail dari exception
        assertEquals("T-ERR-007", exception.getMessageCode());
        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
    }
}