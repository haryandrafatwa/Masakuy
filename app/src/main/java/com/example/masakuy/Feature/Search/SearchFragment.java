package com.example.masakuy.Feature.Search;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.Feature.Beranda.RecipeMore;
import com.example.masakuy.Feature.Beranda.Recyclerview.RecipeAdapter;
import com.example.masakuy.Feature.Beranda.Recyclerview.RecipeModel;
import com.example.masakuy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecipeMore.RecyclerViewReadyCallback recyclerViewReadyCallback;

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private EditText et_search;
    private DatabaseReference recipeRefs;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<RecipeModel> mList = new ArrayList<>();
    private ProgressBar progressBar;

    private TextView tv_recipe_not_found,tv_recipe_is_empty;

    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setStatusBar();
        initialize();
    }

    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.VISIBLE);

        et_search = getActivity().findViewById(R.id.et_search);
        recipeRefs = FirebaseDatabase.getInstance().getReference().child("Recipe");
        progressBar = getActivity().findViewById(R.id.pb_search_food_recipe);
        tv_recipe_not_found = getActivity().findViewById(R.id.tv_recipe_not_found);
        tv_recipe_is_empty = getActivity().findViewById(R.id.tv_recipe_is_empty);
        progressBar.setVisibility(View.INVISIBLE);

        recyclerViewReadyCallback = new RecipeMore.RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() { // fungsi untuk mengecheck apakah recyclerview sudah siap untuk tampil semua item
                progressBar.setVisibility(View.GONE);
            }
        };

        initRecyclerView();

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent != null &&
                                keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (keyEvent == null || !keyEvent.isShiftPressed()) {
                        // the user is done typing.

                        final String subject = textView.getText().toString();
                        tv_recipe_not_found.setVisibility(View.GONE);
                        tv_recipe_is_empty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);

                        recipeRefs.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getChildrenCount() != 0){
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        String nama_masakan = snapshot.child("nama_masakan").getValue().toString().toLowerCase();
                                        if (nama_masakan.contains(subject.toLowerCase())){
                                            if (snapshot.getChildrenCount() != 0){
                                                mList.clear();
                                                snapshot.getRef().orderByKey().addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dats) {
                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        tv_recipe_not_found.setVisibility(View.GONE);
                                                        tv_recipe_is_empty.setVisibility(View.GONE);

                                                        mList.add(new RecipeModel(dats.getKey(),dats.child("nama_masakan").getValue().toString(),dats.child("bahan").getValue().toString(),dats.child("cara_masak").getValue().toString(),
                                                                Integer.valueOf(dats.child("lama_masak").getValue().toString()),dats.child("oleh").getValue().toString(),dats.child("email").getValue().toString(),dats.child("imageURL").getValue().toString(),dats.child("videoURL").getValue().toString(), dats.child("deskripsi").getValue().toString(),Integer.valueOf(dats.child("likeCount").getValue().toString())));
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

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }else{
                                                tv_recipe_not_found.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.INVISIBLE);
                                            }
                                        }else{
                                            tv_recipe_not_found.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }else{
                                    tv_recipe_is_empty.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        return true; // consume.
                    }
                }
                return false;
            }
        });

    }

    private void initRecyclerView(){ // fungsi buat bikin object list resep makanan
        recyclerView = getActivity().findViewById(R.id.rv_search_food_item);
        adapter = new RecipeAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setStatusBar(){ // fungsi buat ubah warna status bar
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

}
