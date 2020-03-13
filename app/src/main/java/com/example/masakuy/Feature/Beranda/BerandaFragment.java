package com.example.masakuy.Feature.Beranda;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class BerandaFragment extends Fragment {

    private RecyclerViewReadyCallback recyclerViewReadyCallback;

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private RecyclerView rvListFood;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<RecipeModel> mList = new ArrayList<>();
    private List<RecipeModel> reverse = new ArrayList<>();
    private ProgressBar progressBar;

    private TextView tv_recipe_empty;

    private DatabaseReference recipeRefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.beranda_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setStatusBar();
        initialize();
    }

    private void initialize(){


        recipeRefs = FirebaseDatabase.getInstance().getReference().child("Recipe");

        progressBar = getActivity().findViewById(R.id.pb_food_recipe);
        tv_recipe_empty = getActivity().findViewById(R.id.tv_recipe_empty);

        recyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() {
                progressBar.setVisibility(View.GONE);
            }
        };

        initRecyclerView();

        recipeRefs.limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    tv_recipe_empty.setVisibility(View.INVISIBLE);
                    mList.clear();
                    for (DataSnapshot dats:dataSnapshot.getChildren()){
                        mList.add(new RecipeModel(dats.child("nama_masakan").getValue().toString(),dats.child("videoURL").getValue().toString()));
                        Log.d("981234783784",dats.child("nama_masakan").getValue().toString());
                        Log.d("981234783784",dats.child("videoURL").getValue().toString());
                        adapter.notifyDataSetChanged();

                        rvListFood.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if(recyclerViewReadyCallback != null){
                                    recyclerViewReadyCallback.onLayoutReady();
                                }
                                rvListFood.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }
                }else{
                    tv_recipe_empty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(){

        rvListFood = getActivity().findViewById(R.id.rv_food_item);
        adapter = new RecipeAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new GridLayoutManager(getActivity(),2);
        ((GridLayoutManager) mLayoutManager).setReverseLayout(false);
        rvListFood.setLayoutManager(mLayoutManager);
        rvListFood.setAdapter(adapter);
    }

    private void setStatusBar(){
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

}
