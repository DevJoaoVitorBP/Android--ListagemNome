package com.example.listadenomes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLoginAdmin, buttonLoginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLoginAdmin = findViewById(R.id.buttonLoginAdmin);
        buttonLoginUser = findViewById(R.id.buttonLoginUser);

        buttonLoginAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateCredentials(editTextUsername.getText().toString(), editTextPassword.getText().toString(), "admin", "admin")) {
                    saveUserRole("Admin");
                    startMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciais inválidas!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateCredentials(editTextUsername.getText().toString(), editTextPassword.getText().toString(), "user", "user")) {
                    saveUserRole("User");
                    startMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciais inválidas!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateCredentials(String username, String password, String storedUsername, String storedPassword) {
        return username.equals(storedUsername) && password.equals(storedPassword);
    }

    private void saveUserRole(String role) {
        SharedPreferences sharedPref = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("role", role);
        editor.apply();
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}