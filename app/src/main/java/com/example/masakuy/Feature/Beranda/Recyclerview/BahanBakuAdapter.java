package com.example.masakuy.Feature.Beranda.Recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class BahanBakuAdapter extends RecyclerView.Adapter<BahanBakuAdapter.ViewHolder> {

    private List<BahanBakuModel> mList = new ArrayList<>();
    private Context mContext;
    private FragmentActivity mActivity;

    public BahanBakuAdapter(List<BahanBakuModel> mList, Context mContext, FragmentActivity mActivity) {
        this.mList = mList;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View v = LayoutInflater.from(mContext).inflate(R.layout.list_bahan_baku,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int position = i;
        final BahanBakuModel model = mList.get(i);

        final DatabaseReference userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference produkRefs = FirebaseDatabase.getInstance().getReference().child("Produk").child(model.getJenis()).child(model.getKey());

        try{
            viewHolder.tv_nama.setText(model.getNama());
            viewHolder.tv_harga.setText("Rp"+model.getHarga()+" /Kg");
            viewHolder.tv_stok.setText("Stok: "+model.getStok()+" Kg");
            viewHolder.tv_deskripsi.setText(model.getDeskripsi());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolder.layout_expand.getVisibility() == View.GONE){
                        viewHolder.iv_arrow.setBackground(mActivity.getDrawable(R.drawable.ic_keyboard_arrow_up));
                        viewHolder.layout_expand.setVisibility(View.VISIBLE);
                    }else {
                        viewHolder.iv_arrow.setBackground(mActivity.getDrawable(R.drawable.ic_keyboard_arrow_down));
                        viewHolder.layout_expand.setVisibility(View.GONE);
                    }
                }
            });

            viewHolder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userRefs.child("Cart").child(model.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()!= 0){
                                final int amount, harga, berat;
                                amount  = Integer.valueOf(dataSnapshot.child("amount").getValue().toString());
                                harga  = Integer.valueOf(dataSnapshot.child("harga").getValue().toString());
                                berat  = Integer.valueOf(dataSnapshot.child("total_berat").getValue().toString());
                                produkRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        final int stok = Integer.valueOf(dataSnapshot.child("stok").getValue().toString());

                                        if ( stok > 0){
                                            HashMap produkMap = new HashMap();
                                            produkMap.put("amount",amount+1);
                                            produkMap.put("total_berat",berat+1);
                                            produkMap.put("harga",harga+model.getHarga());
                                            produkMap.put("jenis",model.getJenis());
                                            userRefs.child("Cart").child(model.getKey()).updateChildren(produkMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    Toast.makeText(mActivity, "Tambah Produk Berhasil!", Toast.LENGTH_SHORT).show();
                                                    dataSnapshot.child("stok").getRef().setValue(stok-1);
                                                }
                                            });
                                        }else{
                                            Toast.makeText(mActivity, "Stok habis!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }else{
                                produkRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        final int stok = Integer.valueOf(dataSnapshot.child("stok").getValue().toString());

                                        if ( stok > 0){
                                            HashMap produkMap = new HashMap();
                                            produkMap.put("nama_produk",model.getNama());
                                            produkMap.put("amount",1);
                                            produkMap.put("total_berat",1);
                                            produkMap.put("harga",model.getHarga());
                                            produkMap.put("jenis",model.getJenis());
                                            userRefs.child("Cart").child(model.getKey()).updateChildren(produkMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    Toast.makeText(mActivity, "Tambah Produk Berhasil!", Toast.LENGTH_SHORT).show();
                                                    dataSnapshot.child("stok").getRef().setValue(stok-1);
                                                }
                                            });
                                        }else{
                                            Toast.makeText(mActivity, "Stok habis!", Toast.LENGTH_SHORT).show();
                                        }
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

        }catch (Throwable throwable){

        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_nama, tv_harga, tv_deskripsi, tv_stok;
        RelativeLayout layout_expand;
        ImageView iv_arrow;
        Button btn_add;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_nama = itemView.findViewById(R.id.nama_bahan_masakan);
            tv_harga = itemView.findViewById(R.id.tv_harga_bahan_makanan);
            tv_deskripsi = itemView.findViewById(R.id.tv_deskripsi_bahan_makanan);
            tv_stok = itemView.findViewById(R.id.tv_stok_bahan_makanan);
            layout_expand = itemView.findViewById(R.id.layout_expand);
            iv_arrow = itemView.findViewById(R.id.iv_arrow_down);
            btn_add = itemView.findViewById(R.id.btn_tambah_produk);
        }
    }
}
