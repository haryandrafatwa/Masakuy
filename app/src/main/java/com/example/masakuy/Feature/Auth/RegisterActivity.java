package com.example.masakuy.Feature.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.masakuy.MainActivity;
import com.example.masakuy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRefs;
    private StorageReference dummyDispPict;

    private TextView tv_masuk;
    private EditText et_username, et_email, et_pass, et_repass;
    private Button btnDaftar;
    private ProgressDialog mDialog;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        dummyDispPict = FirebaseStorage.getInstance().getReference("DisplayPictures/dummy").child("ic_profile.png");
        initialize();
    }

    private void initialize() {

        et_email = findViewById(R.id.et_email_register);
        et_username = findViewById(R.id.et_username_register);
        et_pass = findViewById(R.id.et_pass_register);
        et_repass = findViewById(R.id.et_repass_register);
        btnDaftar = findViewById(R.id.btn_signup);
        tv_masuk = findViewById(R.id.tv_signin);
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        tv_masuk.setOnClickListener(new View.OnClickListener() { // ini buat aktifin kalo misalnya tulisan sign in dipencet bakal ngelakuin apa
            @Override
            public void onClick(View v) {
                setActivity(LoginActivity.class); // ini buat mindahin page, dr register ke login page
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() { // ini buat aktifin tombol daftar/sign up
            @Override
            public void onClick(View v) {
                registerWithEmailandPassword();
            }
        });

        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!et_email.getText().toString().matches(emailPattern)) { // ini buat ngecek email, ada karakter '@' sama '.' apa engga
                    et_email.setTextColor(Color.RED);
                } else {
                    et_email.setTextColor(getResources().getColor(R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void registerWithEmailandPassword() {

        final String email = et_email.getText().toString();
        final String username = et_username.getText().toString();
        String pass = et_pass.getText().toString();
        String repass = et_repass.getText().toString();

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(username) && TextUtils.isEmpty(pass) && TextUtils.isEmpty(repass)) { // ini buat ngecek, form nya ada yg gak disi apa engga
            Toast.makeText(this, "Data harus diisi", Toast.LENGTH_SHORT).show();
        } else {
            if(pass.length() > 5 && repass.length() > 5){ // ini buat cek, si password harus lebih dari 5 atau >= 6
                if (pass.equals(repass)) { // ini buat cek, password sama confirm password sama atau engga
                    mDialog.setTitle("Register");
                    mDialog.setCancelable(true);
                    mDialog.setMessage("Wait a minute .. ");
                    mDialog.show();

                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currentUserID = mAuth.getCurrentUser().getUid(); // ini buat ngambil UID user yg lagi login di firebase nya
                                userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID); // ini inisiasi alamat atau node yg bakalan dipake di firebasenya

                                HashMap userMap = new HashMap(); // ini buat objek baru, setiap mau update ke firebase atau add ke firebase, biasanya pake Map kalo variabel yang dimasukinnya banyak

                                userMap.put("username", username);
                                userMap.put("email", email);

                                dummyDispPict.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) { // ini buat ngambil gambar di Firebase Storage
                                        userRefs.child("displaypicture").setValue(uri.toString()); // selain pake map, update ke firebase juga bisa pake kaya begini
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d("DISPLAY PICTURE FAILED", "OMG");
                                    }
                                });

                                userRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(Task task) {
                                        if (task.isSuccessful()) { // kalo login berhasil, dia bakalan di pindah ke halaman utama
                                            Toast.makeText(RegisterActivity.this, "Daftar Berhasil", Toast.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                            setActivity(MainActivity.class);
                                        } else {
                                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Password harus sama!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Password max 6 karakter!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setActivity(Class activity) {
        Intent mainIntent = new Intent(RegisterActivity.this, activity);
        startActivity(mainIntent);
        finish();
    }

}
