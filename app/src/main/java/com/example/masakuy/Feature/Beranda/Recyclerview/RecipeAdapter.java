package com.example.masakuy.Feature.Beranda.Recyclerview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.Feature.Artikel.ArtikelDetailFragment;
import com.example.masakuy.Feature.Beranda.ResepMakanan.RecipeDetailFragment;
import com.example.masakuy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<RecipeModel> mList = new ArrayList<>();
    private Context mContext;
    private FragmentActivity mActivity;

    public RecipeAdapter(List<RecipeModel> mList, Context mContext, FragmentActivity mActivity) {
        this.mList = mList;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View v = LayoutInflater.from(mContext).inflate(R.layout.recipe_list,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final int position = i;
        final RecipeModel model = mList.get(i);

        try{
            viewHolder.tv_nama_masakan.setText(model.getNama_masakan());

            Log.d("KOKODAKD",model.getImageURL());
            Picasso.get().load(model.getImageURL()).fit().into(viewHolder.iv_item);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("key",model);
                    RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
                    recipeDetailFragment.setArguments(bundle);
                    setFragment(recipeDetailFragment);
                }
            });
        }catch (Throwable throwable){

        }

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_nama_masakan;
        ImageView iv_item;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_nama_masakan = (TextView) itemView.findViewById(R.id.tv_item_name);
            iv_item = itemView.findViewById(R.id.iv_resep_makanan);
        }
    }
}
