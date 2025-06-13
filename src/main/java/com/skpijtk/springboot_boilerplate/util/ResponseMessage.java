package com.skpijtk.springboot_boilerplate.util;

public class ResponseMessage {

    // Success Messages
    public static final String T_SUCC_001 = "Signup successful";
    public static final String T_SUCC_002 = "Login successful";
    public static final String T_SUCC_004 = "Student data successfully found."; // Sesuai tabel No. 7
    public static final String T_SUCC_005 = "Data successfully displayed.";     // Sesuai tabel No. 9
    public static final String T_SUCC_006 = "Student {student name} successfully deleted."; // Sesuai tabel No. 11
    public static final String T_SUCC_007 = "{Checkin / Checkout} successful";   // Sesuai tabel No. 14
    public static final String T_SUCC_008 = "Data successfully saved";          // Sesuai tabel No. 17

    // Error Messages
    public static final String T_ERR_001 = "Invalid username or password";
    public static final String T_ERR_002 = "Internal Server Error. Please try again";
    public static final String T_ERR_003 = "{column} must be at most {max_length} characters";
    public static final String T_ERR_004 = "{column} must be at least {min_length} characters";
    public static final String T_ERR_005 = "Student data not found.";           // Sesuai tabel No. 8
    public static final String T_ERR_006 = "Data failed to display.";           // Sesuai tabel No. 10
    public static final String T_ERR_007 = "Student {student name} deletion failed."; // Sesuai tabel No. 12
    public static final String T_ERR_008 = "Username or Email has been used";   // Sesuai tabel No. 13
    public static final String T_ERR_009 = "{Checkin / Checkout} Failed because the Note is empty"; // Sesuai tabel No. 15
    public static final String T_ERR_010 = "Data failed to be saved.";        // Sesuai tabel No. 18 (dengan perbaikan tata bahasa)

    // Warning Messages
    public static final String T_WAR_001 = "Your {Checkin / Checkout} is Late"; 
    // HAPUS BARIS-BARIS INI:
    public static final String T_ERR_011 = "You have already checked in today.";
    public static final String T_ERR_012 = "You must check in before checking out.";
    public static final String T_ERR_013 = "You have already checked out today.";
    public static final String T_ERR_014 = "You must check out from the previous day before checking in.";
    public static final String T_SUCC_009_CHECKIN = "Check-in successful.";
    public static final String T_SUCC_010_CHECKOUT = "Check-out successful.";

}