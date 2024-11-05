package app.foodpt.exe201.helpers;

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
    public static boolean regexPhoneValidationPattern(String phone) {
        // Biểu thức chính quy để kiểm tra số điện thoại (chỉ chứa số, từ 10-15 chữ số)
        String regex = "^\\d{10,15}$"; // Số điện thoại phải có từ 10-15 chữ số.

        return phone.matches(regex); // Trả về true nếu khớp với biểu thức chính quy, ngược lại là false
    }
    // End Of Set Regular Expression Pattern for Email.
}
