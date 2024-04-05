package com.example.wardani.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wardani.R;
import com.example.wardani.models.AdminKelolaPesananModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AdminKelolaPesananAdapter extends RecyclerView.Adapter<AdminKelolaPesananAdapter.PesananViewHolder> {
    private Context context;
    private List<AdminKelolaPesananModel> pesananList;
    private List<AdminKelolaPesananModel> filteredPesananList;

    public AdminKelolaPesananAdapter(Context context, List<AdminKelolaPesananModel> pesananList, SwipeRefreshLayout swipeRefreshLayout) {
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
        AdminKelolaPesananModel pesanan = filteredPesananList.get(position);

        // Set data to views
        holder.adminKelolaOrder.setText(pesanan.getOrder());
        holder.adminKelolaNama.setText(pesanan.getNama());
        holder.adminKelolaCustomer.setText(pesanan.getCustomer());
        holder.adminKelolaTanggal.setText(pesanan.getTanggal());
        holder.adminKelolaWaktu.setText(pesanan.getWaktu());
        holder.adminKelolaDetail.setText(pesanan.getDetail());
        holder.adminKelolaHarga.setText(pesanan.getHarga());
    }

    @Override
    public int getItemCount() {
        return filteredPesananList.size();
    }

    public static class PesananViewHolder extends RecyclerView.ViewHolder {
        TextView adminKelolaOrder, adminKelolaNama, adminKelolaCustomer, adminKelolaTanggal, adminKelolaWaktu, adminKelolaDetail, adminKelolaHarga;

        public PesananViewHolder(@NonNull View itemView) {
            super(itemView);
            adminKelolaOrder = itemView.findViewById(R.id.admin_kelola_order);
            adminKelolaNama = itemView.findViewById(R.id.admin_kelola_nama);
            adminKelolaCustomer = itemView.findViewById(R.id.admin_kelola_customer);
            adminKelolaTanggal = itemView.findViewById(R.id.admin_kelola_tanggal);
            adminKelolaWaktu = itemView.findViewById(R.id.admin_kelola_waktu);
            adminKelolaDetail = itemView.findViewById(R.id.admin_kelola_detail);
            adminKelolaHarga = itemView.findViewById(R.id.admin_kelola_harga);
        }
    }

    // Method to filter data based on the filter option (terbaru/terlama)
    public void filterData(String filterOption) {
        filteredPesananList.clear();
        filteredPesananList.addAll(pesananList); // Reset filtered list to original list

        // Sorting the filtered list based on order date
        Collections.sort(filteredPesananList, new Comparator<AdminKelolaPesananModel>() {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(AdminKelolaPesananModel o1, AdminKelolaPesananModel o2) {
                try {
                    Date orderDate1 = dateFormat.parse(o1.getOrder());
                    Date orderDate2 = dateFormat.parse(o2.getOrder());
                    if (filterOption.equals("terbaru")) {
                        return orderDate2.compareTo(orderDate1);
                    } else { // For "terlama"
                        return orderDate1.compareTo(orderDate2);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        notifyDataSetChanged(); // Notify adapter about the changes
    }
}