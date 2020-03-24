package com.example.masakuy.Feature.Beranda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.masakuy.Feature.Tambah.AddArtikelFragment;
import com.example.masakuy.Feature.Tambah.AddProdukFragment;
import com.example.masakuy.Feature.Tambah.AddRecipe1Fragment;
import com.example.masakuy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PilihBahanBaku extends Fragment {

    private ImageButton ib_sayur,ib_daging, ib_bumbu;

    private BahanBakuFragment bahanBakuFragment;

    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pilih_bahan_baku_fragment, container, false);
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

        ib_sayur = getActivity().findViewById(R.id.btn_sayuran_bb);
        ib_daging = getActivity().findViewById(R.id.btn_daging_bb);
        ib_bumbu = getActivity().findViewById(R.id.btn_bumbu_bb);

        ib_sayur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bahanBakuFragment = new BahanBakuFragment();
                Bundle bundle = new Bundle();
                bundle.putString("jenis","Sayuran");
                bahanBakuFragment.setArguments(bundle);
                setFragment(bahanBakuFragment);
            }
        });

        ib_daging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bahanBakuFragment = new BahanBakuFragment();
                Bundle bundle = new Bundle();
                bundle.putString("jenis","Daging");
                bahanBakuFragment.setArguments(bundle);
                setFragment(bahanBakuFragment);
            }
        });

        ib_bumbu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bahanBakuFragment = new BahanBakuFragment();
                Bundle bundle = new Bundle();
                bundle.putString("jenis","Bumbu");
                bahanBakuFragment.setArguments(bundle);
                setFragment(bahanBakuFragment);
            }
        });

    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setStatusBar(){
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

}
