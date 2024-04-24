package com.example.wardani.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wardani.BuildConfig;
import com.example.wardani.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.models.BcaBankTransferRequestModel;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.ShippingAddress;
import com.midtrans.sdk.corekit.models.snap.Gopay;
import com.midtrans.sdk.corekit.models.snap.Shopeepay;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements TransactionFinishedCallback {
    private TextView textViewNama, textViewCustomer, textViewTanggal, textViewWaktu, textViewDetail, textViewHarga, textViewOrder, textViewStatus;
    private Button btnPayment;
    private String name, order, customer, address, id_item, stats;
    private Double price;
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pembayaran");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        iniBindViews();

        // Tangkap data yang diteruskan dari intent
        Intent intent = getIntent();
        if (intent != null) {
            String id = intent.getStringExtra("ID_ITEM");
            String nama = intent.getStringExtra("NAMA");
            String pemesan = intent.getStringExtra("CUSTOMER");
            String tanggal = intent.getStringExtra("TANGGAL");
            String waktu = intent.getStringExtra("WAKTU");
            String detail = intent.getStringExtra("DETAIL");
            String harga = intent.getStringExtra("HARGA");
            String status = intent.getStringExtra("STATUS");
            String pesanan = intent.getStringExtra("ORDER");

            name = nama;
            stats = status;
            customer = pemesan;
            address = detail;
            id_item = id;
            order = pesanan;
            price = Double.valueOf(harga);

            // Set data ke TextView
            textViewNama.setText(nama);
            textViewCustomer.setText(pemesan);
            textViewTanggal.setText(tanggal);
            textViewWaktu.setText(waktu);
            textViewDetail.setText(detail);
            textViewHarga.setText("Rp. "+harga);
            textViewOrder.setText(pesanan);
            textViewStatus.setText(status);
        }

        initActionButtons();
        initMidtransSdk();
    }

    private void iniBindViews(){
        // Inisialisasi TextView untuk menampilkan data
        textViewNama = findViewById(R.id.payment_nama);
        textViewCustomer = findViewById(R.id.payment_customer);
        textViewTanggal = findViewById(R.id.payment_tanggal);
        textViewWaktu = findViewById(R.id.payment_waktu);
        textViewDetail = findViewById(R.id.payment_detail);
        textViewHarga = findViewById(R.id.payment_harga);
        textViewOrder = findViewById(R.id.payment_tgl_order);
        textViewStatus = findViewById(R.id.payment_status);
        btnPayment = findViewById(R.id.btnBayar);
    }

    private TransactionRequest initTransactionRequest() {
        // Create new Transaction Request
        TransactionRequest transactionRequestNew = new
                TransactionRequest(System.currentTimeMillis() + "", price);
        transactionRequestNew.setCustomerDetails(initCustomerDetails());
        transactionRequestNew.setGopay(new Gopay("mysamplesdk:://midtrans"));
        transactionRequestNew.setShopeepay(new Shopeepay("mysamplesdk:://midtrans"));

        // Set BCA Virtual Account as payment option
        transactionRequestNew.setBcaVa(new BcaBankTransferRequestModel("228538450983950"));

        // Set item details
        ItemDetails itemDetails1 = new ItemDetails(id_item, price, 1, name);
        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
        itemDetailsList.add(itemDetails1);
        transactionRequestNew.setItemDetails(itemDetailsList);
        return transactionRequestNew;
    }

    private CustomerDetails initCustomerDetails() {
        // Define customer detail (mandatory for coreflow)
        CustomerDetails mCustomerDetails = new CustomerDetails();
        mCustomerDetails.setFirstName(customer);
        mCustomerDetails.setShippingAddress(shippingAddress());
        return mCustomerDetails;
    }

    private ShippingAddress shippingAddress(){
        ShippingAddress shippingAddressNew = new ShippingAddress();
        shippingAddressNew.setAddress(address);
        return shippingAddressNew;
    }

    private void initMidtransSdk() {
        SdkUIFlowBuilder sdkUIFlowBuilder = SdkUIFlowBuilder.init()
                .setContext(this) // context is mandatory
                .setMerchantBaseUrl(BuildConfig.BASE_URL)//set merchant url
                .setClientKey(BuildConfig.CLIENT_KEY) // client_key is mandatory
                .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback
                .enableLog(true) // enable sdk log
                .setLanguage("id");
        sdkUIFlowBuilder.buildSDK();
        uiKitCustomSetting();
    }

    @Override
    public void onTransactionFinished(TransactionResult result) {
        if (result.getResponse() != null) {
            switch (result.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
                    // Menyimpan data pesanan ke koleksi "Riwayat"
                    simpanKeRiwayat(customer, name, textViewTanggal.getText().toString(), textViewWaktu.getText().toString(), address, textViewHarga.getText().toString(), result.getResponse().getTransactionId());
                    // Mengubah status di Firebase Firestore
                    ubahStatusPesanan(auth.getCurrentUser().getUid(), id_item);
                    // Menampilkan pesan transaksi berhasil
                    Toast.makeText(this, "Transaction Finished. ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    setResultAndFinish("Sudah Dibayar");
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaction Pending. ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaction Failed. ID: " + result.getResponse().getTransactionId() + ". Message: " + result.getResponse().getStatusMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        } else if (result.isTransactionCanceled()) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show();
        } else {
            if (result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show();
            }
        }
    }



    private void initActionButtons() {
        btnPayment.setOnClickListener(v -> {
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(PaymentActivity.this);
        });
    }

    private void uiKitCustomSetting() {
        UIKitCustomSetting uIKitCustomSetting = new UIKitCustomSetting();
        uIKitCustomSetting.setSaveCardChecked(true);
        MidtransSDK.getInstance().setUiKitCustomSetting(uIKitCustomSetting);
    }

    private void simpanKeRiwayat(String customer, String nama, String tanggal, String waktu, String detail, String harga, String order) {
        String tglOrder = textViewOrder.getText().toString(); // Mendapatkan tgl_order dari TextView
        if (!tglOrder.isEmpty()) {
            // Membuat data pesanan
            Map<String, Object> pesanan = new HashMap<>();
            pesanan.put("customer", customer);
            pesanan.put("nama", nama);
            pesanan.put("tanggal", tanggal);
            pesanan.put("waktu", waktu);
            pesanan.put("detail", detail);
            pesanan.put("harga", harga);
            pesanan.put("order", order);
            pesanan.put("tgl_order", tglOrder); // Menambahkan tgl_order ke dalam Map

            // Menyimpan data pesanan ke koleksi "Riwayat" di Firestore
            firestore.collection("Riwayat")
                    .add(pesanan)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(PaymentActivity.this, "Pesanan berhasil disimpan dalam riwayat", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PaymentActivity.this, "Gagal menyimpan pesanan dalam riwayat", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void ubahStatusPesanan(String uid, String id_item) {
        firestore.collection("Pesanan")
                .document(auth.getCurrentUser().getUid())
                .collection("User")
                .document(id_item) // gunakan ID item sebagai referensi dokumen
                .update("status", "Sudah Dibayar")
                .addOnSuccessListener(aVoid -> {
                    // Hapus tombol pembayaran
                    btnPayment.setVisibility(View.GONE);
                    Toast.makeText(this, "Status pesanan diperbarui", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memperbarui status pesanan", Toast.LENGTH_SHORT).show();
                });
    }
    private void setResultAndFinish(String updatedStatus) {
        // Mengirim status pembayaran yang diperbarui kembali ke HistoryActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("UPDATED_STATUS", updatedStatus);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
