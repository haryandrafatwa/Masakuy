package com.example.masakuy.Feature.Tambah;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.masakuy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class AddRecipe2Fragment extends Fragment {

    private Button btnTambahkan;
    private ImageButton btn_addvideo;
    private TextView tv_hapus_video;
    private EditText et_nama_masakan, et_bahan, et_cara_masak, et_lama_masak, et_deskripsi;

    private final int PICK_IMAGE_REQUEST = 100;
    private Uri filePath,imagePath;
    private VideoView videoView;
    private MediaController mediaController;

    private DatabaseReference recipeRefs,userRefs;
    private StorageReference videoStorage;

    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recipe2_fragment, container, false);
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

        tv_hapus_video = getActivity().findViewById(R.id.tv_hapus_video_resep);
        videoView = getActivity().findViewById(R.id.vv_recipe);
        mediaController = new MediaController(getActivity(),false);
        btn_addvideo = getActivity().findViewById(R.id.btn_add_video);

        et_lama_masak = getActivity().findViewById(R.id.et_lama_masak);
        et_deskripsi = getActivity().findViewById(R.id.et_deskrpisi);

        recipeRefs = FirebaseDatabase.getInstance().getReference().child("Recipe");
        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        videoStorage = FirebaseStorage.getInstance().getReference().child("RecipeFile");

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
                filePath = null;
                videoView.setVisibility(View.INVISIBLE);
                btn_addvideo.setVisibility(View.VISIBLE);
            }
        });


    }

    private void addRecipe(){

        if (filePath != null){
            final String nama_masakan, bahan, cara_masak, lama_masak, deskripsi;

            Bundle bundle = getArguments();
            HashMap inputResep = (HashMap) bundle.getSerializable("inputResep1");
            nama_masakan = inputResep.get("nama_masakan").toString();
            bahan = inputResep.get("bahan").toString();
            cara_masak = inputResep.get("cara_masak").toString();
            imagePath = Uri.parse(inputResep.get("imageURL").toString());
            lama_masak = et_lama_masak.getText().toString();
            deskripsi = et_deskripsi.getText().toString();

            userRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String username,email;
                    username = dataSnapshot.child("username").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    if(TextUtils.isEmpty(lama_masak) && TextUtils.isEmpty(deskripsi)){
                        Toast.makeText(getActivity(), "Data harus diisi!", Toast.LENGTH_SHORT).show();
                    }else if (filePath == null){
                        Toast.makeText(getActivity(), "Silahkan pilih video terlebih dahulu", Toast.LENGTH_SHORT).show();
                    }else{
                        final String[] itemCount = new String[1];
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
                                recipeMap.put("email",email);
                                recipeMap.put("imageURL","-");
                                recipeMap.put("videoURL","-");
                                recipeMap.put("likeCount",0);
                                recipeRefs.child(itemCount[0]).updateChildren(recipeMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(Task task) {
                                        uploadVideo(itemCount[0]);
                                        uploadImage(itemCount[0]);
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

    private void uploadVideo(final String itemCountRecipe) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        videoStorage.child(itemCountRecipe).child("video.mp4").putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        videoStorage.child(itemCountRecipe).child("video.mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                recipeRefs.child(itemCountRecipe).child("videoURL").setValue(uri.toString());
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

    private void uploadImage(final String itemCountRecipe) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        videoStorage.child(itemCountRecipe).child("image.png").putFile(imagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        videoStorage.child(itemCountRecipe).child("image.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                recipeRefs.child(itemCountRecipe).child("imageURL").setValue(uri.toString());
                                Toast.makeText(getActivity(), "Tambahkan Berhasil!", Toast.LENGTH_SHORT).show();
                                PilihInputFragment pilihInputFragment = new PilihInputFragment();
                                setFragment(pilihInputFragment);
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

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment);
        fragmentTransaction.commit();
    }

}
