package app.foodpt.exe201.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import app.foodpt.exe201.DTO.SupplierInfo;
import com.google.gson.Gson;

public class Utils {
    private static final String SHARED_PREFS = "MyAppPrefs";
    private static final String SUPPLIER_INFO_KEY = "supplier_info";

    // Lưu SupplierInfo vào SharedPreferences
    public static void saveSupplierInfo(Context context, SupplierInfo supplierInfo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Chuyển đổi SupplierInfo thành JSON
        Gson gson = new Gson();
        String supplierInfoJson = gson.toJson(supplierInfo);

        // Lưu JSON vào SharedPreferences
        editor.putString(SUPPLIER_INFO_KEY, supplierInfoJson);
        editor.apply();
    }

    // Lấy SupplierInfo từ SharedPreferences
    public static SupplierInfo getSupplierInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // Lấy chuỗi JSON từ SharedPreferences
        String supplierInfoJson = sharedPreferences.getString(SUPPLIER_INFO_KEY, null);

        if (supplierInfoJson != null) {
            Gson gson = new Gson();
            // Chuyển đổi JSON trở lại thành SupplierInfo
            return gson.fromJson(supplierInfoJson, SupplierInfo.class);
        }
        return null;  // Trả về null nếu không có dữ liệu
    }
}
