package com.example.exe201.API;

public class ApiEndpoints {
    // Địa chỉ cơ sở (base URL) của API

//    private static final String BASE_URL = "http://14.225.206.203:8080/api/v1/";
private static final String BASE_URL = "http://192.168.1.3:8080/api/v1/";

    // Các endpoint API
    public static final String CREATE_SUPPLIER_INFO = BASE_URL + "supplier_info/create";
    public static final String UPDATE_SUPPLIER_INFO = BASE_URL + "supplier_info/update";
    public static final String GET_SUPPLIER_INFO = BASE_URL + "supplier_info/get";
    public static final String DELETE_SUPPLIER_INFO = BASE_URL + "supplier_info/delete";
    public static final String UPDATE_USER_INFO = BASE_URL + "users/update/";


    //User
    public static final String CREATE_USER = BASE_URL + "users/register";
    public static final String LOGIN_USER = BASE_URL + "users/login";

    //Food Item
    public static final String GET_ALL_FOOD_ITEM = BASE_URL + "food_item/get_all_food_items";
    public static final String GET_ALL_FOOD_ITEMS_BY_SUPPLIER = BASE_URL + "food_item/get_food_item_by_supplier";
    public static final String GET_FOOD_ITEM_BY_ID = BASE_URL + "food_item/get_food_item_by_id";
    public static final String UPDATE_FOOD_ITEM = BASE_URL + "food_item/update";
    public static final String DELETE_FOOD_ITEM = BASE_URL + "food_item/delete";
    public static final String GET_FOOD_ITEM_BY_FOOD_TYPE_ID  = BASE_URL + "food_item/get_food_item_by_food_type";
    public static final String GET_FOOD_ITEM_GROUPED_BY_SUPPLIER_ID  = BASE_URL + "food_item/get_food_items_grouped_by_supplierId?keyword=";
    public static final String GET_FOOD_ITEM_NAME_FOR_SUGGESTION  = BASE_URL + "food_item/get_all_food_item_names?keyword=";
    public static final String UPDATE_OFFERED_STATUS  = BASE_URL + "food_item/update_offered_status";
    public static final String GET_FOOD_ITEMS_BY_OFFERED_STATUS  = BASE_URL + "food_item/get_food_items_by_offered_status";
    public static final String GET_FOOD_ITEMS_TOP_SOLD  = BASE_URL + "food_item/top_sold";
    public static final String CREATE_FOOD_ITEM = BASE_URL + "food_item/create";




    //Supplier
    public static final String GET_SUPPLIER_BY_USER_ID = BASE_URL + "supplier_info/get_supplier_by_user_id";
    public static final String UPDATE_OPEN_CLOSE_TIME = BASE_URL + "supplier_info/update_time";
    public static final String GET_ALL_SUPPLIER_TYPES = BASE_URL + "supplier_types/get_all_supplier_types";
    public static final String GET_SUPPLIER_BY_SUPPLIER_TYPE_ID  = BASE_URL + "supplier_info/get_supplier_by_supplier_type_id";
    public static final String GET_SUPPLIER_BY_ID  = BASE_URL + "supplier_info/get_supplier_by_id";
    public static final String UPDATE_SUPPLIER  = BASE_URL + "supplier_info/update";
    public static final String GET_TOP_RATING  = BASE_URL + "supplier_info/top";
    public static final String GET_ALL_SUPPLIERS = BASE_URL + "supplier_info/get_all_suppliers";



    //Food type
    public static final String GET_ALL_FOOD_TYPES = BASE_URL + "food_types/get_all_food_types";
    public static final String GET_FOOD_TYPES_BY_SUPPLIER_ID = BASE_URL + "food_types/get_food_type_by_supplier_id";
    public static final String CREATE_FOOD_TYPE = BASE_URL + "food_types/create";


    // Rating
 public static final String GET_RATING_BY_SUPPLIER_ID = BASE_URL + "order_ratings/get_rating_by_supplier_id";
    public static final String CREATE_RATING = BASE_URL + "order_ratings/add_rating";
    public static final String GET_RATING_DETAIL = BASE_URL + "order_ratings/get_rating_detail";
    public static final String GET_ALL_RATING = BASE_URL + "order_ratings/get_all_messages_by_stars";



    //Food order
    public static final String GET_ORDER_BY_USER_ID = BASE_URL + "food_orders/get_food_order_by_userId";
    public static final String GET_ORDER_DETAIL = BASE_URL + "food_orders/get_food_order_by_id";
    public static final String CREATE_ORDER = BASE_URL + "food_orders/create";
    public static final String UPDATE_PAYMENT_STATUS = BASE_URL + "food_orders/update_payment_status";
    public static final String DELETE_FOOD_ORDER = BASE_URL + "food_orders/delete";


    // Report
    public static final String GET_REPORT_BY_DATE = BASE_URL + "report/report_for_partner_by_date?";
    public static final String GET_REPORT_BY_DATE_RANGE = BASE_URL + "report/report_for_partner_by_range_date?";

  // Banner image
  public static final String GET_ALL_BANNER_ACTIVED = BASE_URL + "image_banner/get_all_image_banners";

  // FAQ
  public static final String GET_ALL_FAQ = BASE_URL + "faq/get_all_faq";
    public static final String GET_FAQ_DETAIL = BASE_URL + "faq/get_faq_detail";

    //ForgotPassword
    public static final String FORGOT_PASSWORD_SEND_OTP = BASE_URL + "forgot_password/send_otp";

    public static final String FORGOT_PASSWORD_VERIFY_OTP = BASE_URL + "forgot_password/verify_otp";
    public static final String FORGOT_PASSWORD_CHANGE_PASSWORD = BASE_URL + "forgot_password/change_password";


    //Promotion
    public static final String CREATE_PROMOTION = BASE_URL + "promotions/create";










    // Thêm các endpoint khác theo nhu cầu
}
