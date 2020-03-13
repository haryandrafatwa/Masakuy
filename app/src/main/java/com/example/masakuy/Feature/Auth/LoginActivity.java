package com.example.masakuy.Feature.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.masakuy.MainActivity;
import com.example.masakuy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mDialog;
    Button btnMasuk;
    EditText et_email, et_password;
    TextView tv_daftar;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        initialize();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        btnMasuk = findViewById(R.id.btn_login);
        et_email = findViewById(R.id.et_email_login);
        et_password = findViewById(R.id.et_password_login);
        tv_daftar = findViewById(R.id.tv_signup);

        tv_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActivity(RegisterActivity.class);
            }
        });

        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!et_email.getText().toString().matches(emailPattern)) { // ini buat ngecek inputan email ada karakter '@' sama '.' apa engga
                    et_email.setTextColor(Color.RED); // kalo gada diubah warna nya jd merah
                } else {
                    et_email.setTextColor(getResources().getColor(R.color.white)); // kalo ada diubah jadi putih
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnMasuk.setOnClickListener(new View.OnClickListener() { //ini buat kalo si button sign in nya dipencet
            @Override
            public void onClick(View v) {
                loginWithEmailandPassword();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) { // ini buat ngecek, ada user yg lg login apa engga, kalo ada dia langsung ngirim ke halaman utama.
            setActivity(MainActivity.class);
        }
    }

    private void loginWithEmailandPassword() {

        String email = et_email.getText().toString(); // ini ngambil email dr kotak email di login
        String pass = et_password.getText().toString(); // ini ngambil password dr kotak email di login

        if (TextUtils.isEmpty(email)) { //kalo emailnya kosong
            Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) { //kalo passwordnya kosong
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else { //kalo diisi semua

            //Ini buat Alert Dialog
            mDialog.setMessage("Tunggu sebentar...");
            mDialog.setCancelable(false);
            mDialog.setTitle("Login");
            mDialog.show();

            //Ini method bawaan firebasenya kalo mau login via email dan password
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) { //Ini kondisi kalo sukses login
                    if (task.isSuccessful()) {
                        setActivity(MainActivity.class);
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }
            });
        }
    }

    public void setActivity(Class activity) {
        Intent mainIntent = new Intent(LoginActivity.this, activity);
        startActivity(mainIntent);
        finish();
    }
}
