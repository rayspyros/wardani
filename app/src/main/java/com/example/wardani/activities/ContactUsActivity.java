package com.example.wardani.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wardani.R;

public class ContactUsActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        toolbar = findViewById(R.id.contactus_toolbar);
        imageView = findViewById(R.id.iconKontak);
        textView = findViewById(R.id.nomor_wa);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomor = "081215409800";
                Intent memanggil = new Intent(Intent.ACTION_DIAL);
                memanggil.setData(Uri.fromParts("tel", nomor, null));
                startActivity(memanggil);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://wa.link/i5a6oa";
                Intent bukaWeb = new Intent(Intent.ACTION_VIEW);
                bukaWeb.setData(Uri.parse(url));
                startActivity(bukaWeb);
            }
        });
    }
}