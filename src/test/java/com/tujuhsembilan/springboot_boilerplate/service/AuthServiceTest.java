package com.tujuhsembilan.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.dto.AdminSignUpRequest;
import com.skpijtk.springboot_boilerplate.dto.SignUpResponseData;
import com.skpijtk.springboot_boilerplate.exception.CustomErrorException;
import com.skpijtk.springboot_boilerplate.model.User;
import com.skpijtk.springboot_boilerplate.model.UserRole;
import com.skpijtk.springboot_boilerplate.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.skpijtk.springboot_boilerplate.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test untuk kelas AuthService.
 * Test ini berfokus pada logika bisnis di dalam service,
 * dengan mengisolasi dependensi eksternal seperti repository dan encoder.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // Mock untuk dependensi, memungkinkan kita mengontrol perilakunya dalam test.
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Instance dari kelas yang akan kita uji. Mockito akan meng-inject mock di atas ke sini.
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Skenario Sukses: Registrasi admin berhasil jika email unik.")
    void testRegisterAdmin_whenEmailIsNew_shouldCreateAdminSuccessfully() {
        // Arrange: Menyiapkan data input dan mendefinisikan perilaku mock.
        AdminSignUpRequest request = new AdminSignUpRequest();
        request.setName("Admin Baru");
        request.setEmail("admin.baru@example.com");
        request.setPassword("passwordValid1");

        // Definisikan perilaku mock #1: Simulasikan email belum ada.
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        // Definisikan perilaku mock #2: Simulasikan proses enkripsi password.
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encryptedPassword");
        
        // Definisikan perilaku mock #3: Method save akan mengembalikan objek yang sama.
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Panggil method yang sedang diuji.
        SignUpResponseData response = authService.registerAdmin(request);

        // Assert: Verifikasi bahwa hasil yang didapat sesuai harapan.
        assertNotNull(response);
        assertEquals(request.getEmail(), response.getEmail());

        // Assert (lanjutan): Tangkap argumen yang dikirim ke userRepository.save() untuk inspeksi lebih dalam.
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        // Verifikasi bahwa user yang disimpan memiliki role dan password yang benar.
        assertEquals(UserRole.ADMIN, savedUser.getRole());
        assertEquals("encryptedPassword", savedUser.getPassword());
        assertEquals("Admin Baru", savedUser.getName());
    }

    @Test
    @DisplayName("Skenario Gagal: Registrasi gagal jika email sudah terdaftar.")
    void testRegisterAdmin_whenEmailExists_shouldThrowException() {
        // Arrange: Menyiapkan data input dan simulasi kondisi error.
        AdminSignUpRequest request = new AdminSignUpRequest();
        request.setEmail("sudah.terdaftar@example.com");
        request.setName("Admin Gagal");
        request.setPassword("passwordGagal1");

        // Definisikan perilaku mock: Simulasikan email sudah ada di database.
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert: Eksekusi method dan pastikan CustomErrorException dilempar.
        CustomErrorException exception = assertThrows(CustomErrorException.class, () -> {
            authService.registerAdmin(request);
        });

        // Verifikasi detail dari exception yang ditangkap.
        assertEquals("T-ERR-008", exception.getMessageCode());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Assert (lanjutan): Pastikan method save tidak pernah dipanggil sama sekali.
        verify(userRepository, never()).save(any());
    }
}