package com.example.wardani.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.wardani.R;
import com.example.wardani.admin.activites.KelolaBeritaActivity;
import com.example.wardani.admin.activites.KelolaCustomerActivity;
import com.example.wardani.admin.activites.KelolaPesananActivity;
import com.example.wardani.admin.activites.KelolaSenimanActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();

        LinearLayout kelolaCustomerLayout = findViewById(R.id.kelola_customer);
        LinearLayout kelolaSenimanLayout = findViewById(R.id.kelola_seniman);
        LinearLayout kelolaPesananLayout = findViewById(R.id.kelola_pemesanan);
        LinearLayout kelolaBeritaLayout = findViewById(R.id.kelola_berita);
        Button checkCustomer = findViewById(R.id.button_check);

        kelolaCustomerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, KelolaCustomerActivity.class);
                startActivity(intent);
            }
        });

        kelolaSenimanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, KelolaSenimanActivity.class);
                startActivity(intent);
            }
        });

        kelolaPesananLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, KelolaPesananActivity.class);
                startActivity(intent);
            }
        });

        kelolaBeritaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, KelolaBeritaActivity.class);
                startActivity(intent);
            }
        });

        checkCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_signout) {
            showSignOutConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSignOutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cek jika auth tidak null sebelum sign out
                        if (auth != null) {
                            auth.signOut();
                            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                            Toast.makeText(AdminActivity.this, "Berhasil Keluar", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AdminActivity.this, "Gagal Keluar, Coba Lagi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }
}
