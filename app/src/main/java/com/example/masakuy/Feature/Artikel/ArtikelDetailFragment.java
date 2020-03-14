package com.example.masakuy.Feature.Artikel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.masakuy.Feature.Artikel.Recyclerview.ArtikelModel;
import com.example.masakuy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ArtikelDetailFragment extends Fragment {

    private String key, subject,body,imageURL;
    private ImageView imageView;
    private TextView tv_artikel_title,tv_artikel_body;
    private ImageButton ib_share, ib_like;

    private DatabaseReference artikelRefs;

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

        Bundle bundle = this.getArguments();
        ArtikelModel artikelModel = bundle.getParcelable("key");

        setKey(artikelModel.getKey());
        setSubject(artikelModel.getSubject());
        setBody(artikelModel.getBody());
        setImageURL(artikelModel.getImageURL());

        imageView = getActivity().findViewById(R.id.iv_artikel_detail);
        tv_artikel_title = getActivity().findViewById(R.id.tv_artikel_title_detail);
        tv_artikel_body = getActivity().findViewById(R.id.tv_body_artikel);

        Picasso.get().load(this.imageURL).fit().into(imageView);
        tv_artikel_title.setText(this.subject);
        tv_artikel_body.setText(this.body);
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

        Toast.makeText(getActivity(), "123: "+getKey(), Toast.LENGTH_SHORT).show();

        artikelRefs = FirebaseDatabase.getInstance().getReference().child("Artikel").child(getKey());
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
}
