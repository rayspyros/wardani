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
import com.example.wardani.models.SenimanModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SenimanAdapter extends RecyclerView.Adapter<SenimanAdapter.ViewHolder> {

    private Context context;
    private List<SenimanModel> list;

    public SenimanAdapter(Context context, List<SenimanModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SenimanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.seniman,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SenimanModel seniman = list.get(position);

        // Tampilkan hanya jika status switch true
        if (seniman.getTampilkan()) {
            Glide.with(context).load(seniman.getImg_url()).into(holder.newImg);
            holder.newNama.setText(seniman.getNama_dalang());

            // Format angka menjadi "1.000.000"
            NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
            String formattedHarga = formatter.format(seniman.getHarga_jasa());
            holder.newHarga.setText(formattedHarga);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailSenimanActivity.class);
                    intent.putExtra("detail", seniman);
                    context.startActivity(intent);
                }
            });
        } else {
            // Sembunyikan item jika status switch false
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView newImg;
        TextView newNama, newHarga;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newImg = itemView.findViewById(R.id.new_img);
            newNama = itemView.findViewById(R.id.nama_seniman);
            newHarga = itemView.findViewById(R.id.harga);
        }
    }
}
