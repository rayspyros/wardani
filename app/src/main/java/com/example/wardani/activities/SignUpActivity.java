package com.example.wardani.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wardani.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText nama, email, password, jalan, kota, provinsi, pos, telepon;
    private CheckBox checkboxConfirmation;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        nama = findViewById(R.id.textNama);
        email = findViewById(R.id.textEmail);
        password = findViewById(R.id.textPassword);
        jalan = findViewById(R.id.textJalan);
        kota = findViewById(R.id.textKota);
        provinsi = findViewById(R.id.textProvinsi);
        pos = findViewById(R.id.textPos);
        telepon = findViewById(R.id.textTelp);
        checkboxConfirmation = findViewById(R.id.checkboxConfirmation);

        pos.setInputType(InputType.TYPE_CLASS_NUMBER);
        telepon.setInputType(InputType.TYPE_CLASS_NUMBER);

        addTextWatcher(nama);
        addTextWatcher(jalan);
        addTextWatcher(kota);
        addTextWatcher(provinsi);

        InputFilter[] filters = new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        }};
        nama.setFilters(filters);
    }

    private void addTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }
                isUpdating = true;
                StringBuilder newText = new StringBuilder();
                boolean capitalizeNext = true;
                for (int i = 0; i < s.length(); i++) {
                    char c = s.charAt(i);
                    if (capitalizeNext && Character.isLetter(c)) {
                        newText.append(Character.toUpperCase(c));
                        capitalizeNext = false;
                    } else {
                        newText.append(c);
                    }
                    if (Character.isWhitespace(c)) {
                        capitalizeNext = true;
                    }
                }
                editText.setText(newText.toString());
                editText.setSelection(editText.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void signup(View view){

        String userNama = nama.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String userJalan = jalan.getText().toString();
        String userKota = kota.getText().toString();
        String userProvinsi = provinsi.getText().toString();
        String userPos = pos.getText().toString();
        String userTelepon = telepon.getText().toString();

        if (!checkboxConfirmation.isChecked()) {
            Toast.makeText(this, "Harap centang kotak konfirmasi!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userNama)){
            Toast.makeText(this, "Masukkan nama anda!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (userNama.length() < 3) {
            Toast.makeText(this, "Nama terlalu pendek, masukkan minimal 3 Karakter", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this, "Masukkan email anda!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userPassword)){
            Toast.makeText(this, "Masukkan password anda!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(userPassword.length() < 6){
            Toast.makeText(this, "Password anda terlalu pendek, masukkan minimal 6 karakter!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userJalan)){
            Toast.makeText(this, "Masukkan alamat anda!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (userJalan.length() < 10) {
            Toast.makeText(this, "Nama jalan terlalu pendek, masukkan minimal 10 Karakter", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userKota)){
            Toast.makeText(this, "Masukkan kota anda!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userProvinsi)){
            Toast.makeText(this, "Masukkan provinsi anda!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userPos)){
            Toast.makeText(this, "Masukkan kode pos anda!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (userPos.length() != 5 || !userPos.matches("\\d{5}")) {
            Toast.makeText(this, "Kode pos harus terdiri dari 5 angka", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userTelepon)) {
            Toast.makeText(this, "Masukkan telepon anda!", Toast.LENGTH_SHORT).show();
            return;
        } else if (userTelepon.length() < 10 || userTelepon.length() > 13) {
            Toast.makeText(this, "Nomor telepon harus memiliki 10 hingga 13 digit", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Pendaftaran berhasil, simpan data pengguna ke Firestore
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("nama", userNama);
                            userData.put("email", userEmail);
                            userData.put("alamat", userJalan);
                            userData.put("kota", userKota);
                            userData.put("provinsi", userProvinsi);
                            userData.put("kodepos", userPos);
                            userData.put("telepon", userTelepon);

                            String userID = auth.getCurrentUser().getUid();

                            FirebaseFirestore.getInstance().collection("Pengguna")
                                    .document(userID)
                                    .set(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "Berhasil Daftar", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Gagal menyimpan data pengguna: " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUpActivity.this, "Gagal Daftar" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void login(View view) {
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
    }

    public void togglePasswordVisibility(View view) {
        EditText passwordEditText = findViewById(R.id.textPassword);

        if (view.getTag() != null && view.getTag().equals("password_toggle")) {
            boolean isPasswordVisible = passwordEditText.getInputType() ==
                    (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            int drawableResId = isPasswordVisible ? R.drawable.hide_password24 : R.drawable.unhide_password24;
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableResId, 0);

            passwordEditText.setInputType(
                    isPasswordVisible ?
                            (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) :
                            (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
            );
            passwordEditText.setTypeface(Typeface.DEFAULT);
            passwordEditText.setSelection(passwordEditText.getText().length());
        }
    }
}
