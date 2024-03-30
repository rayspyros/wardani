package com.example.wardani.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.wardani.R;
import com.example.wardani.adapters.AddressAdapter;
import com.example.wardani.models.AddressModel;
import com.example.wardani.models.SenimanModel;
import com.example.wardani.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectedAlamat{

    Button addAddress;
    RecyclerView recyclerView;
    private List<AddressModel> addressModelList;
    private AddressAdapter addressAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    Button tambahAlamatBtn,bayarBtn;
    Toolbar toolbar;
    String mAlamat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        toolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //dapat data dari Detail Activity
        Object obj = getIntent().getSerializableExtra("jasa");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        recyclerView =findViewById(R.id.address_recycler);
        bayarBtn =findViewById(R.id.payment_btn);
        addAddress = findViewById(R.id.add_address_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addressModelList = new ArrayList<>();
        addressAdapter = new AddressAdapter(getApplicationContext(),addressModelList,this);
        recyclerView.setAdapter(addressAdapter);

        firestore.collection("UserSekarang").document(auth.getCurrentUser().getUid())
                        .collection("Alamat").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                AddressModel addressModel = doc.toObject(AddressModel.class);
                                addressModelList.add(addressModel);
                                addressAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddressActivity.this, OrderActivity.class));
            }
        });

        bayarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int jumlah = 0;
                if (obj instanceof SenimanModel){
                    SenimanModel senimanModel = (SenimanModel) obj;
                    jumlah = senimanModel.getHarga_jasa();
                }
                if (obj instanceof ShowAllModel){
                    ShowAllModel showAllModel = (ShowAllModel) obj;
                    jumlah = showAllModel.getHarga_jasa();
                }
                Intent intent = new Intent(AddressActivity.this, PaymentActivity.class);
                intent.putExtra("jumlah", jumlah);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setAlamat(String alamat) {
        mAlamat = alamat;
    }
}