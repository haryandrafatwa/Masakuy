package com.example.masakuy.Feature.Beranda.Recyclerview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.Feature.Beranda.ResepMakanan.RecipeDetailFragment;
import com.example.masakuy.R;

import java.util.ArrayList;
import java.util.List;

public class KomentarAdapter extends RecyclerView.Adapter<KomentarAdapter.ViewHolder> {

    private List<KomentarModel> mList = new ArrayList<>();
    private Context mContext;
    private FragmentActivity mActivity;

    public KomentarAdapter(List<KomentarModel> mList, Context mContext, FragmentActivity mActivity) {
        this.mList = mList;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View v = LayoutInflater.from(mContext).inflate(R.layout.komentar_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final int position = i;
        final KomentarModel model = mList.get(i);

        try{
            viewHolder.tv_username.setText(model.getUsername());
            viewHolder.tv_message.setText(model.getMessage());

        }catch (Throwable throwable){

        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_username, tv_message;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_username = (TextView) itemView.findViewById(R.id.tv_username_komentar);
            tv_message = itemView.findViewById(R.id.tv_message_komentar);
        }
    }
}
