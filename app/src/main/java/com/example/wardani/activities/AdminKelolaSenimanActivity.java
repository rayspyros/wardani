package com.example.wardani.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wardani.R;
import com.example.wardani.adapters.AdminKelolaSenimanAdapter;
import com.example.wardani.models.AdminKelolaSenimanModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

    private void getDataFromFirestore() {
        db.collection("ShowAll")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                AdminKelolaSenimanModel adminKelolaSenimanModel = document.toObject(AdminKelolaSenimanModel.class);
                                adminKelolaSenimanModelList.add(adminKelolaSenimanModel);
                            }
                            adminKelolaSenimanAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
