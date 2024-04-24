package com.example.wardani.admin.adapters;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wardani.R;
import com.example.wardani.admin.models.KelolaCustomerModel;
import java.util.List;

public class KelolaCustomerAdapter extends RecyclerView.Adapter<KelolaCustomerAdapter.CustomerViewHolder> {
    private Context context;
    private List<KelolaCustomerModel> customerList;

    public KelolaCustomerAdapter(Context context, List<KelolaCustomerModel> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.kelola_customer_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        KelolaCustomerModel customer = customerList.get(position);
        String alamatLengkap = customer.getAlamat() + ", " + customer.getKota() + ", " + customer.getProvinsi() + ", " + customer.getKodepos();
        holder.namaTextView.setText(customer.getNama());
        holder.emailTextView.setText(customer.getEmail());
        holder.alamatTextView.setText(alamatLengkap);
        holder.teleponTextView.setText(customer.getTelepon());

        // Tambahkan listener onClick untuk tombol "Chat"
        holder.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ambil nomor telepon dari model pelanggan
                String phoneNumber = customer.getTelepon();
                showConfirmationDialog(phoneNumber);
            }
        });

    }

    private void showConfirmationDialog(final String phoneNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Anda yakin ingin chat nomor " + phoneNumber + "?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Panggil metode untuk membuka tautan WhatsApp dengan nomor telepon yang sesuai
                openWhatsApp(phoneNumber);
            }
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void openWhatsApp(String phoneNumber) {
        // Periksa apakah nomor telepon dimulai dengan "0"
        if (phoneNumber.startsWith("0")) {
            // Ganti digit pertama "0" dengan "62"
            phoneNumber = "62" + phoneNumber.substring(1);
        }

        // Konstruksi tautan WhatsApp dengan nomor telepon
        String url = "https://wa.me/" + phoneNumber;

        // Buka tautan WhatsApp
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        public TextView namaTextView;
        public TextView emailTextView;
        public TextView alamatTextView;
        public TextView teleponTextView;
        public Button btnChat;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            namaTextView = itemView.findViewById(R.id.kelola_cust_nama);
            emailTextView = itemView.findViewById(R.id.kelola_cust_email);
            alamatTextView = itemView.findViewById(R.id.kelola_cust_alamat);
            teleponTextView = itemView.findViewById(R.id.kelola_cust_telepon);
            btnChat = itemView.findViewById(R.id.btn_chat_cust);
        }
    }
}

