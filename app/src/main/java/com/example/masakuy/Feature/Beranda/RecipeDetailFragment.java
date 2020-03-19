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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.Feature.Beranda.Recyclerview.KomentarAdapter;
import com.example.masakuy.Feature.Beranda.Recyclerview.KomentarModel;
import com.example.masakuy.Feature.Beranda.Recyclerview.RecipeModel;
import com.example.masakuy.Feature.Tambah.MyMediaController;
import com.example.masakuy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeDetailFragment extends Fragment {

    private RecipeMore.RecyclerViewReadyCallback recyclerViewReadyCallback;

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private String key, nama_masakan,bahan,cara_masak,videoURL, oleh, deskripsi;
    private int lama_masak,likeCount;

    private EditText et_komentar;
    private Button btnKirim,btn_update;
    private TextView tv_nama_masakan, tv_oleh, tv_komentar_empty,tv_lama_masak, tv_edit_deskripsi,tv_like_count;
    private EditText tv_deskripsi;
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

    private BottomNavigationView bottomNavigationView;

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

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);

        initRecyclerView();

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
        tv_edit_deskripsi = getActivity().findViewById(R.id.tv_edit_deskrispi);
        btn_update = getActivity().findViewById(R.id.btn_update_deskripsi_resep);
        tv_like_count = getActivity().findViewById(R.id.tv_like_count);

        final Bundle bundleRecipe = this.getArguments();
        final RecipeModel recipeModel = bundleRecipe.getParcelable("key");

        key = recipeModel.getKey();

        komentarRefs = FirebaseDatabase.getInstance().getReference().child("Recipe").child(key);
        komentarRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeModel.setKey(key);
                recipeModel.setBahan(dataSnapshot.child("bahan").getValue().toString());
                recipeModel.setCara_masak(dataSnapshot.child("cara_masak").getValue().toString());
                recipeModel.setDeskripsi(dataSnapshot.child("deskripsi").getValue().toString());
                recipeModel.setEmail(dataSnapshot.child("email").getValue().toString());
                recipeModel.setImageURL(dataSnapshot.child("imageURL").getValue().toString());
                recipeModel.setLama_masak(Integer.valueOf(dataSnapshot.child("lama_masak").getValue().toString()));
                recipeModel.setNama_masakan(dataSnapshot.child("nama_masakan").getValue().toString());
                recipeModel.setOleh(dataSnapshot.child("oleh").getValue().toString());
                recipeModel.setVideoURL(dataSnapshot.child("videoURL").getValue().toString());
                recipeModel.setLikeCount(Integer.valueOf(dataSnapshot.child("likeCount").getValue().toString()));
                if (dataSnapshot.child("email").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    tv_edit_deskripsi.setVisibility(View.VISIBLE);
                }else{
                    tv_edit_deskripsi.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        komentarRefs.child("like").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0){
                    ib_like.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_border));
                    ib_like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap likeMap = new HashMap();
                            likeMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            likeMap.put("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            komentarRefs.child("like").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(likeMap);
                            komentarRefs.child("likeCount").setValue(dataSnapshot.getChildrenCount()+1);
                            tv_like_count.setText((dataSnapshot.getChildrenCount()+1)+" Likes");
                        }
                    });
                }else{
                    for (DataSnapshot dats: dataSnapshot.getChildren()){
                        if (dats.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            ib_like.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite));
                            komentarRefs.child("likeCount").setValue(dataSnapshot.getChildrenCount()-1);
                            tv_like_count.setText((dataSnapshot.getChildrenCount()-1)+" Likes");
                            dats.getRef().removeValue();
                            ib_like.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_border));
                        }else{
                            ib_like.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_border));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nama_masakan = recipeModel.getNama_masakan();
        bahan = recipeModel.getBahan();
        cara_masak = recipeModel.getCara_masak();
        lama_masak = recipeModel.getLama_masak();
        videoURL = recipeModel.getVideoURL();
        oleh = recipeModel.getOleh();
        deskripsi = recipeModel.getDeskripsi();
        likeCount = recipeModel.getLikeCount();

        tv_edit_deskripsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_deskripsi.setFocusableInTouchMode(true);
                btn_update.setVisibility(View.VISIBLE);
                tv_edit_deskripsi.setVisibility(View.GONE);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deskripsi = tv_deskripsi.getText().toString();
                if(TextUtils.isEmpty(deskripsi)){
                    Toast.makeText(getActivity(), "Silahkan isi deskripsi terlebih dahulu!", Toast.LENGTH_SHORT).show();
                }else{
                    komentarRefs.child("deskripsi").setValue(deskripsi).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "Update berhasil!", Toast.LENGTH_SHORT).show();
                            tv_deskripsi.setFocusable(false);
                            btn_update.setVisibility(View.GONE);
                            tv_edit_deskripsi.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("key",recipeModel);
                TutorialMasakFragment tutorialMasakFragment = new TutorialMasakFragment();
                tutorialMasakFragment.setArguments(bundle);
                setFragment(tutorialMasakFragment);
            }
        });

        ib_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("key",recipeModel);
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
                intent.putExtra(Intent.EXTRA_TEXT,"Nama Masakan: "+shareSub+"\n\nLama Masak: "+lama_masak+"\n\nIngredients:\n"+bahan+"\n\nCara Memasak:\n"+cara_masak+"\n\nDeskripsi:\n"+deskripsi+"\n\nLinkVideo:\n"+shareBody);
                getActivity().startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

        progressBar = getActivity().findViewById(R.id.pb_recipe_detail);

        tv_nama_masakan.setText(this.nama_masakan);
        tv_oleh.setText("By "+this.oleh);
        tv_deskripsi.setText(this.deskripsi);
        tv_lama_masak.setText(String.valueOf(this.lama_masak));
        tv_like_count.setText(likeCount+" Likes");

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

        /*komentarRefs.addValueEventListener(new ValueEventListener() {
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
                                if(snapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    Log.d("POKOKNYEINIWOY","qwe: "+snapshot.getKey());
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
        });*/

        /*komentarRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String likeCount = dataSnapshot.child("likeCount").getValue().toString();
                Log.d("INILIKECOUNTEUY","qwe: "+likeCount);
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
                            tv_like_count.setText(String.valueOf(Integer.valueOf(likeCount)+1)+" Likes");
                        }
                    });
                }else{
                    komentarRefs.child("like").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot snapshot:dataSnapshot.getChildren()){
                                Log.d("POKOKNYEINIWOY","qwe: "+snapshot.getKey());
                                if(snapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    Log.d("KEYSAMAKAYAKUIDNIH","qwe: "+snapshot.getKey());
                                    ib_like.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite));
                                    ib_like.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ib_like.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_border));
                                            komentarRefs.child("likeCount").setValue(Integer.valueOf(likeCount)-1);
                                            tv_like_count.setText(String.valueOf(Integer.valueOf(likeCount)-1)+" Likes");
                                            snapshot.getRef().removeValue();
                                        }
                                    });
                                    break;
                                }else{
                                    ib_like.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.d("KEYBEDAKAYAKUIDNIH","qwe: "+snapshot.getKey());
                                            ib_like.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite));
                                            HashMap likeMap = new HashMap();
                                            likeMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            likeMap.put("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                            komentarRefs.child("like").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(likeMap);
                                            komentarRefs.child("likeCount").setValue(Integer.valueOf(likeCount)+1);
                                            tv_like_count.setText(String.valueOf(Integer.valueOf(likeCount)+1)+" Likes");
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
        });*/

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
