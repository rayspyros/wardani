package com.example.wardani.activities;

import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.wardani.R;
import com.example.wardani.models.BeritaModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailBeritaActivity extends AppCompatActivity {

    ImageView bImage;
    TextView judul, waktu, deskripsi;
    Toolbar toolbar;


    //Berita
    BeritaModel beritaModel = null;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_berita);

        toolbar = findViewById(R.id.detail_berita_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TextView detailDeskripsi = findViewById(R.id.detail_bDeskripsi);
            detailDeskripsi.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();

        final Object obj = getIntent().getSerializableExtra("detail");

        if (obj instanceof BeritaModel){
            beritaModel = (BeritaModel) obj;
        }

        bImage = findViewById(R.id.detail_bImg);
        judul = findViewById(R.id.detail_bJudul);
        deskripsi = findViewById(R.id.detail_bDeskripsi);
        waktu = findViewById(R.id.detail_bWaktu);

        if (beritaModel != null){
            Glide.with(getApplicationContext()).load(beritaModel.getImg_url()).into(bImage);
            judul.setText(beritaModel.getJudul());
            deskripsi.setText(beritaModel.getDeskripsi());
            waktu.setText(String.valueOf(beritaModel.getWaktu()));
        }
    }
}