package com.example.wardani.admin.activites;

import static com.example.wardani.admin.adapters.KelolaSenimanAdapter.PICK_IMAGE_REQUEST;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wardani.R;
import com.example.wardani.admin.adapters.KelolaSenimanAdapter;
import com.example.wardani.admin.models.KelolaCustomerModel;
import com.example.wardani.admin.models.KelolaSenimanModel;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KelolaSenimanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private KelolaSenimanAdapter kelolaSenimanAdapter;
    private List<KelolaSenimanModel> kelolaSenimanModelList;
    private EditText searchSenimanEditText;
    private FirebaseFirestore db;
    private ImageView imageView;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_seniman);

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
                getDataFromFirestore();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Button btnTambahSeniman = findViewById(R.id.btn_tambah_seniman);
        btnTambahSeniman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanDialogTambahSeniman();
            }
        });

        recyclerView = findViewById(R.id.recycler_view_seniman);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        kelolaSenimanModelList = new ArrayList<>();
        kelolaSenimanAdapter = new KelolaSenimanAdapter(this, kelolaSenimanModelList);
        recyclerView.setAdapter(kelolaSenimanAdapter);
        searchSenimanEditText = findViewById(R.id.search_kelola_seniman);

        db = FirebaseFirestore.getInstance();

        getDataFromFirestore();

        searchSenimanEditText.addTextChangedListener(new TextWatcher() {
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

    private void tampilkanDialogTambahSeniman() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup_tambah_seniman, null);

        final EditText ppNamaSeniman = view.findViewById(R.id.pp_nama_dalang);
        final EditText ppHargaSeniman = view.findViewById(R.id.pp_harga_jasa);
        final EditText ppDeskripsi = view.findViewById(R.id.pp_deskripsi);
        imageView = view.findViewById(R.id.iv_gambar);
        Button btnPilihFoto = view.findViewById(R.id.btn_pilih_foto);

        btnPilihFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihFoto();
            }
        });

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button btnTambah = view.findViewById(R.id.btn_tambah);
        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama_dalang = ppNamaSeniman.getText().toString();
                String harga_jasa_str = ppHargaSeniman.getText().toString();
                String deskripsi = ppDeskripsi.getText().toString();
                if (TextUtils.isEmpty(nama_dalang) || TextUtils.isEmpty(harga_jasa_str) || TextUtils.isEmpty(deskripsi) || imageUri == null) {
                    Toast.makeText(KelolaSenimanActivity.this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
                } else {
                    int harga_jasa = Integer.parseInt(harga_jasa_str);
                    tampilkanKonfirmasiTambahData(nama_dalang, harga_jasa, deskripsi, imageUri, dialog);
                }
            }
        });
    }


    private void pilihFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void tampilkanKonfirmasiTambahData(String nama_dalang, int harga_jasa, String deskripsi, Uri imageUri, AlertDialog dialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah Anda yakin ingin menambahkan data seniman ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Memanggil method untuk mengunggah gambar ke Firebase Storage
                uploadImageToFirebaseStorage(imageUri, nama_dalang, harga_jasa, deskripsi, dialog);
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void uploadImageToFirebaseStorage(Uri imageUri, String nama_dalang, int harga_jasa, String deskripsi, AlertDialog dialog) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String img_url = uri.toString();
                                tambahkanDataKeFirestore(nama_dalang, harga_jasa, deskripsi, img_url, dialog);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(KelolaSenimanActivity.this, "Gagal mengunggah gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tambahkanDataKeFirestore(String nama_dalang, int harga_jasa, String deskripsi, String img_url, AlertDialog dialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("nama_dalang", nama_dalang);
        data.put("harga_jasa", harga_jasa);
        data.put("deskripsi", deskripsi);
        data.put("img_url", img_url);

        db.collection("ShowAll")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(KelolaSenimanActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        getDataFromFirestore();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(KelolaSenimanActivity.this, "Gagal menambahkan data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDataFromFirestore() {
        db.collection("ShowAll")
                .orderBy("nama_dalang", Query.Direction.ASCENDING) // Mengurutkan data berdasarkan nama_dalang
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            kelolaSenimanModelList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                KelolaSenimanModel kelolaSenimanModel = document.toObject(KelolaSenimanModel.class);
                                kelolaSenimanModel.setId(document.getId());
                                kelolaSenimanModelList.add(kelolaSenimanModel);
                            }
                            kelolaSenimanAdapter.notifyDataSetChanged();
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
        List<KelolaSenimanModel> filteredSenimanList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredSenimanList.addAll(kelolaSenimanModelList);
        } else {
            for (KelolaSenimanModel item : kelolaSenimanModelList) {
                if (item.getNama_dalang().toLowerCase().contains(text.toLowerCase())) {
                    filteredSenimanList.add(item);
                }
            }
        }
        kelolaSenimanAdapter.filterList(filteredSenimanList);
    }
}
