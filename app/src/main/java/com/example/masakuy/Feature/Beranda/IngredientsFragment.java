package com.example.masakuy.Feature.Beranda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.masakuy.R;

public class IngredientsFragment extends Fragment {

    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ingredients_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setStatusBar();
        initialize();
    }

    private void initialize(){
        textView = getActivity().findViewById(R.id.tv_ingredients_recipe_detail);

        Bundle bundle = this.getArguments();
        String bahan = bundle.getString("bahan");
        textView.setText(bahan);
    }

    private void setStatusBar(){ // fungsi buat ubah warna status bar
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

}
