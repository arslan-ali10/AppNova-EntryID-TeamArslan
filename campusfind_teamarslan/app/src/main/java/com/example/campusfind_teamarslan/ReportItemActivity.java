
package com.example.campusfind_teamarslan;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportItemActivity extends AppCompatActivity {

    TextView tvReportTitle;
    EditText etItemName, etDescription, etLocation;
    Spinner spCategory;
    Button btnDate, btnSaveReport;

    DatabaseHelper databaseHelper;

    String itemStatus;
    String reportDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_item);

        tvReportTitle = findViewById(R.id.tvReportTitle);
        etItemName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        spCategory = findViewById(R.id.spCategory);
        btnDate = findViewById(R.id.btnDate);
        btnSaveReport = findViewById(R.id.btnSaveReport);

        databaseHelper = new DatabaseHelper(this);

        // Receive LOST or FOUND status
        itemStatus = getIntent().getStringExtra("ITEM_STATUS");

        if (itemStatus == null) {
            itemStatus = "LOST";
        }

        tvReportTitle.setText(
                itemStatus.equals("LOST")
                        ? "Report Lost Item"
                        : "Report Found Item"
        );

        // Category dropdown
        String[] categories = {
                "Select Category",
                "Electronics",
                "Books",
                "Bags",
                "Clothing",
                "Accessories",
                "Documents",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategory.setAdapter(adapter);

        // Set today's date
        setTodayDate();

        // Open date picker
        btnDate.setOnClickListener(v -> openDatePicker());

        // Save report
        btnSaveReport.setOnClickListener(v -> saveReport());
    }

    private void setTodayDate() {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        );

        reportDate = sdf.format(calendar.getTime());

        btnDate.setText(reportDate);
    }

    private void openDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    reportDate = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            dayOfMonth
                    );

                    btnDate.setText(reportDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.show();
    }

    private void saveReport() {

        String itemName = etItemName.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (itemName.isEmpty()) {
            etItemName.setError("Enter item name");
            return;
        }

        if (spCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(this,
                    "Please select a category",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (location.isEmpty()) {
            etLocation.setError("Enter location");
            return;
        }

        // Get logged-in user's email
        String userEmail = getIntent().getStringExtra("USER_EMAIL");

        if (userEmail == null || userEmail.trim().isEmpty()) {
            Toast.makeText(this,
                    "User information not found. Please log in again.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Insert item into SQLite database
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("user_email", userEmail);
        values.put("item_name", itemName);
        values.put("category", category);
        values.put("description", description);
        values.put("status", itemStatus);
        values.put("location", location);
        values.put("date_reported", reportDate);
        values.put("is_returned", 0);

        long result = db.insert("items", null, values);

        if (result != -1) {

            Toast.makeText(this,
                    "Item reported successfully!",
                    Toast.LENGTH_SHORT).show();

            finish();

        } else {

            Toast.makeText(this,
                    "Unable to save item. Please try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
