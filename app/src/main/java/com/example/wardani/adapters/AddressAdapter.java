package com.example.wardani.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wardani.R;
import com.example.wardani.models.AddressModel;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    Context context;
    List<AddressModel> addressModelList;
    SelectedAlamat selectedAlamat;

    private RadioButton selectedRadioBtn;

    public AddressAdapter(Context context, List<AddressModel> addressModelList, SelectedAlamat selectedAlamat) {
        this.context = context;
        this.addressModelList = addressModelList;
        this.selectedAlamat = selectedAlamat;
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {
        holder.alamat.setText(addressModelList.get(position).getUserAlamat());
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(AddressModel alamat:addressModelList){
                    alamat.setSelected(false);
                }
                addressModelList.get(position).setSelected(true);

                if(selectedRadioBtn != null){
                    selectedRadioBtn.setChecked(false);
                }
                selectedRadioBtn = (RadioButton) view;
                selectedRadioBtn.setChecked(true);
                selectedAlamat.setAlamat(addressModelList.get(position).getUserAlamat());
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView alamat;
        RadioButton radioButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alamat = itemView.findViewById(R.id.address_add);
            radioButton = itemView.findViewById(R.id.select_address);
        }
    }
    public interface SelectedAlamat{
        void setAlamat(String alamat);
    }
}
