package com.example.wardani.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.wardani.R;
import com.example.wardani.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Fragment homeFragment;
    private FirebaseAuth auth;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        homeFragment = new HomeFragment();
        loadFragment(homeFragment);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Mendapatkan objek pengguna saat ini
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Mendapatkan item menu_admin
        MenuItem adminMenuItem = menu.findItem(R.id.menu_admin);

        // Jika pengguna saat ini tidak null dan alamat emailnya adalah "adminwardani123@gmail.com"
        // maka tampilkan opsi menu_admin, jika tidak, sembunyikan opsi tersebut
        if (currentUser != null && currentUser.getEmail().equals("adminwardani123@gmail.com")) {
            adminMenuItem.setVisible(true);
        } else {
            adminMenuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
            showLogoutConfirmationDialog();
        } else if (id == R.id.menu_added) {
            startActivity(new Intent(MainActivity.this, AddedActivity.class));
        } else if (id == R.id.menu_history) {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        } else if (id == R.id.menu_hubungi_kami) {
            startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
        } else if (id == R.id.menu_admin) {
            startActivity(new Intent(MainActivity.this, AdminActivity.class));
        }
        return true;
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Keluar");
        builder.setMessage("Anda yakin ingin keluar?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutUser();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Batal logout, tidak ada tindakan tambahan yang diperlukan
            }
        });
        builder.show();
    }

    private void logoutUser() {
        auth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}