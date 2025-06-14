package com.skpijtk.springboot_boilerplate.util;

public class ResponseMessage {

    public static final String T_SUCC_001 = "Signup successful";
    public static final String T_SUCC_002 = "Login successful";
    public static final String T_SUCC_004 = "Student data successfully found."; 
    public static final String T_SUCC_005 = "Data successfully displayed.";     
    public static final String T_SUCC_006 = "Student {student name} successfully deleted."; 
    public static final String T_SUCC_007 = "{Checkin / Checkout} successful";   
    public static final String T_SUCC_008 = "Data successfully saved";    
    public static final String T_SUCC_009_CHECKIN = "Check-in successful.";
    public static final String T_SUCC_010_CHECKOUT = "Check-out successful."; 

    public static final String T_ERR_001 = "Invalid username or password";
    public static final String T_ERR_002 = "Internal Server Error. Please try again";
    public static final String T_ERR_003 = "{column} must be at most {max_length} characters";
    public static final String T_ERR_004 = "{column} must be at least {min_length} characters";
    public static final String T_ERR_005 = "Student data not found.";           
    public static final String T_ERR_006 = "Data failed to display.";          
    public static final String T_ERR_007 = "Student {student name} deletion failed."; 
    public static final String T_ERR_008 = "Username or Email has been used";  
    public static final String T_ERR_009 = "{Checkin / Checkout} Failed because the Note is empty"; 
    public static final String T_ERR_010 = "Data failed to be saved.";       
    public static final String T_ERR_011 = "You have already checked in today.";
    public static final String T_ERR_012 = "You must check in before checking out.";
    public static final String T_ERR_013 = "You have already checked out today.";
    public static final String T_ERR_014 = "You must check out from the previous day before checking in.";
    
    public static final String T_WAR_001 = "Your {Checkin / Checkout} is Late"; 

}