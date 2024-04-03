package com.example.wardani.activities;

import static com.example.wardani.BuildConfig.BASE_URL;
import static com.example.wardani.BuildConfig.CLIENT_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wardani.R;
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

public class PaymentActivity extends AppCompatActivity implements TransactionFinishedCallback {
    private TextView textViewNama, textViewCustomer, textViewTanggal, textViewWaktu, textViewDetail, textViewHarga;
    private Button btnPayment;

    private String name, customer, address, id_item;
    private Double price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar;
        toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            String id = intent.getStringExtra("ID");
            String nama = intent.getStringExtra("NAMA");
            String pemesan = intent.getStringExtra("CUSTOMER");
            String tanggal = intent.getStringExtra("TANGGAL");
            String waktu = intent.getStringExtra("WAKTU");
            String detail = intent.getStringExtra("DETAIL");
            String harga = intent.getStringExtra("HARGA");

            name = nama;
            customer = pemesan;
            address = detail;
            id_item = id;
            price = Double.valueOf(harga);

            // Set data ke TextView
            textViewNama.setText(nama);
            textViewCustomer.setText(customer);
            textViewTanggal.setText(tanggal);
            textViewWaktu.setText(waktu);
            textViewDetail.setText(detail);
            textViewHarga.setText(harga);
        }

        initActionButtons();
        initMidtransSdk();
    }

    private void iniBindViews(){
        // Inisialisasi TextView untuk menampilkan data
        textViewNama = findViewById(R.id.payment_nama);
        textViewCustomer= findViewById(R.id.payment_customer);
        textViewTanggal = findViewById(R.id.payment_tanggal);
        textViewWaktu = findViewById(R.id.payment_waktu);
        textViewDetail = findViewById(R.id.payment_detail);
        textViewHarga = findViewById(R.id.payment_harga);
        btnPayment = findViewById(R.id.btnBayar);
    }


    private TransactionRequest initTransactionRequest() {
        // Create new Transaction Request
        TransactionRequest transactionRequestNew = new
                TransactionRequest(System.currentTimeMillis() + "", price);
        transactionRequestNew.setCustomerDetails(initCustomerDetails());
        transactionRequestNew.setGopay(new Gopay("mysamplesdk:://midtrans"));
        transactionRequestNew.setShopeepay(new Shopeepay("mysamplesdk:://midtrans"));

        transactionRequestNew.setBcaVa(new BcaBankTransferRequestModel("228538450983950"));

        ItemDetails itemDetails1 = new ItemDetails(id_item, price, 1, name);
        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
        itemDetailsList.add(itemDetails1);
        transactionRequestNew.setItemDetails(itemDetailsList);
        return transactionRequestNew;
    }

    private CustomerDetails initCustomerDetails() {
        //define customer detail (mandatory for coreflow)
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
                .setMerchantBaseUrl(BASE_URL)//set merchant url
                .setClientKey(CLIENT_KEY) // client_key is mandatory
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
                    Toast.makeText(this, "Transaction Finished. ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    PaymentActivity.this.startActivity(intent);
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
}