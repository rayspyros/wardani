package com.example.wardani.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.wardani.R;
import com.example.wardani.admin.activites.KelolaCustomerActivity;
import com.example.wardani.admin.activites.KelolaPesananActivity;
import com.example.wardani.admin.activites.KelolaSenimanActivity;

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

        LinearLayout kelolaCustomerLayout = findViewById(R.id.kelola_customer);
        LinearLayout kelolaSenimanLayout = findViewById(R.id.kelola_seniman);
        LinearLayout kelolaPesananLayout = findViewById(R.id.kelola_pemesanan);

        // Set click listener for "Kelola Seniman" menu

        kelolaCustomerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Intent to open AdminKelolaCustomerActivity
                Intent intent = new Intent(AdminActivity.this, KelolaCustomerActivity.class);
                // Start new activity
                startActivity(intent);
            }
        });
        kelolaSenimanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Intent to open AdminKelolaSenimanActivity
                Intent intent = new Intent(AdminActivity.this, KelolaSenimanActivity.class);
                // Start new activity
                startActivity(intent);
            }
        });

        kelolaPesananLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Intent to open AdminKelolaSenimanActivity
                Intent intent = new Intent(AdminActivity.this, KelolaPesananActivity.class);
                // Start new activity
                startActivity(intent);
            }
        });
    }
}