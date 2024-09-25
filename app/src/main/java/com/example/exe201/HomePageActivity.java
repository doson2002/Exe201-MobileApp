package com.example.exe201;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exe201.Adapter.SupplierTypeAdapter;
import com.example.exe201.DTO.SupplierType;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSupplierTypes;
    private SupplierTypeAdapter supplierTypeAdapter;
    private List<SupplierType> supplierTypeList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        recyclerViewSupplierTypes = findViewById(R.id.recyclerSupplierTypes);
// Cài đặt Layout Manager cho RecyclerView
        LinearLayoutManager supplierTypeLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSupplierTypes.setLayoutManager(supplierTypeLayoutManager);

        // Truyền listener cho FoodTypeAdapter
        supplierTypeAdapter = new SupplierTypeAdapter(supplierTypeList, this, new SupplierTypeAdapter.OnSupplierTypeClickListener() {
            @Override
            public void onSupplierTypeClick(SupplierType supplierType) {
                // Lấy foodTypeId được chọn và gọi phương thức loadFoodItemsByFoodTypeId
                int supplierTypeId = supplierType.getId(); // Lấy ID của FoodType được chọn
                Intent intent = new Intent(HomePageActivity.this, SupplierForCustomer.class); // Thay bằng activity của bạn
                intent.putExtra("supplierTypeId", supplierTypeId);
                startActivity(intent);
            }
        });
        recyclerViewSupplierTypes.setAdapter(supplierTypeAdapter);

        Button test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, SupplierForCustomer.class); // Thay bằng activity của bạn
                startActivity(intent);
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView favoriteIcon = findViewById(R.id.favorite_icon);
        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class); // Thay bằng activity của bạn
                startActivity(intent);
            }
        });


    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.toolbar_menu, menu); // Thay tên file menu của bạn ở đây
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_favorite) {
//            // Xử lý khi nhấn vào icon trái tim
//            // Chuyển đến trang lưu các nhà hàng yêu thích
//            Intent intent = new Intent(this, ProfileActivity.class); // Thay bằng activity của bạn
//            startActivity(intent);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}