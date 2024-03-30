package com.example.wardani.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.wardani.R;
import com.example.wardani.adapters.ShowAllAdapter;
import com.example.wardani.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowAllActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ShowAllAdapter showAllAdapter;
    List<ShowAllModel> showAllModelList;
    Toolbar toolbar;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        // Inisialisasi elemen UI dan Firebase
        toolbar = findViewById(R.id.detail_showall_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.show_all_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        // Inisialisasi daftar dan adapter
        showAllModelList = new ArrayList<>();
        showAllAdapter = new ShowAllAdapter(this, showAllModelList);
        recyclerView.setAdapter(showAllAdapter);

        // Mendapatkan data dari Firestore dan menambahkannya ke daftar
        firestore.collection("ShowAll")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                ShowAllModel showAllModel = doc.toObject(ShowAllModel.class);
                                showAllModelList.add(showAllModel);
                            }
                            // Menyaring dan menampilkan daftar saat pertama kali dibuka
                            filter("");
                        }
                    }
                });

        // Mengatur TextWatcher untuk EditText pencarian
        EditText searchEditText = findViewById(R.id.search_seniman);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Tidak perlu diimplementasikan
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Tidak perlu diimplementasikan
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Memfilter daftar berdasarkan teks yang dimasukkan oleh pengguna
                filter(editable.toString());
            }
        });
    }

    // Metode untuk menyaring daftar berdasarkan teks pencarian
    private void filter(String text) {
        List<ShowAllModel> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            // Jika teks pencarian kosong, tampilkan semua item
            filteredList.addAll(showAllModelList);
        } else {
            // Jika tidak, filter daftar berdasarkan teks yang dimasukkan
            for (ShowAllModel item : showAllModelList) {
                if (item.getNama_dalang().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        // Setelah memfilter, perbarui daftar yang ditampilkan di RecyclerView
        showAllAdapter.filterList(filteredList);

        // Menampilkan RecyclerView jika hasil pencarian ditemukan
        if (filteredList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
