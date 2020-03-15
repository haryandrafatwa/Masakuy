package com.example.masakuy.Feature.Beranda;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.Feature.Artikel.Recyclerview.ArtikelModel;
import com.example.masakuy.Feature.Beranda.Recyclerview.KomentarAdapter;
import com.example.masakuy.Feature.Beranda.Recyclerview.KomentarModel;
import com.example.masakuy.Feature.Beranda.Recyclerview.RecipeAdapter;
import com.example.masakuy.Feature.Beranda.Recyclerview.RecipeModel;
import com.example.masakuy.Feature.Recipe.MyMediaController;
import com.example.masakuy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeDetailFragment extends Fragment {

    private RecipeMore.RecyclerViewReadyCallback recyclerViewReadyCallback;

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private String key, nama_masakan,bahan,cara_masak,videoURL, oleh, deskripsi;
    private int lama_masak;

    private EditText et_komentar;
    private Button btnKirim;
    private TextView tv_nama_masakan, tv_oleh, tv_deskripsi, tv_komentar_empty,tv_lama_masak;
    private VideoView videoView;
    private MediaController mediaController;
    private ProgressBar progressBar;
    private ImageButton ib_share,ib_like,ib_arrow;
    private RelativeLayout relativeLayout;

    private List<KomentarModel> mList = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;

    private DatabaseReference komentarRefs,userRefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recipe_detail_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setStatusBar();
        initialize();
    }

    private void initialize(){

        Bundle bundle = this.getArguments();
        RecipeModel recipeModel = bundle.getParcelable("key");

        key = recipeModel.getKey();
        nama_masakan = recipeModel.getNama_masakan();
        bahan = recipeModel.getBahan();
        cara_masak = recipeModel.getCara_masak();
        lama_masak = recipeModel.getLama_masak();
        videoURL = recipeModel.getVideoURL();
        oleh = recipeModel.getOleh();
        deskripsi = recipeModel.getDeskripsi();

        initRecyclerView();

        komentarRefs = FirebaseDatabase.getInstance().getReference().child("Recipe").child(key);
        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        videoView = getActivity().findViewById(R.id.vv_recipe_detail);
        mediaController = new MediaController(getActivity(),false);
        tv_nama_masakan = getActivity().findViewById(R.id.tv_nama_masakan_detail);
        tv_oleh = getActivity().findViewById(R.id.tv_oleh);
        tv_deskripsi = getActivity().findViewById(R.id.tv_deskripsi_recipe_detail);
        tv_komentar_empty = getActivity().findViewById(R.id.tv_komentar_empty);
        recyclerView = getActivity().findViewById(R.id.rv_list_komentar);
        et_komentar = getActivity().findViewById(R.id.et_komentar);
        btnKirim = getActivity().findViewById(R.id.btn_kirim_komentar);
        ib_share = getActivity().findViewById(R.id.ib_share_recipe_detail);
        ib_like = getActivity().findViewById(R.id.ib_like_recipe_detail);
        tv_lama_masak = getActivity().findViewById(R.id.tv_lama_masak);
        relativeLayout = getActivity().findViewById(R.id.rv_tutorial_masak);
        ib_arrow = getActivity().findViewById(R.id.ib_arrow_tutorial_recipe);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("bahan",bahan);
                bundle.putString("manner",cara_masak);
                TutorialMasakFragment tutorialMasakFragment = new TutorialMasakFragment();
                tutorialMasakFragment.setArguments(bundle);
                setFragment(tutorialMasakFragment);
            }
        });

        ib_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("bahan",bahan);
                bundle.putString("manner",cara_masak);
                TutorialMasakFragment tutorialMasakFragment = new TutorialMasakFragment();
                tutorialMasakFragment.setArguments(bundle);
                setFragment(tutorialMasakFragment);
            }
        });

        ib_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/*");
                String shareBody = videoURL;
                String shareSub = nama_masakan;
                intent.putExtra(Intent.EXTRA_TEXT,shareSub+"\n\nLinkVideo:\n"+shareBody);
                getActivity().startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

        progressBar = getActivity().findViewById(R.id.pb_recipe_detail);

        tv_nama_masakan.setText(this.nama_masakan);
        tv_oleh.setText("By "+this.oleh);
        tv_deskripsi.setText(this.deskripsi);
        tv_lama_masak.setText(String.valueOf(this.lama_masak));

        komentarRefs.child("komentar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                recyclerView.setVisibility(View.VISIBLE);
                if (dataSnapshot.getChildrenCount() != 0){
                    tv_komentar_empty.setVisibility(View.GONE);for (DataSnapshot dats:dataSnapshot.getChildren()){
                        mList.add(new KomentarModel(dats.child("username").getValue().toString(),dats.child("message").getValue().toString()));
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
                }else{
                    tv_komentar_empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = et_komentar.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(getActivity(), "Silahkan isi komentar terlebih dahulu!", Toast.LENGTH_SHORT).show();
                }else{
                    komentarRefs.child("komentar").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final long komentarCount = dataSnapshot.getChildrenCount();
                            userRefs.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String username = dataSnapshot.child("username").getValue().toString();
                                    HashMap komentarMap = new HashMap();
                                    komentarMap.put("username",username);
                                    komentarMap.put("message",message);
                                    komentarRefs.child("komentar").child(String.valueOf(komentarCount+1)).updateChildren(komentarMap);
                                    et_komentar.setText(null);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        videoView.setVideoURI(Uri.parse(this.videoURL));
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                progressBar.setVisibility(View.GONE);

                MyMediaController mediaController = new MyMediaController(videoView.getContext(), false);

                RelativeLayout parentLayout = (RelativeLayout) videoView.getParent();
                RelativeLayout.LayoutParams parentParams = (RelativeLayout.LayoutParams) parentLayout.getLayoutParams();
                parentParams.height = videoView.getHeight();
                parentLayout.setLayoutParams(parentParams);

                FrameLayout frameLayout = (FrameLayout) mediaController.getParent();
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, videoView.getId());

                ((ViewGroup)frameLayout.getParent()).removeView(frameLayout);
                parentLayout.addView(frameLayout, layoutParams);

                mediaController.setAnchorView(videoView);
                mediaController.hide();

                videoView.setMediaController(mediaController);
            }
        });

        komentarRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String likeCount = dataSnapshot.child("likeCount").getValue().toString();
                if (likeCount.equals("0")){
                    ib_like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ib_like.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite));
                            HashMap likeMap = new HashMap();
                            likeMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            likeMap.put("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            komentarRefs.child("like").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(likeMap);
                            komentarRefs.child("likeCount").setValue(Integer.valueOf(likeCount)+1);
                        }
                    });
                }else{
                    komentarRefs.child("like").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot snapshot:dataSnapshot.getChildren()){
                                Log.d("POKOKNYEINIWOY","qwe: "+snapshot.getKey());
                                if(snapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    ib_like.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite));
                                    ib_like.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ib_like.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_border));
                                            komentarRefs.child("likeCount").setValue(Integer.valueOf(likeCount)-1);
                                            snapshot.getRef().removeValue();
                                        }
                                    });
                                    break;
                                }else{
                                    ib_like.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ib_like.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite));
                                            HashMap likeMap = new HashMap();
                                            likeMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            likeMap.put("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                            komentarRefs.child("like").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(likeMap);
                                            komentarRefs.child("likeCount").setValue(Integer.valueOf(likeCount)+1);
                                        }
                                    });
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initRecyclerView(){ // fungsi buat bikin object list resep makanan
        recyclerView = getActivity().findViewById(R.id.rv_list_komentar);
        adapter = new KomentarAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
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
