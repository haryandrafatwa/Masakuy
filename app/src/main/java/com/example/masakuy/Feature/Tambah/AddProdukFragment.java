package com.example.masakuy.Feature.Tambah;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class AddProdukFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private ImageView imageView;
    private EditText et_nama, et_deskripsi,et_harga,et_stock;
    private TextView tv_hapus_foto;
    private ImageButton btn_addfoto;
    private Button btnPublish;
    private Uri filePath;

    private StorageReference fotoStorage;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_produk_fragment, container, false);
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

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Produk");

        tv_hapus_foto = getActivity().findViewById(R.id.tv_hapus_foto_resep);
        imageView = getActivity().findViewById(R.id.iv_foto_produk);
        btn_addfoto = getActivity().findViewById(R.id.btn_add_foto_produk);
        et_nama = getActivity().findViewById(R.id.et_nama_produk);
        et_deskripsi = getActivity().findViewById(R.id.et_deskripsi_produk);
        et_harga = getActivity().findViewById(R.id.et_harga_produk);
        et_stock = getActivity().findViewById(R.id.et_stok_produk);

        fotoStorage = FirebaseStorage.getInstance().getReference().child("ProdukFile");

        btnPublish = getActivity().findViewById(R.id.btn_publish_artikel);

        btn_addfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoFromGallery();
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArtikel();
            }
        });

        tv_hapus_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageBitmap(null);
                imageView.setVisibility(View.INVISIBLE);
                btn_addfoto.setVisibility(View.VISIBLE);
                filePath = null;
            }
        });

    }

    private void addArtikel(){
        if(TextUtils.isEmpty(et_nama.getText().toString()) && TextUtils.isEmpty(et_harga.getText().toString()) && TextUtils.isEmpty(et_deskripsi.getText().toString()) && TextUtils.isEmpty(et_stock.getText().toString())){
            Toast.makeText(getActivity(), "Data harus diisi terlebih dahulu", Toast.LENGTH_SHORT).show();
        }else if(filePath == null){
            Toast.makeText(getActivity(), "Silahkan pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
        }else{
            final String[] itemCount = new String[1];
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    itemCount[0] = String.valueOf(dataSnapshot.getChildrenCount());
                    HashMap artikelMap = new HashMap();
                    artikelMap.put("nama_produk",et_nama.getText().toString());
                    artikelMap.put("harga",Integer.valueOf(et_harga.getText().toString()));
                    artikelMap.put("stok",Integer.valueOf(et_stock.getText().toString()));
                    artikelMap.put("deskripsi",et_deskripsi.getText().toString());
                    artikelMap.put("image","-");
                    databaseReference.child(itemCount[0]).updateChildren(artikelMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(Task task) {
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

    private void uploadImage(final String itemCountRecipe) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        fotoStorage.child(itemCountRecipe).child("image.png").putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        fotoStorage.child(itemCountRecipe).child("image.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseReference.child(itemCountRecipe).child("image").setValue(uri.toString());
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

    private void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Photo"), 1 );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            imageView.setVisibility(View.VISIBLE);
            btn_addfoto.setVisibility(View.GONE);
            filePath = data.getData();

            if(requestCode == 1 && data != null && data.getData() != null){
                try {
                    Picasso.get().load(filePath).fit().into(imageView);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setStatusBar(){
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

}
