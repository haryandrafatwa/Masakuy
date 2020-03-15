package com.example.masakuy.Feature.Beranda;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.Feature.Beranda.Recyclerview.RecipeAdapter;
import com.example.masakuy.Feature.Beranda.Recyclerview.RecipeModel;
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
    private ImageButton ib_recipe;

    private RecipeMore recipeMore;

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

    private void initialize(){ // fungsi untuk inisiasi semua object yang ada pada layout beranda

        recipeRefs = FirebaseDatabase.getInstance().getReference().child("Recipe");

        progressBar = getActivity().findViewById(R.id.pb_food_recipe);
        tv_recipe_empty = getActivity().findViewById(R.id.tv_recipe_empty);
        ib_recipe = getActivity().findViewById(R.id.ib_arrow_more_recipe);

        recyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() { // fungsi untuk mengecheck apakah recyclerview sudah siap untuk tampil semua item
                progressBar.setVisibility(View.GONE);
            }
        };

        ib_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipeMore = new RecipeMore();
                setFragment(recipeMore);
            }
        });

        initRecyclerView();

        recipeRefs.limitToLast(4).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    tv_recipe_empty.setVisibility(View.INVISIBLE);
                    mList.clear();
                    reverse.clear();
                    for (DataSnapshot dats:dataSnapshot.getChildren()){
                        mList.add(new RecipeModel(dats.getKey(),dats.child("nama_masakan").getValue().toString(),dats.child("bahan").getValue().toString(),dats.child("cara_masak").getValue().toString(),
                                Integer.valueOf(dats.child("lama_masak").getValue().toString()),dats.child("oleh").getValue().toString(),dats.child("videoURL").getValue().toString(), dats.child("deskripsi").getValue().toString()));
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
                    for (int i = 0; i < mList.size(); i++) {
                        Log.d("mList ===>",mList.get(i).getNama_masakan());
                    }
                    Collections.reverse(mList);
                    reverse.addAll(mList);
                    for (int i = 0; i < reverse.size(); i++) {
                        Log.d("reverse ===>",reverse.get(i).getNama_masakan());
                    }
                }else{
                    tv_recipe_empty.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(){ // fungsi buat bikin object list resep makanan
        rvListFood = getActivity().findViewById(R.id.rv_food_item);
        adapter = new RecipeAdapter(reverse,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new GridLayoutManager(getActivity(),2);
        rvListFood.setLayoutManager(mLayoutManager);
        rvListFood.setAdapter(adapter);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setStatusBar(){ // fungsi buat ubah warna status bar
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

}
