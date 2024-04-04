package com.example.wardani.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wardani.R;
import com.example.wardani.adapters.AdminKelolaSenimanAdapter;
import com.example.wardani.models.AdminKelolaSenimanModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminKelolaSenimanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button tambahSenimanButton;
    private AdminKelolaSenimanAdapter adminKelolaSenimanAdapter;
    private List<AdminKelolaSenimanModel> adminKelolaSenimanModelList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_kelola_seniman);

        Toolbar toolbar = findViewById(R.id.kelola_seniman_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Panggil metode getDataFromFirestore() untuk mendapatkan data terbaru
                getDataFromFirestore();
                swipeRefreshLayout.setRefreshing(false); // Akhiri animasi refresh setelah selesai
            }
        });

        Button btnTambahSeniman = findViewById(R.id.btn_tambah_seniman);
        btnTambahSeniman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tampilkan dialog pop-up
                tampilkanDialogTambahSeniman();
            }
        });

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recycler_view_seniman);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adminKelolaSenimanModelList = new ArrayList<>();
        adminKelolaSenimanAdapter = new AdminKelolaSenimanAdapter(this, adminKelolaSenimanModelList);
        recyclerView.setAdapter(adminKelolaSenimanAdapter);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Mendapatkan data dari Firestore
        getDataFromFirestore();
    }

    private void tampilkanDialogTambahSeniman() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup_tambah_seniman, null);

        final EditText ppUrlGambar = view.findViewById(R.id.pp_url_gambar);
        final EditText ppNamaSeniman = view.findViewById(R.id.pp_nama_dalang);
        final EditText ppHargaSeniman = view.findViewById(R.id.pp_harga_jasa);
        final EditText ppDeskripsi = view.findViewById(R.id.pp_deskripsi);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button btnTambah = view.findViewById(R.id.btn_tambah);
        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil data dari EditText
                String img_url = ppUrlGambar.getText().toString();
                String nama_dalang = ppNamaSeniman.getText().toString();
                String harga_jasa_str = ppHargaSeniman.getText().toString();
                String deskripsi = ppDeskripsi.getText().toString();

                // Cek apakah semua field telah diisi
                if (TextUtils.isEmpty(img_url) || TextUtils.isEmpty(nama_dalang) || TextUtils.isEmpty(harga_jasa_str) || TextUtils.isEmpty(deskripsi)) {
                    Toast.makeText(AdminKelolaSenimanActivity.this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
                } else {
                    // Tampilkan dialog konfirmasi sebelum menambahkan data
                    int harga_jasa = Integer.parseInt(harga_jasa_str);
                    tampilkanKonfirmasiTambahData(img_url, nama_dalang, harga_jasa, deskripsi, dialog);
                }
            }
        });
    }

    private void tampilkanKonfirmasiTambahData(String img_url, String nama_dalang, int harga_jasa, String deskripsi, AlertDialog dialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah Anda yakin ingin menambahkan data seniman ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Tambahkan data ke Firestore setelah pengguna mengonfirmasi
                tambahkanDataKeFirestore(img_url, nama_dalang, harga_jasa, deskripsi);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Batal menambahkan data, jadi hanya keluar dari dialog konfirmasi
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void tambahkanDataKeFirestore(String img_url, String nama_dalang, int harga_jasa, String deskripsi) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("img_url", img_url);
        data.put("nama_dalang", nama_dalang);
        data.put("harga_jasa", harga_jasa);
        data.put("deskripsi", deskripsi);

        db.collection("ShowAll")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Data berhasil ditambahkan
                        Toast.makeText(AdminKelolaSenimanActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        // Ambil data terbaru dari Firestore
                        getDataFromFirestore();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gagal menambahkan data
                        Toast.makeText(AdminKelolaSenimanActivity.this, "Gagal menambahkan data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDataFromFirestore() {
        db.collection("ShowAll")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Bersihkan data yang ada
                            adminKelolaSenimanModelList.clear();
                            // Tambahkan data yang baru dari Firestore
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                AdminKelolaSenimanModel adminKelolaSenimanModel = document.toObject(AdminKelolaSenimanModel.class);
                                adminKelolaSenimanModel.setId(document.getId()); // Set ID dari dokumen
                                adminKelolaSenimanModelList.add(adminKelolaSenimanModel);
                            }
                            // Memberitahu adapter bahwa data telah berubah
                            adminKelolaSenimanAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}