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
import com.example.wardani.models.ShowAllModel;

import java.util.ArrayList;
import java.util.List;

public class ShowAllAdapter extends RecyclerView.Adapter<ShowAllAdapter.ViewHolder> {

    private Context context;
    private List<ShowAllModel> showAllModelList;
    private List<ShowAllModel> filteredList;

    public ShowAllAdapter(Context context, List<ShowAllModel> showAllModelList) {
        this.context = context;
        this.showAllModelList = showAllModelList;
        this.filteredList = new ArrayList<>(showAllModelList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.show_all_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShowAllModel item = filteredList.get(position);
        Glide.with(context).load(item.getImg_url()).into(holder.sItemImage);
        holder.sHarga.setText("Rp "+ item.getHarga_jasa());
        holder.sNama.setText(item.getNama_dalang());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailSenimanActivity.class);
                intent.putExtra("detail", item);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView sItemImage;
        private TextView sHarga;
        private TextView sNama;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sItemImage = itemView.findViewById(R.id.item_image);
            sNama = itemView.findViewById(R.id.item_nama);
            sHarga = itemView.findViewById(R.id.item_harga);
        }
    }

    public void filterList(List<ShowAllModel> filteredList) {
        this.filteredList = filteredList;
        notifyDataSetChanged();
    }
}
