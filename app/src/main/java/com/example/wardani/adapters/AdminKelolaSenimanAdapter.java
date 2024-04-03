package com.example.wardani.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wardani.R;
import com.example.wardani.activities.DetailSenimanActivity;
import com.example.wardani.models.AdminKelolaSenimanModel;

import java.util.List;

public class AdminKelolaSenimanAdapter extends RecyclerView.Adapter<AdminKelolaSenimanAdapter.ViewHolder> {

    private Context context;
    private List<AdminKelolaSenimanModel> adminKelolaSenimanModelList;

    public AdminKelolaSenimanAdapter(Context context, List<AdminKelolaSenimanModel> adminKelolaSenimanModelList) {
        this.context = context;
        this.adminKelolaSenimanModelList = adminKelolaSenimanModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kelola_seniman_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminKelolaSenimanModel item = adminKelolaSenimanModelList.get(position);
        holder.sNama.setText(item.getNama_dalang());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailSenimanActivity.class);
                // Kirim data ke activity DetailSenimanActivity
                // intent.putExtra("detail", item);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return adminKelolaSenimanModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sNama;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sNama = itemView.findViewById(R.id.admin_nama_seniman);
        }
    }
}
