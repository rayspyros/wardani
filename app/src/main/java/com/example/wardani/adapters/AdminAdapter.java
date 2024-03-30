package com.example.wardani.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wardani.R;
import com.example.wardani.models.AdminModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder> {
    private Context context;
    private List<AdminModel> adminModelList;

    public AdminAdapter(Context context, List<AdminModel> adminModelList) {
        this.context = context;
        this.adminModelList = adminModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pesanan_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminModel adminModel = adminModelList.get(position);
        holder.bind(adminModel);
    }

    @Override
    public int getItemCount() {
        return adminModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView namaSenimanTextView, hargaSenimanTextView, namaTextView,
                tanggalTextView, waktuTextView, detailTextView, hargaTextView;
        private Button batalkanButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namaSenimanTextView = itemView.findViewById(R.id.pesanan_nama);
            hargaSenimanTextView = itemView.findViewById(R.id.pesanan_order);
            namaTextView = itemView.findViewById(R.id.pesanan_nama);
            tanggalTextView = itemView.findViewById(R.id.pesanan_tanggal);
            waktuTextView = itemView.findViewById(R.id.pesanan_waktu);
            detailTextView = itemView.findViewById(R.id.pesanan_detail);
            hargaTextView = itemView.findViewById(R.id.pesanan_harga);
            batalkanButton = itemView.findViewById(R.id.btn_batalkan);
        }

        public void bind(AdminModel adminModel) {
            namaSenimanTextView.setText(adminModel.getNamaSeniman());
            hargaSenimanTextView.setText(adminModel.getHargaSeniman());
            namaTextView.setText(adminModel.getNama());
            tanggalTextView.setText(adminModel.getTanggal());
            waktuTextView.setText(adminModel.getStartTime() + " - " + adminModel.getEndTime());
            detailTextView.setText(adminModel.getNama() + ", " + adminModel.getJalan() + ", " +
                    adminModel.getKota() + ", " + adminModel.getProvinsi() + ", " + adminModel.getKodepos());
            hargaTextView.setText(adminModel.getHargaSeniman());

            batalkanButton.setOnClickListener(v -> {
            });
        }
    }
}

