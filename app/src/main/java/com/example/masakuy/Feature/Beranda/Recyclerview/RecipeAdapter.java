package com.example.masakuy.Feature.Beranda.Recyclerview;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.R;

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


            viewHolder.vv_item.setVideoURI(Uri.parse(model.getVideoURL()));
            viewHolder.vv_item.seekTo(5000);
//            viewHolder.iv_item.setBackground(new BitmapDrawable(retriveVideoFrameFromVideo(model.getVideoURL())));
        }catch (Throwable throwable){

        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_nama_masakan;
        CardView iv_item;
        VideoView vv_item;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_nama_masakan = (TextView) itemView.findViewById(R.id.tv_item_name);
//            iv_item = (CardView) itemView.findViewById(R.id.cvItemListSetoran);
            vv_item = itemView.findViewById(R.id.vv_resep_makanan);
        }
    }
}
