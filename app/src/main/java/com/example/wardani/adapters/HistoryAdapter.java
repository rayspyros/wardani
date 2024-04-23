package com.example.wardani.adapters;

import static com.example.wardani.activities.HistoryActivity.PAYMENT_REQUEST_CODE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        holder.customer.setText(historyModel.getNama());
        holder.nama.setText(historyModel.getNamaSeniman());
        holder.tanggal.setText(historyModel.getTanggal());
        holder.waktu.setText(historyModel.getStartTime() + " - " + historyModel.getEndTime());
        holder.detail.setText(historyModel.getJalan() + ", " + historyModel.getKota() + ", " + historyModel.getProvinsi() + ", " + historyModel.getKodepos());
        holder.harga.setText("Rp." + historyModel.getHargaSeniman());
        holder.status.setText(historyModel.getStatus());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd "+"/"+" HH:mm:ss", Locale.getDefault());
        Timestamp timestamp = historyModel.getTimeOrder();
        Date date = timestamp.toDate();
        String formattedDate = dateFormat.format(date);
        holder.order.setText(formattedDate);

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelConfirmationDialog(position, historyModel.getDocumentId());
            }
        });

        holder.bayarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!historyModel.isPaymentConfirmed()) {
                    showPaymentConfirmationDialog(historyModel.getNamaSeniman(), historyModel.getNama(), historyModel.getTanggal(),
                            historyModel.getStartTime() + " - " + historyModel.getEndTime(),
                            historyModel.getJalan() + ", " + historyModel.getKota() + ", " + historyModel.getProvinsi() + ", " + historyModel.getKodepos(),
                            String.valueOf(historyModel.getHargaSeniman()), holder.order.getText().toString(), historyModel, position);
                } else {
                    // Start PaymentActivity for result
                    Intent intent = new Intent(context, PaymentActivity.class);
                    intent.putExtra("ID_ITEM", historyModel.getDocumentId());
                    intent.putExtra("CUSTOMER", historyModel.getNama());
                    intent.putExtra("NAMA", historyModel.getNamaSeniman());
                    intent.putExtra("TANGGAL", historyModel.getTanggal());
                    intent.putExtra("WAKTU", historyModel.getStartTime() + " - " + historyModel.getEndTime());
                    intent.putExtra("DETAIL", historyModel.getJalan() + ", " + historyModel.getKota() + ", " + historyModel.getProvinsi() + ", " + historyModel.getKodepos());
                    intent.putExtra("HARGA", String.valueOf(historyModel.getHargaSeniman()));
                    intent.putExtra("ORDER", holder.order.getText().toString());
                    intent.putExtra("POSITION", position);
                    ((Activity) context).startActivityForResult(intent, PAYMENT_REQUEST_CODE);
                }
            }
        });
    }

    private void goToPaymentActivity(String nama, String customer, String tanggal, String waktu, String detail, String harga, String order, String status, int position) {
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra("CUSTOMER", customer);
        intent.putExtra("NAMA", nama);
        intent.putExtra("TANGGAL", tanggal);
        intent.putExtra("WAKTU", waktu);
        intent.putExtra("DETAIL", detail);
        intent.putExtra("HARGA", harga);
        intent.putExtra("ORDER", order);
        intent.putExtra("STATUS", status); // Tambahkan status ke dalam intent
        intent.putExtra("POSITION", position); // Pass position to PaymentActivity
        context.startActivity(intent);
    }


    private void showPaymentConfirmationDialog(String nama, String customer, String tanggal, String waktu, String detail, String harga, String order, HistoryModel historyModel, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Konfirmasi Pembayaran");
        builder.setMessage("Anda yakin ingin membayar pesanan ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                historyModel.setPaymentConfirmed(true);
                notifyDataSetChanged();
                goToPaymentActivity(nama, customer, tanggal, waktu, detail, harga, order, historyModel.getStatus(), position); // Tambahkan status
            }
        });
        builder.setNegativeButton("Tidak", null);

        AlertDialog dialog = builder.create();
        dialog.show();
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
        TextView status, customer, nama, tanggal, waktu, detail, harga, order;
        Button cancelBtn, bayarBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.history_status);
            customer = itemView.findViewById(R.id.history_customer);
            nama = itemView.findViewById(R.id.history_nama);
            tanggal = itemView.findViewById(R.id.history_tanggal);
            waktu = itemView.findViewById(R.id.history_waktu);
            detail = itemView.findViewById(R.id.history_detail);
            harga = itemView.findViewById(R.id.history_harga);
            order = itemView.findViewById(R.id.history_order);
            cancelBtn = itemView.findViewById(R.id.btn_batal);
            bayarBtn = itemView.findViewById(R.id.btn_bayar);
        }
    }
}
