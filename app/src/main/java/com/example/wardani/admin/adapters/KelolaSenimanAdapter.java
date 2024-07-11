package com.example.wardani.admin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wardani.R;
import com.example.wardani.admin.models.KelolaSenimanModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KelolaSenimanAdapter extends RecyclerView.Adapter<KelolaSenimanAdapter.ViewHolder> {

    private Context context;
    private List<KelolaSenimanModel> kelolaSenimanModelList;
    private List<KelolaSenimanModel> filteredSenimanList;
    private SharedPreferences sharedPreferences;
    public static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    public KelolaSenimanAdapter(Context context, List<KelolaSenimanModel> kelolaSenimanModelList) {
        this.context = context;
        this.kelolaSenimanModelList = kelolaSenimanModelList;
        this.filteredSenimanList = new ArrayList<>(kelolaSenimanModelList);
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
        KelolaSenimanModel item = filteredSenimanList.get(position);
        holder.sNama.setText(item.getNama_dalang());

        holder.aSwitch.setChecked(sharedPreferences.getBoolean(item.getId(), true));

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilkanDialogEdit(item);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilkanKonfirmasiHapus(item);
            }
        });

        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                tampilkanKonfirmasiSwitch(item, isChecked);
            }
        });
    }

    private void tampilkanDialogEdit(KelolaSenimanModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.popup_edit_seniman, null);

        EditText pp2NamaSeniman = view.findViewById(R.id.pp2_nama_dalang);
        EditText pp2HargaSeniman = view.findViewById(R.id.pp2_harga_jasa);
        EditText pp2Deskripsi = view.findViewById(R.id.pp2_deskripsi);
        Button btnPilihFoto = view.findViewById(R.id.btn2_pilih_foto);
        Button btnSave = view.findViewById(R.id.btn_simpan);
        ImageView ivGambar = view.findViewById(R.id.iv2_gambar);

        Glide.with(context)
                .load(item.getImg_url())
                .into(ivGambar);

        pp2NamaSeniman.setText(item.getNama_dalang());
        pp2HargaSeniman.setText(String.valueOf(item.getHarga_jasa()));
        pp2Deskripsi.setText(item.getDeskripsi());

        builder.setView(view);
        AlertDialog dialog = builder.create();

        btnPilihFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ((Activity) context).startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanKonfirmasiSimpan(item, dialog, pp2NamaSeniman.getText().toString(),
                        Integer.parseInt(pp2HargaSeniman.getText().toString()), pp2Deskripsi.getText().toString());
            }
        });

        dialog.show();
    }

    private void tampilkanKonfirmasiSimpan(KelolaSenimanModel item, AlertDialog dialog, String nama_dalang, int harga_jasa, String deskripsi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah Anda yakin ingin menyimpan perubahan?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Perbarui data ke Firestore setelah diedit
                perbaruiDataDiFirestore(item.getId(), nama_dalang, harga_jasa, deskripsi);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    private void perbaruiDataDiFirestore(String documentId, String nama_dalang, int harga_jasa, String deskripsi) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
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

    private void updateDataDiFirestore(KelolaSenimanModel item, boolean isChecked) {
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

    private void hapusDataDariFirestore(String namaDalang) {
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

    private void tampilkanKonfirmasiHapus(KelolaSenimanModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah Anda yakin ingin menghapus data seniman ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hapusDataDariFirestore(item.getNama_dalang());
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    private void tampilkanKonfirmasiSwitch(KelolaSenimanModel item, boolean isChecked) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String message = isChecked ? "Apakah Anda ingin menampilkan seniman ini?" : "Apakah Anda ingin menyembunyikan seniman ini?";
        builder.setMessage(message);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(item.getId(), isChecked);
                editor.apply();
                updateDataDiFirestore(item, isChecked);
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return filteredSenimanList.size();
    }

    public void filterList(List<KelolaSenimanModel> filteredList) {
        filteredSenimanList = filteredList;
        notifyDataSetChanged();
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
