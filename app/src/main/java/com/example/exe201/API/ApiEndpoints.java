package com.example.exe201.API;

public class ApiEndpoints {
    // Địa chỉ cơ sở (base URL) của API
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




    //Supplier
    public static final String GET_SUPPLIER_BY_USER_ID = BASE_URL + "supplier_info/get_supplier_by_user_id";
    public static final String UPDATE_OPEN_CLOSE_TIME = BASE_URL + "supplier_info/update_time";
    public static final String GET_ALL_SUPPLIER_TYPES = BASE_URL + "supplier_types/get_all_supplier_types";
    public static final String GET_SUPPLIER_BY_SUPPLIER_TYPE_ID  = BASE_URL + "supplier_info/get_supplier_by_supplier_type_id";
    public static final String GET_SUPPLIER_BY_ID  = BASE_URL + "supplier_info/get_supplier_by_id";


    //Food type
    public static final String GET_ALL_FOOD_TYPES = BASE_URL + "food_types/get_all_food_types";
    public static final String GET_FOOD_TYPES_BY_SUPPLIER_ID = BASE_URL + "food_types/get_food_type_by_supplier_id";

 // Rating
 public static final String GET_RATING_BY_SUPPLIER_ID = BASE_URL + "order_ratings/get_rating_by_supplier_id";
    public static final String CREATE_RATING = BASE_URL + "order_ratings/add_rating";

    //Food order
    public static final String GET_ORDER_BY_USER_ID = BASE_URL + "food_orders/get_food_order_by_userId";
    public static final String GET_ORDER_DETAIL = BASE_URL + "food_orders/get_food_order_by_id";


 // Report
    public static final String GET_REPORT_BY_DATE = BASE_URL + "report/report_for_partner_by_date?";
    public static final String GET_REPORT_BY_DATE_RANGE = BASE_URL + "report/report_for_partner_by_range_date?";







    // Thêm các endpoint khác theo nhu cầu
}
