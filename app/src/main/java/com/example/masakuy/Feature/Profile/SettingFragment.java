package com.example.masakuy.Feature.Profile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.masakuy.Feature.Auth.LoginActivity;
import com.example.masakuy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingFragment extends Fragment {

    private String old, newPass, confirm;
    private TextView tv_changePass, tv_about, tv_logout;
    private EditText et_old, et_new, et_confirm;

    private AuthCredential authCredential;
    private FirebaseUser firebaseUser;

    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setStatusBar();
        initialize();
    }

    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);

        tv_changePass = getActivity().findViewById(R.id.tv_changepassword);
        tv_about = getActivity().findViewById(R.id.tv_about);
        tv_logout = getActivity().findViewById(R.id.tv_logout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        tv_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeDialogChangePassword();
            }
        });

        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutFragment aboutFragment = new AboutFragment();
                setFragment(aboutFragment);
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertsignout();
            }
        });

    }

    private void initializeDialogChangePassword(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.change_password);
        dialog.setCancelable(false);
        dialog.setTitle("Change Password");


        TextView btnAccept = dialog.findViewById(R.id.btn_accept_changepassword);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel_changepassword);

        et_old = dialog.findViewById(R.id.et_old_password);
        et_new = dialog.findViewById(R.id.et_new_password);
        et_confirm = dialog.findViewById(R.id.et_confirm_password);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                old = et_old.getText().toString();
                newPass = et_new.getText().toString();
                confirm = et_confirm.getText().toString();
                if(old.isEmpty()){
                    Toast.makeText(getActivity(), "Silahkan isi Password Lama terlebih dahulu", Toast.LENGTH_SHORT).show();
                }else{
                    if(newPass.isEmpty()){
                        Toast.makeText(getActivity(), "Silahkan isi Password Baru terlebih dahulu", Toast.LENGTH_SHORT).show();
                    }else{
                        if(confirm.isEmpty()){
                            Toast.makeText(getActivity(), "Silahkan isi Konfirmasi Password terlebih dahulu", Toast.LENGTH_SHORT).show();
                        }else{
                            if (newPass.equals(confirm)){
                                authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),old);

                                firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) { // ini buat ngecek kalo si password lama nya itu bener apa engga
                                        if (task.isSuccessful()){
                                            firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    task.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getActivity(), "Password updated!", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }else{
                                            Toast.makeText(getActivity(), "Old password is wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(getActivity(), "Password tidak cocok!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void alertsignout(){ // fungsi untuk membuat alert dialog ketika ingin logout
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Konfirmasi Keluar");

        alertDialog2.setCancelable(false);

        // Setting Dialog Message
        alertDialog2.setMessage("Apakah anda yakin ingin keluar?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                FirebaseAuth.getInstance().signOut();
                closeActivity(LoginActivity.class);
            }
        });

        alertDialog2.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog2.show();

    }

    //Todo: method closeActivity itu sama hal nya kayak setActivity, yg ngebedain, close Activity itu mulai si activitynya dr awal lg, kalo setActivity aja, dia cuma pindah aja
    private void closeActivity(Class activity) { // fungsi untuk kelarin activity terakhir, dan diganti ke activity baru trus dikirim ke halaman login
        Intent mainIntent = new Intent(getActivity(), activity);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        getActivity().finish();
    }

    private void setStatusBar(){
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

}
