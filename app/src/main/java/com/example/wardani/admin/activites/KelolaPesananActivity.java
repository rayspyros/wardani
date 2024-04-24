package com.example.wardani.admin.activites;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wardani.R;
import com.example.wardani.admin.adapters.KelolaPesananAdapter;
import com.example.wardani.admin.models.KelolaPesananModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class KelolaPesananActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPesanan;
    private FirebaseFirestore db;
    private KelolaPesananAdapter adapter;
    private List<KelolaPesananModel> pesananList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_pesanan);

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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerViewPesanan.setLayoutManager(new LinearLayoutManager(this));
        pesananList = new ArrayList<>();
        adapter = new KelolaPesananAdapter(this, pesananList, swipeRefreshLayout);
        recyclerViewPesanan.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadDataFromFirestore();

//        Button btnFilter = findViewById(R.id.btnFilter);
//        btnFilter.setOnClickListener(v -> {
//            showFilterDialog();
//        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromFirestore();
            }
        });
    }

    private void loadDataFromFirestore() {
        db.collection("Riwayat")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pesananList.clear(); // Hapus daftar sebelum menambahkan data baru
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Ambil data tgl_order secara eksplisit dari documentSnapshot
                        String tglOrder = documentSnapshot.getString("tgl_order");
                        KelolaPesananModel model = documentSnapshot.toObject(KelolaPesananModel.class);
                        model.setTglOrder(tglOrder); // Atur tglOrder ke dalam objek model
                        pesananList.add(model);
                    }
                    adapter.notifyDataSetChanged(); // Tambahkan ini untuk memperbarui tampilan
                    adapter.filterData("terbaru"); // Panggil filterData dengan opsi "terbaru"
                    stopRefreshing(); // Berhenti menyegarkan ketika data dimuat
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(KelolaPesananActivity.this, "Gagal memuat data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    stopRefreshing(); // Berhenti menyegarkan jika ada kegagalan
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
            String selectedOption = filterOptions[which].toLowerCase(); // Mengubah pilihan ke huruf kecil
            filterData(selectedOption);
        });
        builder.show();
    }

    private void filterData(String filterOption) {
        adapter.filterData(filterOption);
    }
}