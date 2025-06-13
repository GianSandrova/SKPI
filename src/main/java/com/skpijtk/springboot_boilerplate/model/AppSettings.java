package com.skpijtk.springboot_boilerplate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appsettings") // Sesuai dengan nama tabel di DDL Anda
public class AppSettings {

    @Id
    // Kita tidak menggunakan @GeneratedValue karena ID-nya tetap '1'
    @Column(name = "id")
    private Integer id;

    @Column(name = "default_check_in_time", nullable = false)
    private LocalTime defaultCheckInTime; // Tipe data TIME di SQL dipetakan ke LocalTime di Java

    @Column(name = "default_check_out_time", nullable = false)
    private LocalTime defaultCheckOutTime; // Tipe data TIME di SQL dipetakan ke LocalTime di Java

    @Column(name = "check_in_late_tolerance_minutes", nullable = false)
    private Integer checkInLateToleranceMinutes;

    @Column(name = "check_out_late_tolerance_minutes", nullable = false)
    private Integer checkOutLateToleranceMinutes;

    @UpdateTimestamp // Otomatis mengisi waktu saat baris di-update
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // Tipe data TIMESTAMP dipetakan ke LocalDateTime
}