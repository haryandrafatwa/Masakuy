package com.example.masakuy.Feature.Tambah;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.masakuy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PilihInputFragment extends Fragment {

    private Button tambah_recipe,tambah_artikel, tambah_produk;
    private AddRecipe1Fragment addRecipe1Fragment;
    private AddArtikelFragment addArtikelFragment;
    private AddProdukFragment addProdukFragment;

    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pilih_input_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setStatusBar();
        initialize();
    }

    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.VISIBLE);

        tambah_recipe = getActivity().findViewById(R.id.btn_tambah_resep);
        tambah_artikel = getActivity().findViewById(R.id.btn_tambah_artikel);
        tambah_produk = getActivity().findViewById(R.id.btn_tambah_shop);

        addRecipe1Fragment = new AddRecipe1Fragment();
        addArtikelFragment = new AddArtikelFragment();
        addProdukFragment = new AddProdukFragment();

        tambah_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(addRecipe1Fragment);
            }
        });

        tambah_artikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(addArtikelFragment);
            }
        });

        tambah_produk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(addProdukFragment);
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
