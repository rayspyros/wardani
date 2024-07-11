package com.example.wardani.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wardani.R;
import com.example.wardani.activities.DetailSenimanActivity;
import com.example.wardani.models.AddedModel;
import com.example.wardani.models.SenimanModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AddedAdapter extends RecyclerView.Adapter<AddedAdapter.ViewHolder> {

    Context context;
    List<AddedModel> list;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private void getDetailSeniman(String namaSeniman) {
        firestore.collection("Seniman")
                .whereEqualTo("nama_dalang", namaSeniman)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        SenimanModel senimanModel = documentSnapshot.toObject(SenimanModel.class);
                        if (senimanModel != null) {
                            Intent intent = new Intent(context, DetailSenimanActivity.class);
                            intent.putExtra("detail", senimanModel);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Detail seniman tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Gagal mengambil detail seniman", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public AddedAdapter(Context context, List<AddedModel> list) {
        this.context = context;
        this.list = list;

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AddedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_added_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddedModel addedModel = list.get(position);

        holder.nama.setText(addedModel.getSenimanNama());

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusItem(position, addedModel.getDocumentId());
                Toast.makeText(context, "Berhasil dihapus!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.lihatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namaSeniman = addedModel.getSenimanNama();
                getDetailSeniman(namaSeniman);
            }
        });
    }

    private void hapusItem(int position, String documentId) {
        firestore.collection("Simpan")
                .document(auth.getCurrentUser().getUid())
                .collection("User")
                .document(documentId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.remove(position);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, harga;
        Button deleteBtn, lihatBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.added_nama);
            deleteBtn = itemView.findViewById(R.id.btn_hapus);
            lihatBtn = itemView.findViewById(R.id.btn_lihat);
        }
    }
}
