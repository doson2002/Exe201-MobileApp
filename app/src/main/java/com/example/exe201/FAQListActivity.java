package com.example.exe201;

import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.FAQAdapter;
import com.example.exe201.DTO.FAQ;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FAQListActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private List<FAQ> faqList;
    private FAQAdapter faqAdapter;
    private RecyclerView faqRecyclerView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqlist);

        requestQueue = Volley.newRequestQueue(this);
        faqList = new ArrayList<>();

        // Setup RecyclerView
        faqRecyclerView = findViewById(R.id.faqRecyclerView);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        faqAdapter = new FAQAdapter(faqList);
        faqRecyclerView.setAdapter(faqAdapter);

        // Setup SearchView
        searchView = findViewById(R.id.searchView);
        try {
            // Sử dụng reflection để truy cập EditText
            Field searchField = SearchView.class.getDeclaredField("mSearchSrcTextView");
            searchField.setAccessible(true);
            EditText searchEditText = (EditText) searchField.get(searchView);

            // Thay đổi màu chữ thành màu đen
            searchEditText.setTextColor(Color.BLACK);

            // Thay đổi màu gợi ý thành màu xám (tùy chọn)
            searchEditText.setHintTextColor(Color.GRAY);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        // Đảm bảo SearchView không bị thu gọn
        searchView.setIconifiedByDefault(false);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);  // Mở rộng SearchView khi nhấn vào bất kỳ đâu
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Call API with search query
                getFAQs(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optional: Call API while typing if you want real-time search
                getFAQs(newText);
                return false;
            }
        });

        // Initial API call to load all FAQs
        getFAQs("");
    }

    private void getFAQs(String searchQuery) {
        String urlWithQuery = ApiEndpoints.GET_ALL_FAQ +"?search="+ searchQuery;
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlWithQuery, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            faqList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject faqObject = response.getJSONObject(i);

                                FAQ faq = new FAQ();
                                faq.setId(faqObject.getInt("id"));
                                faq.setTitle(faqObject.getString("title"));
                                faq.setArticle(faqObject.getString("article"));

                                faqList.add(faq);
                            }

                            // Update RecyclerView after loading data
                            faqAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FAQListActivity.this, "Failed to parse data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FAQListActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error: " + error.toString());
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                return headers;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }
}