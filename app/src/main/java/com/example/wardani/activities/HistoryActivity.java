package com.example.wardani.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wardani.R;
import com.example.wardani.adapters.HistoryAdapter;
import com.example.wardani.models.HistoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    public static final int PAYMENT_REQUEST_CODE = 1;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<HistoryModel> historyModelList;
    private HistoryAdapter historyAdapter;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ImageButton btnHome = findViewById(R.id.btn_home);
        ImageButton btnKontak = findViewById(R.id.btn_kontakkami);
        ImageButton btnSearch = findViewById(R.id.btn_artist);
        ImageButton btnProfile = findViewById(R.id.btn_profile);

        toolbar = findViewById(R.id.history_toolbar);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryActivity.this, MainActivity.class));
            }
        });
        btnKontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryActivity.this, ContactUsActivity.class));
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryActivity.this, ShowAllActivity.class));
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryActivity.this, ProfileActivity.class));
            }
        });

        recyclerView = findViewById(R.id.history_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyModelList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(this, historyModelList);
        recyclerView.setAdapter(historyAdapter);

        firestore.collection("Pesanan").document(auth.getCurrentUser().getUid())
                .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                HistoryModel historyModel = doc.toObject(HistoryModel.class);
                                historyModel.setDocumentId(doc.getId());
                                historyModelList.add(historyModel);
                            }
                            historyAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Mendapatkan status pembayaran yang diperbarui dari Intent
                String updatedStatus = data.getStringExtra("UPDATED_STATUS");
                if (updatedStatus != null && updatedStatus.equals("Sudah Dibayar")) {
                    // Menampilkan pesan bahwa pembayaran berhasil
                    Toast.makeText(this, "Pembayaran berhasil", Toast.LENGTH_SHORT).show();
                    // Memperbarui tampilan status di RecyclerView
                    int position = data.getIntExtra("POSITION", -1);
                    if (position != -1) {
                        historyModelList.get(position).setStatus(updatedStatus);
                        historyAdapter.notifyItemChanged(position);
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Menampilkan pesan bahwa pembayaran dibatalkan
                Toast.makeText(this, "Pembayaran dibatalkan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
