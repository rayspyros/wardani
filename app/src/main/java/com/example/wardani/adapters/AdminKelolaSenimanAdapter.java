package com.example.wardani.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wardani.R;
import com.example.wardani.activities.DetailSenimanActivity;
import com.example.wardani.models.AdminKelolaSenimanModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminKelolaSenimanAdapter extends RecyclerView.Adapter<AdminKelolaSenimanAdapter.ViewHolder> {

    private Context context;
    private List<AdminKelolaSenimanModel> adminKelolaSenimanModelList;
    private SharedPreferences sharedPreferences;

    public AdminKelolaSenimanAdapter(Context context, List<AdminKelolaSenimanModel> adminKelolaSenimanModelList) {
        this.context = context;
        this.adminKelolaSenimanModelList = adminKelolaSenimanModelList;
        this.sharedPreferences = context.getSharedPreferences("SwitchStatus", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kelola_seniman_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminKelolaSenimanModel item = adminKelolaSenimanModelList.get(position);
        holder.sNama.setText(item.getNama_dalang());

        // Set status switch berdasarkan nilai dari SharedPreferences
        holder.aSwitch.setChecked(sharedPreferences.getBoolean(item.getId(), false));

        // Set onClickListener untuk tombol edit
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilkanDialogEdit(item);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Panggil method untuk menghapus data dari Firestore
                hapusDataDariFirestore(item.getNama_dalang());
            }
        });

        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Simpan status switch ke SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(item.getId(), isChecked);
                editor.apply();

                // Update status switch di Firestore
                updateDataDiFirestore(item, isChecked);
            }
        });
    }

    private void updateDataDiFirestore(AdminKelolaSenimanModel item, boolean isChecked) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ShowAll").document(item.getId())
                .update("tampilkan", isChecked)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tampilkanDialogEdit(AdminKelolaSenimanModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.popup_edit_seniman, null);

        EditText pp2UrlGambar = view.findViewById(R.id.pp2_url_gambar);
        EditText pp2NamaSeniman = view.findViewById(R.id.pp2_nama_dalang);
        EditText pp2HargaSeniman = view.findViewById(R.id.pp2_harga_jasa);
        EditText pp2Deskripsi = view.findViewById(R.id.pp2_deskripsi);

        // Tampilkan data saat ini pada dialog edit
        pp2UrlGambar.setText(item.getImg_url());
        pp2NamaSeniman.setText(item.getNama_dalang());
        pp2HargaSeniman.setText(String.valueOf(item.getHarga_jasa()));
        pp2Deskripsi.setText(item.getDeskripsi());

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnSimpan = view.findViewById(R.id.btn_simpan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perbarui data ke Firestore setelah diedit
                String img_url = pp2UrlGambar.getText().toString();
                String nama_dalang = pp2NamaSeniman.getText().toString();
                int harga_jasa = Integer.parseInt(pp2HargaSeniman.getText().toString());
                String deskripsi = pp2Deskripsi.getText().toString();

                // Memperbarui data menggunakan ID dokumen yang sesuai
                perbaruiDataDiFirestore(item.getId(), img_url, nama_dalang, harga_jasa, deskripsi);
                dialog.dismiss();
            }
        });
    }

    private void perbaruiDataDiFirestore(String documentId, String img_url, String nama_dalang, int harga_jasa, String deskripsi) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("img_url", img_url);
        data.put("nama_dalang", nama_dalang);
        data.put("harga_jasa", harga_jasa);
        data.put("deskripsi", deskripsi);

        if (documentId != null) {
            db.collection("ShowAll").document(documentId) // Menggunakan documentId yang sesuai
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(context, "ID dokumen tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void hapusDataDariFirestore(String namaDalang) {
        // Akses Firestore dan hapus data dengan nama dalang yang sesuai
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ShowAll")
                .whereEqualTo("nama_dalang", namaDalang)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Iterate through the result documents
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Get the document ID
                            String documentId = documentSnapshot.getId();
                            // Delete the document
                            db.collection("ShowAll").document(documentId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Data berhasil dihapus
                                            Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Gagal menghapus data
                                            Toast.makeText(context, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gagal mengakses data
                        Toast.makeText(context, "Gagal mengakses data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return adminKelolaSenimanModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sNama;
        private Button btnDelete, btnEdit;
        private Switch aSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sNama = itemView.findViewById(R.id.admin_nama_seniman);
            btnDelete = itemView.findViewById(R.id.admin_btn_delete);
            btnEdit = itemView.findViewById(R.id.admin_btn_edit);
            aSwitch = itemView.findViewById(R.id.admin_switch);
        }
    }
}