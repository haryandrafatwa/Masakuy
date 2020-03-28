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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.Feature.Beranda.Recyclerview.CartModel;
import com.example.masakuy.Feature.Beranda.Recyclerview.CheckoutAdapter;
import com.example.masakuy.Feature.Beranda.Recyclerview.CourierAdapter;
import com.example.masakuy.Feature.Beranda.Recyclerview.CourierModel;
import com.example.masakuy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckoutFragment extends Fragment {

    private RecyclerViewReadyCallback recyclerViewReadyCallback;

    private interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<CartModel> mList = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView tv_subtotal,tv_totalprice,tv_nama,tv_alamat,tv_nohp;
    private Button btn_checkout;

    private DatabaseReference cartRefs,historyRefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.checkout_fragment, container, false);
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
        historyRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("History");

        progressBar = getActivity().findViewById(R.id.pb_checkout);
        tv_subtotal = getActivity().findViewById(R.id.tv_subtotal_checkout);
        tv_totalprice = getActivity().findViewById(R.id.tv_total_checkout);
        tv_nama = getActivity().findViewById(R.id.tv_nama_courier_checkout);
        tv_alamat = getActivity().findViewById(R.id.tv_alamat_courier_checkout);
        tv_nohp = getActivity().findViewById(R.id.tv_nohp_courier_checkout);
        btn_checkout = getActivity().findViewById(R.id.btn_checkout_checkout);

        Bundle bundle = getArguments();
        final CourierModel model = bundle.getParcelable("courier");

        tv_nama.setText(model.getNama().toUpperCase());
        tv_alamat.setText(model.getAlamat().toUpperCase());
        tv_nohp.setText(model.getNohp());

        recyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() { // fungsi untuk mengecheck apakah recyclerview sudah siap untuk tampil semua item
                progressBar.setVisibility(View.GONE);
            }
        };

        cartRefs.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
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
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()!=0){
                            final String title = dataSnapshot.getChildrenCount()+"";
                            cartRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    historyRefs.child(title).child("Produk").setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            String[] splitPrice = tv_totalprice.getText().toString().split("Rp");
                                            HashMap courierMap = new HashMap();
                                            courierMap.put("nama_courier",model.getNama());
                                            courierMap.put("alamat",model.getAlamat());
                                            courierMap.put("phoneNumber",model.getNohp());
                                            courierMap.put("total_price",Integer.valueOf(splitPrice[1]));
                                            courierMap.put("status","on Progress");
                                            historyRefs.child("0").updateChildren(courierMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    cartRefs.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            initializeDialogCheckoutSuccess();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else{
                            cartRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    historyRefs.child("0").child("Produk").setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            String[] splitPrice = tv_totalprice.getText().toString().split("Rp");
                                            HashMap courierMap = new HashMap();
                                            courierMap.put("nama_courier",model.getNama());
                                            courierMap.put("alamat",model.getAlamat());
                                            courierMap.put("phoneNumber",model.getNohp());
                                            courierMap.put("total_price",Integer.valueOf(splitPrice[1]));
                                            courierMap.put("status","on Progress");
                                            historyRefs.child("0").updateChildren(courierMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    cartRefs.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            initializeDialogCheckoutSuccess();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    public void initializeDialogCheckoutSuccess(){ // fungsi untuk membuat alert dialog ketika ingin logout
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Checkout Success");

        alertDialog2.setCancelable(false);

        // Setting Dialog Message
        alertDialog2.setMessage("Pesanan diterima. Tunggu pesanan kamu sampai yaa ..");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                BerandaFragment berandaFragment = new BerandaFragment();
                setFragment(berandaFragment);
            }
        });

        alertDialog2.show();

    }

    private void initRecyclerView(){ // fungsi buat bikin object list resep makanan
        recyclerView = getActivity().findViewById(R.id.rv_checkout);
        adapter = new CheckoutAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment);
        fragmentTransaction.commit();
    }

    private void setStatusBar(){ // fungsi buat ubah warna status bar
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

}
