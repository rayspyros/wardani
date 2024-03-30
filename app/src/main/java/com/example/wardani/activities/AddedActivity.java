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
import com.example.wardani.adapters.AddedAdapter;
import com.example.wardani.models.AddedModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddedActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    List<AddedModel> addedModelList;
    AddedAdapter addedAdapter;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.added_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.added_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addedModelList = new ArrayList<>();
        addedAdapter = new AddedAdapter(this, addedModelList);
        recyclerView.setAdapter(addedAdapter);


        firestore.collection("Simpan").document(auth.getCurrentUser().getUid())
                .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                AddedModel addedModel = doc.toObject(AddedModel.class);
                                addedModel.setDocumentId(doc.getId());
                                addedModelList.add(addedModel);
                                addedAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
}