package com.example.wardani.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wardani.R;
import com.example.wardani.admin.models.KelolaCustomerModel;
import com.example.wardani.admin.models.KelolaPesananModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

        // Set data to views
        holder.adminKelolaOrder.setText(pesanan.getOrder());
        holder.adminKelolaNama.setText(pesanan.getNama());
        holder.adminKelolaCustomer.setText(pesanan.getCustomer());
        holder.adminKelolaTanggal.setText(pesanan.getTanggal());
        holder.adminKelolaWaktu.setText(pesanan.getWaktu());
        holder.adminKelolaDetail.setText(pesanan.getDetail());
        holder.adminKelolaHarga.setText(pesanan.getHarga());
        holder.adminKelolaTglOrder.setText(pesanan.getTglOrder());
    }

    @Override
    public int getItemCount() {
        return filteredPesananList.size();
    }

    public static class PesananViewHolder extends RecyclerView.ViewHolder {
        TextView adminKelolaOrder, adminKelolaNama, adminKelolaCustomer, adminKelolaTanggal, adminKelolaWaktu, adminKelolaDetail, adminKelolaHarga, adminKelolaTglOrder;

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
        }
    }

    public void filterList(List<KelolaPesananModel> filteredList) {
        filteredPesananList = filteredList;
        notifyDataSetChanged();
    }
}