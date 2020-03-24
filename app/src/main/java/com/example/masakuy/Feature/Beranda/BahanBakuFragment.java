package com.example.masakuy.Feature.Beranda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.Feature.Artikel.ArtikelFragment;
import com.example.masakuy.Feature.Artikel.Recyclerview.ArtikelModel;
import com.example.masakuy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BahanBakuFragment extends Fragment {

    private ArtikelFragment.RecyclerViewReadyCallback recyclerViewReadyCallback;

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private TextView tv_judul,tv_produk_empty;
    private ProgressBar progressBar;

    private BottomNavigationView bottomNavigationView;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<BahanBakuModel> mList = new ArrayList<>();

    private DatabaseReference produkRefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bahan_baku_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setStatusBar();
        initialize();
    }

    private void initialize(){ // fungsi untuk inisiasi semua object yang ada pada layout beranda
        initRecyclerView();

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);

        tv_judul = getActivity().findViewById(R.id.judul_bahan_makanan);
        tv_produk_empty = getActivity().findViewById(R.id.tv_produk_empty);
        progressBar = getActivity().findViewById(R.id.pb_produk);

        produkRefs = FirebaseDatabase.getInstance().getReference().child("Produk");

        recyclerViewReadyCallback = new ArtikelFragment.RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() { // fungsi untuk mengecheck apakah recyclerview sudah siap untuk tampil semua item
                progressBar.setVisibility(View.GONE);
            }
        };

        Bundle bundle = new Bundle();
        bundle = getArguments();

        String judul = bundle.getString("jenis");

        tv_judul.setText(judul);

        produkRefs.child(judul).orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    tv_produk_empty.setVisibility(View.INVISIBLE);
                    mList.clear();
                    for (DataSnapshot dats:dataSnapshot.getChildren()){
                        mList.add(new BahanBakuModel(dats.child("nama_produk").getValue().toString(),dats.child("jenis").getValue().toString(),dats.child("deskripsi").getValue().toString(), Integer.valueOf(dats.child("stok").getValue().toString()), Integer.valueOf(dats.child("harga").getValue().toString())));
                        adapter.notifyDataSetChanged();

                        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if(recyclerViewReadyCallback != null){
                                    recyclerViewReadyCallback.onLayoutReady();
                                }
                                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }
                }else{
                    tv_produk_empty.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(){ // fungsi buat bikin object list resep makanan
        recyclerView = getActivity().findViewById(R.id.rv_bahan_baku);
        adapter = new BahanBakuAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setStatusBar(){ // fungsi buat ubah warna status bar
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

}
