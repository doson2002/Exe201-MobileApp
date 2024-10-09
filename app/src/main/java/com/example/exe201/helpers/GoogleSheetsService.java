package com.example.exe201.helpers;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class GoogleSheetsService {
    private Context context;

    public GoogleSheetsService(Context context) {
        this.context = context;
    }

    public Sheets getSheetsService() throws IOException {
        // Mở tệp JSON từ assets
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("exe201-c1658-c39b32477d11.json");

        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Sheets.Builder(new com.google.api.client.http.javanet.NetHttpTransport(),
                new com.google.api.client.json.jackson2.JacksonFactory(),
                requestInitializer)
                .setApplicationName("Exe201report")
                .build();
    }

    public String getAccessToken() throws IOException {
        // Mở tệp JSON từ assets để lấy access token
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("exe201-c1658-c39b32477d11.json");

        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        credentials.refreshIfExpired(); // Làm mới token nếu hết hạn
        return credentials.getAccessToken().getTokenValue();
    }
}
