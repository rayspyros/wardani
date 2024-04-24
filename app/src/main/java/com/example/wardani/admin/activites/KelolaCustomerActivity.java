package com.example.wardani.admin.activites;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wardani.R;
import com.example.wardani.admin.adapters.KelolaCustomerAdapter;
import com.example.wardani.admin.models.KelolaCustomerModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class KelolaCustomerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCustomer;
    private FirebaseFirestore db;
    private KelolaCustomerAdapter adapter;
    private List<KelolaCustomerModel> kelolaCustomerModelList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_customer);

        Toolbar toolbar = findViewById(R.id.admin_kelola_cust);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerViewCustomer = findViewById(R.id.recyclerViewCustomer);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerViewCustomer.setLayoutManager(new LinearLayoutManager(this));
        kelolaCustomerModelList = new ArrayList<>();
        adapter = new KelolaCustomerAdapter(this, kelolaCustomerModelList);
        recyclerViewCustomer.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadDataFromFirestore();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromFirestore();
            }
        });
    }

    private void loadDataFromFirestore() {
        db.collection("Pengguna")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        kelolaCustomerModelList.clear(); // Bersihkan daftar sebelum menambahkan data baru
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Konversi dokumen Firestore menjadi objek model
                            KelolaCustomerModel customer = documentSnapshot.toObject(KelolaCustomerModel.class);
                            kelolaCustomerModelList.add(customer);
                        }
                        adapter.notifyDataSetChanged(); // Memperbarui RecyclerView setelah data dimuat
                        stopRefreshing(); // Hentikan refreshing setelah data dimuat
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("KelolaCustomerActivity", "Error getting documents.", e);
                        stopRefreshing();
                    }
                });
    }


    private void stopRefreshing() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
