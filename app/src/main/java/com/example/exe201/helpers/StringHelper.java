package com.example.exe201.helpers;

public class StringHelper {

    // Set Regular Expression Pattern for Email:
    public static boolean regexEmailValidationPattern(String email) {
        // Set Pattern:
        String regex = "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})";

        if(email.matches(regex)) {
            return true;
        }
        return false;
    }
//    public static boolean regexPhoneValidationPattern(String phone) {
//        // Set Pattern:
//        String regex = "([0-9])";
//
//        if(phone.matches(regex)) {
//            return true;
//        }
//        return false;
//    }
    // End Of Set Regular Expression Pattern for Email.
}
