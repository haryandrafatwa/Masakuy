package com.example.masakuy.Feature.Profile;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.Feature.Beranda.BerandaFragment;
import com.example.masakuy.Feature.Beranda.Recyclerview.RecipeAdapter;
import com.example.masakuy.Feature.Beranda.Recyclerview.RecipeModel;
import com.example.masakuy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private ProfileFragment.RecyclerViewReadyCallback recyclerViewReadyCallback;

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private String username, email;
    private ImageButton ib_setting;
    private CircleImageView circleImageView;
    private TextView tv_username,tv_riwayat_kosong;
    private ProgressBar pb_photo,pb_riwayat;

    private DatabaseReference userRefs,recipeRefs;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<RecipeModel> mList = new ArrayList<>();
    private List<RecipeModel> reverse = new ArrayList<>();

    private BottomNavigationView bottomNavigationView;
    private SettingFragment settingFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
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

        settingFragment = new SettingFragment();

        ib_setting = getActivity().findViewById(R.id.ib_setting);
        circleImageView = getActivity().findViewById(R.id.civ_photoprofile);
        tv_username = getActivity().findViewById(R.id.tv_username);
        tv_riwayat_kosong = getActivity().findViewById(R.id.tv_riwayat_empty);
        pb_photo = getActivity().findViewById(R.id.progress_profile);
        pb_riwayat = getActivity().findViewById(R.id.pb_riwayat);

        initRecyclerView();

        ib_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(settingFragment);
            }
        });

        recyclerViewReadyCallback = new ProfileFragment.RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() {
                pb_riwayat.setVisibility(View.GONE);
            }
        };

        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRefs.addValueEventListener(new ValueEventListener() { // fungsi untuk ngambil informasi user pd firebase database
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                username = dataSnapshot.child("username").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                tv_username.setText(dataSnapshot.child("username").getValue().toString());

                if(dataSnapshot.hasChild("displayPicture")){
                    Picasso.get().load(dataSnapshot.child("displayPicture").getValue().toString()).into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            circleImageView.setVisibility(View.VISIBLE);
                            pb_photo.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }else{
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/masakuy-telu.appspot.com/o/DisplayPictures%2Fdummy%2Fic_user.png?alt=media&token=31c1365c-3809-4c06-a013-218f2265eabb").into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recipeRefs = FirebaseDatabase.getInstance().getReference().child("Recipe");

        recipeRefs.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    tv_riwayat_kosong.setVisibility(View.INVISIBLE);
                    mList.clear();
                    reverse.clear();
                    for (DataSnapshot dats:dataSnapshot.getChildren()){
                        String emailRecipe = dats.child("email").getValue().toString().toLowerCase();
                        String usernameRecipe = dats.child("oleh").getValue().toString().toLowerCase();
                        if (emailRecipe.equals(email.toLowerCase()) && usernameRecipe.equals(username.toLowerCase())){
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
                    tv_riwayat_kosong.setVisibility(View.VISIBLE);
                    pb_riwayat.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initRecyclerView(){

        recyclerView = getActivity().findViewById(R.id.rv_riwayat);
        adapter = new RecipeAdapter(reverse,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setStatusBar(){
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

}
