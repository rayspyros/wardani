package com.example.wardani.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wardani.R;

public class PaymentActivity extends AppCompatActivity {
    private TextView textViewNama, textViewTanggal, textViewWaktu, textViewDetail, textViewHarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Inisialisasi TextView untuk menampilkan data
        textViewNama = findViewById(R.id.payment_nama);
        textViewTanggal = findViewById(R.id.payment_tanggal);
        textViewWaktu = findViewById(R.id.payment_waktu);
        textViewDetail = findViewById(R.id.payment_detail);
        textViewHarga = findViewById(R.id.payment_harga);

        // Tangkap data yang diteruskan dari intent
        Intent intent = getIntent();
        if (intent != null) {
            String nama = intent.getStringExtra("NAMA");
            String tanggal = intent.getStringExtra("TANGGAL");
            String waktu = intent.getStringExtra("WAKTU");
            String detail = intent.getStringExtra("DETAIL");
            String harga = intent.getStringExtra("HARGA");

            // Set data ke TextView
            textViewNama.setText(nama);
            textViewTanggal.setText(tanggal);
            textViewWaktu.setText(waktu);
            textViewDetail.setText(detail);
            textViewHarga.setText(harga);
        }
    }
}


