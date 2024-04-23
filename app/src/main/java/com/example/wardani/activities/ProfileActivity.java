package com.example.wardani.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.wardani.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText nama;
    EditText email;
    EditText alamat;
    EditText kota;
    EditText provinsi;
    EditText kodepos;
    EditText telepon;
    Button btn_simpan;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.profile_toolbar);

        ImageButton btnHome = findViewById(R.id.btn_home);
        ImageButton btnKontak = findViewById(R.id.btn_kontakkami);
        ImageButton btnSearch = findViewById(R.id.btn_artist);
        ImageButton btnHistory = findViewById(R.id.btn_history);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
        btnKontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ContactUsActivity.class));
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ShowAllActivity.class));
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HistoryActivity.class));
            }
        });

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        nama = findViewById(R.id.profile_nama);
        email = findViewById(R.id.profile_email);
        alamat = findViewById(R.id.profile_alamat);
        kota = findViewById(R.id.profile_kota);
        provinsi = findViewById(R.id.profile_prov);
        kodepos = findViewById(R.id.profile_kode);
        telepon = findViewById(R.id.profile_telepon);
        btn_simpan = findViewById(R.id.btn_profile_simpan);

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanKonfirmasiSimpan();
            }
        });

        getDataFromFirestore();
    }

    private void getDataFromFirestore() {
        firestore.collection("Pengguna").document(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Mengisi otomatis EditText dengan data dari Firestore
                                nama.setText(document.getString("nama"));
                                email.setText(document.getString("email"));
                                alamat.setText(document.getString("alamat"));
                                kota.setText(document.getString("kota"));
                                provinsi.setText(document.getString("provinsi"));
                                kodepos.setText(document.getString("kodepos"));
                                telepon.setText(document.getString("telepon"));
                            } else {
                                Toast.makeText(ProfileActivity.this, "Document tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Gagal mengambil data: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void tampilkanKonfirmasiSimpan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Simpan");
        builder.setMessage("Apakah Anda yakin ingin menyimpan data profile Anda?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simpanDataKeFirestore();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Tutup dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void simpanDataKeFirestore() {
        String namaText = nama.getText().toString();
        String emailText = email.getText().toString();
        String alamatText = alamat.getText().toString();
        String kotaText = kota.getText().toString();
        String provinsiText = provinsi.getText().toString();
        String kodeposText = kodepos.getText().toString();
        String teleponText = telepon.getText().toString();

        // Mengupdate data ke Firestore
        firestore.collection("Pengguna").document(auth.getCurrentUser().getUid())
                .update(
                        "nama", namaText,
                        "email", emailText,
                        "alamat", alamatText,
                        "kota", kotaText,
                        "provinsi", provinsiText,
                        "kodepos", kodeposText,
                        "telepon", teleponText
                )
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Gagal menyimpan data: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}