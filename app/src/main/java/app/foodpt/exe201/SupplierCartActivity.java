package app.foodpt.exe201;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.foodpt.exe201.Adapter.SupplierCartAdapter;
import app.foodpt.exe201.DTO.Menu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class SupplierCartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSuppliers;
    private SupplierCartAdapter supplierCartAdapter;
    private HashMap<Integer, List<Menu>> cartMap;
    private ImageView backArrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cartMap = loadCartMapFromPreferences();
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });
//        // Nhận cartMap từ Intent
//        cartMap = (HashMap<Integer, List<Menu>>) getIntent().getSerializableExtra("cart_map");

        recyclerViewSuppliers = findViewById(R.id.recyclerViewSuppliers);
        recyclerViewSuppliers.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter và truyền cartMap vào Adapter
        supplierCartAdapter = new SupplierCartAdapter(cartMap);
        recyclerViewSuppliers.setAdapter(supplierCartAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewSuppliers.getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider)); // Thay đổi đường gạch ngang
        recyclerViewSuppliers.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Gọi phương thức để làm mới cartMap từ SharedPreferences
        refreshCartData();
    }

    private HashMap<Integer, List<Menu>> loadCartMapFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("cart_map", null);

        Type type = new TypeToken<HashMap<Integer, List<Menu>>>() {}.getType();
        return new Gson().fromJson(json, type); // Chuyển đổi JSON về HashMap
    }

    // Gọi phương thức này để làm mới cartMap từ SharedPreferences
    private void refreshCartData() {
        HashMap<Integer, List<Menu>> updatedCartMap = loadCartMapFromPreferences(); // Load lại từ SharedPreferences
        if (updatedCartMap != null) {
            supplierCartAdapter.updateCartMap(updatedCartMap); // Cập nhật Adapter với cartMap mới
        }
    }
}