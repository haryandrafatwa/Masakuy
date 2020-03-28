package com.example.masakuy.Feature.Beranda;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.Feature.Auth.LoginActivity;
import com.example.masakuy.Feature.Beranda.Recyclerview.BahanBakuAdapter;
import com.example.masakuy.Feature.Beranda.Recyclerview.BahanBakuModel;
import com.example.masakuy.Feature.Beranda.Recyclerview.CartAdapter;
import com.example.masakuy.Feature.Beranda.Recyclerview.CartModel;
import com.example.masakuy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    private RecyclerViewReadyCallback recyclerViewReadyCallback;

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private ProgressBar progressBar;
    private TextView tv_cart_empty,tv_subtotal,tv_totalprice;
    private RelativeLayout layoutResume;
    private Button btn_placeorder;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<CartModel> mList = new ArrayList<>();

    private DatabaseReference cartRefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cart_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setStatusBar();
        initialize();
    }

    private void initialize(){ // fungsi untuk inisiasi semua object yang ada pada layout beranda
        initRecyclerView();

        cartRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");

        progressBar = getActivity().findViewById(R.id.pb_cart);
        tv_cart_empty = getActivity().findViewById(R.id.tv_cart_empty);
        tv_subtotal = getActivity().findViewById(R.id.tv_subtotal_cart);
        tv_totalprice = getActivity().findViewById(R.id.tv_total_cart);
        layoutResume = getActivity().findViewById(R.id.layout_resume);
        btn_placeorder = getActivity().findViewById(R.id.btn_placeorder_cart);


        recyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() { // fungsi untuk mengecheck apakah recyclerview sudah siap untuk tampil semua item
                progressBar.setVisibility(View.GONE);
                layoutResume.setVisibility(View.VISIBLE);
                btn_placeorder.setVisibility(View.VISIBLE);
            }
        };

        cartRefs.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    tv_cart_empty.setVisibility(View.INVISIBLE);
                    mList.clear();
                    int harga = 0;
                    for (DataSnapshot dats:dataSnapshot.getChildren()){
                        mList.add(new CartModel(dats.getKey(),dats.child("nama_produk").getValue().toString(), dats.child("jenis").getValue().toString(), Integer.valueOf(dats.child("total_berat").getValue().toString()), Integer.valueOf(dats.child("amount").getValue().toString()), Integer.valueOf(dats.child("harga").getValue().toString())));
                        harga += Integer.valueOf(dats.child("harga").getValue().toString());

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
                    tv_subtotal.setText("Rp"+harga);
                    tv_totalprice.setText("Rp"+harga);
                }else{
                    tv_cart_empty.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    layoutResume.setVisibility(View.GONE);
                    btn_placeorder.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeDialogChooseCourier();
            }
        });
    }

    private void initRecyclerView(){ // fungsi buat bikin object list resep makanan
        recyclerView = getActivity().findViewById(R.id.rv_cart);
        adapter = new CartAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void initializeDialogChooseCourier(){ // fungsi untuk membuat alert dialog ketika ingin logout
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Payment Method");

        alertDialog2.setCancelable(false);

        // Setting Dialog Message
        alertDialog2.setMessage("Hay, Pembayaran saat ini hanya bisa dilakukan dengan COD yaa. Silahkan tekan oke untuk memilih kurir yang sedang menunggumu ..");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                CourierFragment courierFragment = new CourierFragment();
                setFragment(courierFragment);
            }
        });

        alertDialog2.show();

    }

    private void setStatusBar(){ // fungsi buat ubah warna status bar
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

}
