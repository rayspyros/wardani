package com.example.wardani.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wardani.R;
import com.example.wardani.models.SenimanModel;
import com.example.wardani.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class DetailSenimanActivity extends AppCompatActivity {

    ImageView sImage;
    TextView nama, deskripsi, harga;
    Button bookingWa, tambahSimpan, pesanSekarang;
    Toolbar toolbar;

    int totalHarga = 0;

    //Seniman
    SenimanModel senimanModel = null;

    //Show All
    ShowAllModel showAllModel = null;

    FirebaseAuth auth;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_seniman);

        toolbar = findViewById(R.id.detail_seniman_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final Object obj = getIntent().getSerializableExtra("detail");

        if (obj instanceof SenimanModel) {
            senimanModel = (SenimanModel) obj;
        } else if (obj instanceof ShowAllModel) {
            showAllModel = (ShowAllModel) obj;
        }

        //Seniman
        sImage = findViewById(R.id.detail_img);
        nama = findViewById(R.id.detail_nama);
        deskripsi = findViewById(R.id.detail_deskripsi);
        harga = findViewById(R.id.detail_harga);

        bookingWa = findViewById(R.id.booking_wa);
        tambahSimpan = findViewById(R.id.tambah_seniman);
        pesanSekarang = findViewById(R.id.pesan_sekarang);

        //Seniman
        if (senimanModel != null) {
            Glide.with(getApplicationContext()).load(senimanModel.getImg_url()).into(sImage);
            nama.setText(senimanModel.getNama_dalang());
            deskripsi.setText(senimanModel.getDeskripsi());
            harga.setText(String.valueOf(senimanModel.getHarga_jasa()));

            totalHarga = senimanModel.getHarga_jasa();
        }

        //Show All
        if (showAllModel != null) {
            Glide.with(getApplicationContext()).load(showAllModel.getImg_url()).into(sImage);
            nama.setText(showAllModel.getNama_dalang());
            deskripsi.setText(showAllModel.getDeskripsi());
            harga.setText(String.valueOf(showAllModel.getHarga_jasa()));

            totalHarga = showAllModel.getHarga_jasa();
        }

        bookingWa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String url = "https://wa.link/ajqazg";
                String url = "https://wa.link/i5a6oa";
                Intent bukaWeb = new Intent(Intent.ACTION_VIEW);
                bukaWeb.setData(Uri.parse(url));
                startActivity(bukaWeb);
            }
        });

        //Pesanan
        pesanSekarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailSenimanActivity.this, OrderActivity.class);

                // Meneruskan data nama seniman dan harga seniman ke OrderActivity
                if (senimanModel != null) {
                    intent.putExtra("nama_seniman", senimanModel.getNama_dalang());
                    intent.putExtra("harga_seniman", senimanModel.getHarga_jasa());
                }
                if (showAllModel != null) {
                    intent.putExtra("nama_seniman", showAllModel.getNama_dalang());
                    intent.putExtra("harga_seniman", showAllModel.getHarga_jasa());
                }
                startActivity(intent);
            }
        });
        //Tambah Simpan
        tambahSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Menampilkan dialog konfirmasi
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailSenimanActivity.this);
                builder.setMessage("Masukkan ke Daftar Favorit Anda?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Jika pengguna memilih "Ya", tambahkan seniman ke daftar favorit
                        tambahSimpan();
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Jika pengguna memilih "Tidak", tutup dialog
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void tambahSimpan() {
        if (auth.getCurrentUser() != null) {
            // Pastikan pengguna sudah login sebelum menambahkan ke daftar favorit

            final String senimanNama = nama.getText().toString();
            final int senimanHarga = Integer.parseInt(harga.getText().toString());

            firestore.collection("Simpan").document(auth.getCurrentUser().getUid())
                    .collection("User").document(senimanNama).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Jika seniman dengan nama yang sama sudah ada di daftar favorit
                                    Toast.makeText(DetailSenimanActivity.this,
                                            "Seniman ini sudah ada di Favorit Anda", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Jika seniman belum ada di daftar favorit, tambahkan
                                    HashMap<String, Object> simpanMap = new HashMap<>();
                                    simpanMap.put("senimanNama", senimanNama);
                                    simpanMap.put("senimanHarga", senimanHarga);

                                    firestore.collection("Simpan").document(auth.getCurrentUser().getUid())
                                            .collection("User").document(senimanNama).set(simpanMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(DetailSenimanActivity.this,
                                                                "Seniman ditambahkan ke favorit",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(DetailSenimanActivity.this,
                                                                "Gagal menambahkan seniman ke favorit",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(DetailSenimanActivity.this,
                                        "Gagal menambahkan seniman ke favorit", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Jika pengguna belum login, arahkan ke aktivitas login
            startActivity(new Intent(DetailSenimanActivity.this, LoginActivity.class));
        }
    }
}
