package com.example.wardani.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.wardani.R;
import com.example.wardani.activities.ShowAllActivity;
import com.example.wardani.adapters.BeritaAdapter;
import com.example.wardani.adapters.SenimanAdapter;
import com.example.wardani.models.BeritaModel;
import com.example.wardani.models.SenimanModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    TextView senimanShowAll;
    LinearLayout linearLayout;
    ProgressDialog progressDialog;
    RecyclerView senimanRecyclerview, beritaRecyclerview;

    //Seniman Recyclerview
    SenimanAdapter senimanAdapter;
    List<SenimanModel> senimanModelList;

    //Berita Recyclerview
    BeritaAdapter beritaAdapter;
    List<BeritaModel> beritaModelList;

    //Firestore
    FirebaseFirestore db;
    SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(getActivity());
        senimanRecyclerview = root.findViewById(R.id.seniman_rec);
        beritaRecyclerview = root.findViewById(R.id.berita_rec);
        senimanShowAll = root.findViewById(R.id.seniman_see_all);

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh data
                fetchData();
            }
        });

        senimanShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                startActivity(intent);
            }
        });

        linearLayout = root.findViewById(R.id.home_layout);
        linearLayout.setVisibility(View.GONE);

        //ImageSlider
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.banner1, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner2, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner3, ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels);

        progressDialog.setTitle("Selamat Datang di Wardani App");
        progressDialog.setMessage("Tunggu sebentar...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Seniman
        senimanRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        senimanModelList = new ArrayList<>();
        senimanAdapter = new SenimanAdapter(getContext(), senimanModelList);
        senimanRecyclerview.setAdapter(senimanAdapter);

        //Berita
        beritaRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        beritaModelList = new ArrayList<>();
        beritaAdapter = new BeritaAdapter(getContext(), beritaModelList);
        beritaRecyclerview.setAdapter(beritaAdapter);

        // Fetch data
        fetchData();

        return root;
    }

    private void fetchData() {
        // Clear previous data
        senimanModelList.clear();
        beritaModelList.clear();

        // Fetch new data
        db.collection("ShowAll")
                .whereEqualTo("tampilkan", true) // Hanya ambil data dengan status switch true
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SenimanModel senimanModel = document.toObject(SenimanModel.class);
                                senimanModelList.add(senimanModel);
                            }
                            senimanAdapter.notifyDataSetChanged();
                            linearLayout.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        // Stop refreshing
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        db.collection("Berita")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BeritaModel beritaModel = document.toObject(BeritaModel.class);
                                beritaModelList.add(beritaModel);
                            }
                            beritaAdapter.notifyDataSetChanged();
                            linearLayout.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        // Stop refreshing
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }
}