package com.example.wardani.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wardani.R;
import com.example.wardani.adapters.AdminAdapter;
import com.example.wardani.adapters.HistoryAdapter;
import com.example.wardani.models.AdminModel;
import com.example.wardani.models.HistoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AdminAdapter adminAdapter;
    private List<AdminModel> adminModelList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance();

        // Toolbar
        toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.admin_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adminModelList = new ArrayList<>();
        adminAdapter = new AdminAdapter(this, adminModelList);
        recyclerView.setAdapter(adminAdapter);

        // Ambil data dari Firestore
        loadDataPesanan();
    }

    private void loadDataPesanan() {
        firestore.collection("Pesanan")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AdminModel pesananModel = document.toObject(AdminModel.class);
                            adminModelList.add(pesananModel);
                        }
                        adminAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AdminActivity.this, "Gagal mengambil data pesanan: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


