package com.example.wardani.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wardani.R;
import com.example.wardani.adapters.AdminKelolaPesananAdapter;
import com.example.wardani.models.AdminKelolaPesananModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class AdminKelolaPesananActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPesanan;
    private FirebaseFirestore db;
    private AdminKelolaPesananAdapter adapter;
    private List<AdminKelolaPesananModel> pesananList;
    private SwipeRefreshLayout swipeRefreshLayout; // Deklarasikan SwipeRefreshLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_kelola_pesanan);

        Toolbar toolbar = findViewById(R.id.admin_kelola_pesanan);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerViewPesanan = findViewById(R.id.recyclerViewPesanan);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Inisialisasi SwipeRefreshLayout
        recyclerViewPesanan.setLayoutManager(new LinearLayoutManager(this));
        pesananList = new ArrayList<>();
        adapter = new AdminKelolaPesananAdapter(this, pesananList, swipeRefreshLayout); // Sambungkan SwipeRefreshLayout dengan adapter
        recyclerViewPesanan.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadDataFromFirestore();

        Button btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> {
            showFilterDialog();
        });

        // Atur aksi ketika swipe refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Load data from Firestore again
                loadDataFromFirestore();
            }
        });
    }

    private void loadDataFromFirestore() {
        db.collection("Riwayat")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pesananList.clear(); // Clear the list before adding new data
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        AdminKelolaPesananModel model = documentSnapshot.toObject(AdminKelolaPesananModel.class);
                        pesananList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                    stopRefreshing(); // Stop refreshing when data is loaded
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminKelolaPesananActivity.this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    stopRefreshing(); // Stop refreshing when there's a failure
                });
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Filter");
        String[] filterOptions = {"Terbaru", "Terlama"};
        builder.setItems(filterOptions, (dialog, which) -> {
            String selectedOption = filterOptions[which];
            filterData(selectedOption);
        });
        builder.show();
    }

    private void filterData(String filterOption) {
        adapter.filterData(filterOption);
    }
}