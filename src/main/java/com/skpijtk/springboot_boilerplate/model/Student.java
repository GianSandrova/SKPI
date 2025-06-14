package com.skpijtk.springboot_boilerplate.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity yang merepresentasikan mahasiswa.
 * Setiap mahasiswa terhubung ke satu user akun melalui relasi One-to-One.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student {

    /**
     * Primary key otomatis.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relasi ke entitas User.
     * Setiap mahasiswa memiliki satu akun user.
     * FetchType.LAZY untuk menghindari auto-fetch jika tidak diperlukan.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Nomor Induk Mahasiswa (NIM).
     * Harus unik dan tidak null, maksimal 50 karakter.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String nim;

    /**
     * Waktu saat entitas dibuat (otomatis oleh Hibernate).
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Waktu saat entitas terakhir diperbarui (otomatis oleh Hibernate).
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
