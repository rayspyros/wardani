package com.example.wardani.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.wardani.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout kelolaSenimanLayout = findViewById(R.id.kelola_seniman);
        LinearLayout kelolaPesananLayout = findViewById(R.id.kelola_pemesanan);

        // Set click listener for "Kelola Seniman" menu
        kelolaSenimanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Intent to open AdminKelolaSenimanActivity
                Intent intent = new Intent(AdminActivity.this, AdminKelolaSenimanActivity.class);
                // Start new activity
                startActivity(intent);
            }
        });

        kelolaPesananLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Intent to open AdminKelolaSenimanActivity
                Intent intent = new Intent(AdminActivity.this, AdminKelolaPesananActivity.class);
                // Start new activity
                startActivity(intent);
            }
        });
    }
}