package com.example.masakuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

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

public class registerActivity extends AppCompatActivity {

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

        tv_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActivity(loginActivity.class);
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
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
                if (!et_email.getText().toString().matches(emailPattern)) {
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

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(username) && TextUtils.isEmpty(pass) && TextUtils.isEmpty(repass)) {
            Toast.makeText(this, "Data harus diisi", Toast.LENGTH_SHORT).show();
        } else {
            if(pass.length() > 5 && repass.length() > 5){
                if (pass.equals(repass)) {
                    mDialog.setTitle("Register");
                    mDialog.setCancelable(true);
                    mDialog.setMessage("Wait a minute .. ");
                    mDialog.show();

                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currentUserID = mAuth.getCurrentUser().getUid();
                                userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);
                                HashMap userMap = new HashMap();
                                userMap.put("username", username);
                                userMap.put("email", email);

                                dummyDispPict.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        userRefs.child("displaypicture").setValue(uri.toString());
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
                                        if (task.isSuccessful()) {
                                            Toast.makeText(registerActivity.this, "Daftar Berhasil", Toast.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                            setActivity(MainActivity.class);
                                        } else {
                                            Toast.makeText(registerActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(registerActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent mainIntent = new Intent(registerActivity.this, activity);
        startActivity(mainIntent);
        finish();
    }

}
