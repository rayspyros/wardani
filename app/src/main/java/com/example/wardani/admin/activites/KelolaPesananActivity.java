package com.example.wardani.admin.activites;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wardani.R;
import com.example.wardani.admin.adapters.KelolaPesananAdapter;
import com.example.wardani.admin.models.KelolaCustomerModel;
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
    private EditText searchPesananEditText;

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
        searchPesananEditText = findViewById(R.id.search_pesanan);

        recyclerViewPesanan.setLayoutManager(new LinearLayoutManager(this));
        pesananList = new ArrayList<>();
        adapter = new KelolaPesananAdapter(this, pesananList, swipeRefreshLayout);
        recyclerViewPesanan.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadDataFromFirestore();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromFirestore();
            }
        });

        searchPesananEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

    }

    private void loadDataFromFirestore() {
        db.collection("Riwayat")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pesananList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        String tglOrder = documentSnapshot.getString("tgl_order");
                        String id = documentSnapshot.getId();
                        KelolaPesananModel model = documentSnapshot.toObject(KelolaPesananModel.class);
                        model.setTglOrder(tglOrder);
                        model.setId(id);
                        pesananList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                    stopRefreshing();
                    filter("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(KelolaPesananActivity.this, "Gagal memuat data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    stopRefreshing();
                });
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void filter(String text) {
        List<KelolaPesananModel> filteredPesananList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredPesananList.addAll(pesananList);
        } else {
            for (KelolaPesananModel item : pesananList) {
                if (item.getNama().toLowerCase().contains(text.toLowerCase()) ||
                        item.getStatus().toLowerCase().contains(text.toLowerCase()) ||
                        item.getCustomer().toLowerCase().contains(text.toLowerCase())) {
                    filteredPesananList.add(item);
                }
            }
        }
        adapter.filterList(filteredPesananList);
    }
}