package com.example.wardani.admin.activites;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wardani.R;
import com.example.wardani.admin.adapters.KelolaBeritaAdapter;
import com.example.wardani.admin.models.KelolaBeritaModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class KelolaBeritaActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private KelolaBeritaAdapter kelolaBeritaAdapter;
    private List<KelolaBeritaModel> kelolaBeritaModelList;
    private EditText searchBeritaEditText;
    private FirebaseFirestore db;
    private ImageView imageView;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_berita);

        Toolbar toolbar = findViewById(R.id.kelola_berita_toolbar);
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
                getDataFromFirestore();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Button btnTambahBerita = findViewById(R.id.btn_tambah_berita);
        btnTambahBerita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanDialogTambahBerita();
            }
        });

        recyclerView = findViewById(R.id.recycler_view_berita);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        kelolaBeritaModelList = new ArrayList<>();
        kelolaBeritaAdapter = new KelolaBeritaAdapter(this, kelolaBeritaModelList);
        recyclerView.setAdapter(kelolaBeritaAdapter);
        searchBeritaEditText = findViewById(R.id.search_kelola_berita);

        db = FirebaseFirestore.getInstance();

        getDataFromFirestore();

        searchBeritaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        recyclerView.setVisibility(View.VISIBLE);
    }

    private void tampilkanDialogTambahBerita() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup_tambah_berita, null);

        final EditText ppJudulBerita = view.findViewById(R.id.pp_judul_berita);
        final TextView ppTanggalUnggah = view.findViewById(R.id.pp_waktu_upload);
        final EditText ppDeskripsiBerita = view.findViewById(R.id.pp_deskripsi_berita);
        imageView = view.findViewById(R.id.iv_gambar_berita);
        Button btnPilihFoto = view.findViewById(R.id.btn_pilih_foto_berita);

        btnPilihFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihFoto();
            }
        });

        ppTanggalUnggah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(KelolaBeritaActivity.this,
                        R.style.DatePickerTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Format the selected date
                                SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));
                                calendar.set(year, monthOfYear, dayOfMonth);
                                ppTanggalUnggah.setText(sdf.format(calendar.getTime()));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button btnTambah = view.findViewById(R.id.btn_tambah_pp);
        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String judul = ppJudulBerita.getText().toString();
                String waktu = ppTanggalUnggah.getText().toString();
                String deskripsi = ppDeskripsiBerita.getText().toString();

                if (TextUtils.isEmpty(judul) || TextUtils.isEmpty(waktu) || TextUtils.isEmpty(deskripsi) || imageUri == null) {
                    Toast.makeText(KelolaBeritaActivity.this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
                } else {
                    tampilkanKonfirmasiTambahData(judul, waktu, deskripsi, imageUri, dialog);
                }
            }
        });
    }
    private void pilihFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void tampilkanKonfirmasiTambahData(String judul, String waktu, String deskripsi, Uri imageUri, AlertDialog dialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah Anda yakin ingin menambahkan data berita ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Memanggil method untuk mengunggah gambar ke Firebase Storage
                uploadImageToFirebaseStorage(imageUri, judul, waktu, deskripsi, dialog);
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss(); // Menutup dialog jika pengguna memilih "Tidak"
            }
        });
        builder.show();
    }

    private void uploadImageToFirebaseStorage(Uri imageUri, String judul, String waktu, String deskripsi, AlertDialog dialog) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String img_url = uri.toString();
                                // Memanggil method untuk menambahkan data ke Firestore
                                tambahkanDataKeFirestore(judul, waktu, deskripsi, img_url, dialog);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(KelolaBeritaActivity.this, "Gagal mengunggah gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method untuk menambahkan data ke Firestore
    private void tambahkanDataKeFirestore(String judul, String waktu, String deskripsi, String img_url, AlertDialog dialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("judul", judul);
        data.put("waktu", waktu);
        data.put("deskripsi", deskripsi);
        data.put("img_url", img_url);

        db.collection("Berita")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(KelolaBeritaActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        getDataFromFirestore();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(KelolaBeritaActivity.this, "Gagal menambahkan data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDataFromFirestore() {
        db.collection("Berita")
                .orderBy("judul", Query.Direction.ASCENDING) // Mengurutkan data berdasarkan nama_dalang
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            kelolaBeritaModelList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                KelolaBeritaModel kelolaBeritaModel = document.toObject(KelolaBeritaModel.class);
                                kelolaBeritaModel.setId(document.getId());
                                kelolaBeritaModelList.add(kelolaBeritaModel);
                            }
                            kelolaBeritaAdapter.notifyDataSetChanged();
                            // Menyaring dan menampilkan daftar saat pertama kali dibuka
                            filter("");
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void filter(String text) {
        List<KelolaBeritaModel> filteredBeritaList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredBeritaList.addAll(kelolaBeritaModelList);
        } else {
            for (KelolaBeritaModel item : kelolaBeritaModelList) {
                if (item.getJudul().toLowerCase().contains(text.toLowerCase())) {
                    filteredBeritaList.add(item);
                }
            }
        }
        KelolaBeritaAdapter.filterList(filteredBeritaList);
    }
}