package com.example.exe201;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FaqDetailActivity extends AppCompatActivity {

    private TextView faqTitleTextView;
    private WebView faqWebView;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faq_detail);
        View rootView = findViewById(R.id.root_view);
        // Thiết lập WindowInsetsListener
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                // Áp dụng padding để tránh bị thanh hệ thống che
                v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
                return insets.consumeSystemWindowInsets();
            }
        });
        faqTitleTextView = findViewById(R.id.faqTitle);
        faqWebView = findViewById(R.id.faqWebView);
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set up WebView settings
        WebSettings webSettings = faqWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript (if needed)
        webSettings.setDomStorageEnabled(true); // Enable DOM storage for better HTML rendering
        faqWebView.setWebViewClient(new WebViewClient());

        // Get the FAQ ID from the intent
        int faqId = getIntent().getIntExtra("FAQ_ID", -1);

        if (faqId != -1) {
            loadFaqDetail(faqId);
        } else {
            Toast.makeText(this, "Invalid FAQ ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFaqDetail(int faqId) {
        String url = ApiEndpoints.GET_FAQ_DETAIL + "/" + faqId;
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);

        // Make a Volley request to get FAQ details
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract the title and article fields from the JSON response
                            String title = response.getString("title");
                            String articleHtml = response.getString("article");

                            // Set the title in the TextView
                            faqTitleTextView.setText(title);

                            // Load the article HTML into the WebView with base URL
                            faqWebView.loadDataWithBaseURL(null, articleHtml, "text/html", "UTF-8", null);
                        } catch (JSONException e) {
                            Toast.makeText(FaqDetailActivity.this, "Error parsing FAQ details", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FaqDetailActivity.this, "Error loading FAQ details", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }
}
