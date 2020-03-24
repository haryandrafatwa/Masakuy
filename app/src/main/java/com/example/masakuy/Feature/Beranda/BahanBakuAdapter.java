package com.example.masakuy.Feature.Beranda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.R;

import java.util.ArrayList;
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

        public ViewHolder(View itemView) {
            super(itemView);
            tv_nama = itemView.findViewById(R.id.nama_bahan_masakan);
            tv_harga = itemView.findViewById(R.id.tv_harga_bahan_makanan);
            tv_deskripsi = itemView.findViewById(R.id.tv_deskripsi_bahan_makanan);
            tv_stok = itemView.findViewById(R.id.tv_stok_bahan_makanan);
            layout_expand = itemView.findViewById(R.id.layout_expand);
            iv_arrow = itemView.findViewById(R.id.iv_arrow_down);
        }
    }
}
