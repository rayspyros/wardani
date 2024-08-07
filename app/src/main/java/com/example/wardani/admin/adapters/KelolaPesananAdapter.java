package com.example.wardani.admin.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wardani.R;
import com.example.wardani.admin.models.KelolaPesananModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class KelolaPesananAdapter extends RecyclerView.Adapter<KelolaPesananAdapter.PesananViewHolder> {
    private Context context;
    private List<KelolaPesananModel> pesananList;
    private List<KelolaPesananModel> filteredPesananList;

    public KelolaPesananAdapter(Context context, List<KelolaPesananModel> pesananList, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.pesananList = pesananList;
        this.filteredPesananList = new ArrayList<>(pesananList);
    }

    @NonNull
    @Override
    public PesananViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.kelola_pesanan_item, parent, false);
        return new PesananViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesananViewHolder holder, int position) {
        KelolaPesananModel pesanan = filteredPesananList.get(position);

        holder.adminKelolaOrder.setText(pesanan.getOrder());
        holder.adminKelolaNama.setText(pesanan.getNama());
        holder.adminKelolaCustomer.setText(pesanan.getCustomer());
        holder.adminKelolaTanggal.setText(pesanan.getTanggal());
        holder.adminKelolaWaktu.setText(pesanan.getWaktu());
        holder.adminKelolaDetail.setText(pesanan.getDetail());
        holder.adminKelolaHarga.setText(pesanan.getHarga());
        holder.adminKelolaTglOrder.setText(pesanan.getTglOrder());
        holder.adminKelolaStatus.setText(pesanan.getStatus());

        holder.btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Konfirmasi Hapus");
                builder.setMessage("Apakah Anda yakin ingin menghapus pesanan ini?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hapusPesanan(pesanan.getId());
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Batal menghapus, tutup dialog
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.btnSelesai.setVisibility(View.VISIBLE);
        holder.btnSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Konfirmasi Selesai");
                builder.setMessage("Untuk menandai pesanan sebagai selesai, masukkan kata 'KONFIRMASI' dan klik Kirim.");

                final EditText input = new EditText(context);
                builder.setView(input);

                builder.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String konfirmasi = input.getText().toString().trim();
                        if (konfirmasi.equalsIgnoreCase("KONFIRMASI")) {
                            tandaiSelesai(pesanan.getId());
                        } else {
                            Toast.makeText(context, "Konfirmasi tidak valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        if (pesanan.getStatus().equalsIgnoreCase("Sudah Selesai")) {
            holder.btnSelesai.setVisibility(View.GONE);
        } else {
            holder.btnSelesai.setVisibility(View.VISIBLE);
        }
    }

    private void hapusPesanan(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Riwayat").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        removeItem(id);
                        Toast.makeText(context, "Pesanan berhasil dihapus", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Gagal menghapus pesanan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void tandaiSelesai(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Riwayat").document(id)
                .update("status", "Sudah Selesai")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        notifyDataSetChanged();
                        Toast.makeText(context, "Pesanan berhasil ditandai sebagai selesai", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Gagal menandai pesanan sebagai selesai: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeItem(String id) {
        for (int i = 0; i < filteredPesananList.size(); i++) {
            if (filteredPesananList.get(i).getId().equals(id)) {
                filteredPesananList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return filteredPesananList.size();
    }

    public static class PesananViewHolder extends RecyclerView.ViewHolder {
        TextView adminKelolaOrder, adminKelolaNama, adminKelolaCustomer, adminKelolaTanggal, adminKelolaWaktu, adminKelolaDetail, adminKelolaHarga, adminKelolaTglOrder, adminKelolaStatus;
        Button btnHapus, btnSelesai;

        public PesananViewHolder(@NonNull View itemView) {
            super(itemView);
            adminKelolaOrder = itemView.findViewById(R.id.admin_kelola_order);
            adminKelolaNama = itemView.findViewById(R.id.admin_kelola_nama);
            adminKelolaCustomer = itemView.findViewById(R.id.admin_kelola_customer);
            adminKelolaTanggal = itemView.findViewById(R.id.admin_kelola_tanggal);
            adminKelolaWaktu = itemView.findViewById(R.id.admin_kelola_waktu);
            adminKelolaDetail = itemView.findViewById(R.id.admin_kelola_detail);
            adminKelolaHarga = itemView.findViewById(R.id.admin_kelola_harga);
            adminKelolaTglOrder = itemView.findViewById(R.id.admin_tgl_order);
            adminKelolaStatus = itemView.findViewById(R.id.admin_kelola_status);
            btnHapus = itemView.findViewById(R.id.btn_hapus_pesanan);
            btnSelesai = itemView.findViewById(R.id.btn_selesai_pesanan);
        }
    }

    public void filterList(List<KelolaPesananModel> filteredList) {
        filteredPesananList = filteredList;
        notifyDataSetChanged();
    }
}
