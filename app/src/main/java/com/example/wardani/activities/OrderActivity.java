package com.example.wardani.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.wardani.BuildConfig.BASE_URL;
import static com.example.wardani.BuildConfig.CLIENT_KEY;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.wardani.BuildConfig;
import com.example.wardani.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.PaymentMethod;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;
import com.midtrans.sdk.uikit.api.model.BankType;
import com.midtrans.sdk.uikit.external.UiKitApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity
//        implements TransactionFinishedCallback
{

    EditText namaSeniman;
    EditText hargaSeniman;
    static EditText nama;
    EditText email;
    EditText jalan;
    EditText kota;
    EditText provinsi;
    EditText kode;
    EditText telepon;
    Toolbar toolbar;
    Button bayarBtn;
    private EditText tanggal;
    private EditText startTimeEditText, endTimeEditText;
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        toolbar = findViewById(R.id.add_address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        namaSeniman = findViewById(R.id.ad_namaSeniman);
        hargaSeniman = findViewById(R.id.ad_hargaSeniman);
        nama = findViewById(R.id.ad_nama);
        email = findViewById(R.id.ad_email);
        jalan = findViewById(R.id.ad_jalan);
        kota = findViewById(R.id.ad_kota);
        provinsi = findViewById(R.id.ad_provinsi);
        kode = findViewById(R.id.ad_kode);
        telepon = findViewById(R.id.ad_telepon);
        tanggal = findViewById(R.id.ad_date);
        startTimeEditText = findViewById(R.id.etStartTime);
        endTimeEditText = findViewById(R.id.etEndTime);
        bayarBtn = findViewById(R.id.now_bayar);

        // Mengambil data dari Firestore dan mengisi otomatis EditText
        getDataFromFirestore();

        // Panggil initMidTransSDK() di dalam onCreate() sebelum menggunakan bayarBtn
//        initMidTransSDK();

        bayarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNama = nama.getText().toString();
                String userEmail = email.getText().toString();
                String userJalan = jalan.getText().toString();
                String userKota = kota.getText().toString();
                String userProvinsi = provinsi.getText().toString();
                String userKode = kode.getText().toString();
                String userTelepon = telepon.getText().toString();
                String userTanggal = tanggal.getText().toString();
                String startTime = startTimeEditText.getText().toString();
                String endTime = endTimeEditText.getText().toString();

                // Memastikan semua field telah diisi
                if (!userNama.isEmpty() && !userEmail.isEmpty() && !userJalan.isEmpty() && !userKota.isEmpty() && !userProvinsi.isEmpty() && !userKode.isEmpty() && !userTelepon.isEmpty() && !userTanggal.isEmpty() && !startTime.isEmpty() && !endTime.isEmpty()) {
                    // Menampilkan dialog konfirmasi
                    showConfirmationDialog();
                } else {
                    Toast.makeText(OrderActivity.this, "Silahkan isi semua data!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClickListener for startTimeEditText
        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(startTimeEditText);
            }
        });

        // Mendapatkan data yang diteruskan dari Intent
        Intent intent = getIntent();
        if (intent != null) {
            String namaSenimanStr = intent.getStringExtra("nama_seniman");
            int hargaSenimanInt = intent.getIntExtra("harga_seniman", 0); // Ubah menjadi int

            // Mengisi EditText atau variabel lain yang diperlukan
            namaSeniman.setText(namaSenimanStr);
            hargaSeniman.setText(String.valueOf(hargaSenimanInt)); // Ubah ke string
        }
    }

    // Method untuk menampilkan dialog konfirmasi
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah data pemesanan Anda sudah benar?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Simpan data pemesanan ke Firestore
                saveOrderToFirestore();
            }
        });
        builder.setNegativeButton("Cek lagi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // Method untuk menyimpan data pemesanan ke Firestore
    private void saveOrderToFirestore() {
        if (auth.getCurrentUser() != null) {
            // Pastikan pengguna sudah login sebelum menyimpan pesanan

            final HashMap<String, Object> orderData = new HashMap<>();
            orderData.put("namaSeniman", namaSeniman.getText().toString());
            orderData.put("hargaSeniman", hargaSeniman.getText().toString());
            orderData.put("nama", nama.getText().toString());
            orderData.put("email", email.getText().toString());
            orderData.put("jalan", jalan.getText().toString());
            orderData.put("kota", kota.getText().toString());
            orderData.put("provinsi", provinsi.getText().toString());
            orderData.put("kodepos", kode.getText().toString());
            orderData.put("telepon", telepon.getText().toString());
            orderData.put("tanggal", tanggal.getText().toString());
            orderData.put("startTime", startTimeEditText.getText().toString());
            orderData.put("endTime", endTimeEditText.getText().toString());

            // Mengatur tanggal real-time
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = dateFormat.format(calendar.getTime());
            orderData.put("tanggal_order", currentDate); // Menggunakan "tanggal_order" untuk membedakan dengan "tanggal"

            firestore.collection("Pesanan").document(auth.getCurrentUser().getUid())
                    .collection("User").document().set(orderData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Pesanan berhasil disimpan
                                Toast.makeText(OrderActivity.this, "Pemesanan ditambahkan!", Toast.LENGTH_SHORT).show();

//                                // Memulai HistoryActivity setelah pesanan berhasil disimpan
                                startActivity(new Intent(OrderActivity.this, HistoryActivity.class));
                            } else {
                                // Pesanan gagal disimpan
                                Toast.makeText(OrderActivity.this, "Gagal menyimpan pemesanan: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Jika pengguna belum login, arahkan ke aktivitas login
            startActivity(new Intent(OrderActivity.this, LoginActivity.class));
        }
    }

    public void showDatePickerDialog(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Handle the selected date (if needed)
                        // You can update a TextView or any other UI component with the selected date
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                        // Format tanggal yang dipilih menjadi format "Sabtu, 16 Maret 2024"
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
                        tanggal.setText(dateFormat.format(selectedCalendar.getTime()));

                        // Validate the selected date (minimum 2 days from today)
                        if (isDateValid(year, month, dayOfMonth)) {
                            // The selected date is valid, you can proceed
                            Toast.makeText(OrderActivity.this, "Tanggal valid", Toast.LENGTH_SHORT).show();
                        } else {
                            // The selected date is not valid, show a toast
                            Toast.makeText(OrderActivity.this, "Pilih tanggal pemesanan H-3!", Toast.LENGTH_SHORT).show();
                            tanggal.setText(""); // Clear the selected date
                        }
                    }
                },
                year,
                month,
                day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private boolean isDateValid(int selectedYear, int selectedMonth, int selectedDay) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(selectedYear, selectedMonth, selectedDay);

        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH, 2); // Add 2 days to today's date

        return selectedDate.after(today);
    }

    // Method to show time picker dialog
    private void showTimePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(OrderActivity.this,
                R.style.TimePickerTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (isTimeValid(hourOfDay, minute)) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            editText.setText(sdf.format(calendar.getTime()));

                            // Set endTimeEditText to 3 hours later than startTimeEditText
                            calendar.add(Calendar.HOUR_OF_DAY, 3);
                            endTimeEditText.setText(sdf.format(calendar.getTime()));
                        } else {
                            // Show toast for invalid time selection
                            Toast.makeText(OrderActivity.this, "Waktu tidak valid. Pilih waktu antara 20:00 dan 01:00.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    // Method untuk memvalidasi waktu yang dipilih
    private boolean isTimeValid(int hourOfDay, int minute) {
        // Check if selected time is between 20:00 and 01:00
        return (hourOfDay >= 20 && hourOfDay <= 23) || (hourOfDay == 0 && minute == 0) || (hourOfDay == 1 && minute == 0);
    }

    // Method untuk mengambil data dari Firestore dan mengisi otomatis EditText
    private void getDataFromFirestore() {
        firestore.collection("Pengguna").document(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Mengisi otomatis EditText dengan data dari Firestore
                                nama.setText(document.getString("nama"));
                                email.setText(document.getString("email"));
                                jalan.setText(document.getString("alamat"));
                                kota.setText(document.getString("kota"));
                                provinsi.setText(document.getString("provinsi"));
                                kode.setText(document.getString("kodepos"));
                                telepon.setText(document.getString("telepon"));
                            } else {
                                Toast.makeText(OrderActivity.this, "Document tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(OrderActivity.this, "Gagal mengambil data: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void initMidTransSDK(){
//        SdkUIFlowBuilder.init()
//                .setContext(this)
//                .setMerchantBaseUrl(BuildConfig.BASE_URL)
//                .setClientKey(BuildConfig.CLIENT_KEY)
//                .setTransactionFinishedCallback(this)
//                .enableLog(true)
//                .setColorTheme(new CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
//                .buildSDK();
//    }
//    private void actionButton(){
//        // Ambil nilai dari formulir
//        String itemName = namaSeniman.getText().toString();
//        double itemPrice = Double.parseDouble(hargaSeniman.getText().toString());
//        int quantity = 1;
//
//        // Konfigurasi item yang dibeli
//        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
//        ItemDetails itemDetails = new ItemDetails("1", itemPrice, quantity, itemName);
//        itemDetailsList.add(itemDetails);
//
//        // Konfigurasi data pelanggan
//        CustomerDetails customerDetails = new CustomerDetails();
//        customerDetails.setEmail(email.getText().toString());
//        customerDetails.setPhone(telepon.getText().toString());
//
//        // Konfigurasi permintaan transaksi
//        TransactionRequest transactionRequest = new TransactionRequest(System.currentTimeMillis() + "", 20000);
//        transactionRequest.setCustomerDetails(customerDetails);
//        transactionRequest.setItemDetails(itemDetailsList);
//
//        // Mulai alur pembayaran
//        MidtransSDK.getInstance().setTransactionRequest(transactionRequest);
//        MidtransSDK.getInstance().startPaymentUiFlow(this);
//    }
//
//    @Override
//    public void onTransactionFinished(TransactionResult result) {
//        if(result.getResponse() != null){
//            switch (result.getStatus()){
//                case TransactionResult.STATUS_SUCCESS:
//                    Toast.makeText(this, "Transaksi Berhasil ID: "+result.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
//                    break;
//                case TransactionResult.STATUS_PENDING:
//                    Toast.makeText(this, "Transaksi Pending ID: "+result.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
//                    break;
//                case TransactionResult.STATUS_FAILED:
//                    Toast.makeText(this, "Transaksi Gagal ID: "+result.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
//                    break;
//            }
//            result.getResponse().getStatusMessage();
//        }else if(result.isTransactionCanceled()){
//            Toast.makeText(this, "Transaksi Dibatalkan", Toast.LENGTH_SHORT).show();
//        }else{
//            if(result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)){
//                Toast.makeText(this, "Transaksi Invalid", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(this, "Transaksi Selesai dengan Kesalahan", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
