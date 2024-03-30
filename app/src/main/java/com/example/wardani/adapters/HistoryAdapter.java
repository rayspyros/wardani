package com.example.wardani.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wardani.R;
import com.example.wardani.activities.PaymentActivity;
import com.example.wardani.models.HistoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context context;
    private List<HistoryModel> historyModelList;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    public HistoryAdapter(Context context, List<HistoryModel> historyModelList) {
        this.context = context;
        this.historyModelList = historyModelList;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryModel historyModel = historyModelList.get(position);

        holder.nama.setText(historyModel.getNamaSeniman());
        holder.tanggal.setText(historyModel.getTanggal());
        holder.waktu.setText(historyModel.getStartTime() + " - " + historyModel.getEndTime());
        holder.detail.setText(historyModel.getNama() + ", " + historyModel.getJalan() + ", " +
                historyModel.getKota() + ", " + historyModel.getProvinsi() + ", " + historyModel.getKodepos());
        holder.harga.setText(String.valueOf(historyModel.getHargaSeniman()));

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelConfirmationDialog(position, historyModel.getDocumentId());
            }
        });

        holder.bayarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Panggil method untuk memulai PaymentActivity dan teruskan data yang diperlukan
                goToPaymentActivity(historyModel.getNamaSeniman(), historyModel.getTanggal(),
                        historyModel.getStartTime() + " - " + historyModel.getEndTime(),
                        historyModel.getNama() + ", " + historyModel.getJalan() + ", " +
                                historyModel.getKota() + ", " + historyModel.getProvinsi() + ", " + historyModel.getKodepos(),
                        String.valueOf(historyModel.getHargaSeniman()));
            }
        });
    }

    private void goToPaymentActivity(String nama, String tanggal, String waktu, String detail, String harga) {
        // Buat intent untuk memulai PaymentActivity
        Intent intent = new Intent(context, PaymentActivity.class);

        // Meneruskan data melalui intent menggunakan putExtra()
        intent.putExtra("NAMA", nama);
        intent.putExtra("TANGGAL", tanggal);
        intent.putExtra("WAKTU", waktu);
        intent.putExtra("DETAIL", detail);
        intent.putExtra("HARGA", harga);

        // Mulai aktivitas baru
        context.startActivity(intent);
    }

    private void showCancelConfirmationDialog(int position, String documentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Konfirmasi Pembatalan");
        builder.setMessage("Anda yakin ingin membatalkan pemesanan ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelPesanan(position, documentId);
                Toast.makeText(context, "Pemesanan dibatalkan", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Tidak", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cancelPesanan(int position, String documentId) {
        firestore.collection("Pesanan")
                .document(auth.getCurrentUser().getUid())
                .collection("User")
                .document(documentId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        historyModelList.remove(position);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return historyModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, tanggal, waktu, detail, harga;
        Button cancelBtn, bayarBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.history_nama);
            tanggal = itemView.findViewById(R.id.history_tanggal);
            waktu = itemView.findViewById(R.id.history_waktu);
            detail = itemView.findViewById(R.id.history_detail);
            harga = itemView.findViewById(R.id.history_harga);
            cancelBtn = itemView.findViewById(R.id.btn_batal);
            bayarBtn = itemView.findViewById(R.id.btn_bayar);
        }
    }
}