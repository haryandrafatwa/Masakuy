package com.example.masakuy.Feature.Beranda.Recyclerview;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartModel> mList = new ArrayList<>();
    private Context mContext;
    private FragmentActivity mActivity;

    private DatabaseReference cartRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
    private DatabaseReference produkRefs = FirebaseDatabase.getInstance().getReference().child("Produk");

    public CartAdapter(List<CartModel> mList, Context mContext, FragmentActivity mActivity) {
        this.mList = mList;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View v = LayoutInflater.from(mContext).inflate(R.layout.cart_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int position = i;
        final CartModel model = mList.get(i);
        try{

            viewHolder.tv_nama.setText(model.getNama());
            viewHolder.tv_harga.setText("Rp"+model.getHarga());
            viewHolder.tv_amount.setText("Qty: "+model.getAmount());
            viewHolder.tv_berat.setText(model.getBerat()+" Kg");

            cartRefs.child(model.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int amount = Integer.valueOf(dataSnapshot.child("amount").getValue().toString());
                    if(amount == 1){
                        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cartRefs.child(model.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mActivity, "Hapus berhasil!", Toast.LENGTH_SHORT).show();
                                        produkRefs.child(model.getJenis()).child(model.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int stok = Integer.valueOf(dataSnapshot.child("stok").getValue().toString());
                                                produkRefs.child(model.getJenis()).child(model.getKey()).child("stok").setValue(stok+1);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }else{
                        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                produkRefs.child(model.getJenis()).child(model.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final int hargaSatuan = Integer.valueOf(dataSnapshot.child("harga").getValue().toString());
                                        int stok = Integer.valueOf(dataSnapshot.child("stok").getValue().toString());
                                        produkRefs.child(model.getJenis()).child(model.getKey()).child("stok").setValue(stok+1);
                                        cartRefs.child(model.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int harga = Integer.valueOf(dataSnapshot.child("harga").getValue().toString());
                                                int amount = Integer.valueOf(dataSnapshot.child("amount").getValue().toString());
                                                int berat = Integer.valueOf(dataSnapshot.child("total_berat").getValue().toString());
                                                HashMap cartMap = new HashMap();
                                                cartMap.put("amount",amount-1);
                                                cartMap.put("harga",harga-hargaSatuan);
                                                cartMap.put("total_berat",berat-1);
                                                cartRefs.child(model.getKey()).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {

                                                        Toast.makeText(mActivity, "Hapus berhasil!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Throwable throwable){

        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_nama, tv_harga, tv_berat, tv_amount;
        ImageButton btn_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_nama = itemView.findViewById(R.id.tv_nama_produk_cart);
            tv_harga = itemView.findViewById(R.id.tv_harga_cart);
            tv_berat = itemView.findViewById(R.id.tv_berat_cart);
            tv_amount = itemView.findViewById(R.id.tv_quantity_cart);
            btn_delete = itemView.findViewById(R.id.ib_delete_cart);
        }
    }
}
