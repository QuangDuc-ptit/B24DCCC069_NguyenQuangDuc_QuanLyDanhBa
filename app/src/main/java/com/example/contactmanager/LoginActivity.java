package com.example.contactmanager;

import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername;
    private EditText edtPassword;
    private TextView tvError;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> login());
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvError.setVisibility(TextView.GONE);
            }
            @Override public void afterTextChanged(Editable s) { }
        };
        edtUsername.addTextChangedListener(watcher);
        edtPassword.addTextChangedListener(watcher);
    }

    private void login() {
        // Kiểm tra thông tin đăng nhập cố định trước khi mở danh bạ.
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.login_empty_error));
            return;
        }
        if (!"admin".equals(username) || !"123456".equals(password)) {
            showError(getString(R.string.login_invalid_error));
            return;
        }
        tvError.setVisibility(TextView.GONE);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(TextView.VISIBLE);
    }
}
