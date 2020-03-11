package com.example.masakuy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_logout = findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertsignout();
            }
        });

    }

    public void alertsignout(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog2.setTitle("Konfirmasi Keluar");

        // Setting Dialog Message
        alertDialog2.setMessage("Apakah anda yakin ingin keluar?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                FirebaseAuth.getInstance().signOut();
                closeActivity(loginActivity.class);
            }
        });

        alertDialog2.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog2.show();

    }

    private void closeActivity(Class activity) {
        Intent mainIntent = new Intent(MainActivity.this, activity);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }
}
