package com.example.wardani.admin.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import com.example.wardani.admin.activites.KelolaBeritaActivity;
import com.example.wardani.admin.models.KelolaBeritaModel;
import com.example.wardani.admin.models.KelolaSenimanModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class KelolaBeritaAdapter extends RecyclerView.Adapter<KelolaBeritaAdapter.ViewHolder>{

    private Context context;
    private List<KelolaBeritaModel> kelolaBeritaModelList;
    private static List<KelolaBeritaModel> filteredBeritaList;
    private SharedPreferences sharedPreferences;
    public static final int PICK_IMAGE_REQUEST = 1;

    public KelolaBeritaAdapter(Context context, List<KelolaBeritaModel> kelolaBeritaModelList) {
        this.context = context;
        this.kelolaBeritaModelList = kelolaBeritaModelList;
        this.filteredBeritaList = new ArrayList<>(kelolaBeritaModelList);
    }
    @NonNull
    @Override
    public KelolaBeritaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kelola_berita_item, parent, false);
        return new KelolaBeritaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KelolaBeritaAdapter.ViewHolder holder, int position) {
        KelolaBeritaModel item = filteredBeritaList.get(position);
        holder.sJudul.setText(item.getJudul());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilkanDialogEdit(item);
            }
        });


        // Set onClickListener untuk tombol delete
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilkanKonfirmasiHapus(item);
            }
        });
    }

    private void tampilkanDialogEdit(KelolaBeritaModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.popup_edit_berita, null);

        EditText pp2JudulBerita = view.findViewById(R.id.pp2_judul_berita);
        TextView pp2WaktuUnggah = view.findViewById(R.id.pp2_waktu_upload);
        EditText pp2DeskripsiBerita = view.findViewById(R.id.pp2_deskripsi_berita);
        Button btnPilihFoto = view.findViewById(R.id.btn2_pilih_foto);
        Button btnSave = view.findViewById(R.id.btn_simpan);
        ImageView ivGambar = view.findViewById(R.id.iv2_gambar);

        // Mengambil URL gambar dari Firestore dan menampilkannya menggunakan Glide
        Glide.with(context)
                .load(item.getImg_url())
                .into(ivGambar);

        pp2JudulBerita.setText(item.getJudul());
        pp2WaktuUnggah.setText(String.valueOf(item.getWaktu()));
        pp2DeskripsiBerita.setText(item.getDeskripsi());

        builder.setView(view);
        AlertDialog dialog = builder.create();

        pp2WaktuUnggah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        R.style.DatePickerTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Format the selected date
                                SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));
                                calendar.set(year, monthOfYear, dayOfMonth);
                                pp2WaktuUnggah.setText(sdf.format(calendar.getTime()));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

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
                tampilkanKonfirmasiSimpan(item, dialog, pp2JudulBerita.getText().toString(), pp2WaktuUnggah.getText().toString(), pp2DeskripsiBerita.getText().toString());
            }
        });

        dialog.show();
    }


    private void tampilkanKonfirmasiSimpan(KelolaBeritaModel item, AlertDialog dialog, String judul, String waktu, String deskripsi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah Anda yakin ingin menyimpan perubahan?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Perbarui data ke Firestore setelah diedit
                perbaruiDataDiFirestore(item.getId(), judul, waktu, deskripsi);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    private void perbaruiDataDiFirestore(String documentId, String judul, String waktu, String deskripsi) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("judul", judul);
        data.put("waktu", waktu);
        data.put("deskripsi", deskripsi);

        if (documentId != null) {
            db.collection("Berita").document(documentId) // Menggunakan documentId yang sesuai
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

    private void updateDataDiFirestore(KelolaBeritaModel item, boolean isChecked) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Berita").document(item.getId())
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

    private void hapusDataDariFirestore(String judul) {
        // Akses Firestore dan hapus data dengan nama dalang yang sesuai
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Berita")
                .whereEqualTo("judul", judul)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Iterate through the result documents
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Get the document ID
                            String documentId = documentSnapshot.getId();
                            // Delete the document
                            db.collection("Berita").document(documentId)
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

    private void tampilkanKonfirmasiHapus(KelolaBeritaModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah Anda yakin ingin menghapus data berita ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Panggil method hapusDataDariFirestore untuk menghapus data dari Firestore
                hapusDataDariFirestore(item.getJudul());
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return filteredBeritaList.size();
    }

    public static void filterList(List<KelolaBeritaModel> filteredList) {
        filteredBeritaList = filteredList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sJudul;
        private Button btnDelete, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sJudul = itemView.findViewById(R.id.admin_judul_berita);
            btnDelete = itemView.findViewById(R.id.admin_btn_delete);
            btnEdit = itemView.findViewById(R.id.admin_btn_edit);
        }
    }
}
