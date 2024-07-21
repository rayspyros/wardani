package com.example.wardani.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wardani.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SignInMethodQueryResult;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    private FirebaseAuth auth;
    private static final String ADMIN_EMAIL = "adminwardani123@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            String currentUserEmail = auth.getCurrentUser().getEmail();
            if (ADMIN_EMAIL.equals(currentUserEmail)) {
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
            finish();
        }

        email = findViewById(R.id.textEmail);
        password = findViewById(R.id.textPassword);
    }

    public void login(View view) {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Masukkan username anda!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Masukkan password anda!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.length() < 6) {
            Toast.makeText(this, "Password anda terlalu pendek, masukkan minimal 6 karakter!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lakukan proses masuk
        signInWithEmailAndPassword(userEmail, userPassword);
    }

    private void signInWithEmailAndPassword(String userEmail, String userPassword) {
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Berhasil Masuk", Toast.LENGTH_SHORT).show();
                            String currentUserEmail = auth.getCurrentUser().getEmail();
                            if (ADMIN_EMAIL.equals(currentUserEmail)) {
                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                            finish();
                        } else {
                            // Gagal masuk, tampilkan pesan kesalahan
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            handleLoginError(errorCode);
                        }
                    }
                });
    }

    private void handleLoginError(String errorCode) {
        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                Toast.makeText(LoginActivity.this, "Email tidak valid.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(LoginActivity.this, "Password salah.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR_USER_NOT_FOUND":
                showRegisterDialog();
                break;
            default:
                Toast.makeText(LoginActivity.this, "Gagal Masuk. Periksa kembali Email dan Password Anda.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Email belum terdaftar. Apakah Anda ingin mendaftar?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void signup(View view) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
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

    public void forget(View view) {
        // Create an EditText for the email input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        // Create an AlertDialog to get the user's email
        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Masukkan email untuk mereset password")
                .setView(input)
                .setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String email = input.getText().toString().trim();
                        if (!email.isEmpty()) {
                            sendPasswordResetEmail(email);
                        } else {
                            input.setError("Email harus diisi");
                        }
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled
                    }
                }).show();
    }

    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Email reset password telah dikirim", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Gagal mengirim email reset password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}