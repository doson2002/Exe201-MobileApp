//package com.example.exe201;
//
//import android.app.Dialog;
//import android.app.TimePickerDialog;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.Calendar;
//
//public class TimePickerActivity extends AppCompatActivity {
//
//    private TextView openTimeTextView, closeTimeTextView;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_time_picker);
//
//        openTimeTextView = findViewById(R.id.open_time_text_view);
//        closeTimeTextView = findViewById(R.id.close_time_text_view);
//
//        Button openTimeButton = findViewById(R.id.open_time_button);
//        Button closeTimeButton = findViewById(R.id.close_time_button);
//        Button saveButton = findViewById(R.id.save_button_time_picker);
//        ImageView closeButton = findViewById(R.id.close_button);
//
//        openTimeButton.setOnClickListener(v -> showTimePicker(true));
//        closeTimeButton.setOnClickListener(v -> showTimePicker(false));
//
//        closeButton.setOnClickListener(v -> finish()); // Đóng activity
//
//        saveButton.setOnClickListener(v -> {
//            Intent resultIntent = new Intent();
//            resultIntent.putExtra("openTime", openTimeTextView.getText().toString());
//            resultIntent.putExtra("closeTime", closeTimeTextView.getText().toString());
//            setResult(RESULT_OK, resultIntent);
//            finish(); // Đóng activity
//        });
//    }
//
//    private void showTimePicker(boolean isOpenTime) {
//        Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(
//                this,
//                (view, selectedHour, selectedMinute) -> {
//                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
//                    if (isOpenTime) {
//                        openTimeTextView.setText(time);
//                    } else {
//                        closeTimeTextView.setText(time);
//                    }
//                },
//                hour, minute, true);
//
//        timePickerDialog.show();
//    }
//}