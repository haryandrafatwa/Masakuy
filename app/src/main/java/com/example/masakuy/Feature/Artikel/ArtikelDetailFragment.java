package com.example.masakuy.Feature.Artikel;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masakuy.Feature.Artikel.Recyclerview.ArtikelModel;
import com.example.masakuy.Feature.Beranda.RecipeMore;
import com.example.masakuy.Feature.Beranda.Recyclerview.KomentarAdapter;
import com.example.masakuy.Feature.Beranda.Recyclerview.KomentarModel;
import com.example.masakuy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class ArtikelDetailFragment extends Fragment {

    private RecipeMore.RecyclerViewReadyCallback recyclerViewReadyCallback;

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private String key, subject,body,imageURL;
    private int likeCount;
    private ImageView imageView;
    private TextView tv_artikel_title,tv_artikel_body,tv_like_count,tv_komentar_empty;
    private ImageButton ib_share, ib_like;

    private EditText et_komentar;
    private Button btn_kirim;

    private List<KomentarModel> mList = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;

    private DatabaseReference artikelRefs,userRefs;

    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.artikel_detail_fragment, container, false);
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

        Bundle bundle = this.getArguments();
        ArtikelModel artikelModel = bundle.getParcelable("key");

        setKey(artikelModel.getKey());
        setSubject(artikelModel.getSubject());
        setBody(artikelModel.getBody());
        setImageURL(artikelModel.getImageURL());
        setLikeCount(artikelModel.getLikeCount());

        initRecyclerView();

        imageView = getActivity().findViewById(R.id.iv_artikel_detail);
        tv_artikel_title = getActivity().findViewById(R.id.tv_artikel_title_detail);
        tv_artikel_body = getActivity().findViewById(R.id.tv_body_artikel);
        tv_like_count = getActivity().findViewById(R.id.tv_like_count);
        tv_komentar_empty = getActivity().findViewById(R.id.tv_komentar_empty);
        et_komentar = getActivity().findViewById(R.id.et_komentar);
        btn_kirim = getActivity().findViewById(R.id.btn_kirim_komentar);

        Picasso.get().load(this.imageURL).fit().into(imageView);
        tv_artikel_title.setText(this.subject);
        tv_artikel_body.setText(this.body);
        tv_like_count.setText(String.valueOf(this.likeCount)+" Likes");
        ib_share = (ImageButton) getActivity().findViewById(R.id.ib_share_artikel_detail);
        ib_like = (ImageButton) getActivity().findViewById(R.id.ib_like_artikel_detail);

        ib_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = getBody();
                String shareSub = getSubject();
                intent.putExtra(Intent.EXTRA_TEXT,shareSub+"\n\n"+shareBody);
                getActivity().startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

        artikelRefs = FirebaseDatabase.getInstance().getReference().child("Artikel").child(getKey());
        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        artikelRefs.addValueEventListener(new ValueEventListener() {
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
                            artikelRefs.child("like").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(likeMap);
                            artikelRefs.child("likeCount").setValue(Integer.valueOf(likeCount)+1);
                            tv_like_count.setText(String.valueOf(Integer.valueOf(likeCount)+1)+" Likes");
                        }
                    });
                }else{
                    artikelRefs.child("like").addValueEventListener(new ValueEventListener() {
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
                                            artikelRefs.child("likeCount").setValue(Integer.valueOf(likeCount)-1);
                                            tv_like_count.setText(String.valueOf(Integer.valueOf(likeCount)-1)+" Likes");
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
                                            artikelRefs.child("like").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(likeMap);
                                            artikelRefs.child("likeCount").setValue(Integer.valueOf(likeCount)+1);
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
        });

        artikelRefs.child("komentar").addValueEventListener(new ValueEventListener() {
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

        btn_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = et_komentar.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(getActivity(), "Silahkan isi komentar terlebih dahulu!", Toast.LENGTH_SHORT).show();
                }else{
                    artikelRefs.child("komentar").addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    artikelRefs.child("komentar").child(String.valueOf(komentarCount+1)).updateChildren(komentarMap);
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

    }

    private void initRecyclerView(){ // fungsi buat bikin object list resep makanan
        recyclerView = getActivity().findViewById(R.id.rv_list_komentar);
        adapter = new KomentarAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setStatusBar(){ // fungsi buat ubah warna status bar
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
