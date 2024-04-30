package com.example.wardani.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wardani.R;
import com.example.wardani.models.ContactUsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.database.DatabaseReference;

public class ContactUsActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    Toolbar toolbar;
    private boolean isContactClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        toolbar = findViewById(R.id.contactus_toolbar);
        imageView = findViewById(R.id.iconKontak);
        textView = findViewById(R.id.nomor_wa);
        ImageView editButton = findViewById(R.id.ic_edit);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Periksa apakah pengguna yang saat ini masuk adalah admin
        if (currentUser != null && currentUser.getEmail().equals("adminwardani123@gmail.com")) {
            // Jika ya, maka atur visibilitas tombol edit menjadi terlihat
            editButton.setVisibility(View.VISIBLE);
        } else {
            // Jika tidak, maka atur visibilitas tombol edit menjadi tidak terlihat
            editButton.setVisibility(View.GONE);
        }


        // Mengambil data alamat dari Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ContactUs").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Mendapatkan ID dokumen yang sesuai dengan alamat
                    String documentId = document.getId();
                    ContactUsModel contactUsModel = document.toObject(ContactUsModel.class);
                    // Mendapatkan alamat dari ContactUsModel
                    String alamat = contactUsModel.getAlamat();
                    // Atur teks alamatKontak TextView
                    TextView alamatTextView = findViewById(R.id.alamatKontak);
                    alamatTextView.setText(alamat);
                    // Jika tombol edit ditekan, beri tahu showEditAddressDialog() ID dokumen yang sesuai
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Tampilkan pop-up untuk mengedit alamat dengan ID dokumen yang sesuai
                            showEditAddressDialog(documentId);
                        }
                    });
                }
            } else {
                // Jika gagal mendapatkan data dari Firestore
                Toast.makeText(ContactUsActivity.this, "Gagal mengambil data alamat", Toast.LENGTH_SHORT).show();
            }
        });


        ImageButton btnHome = findViewById(R.id.btn_home);
        ImageButton btnHistory = findViewById(R.id.btn_history);
        ImageButton btnSearch = findViewById(R.id.btn_artist);
        ImageButton btnProfile = findViewById(R.id.btn_profile);
        ImageButton btnContact = findViewById(R.id.btn_kontakkami);

        // Memeriksa apakah activity saat ini adalah ContactUsActivity
        if (getClass().getSimpleName().equals("ContactUsActivity")) {
            // Jika ya, maka atur latar belakang tombol "Contact" menjadi putih
            btnContact.setBackgroundResource(R.drawable.white_rounded_corner);
        }

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isContactClicked) {
                    isContactClicked = true;
                    Toast.makeText(ContactUsActivity.this, "Anda sudah di Halaman Kontak Kami", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactUsActivity.this, MainActivity.class));
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactUsActivity.this, HistoryActivity.class));
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactUsActivity.this, ShowAllActivity.class));
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactUsActivity.this, ProfileActivity.class));
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
    private void showEditAddressDialog(String documentId) {
        // Buat tampilan untuk dialog
        View dialogView = getLayoutInflater().inflate(R.layout.popup_edit_alamat, null);

        // Inisialisasi EditText dan Button di dalam dialog
        EditText editTextAddress = dialogView.findViewById(R.id.pp_alamat);
        Button buttonSave = dialogView.findViewById(R.id.btn_simpan);

        // Buat dialog AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Buat dialog
        AlertDialog alertDialog = builder.create();

        // Ambil alamat dari Firestore menggunakan ID dokumen yang sesuai
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ContactUs").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ContactUsModel contactUsModel = documentSnapshot.toObject(ContactUsModel.class);
                        if (contactUsModel != null) {
                            // Set teks EditText dengan alamat yang diambil dari Firestore
                            editTextAddress.setText(contactUsModel.getAlamat());
                        }
                    } else {
                        Toast.makeText(ContactUsActivity.this, "Dokumen tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ContactUsActivity.this, "Gagal mengambil alamat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Ketika tombol Simpan diklik
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil alamat yang diedit dari EditText
                String editedAddress = editTextAddress.getText().toString().trim();

                // Simpan perubahan ke Firebase Firestore
                ContactUsModel newContactUsModel = new ContactUsModel(documentId, editedAddress);
                db.collection("ContactUs").document(documentId).set(newContactUsModel)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ContactUsActivity.this, "Alamat berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ContactUsActivity.this, "Gagal menyimpan alamat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // Tampilkan dialog
        alertDialog.show();
    }

}