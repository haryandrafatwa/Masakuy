package com.example.masakuy.Feature.Recipe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.masakuy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class RecipeFragment extends Fragment {

    private Button btnTambahkan;
    private ImageButton btn_addvideo;
    private TextView tv_hapus_video;
    private EditText et_nama_masakan, et_bahan, et_cara_masak, et_lama_masak, et_deskripsi;

    private final int PICK_IMAGE_REQUEST = 100;
    private Uri filePath;
    private VideoView videoView;
    private MediaController mediaController;

    private DatabaseReference recipeRefs,userRefs;
    private StorageReference videoStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recipe_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setStatusBar();
        initialize();
    }

    private void initialize(){

        tv_hapus_video = getActivity().findViewById(R.id.tv_hapus_video);
        videoView = getActivity().findViewById(R.id.vv_recipe);
        mediaController = new MediaController(getActivity(),false);
        btn_addvideo = getActivity().findViewById(R.id.btn_add_video);

        et_nama_masakan = getActivity().findViewById(R.id.et_nama_masakan);
        et_bahan = getActivity().findViewById(R.id.et_bahan);
        et_cara_masak = getActivity().findViewById(R.id.et_cara_masak);
        et_lama_masak = getActivity().findViewById(R.id.et_lama_masak);
        et_deskripsi = getActivity().findViewById(R.id.et_deskrpisi);

        recipeRefs = FirebaseDatabase.getInstance().getReference().child("Recipe");
        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        videoStorage = FirebaseStorage.getInstance().getReference().child("VideoTutorial");

        btnTambahkan = getActivity().findViewById(R.id.btn_tambahkan);

        btn_addvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnTambahkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecipe();
            }
        });

        tv_hapus_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.setVideoURI(null);
                videoView.setVisibility(View.INVISIBLE);
                btn_addvideo.setVisibility(View.VISIBLE);
            }
        });


    }

    private void addRecipe(){

        if (filePath != null){
            final String nama_masakan, bahan, cara_masak, lama_masak, deskripsi;
            nama_masakan = et_nama_masakan.getText().toString();
            bahan = et_bahan.getText().toString();
            cara_masak = et_cara_masak.getText().toString();
            lama_masak = et_lama_masak.getText().toString();
            deskripsi = et_deskripsi.getText().toString();

            userRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String username;
                    username = dataSnapshot.child("username").getValue().toString();
                    if(TextUtils.isEmpty(nama_masakan) && TextUtils.isEmpty(bahan) && TextUtils.isEmpty(cara_masak) && TextUtils.isEmpty(lama_masak) && TextUtils.isEmpty(deskripsi)){
                        Toast.makeText(getActivity(), "Data harus diisi!", Toast.LENGTH_SHORT).show();
                    }else{
                        final String[] itemCount = new String[2];
                        recipeRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                itemCount[0] = String.valueOf(dataSnapshot.getChildrenCount());
                                HashMap recipeMap = new HashMap();
                                recipeMap.put("nama_masakan", nama_masakan);
                                recipeMap.put("bahan",bahan);
                                recipeMap.put("cara_masak",cara_masak);
                                recipeMap.put("lama_masak",lama_masak);
                                recipeMap.put("deskripsi",deskripsi);
                                recipeMap.put("oleh",username);
                                recipeMap.put("videoURL","-");
                                recipeMap.put("likeCount",0);
                                recipeRefs.child(itemCount[0]).updateChildren(recipeMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(Task task) {
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        userRefs.child("recipe").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                itemCount[1] = String.valueOf(dataSnapshot.getChildrenCount());
                                HashMap recipeMap = new HashMap();
                                recipeMap.put("nama_masakan", nama_masakan);
                                recipeMap.put("bahan",bahan);
                                recipeMap.put("cara_masak",cara_masak);
                                recipeMap.put("lama_masak",lama_masak);
                                recipeMap.put("deskripsi",deskripsi);
                                recipeMap.put("oleh",username);
                                recipeMap.put("videoURL","-");
                                userRefs.child("recipe").child(itemCount[1]).updateChildren(recipeMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(Task task) {
                                        uploadImage(itemCount[0],itemCount[1]);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else{
            Toast.makeText(getActivity(), "Silahkan pilih video terlebih dahulu!", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadImage(final String itemCountRecipe, final String itemCountUser) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        videoStorage.child(itemCountRecipe).child("tutorial_video").putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        videoStorage.child(itemCountRecipe).child("tutorial_video").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                recipeRefs.child(itemCountRecipe).child("videoURL").setValue(uri.toString());
                                userRefs.child("recipe").child(itemCountUser).child("videoURL").setValue(uri.toString());
                                Toast.makeText(getActivity(), "Tambahkan Berhasil!", Toast.LENGTH_SHORT).show();
                                setClearInput();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_IMAGE_REQUEST );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            videoView.setVisibility(View.VISIBLE);
            btn_addvideo.setVisibility(View.GONE);
            filePath = data.getData();

            try {

                videoView.setVideoURI(filePath);
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
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
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void setStatusBar(){
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

    private void setClearInput(){
        et_nama_masakan.setText("");
        et_bahan.setText("");
        et_cara_masak.setText("");
        et_lama_masak.setText("");
        et_deskripsi.setText("");
        videoView.setVideoURI(null);
        videoView.setVisibility(View.INVISIBLE);
        btn_addvideo.setVisibility(View.VISIBLE);
    }

}
