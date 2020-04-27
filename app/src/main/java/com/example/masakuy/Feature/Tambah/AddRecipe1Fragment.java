package com.example.masakuy.Feature.Tambah;

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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.masakuy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class AddRecipe1Fragment extends Fragment {

    private static final int MY_CAMERA_PERMISSION_CODE = 1883;
    private Button btnNext;
    private ImageButton btn_addfoto;
    private TextView tv_hapus_foto;
    private EditText et_nama_masakan, et_bahan, et_cara_masak;

    private static final int GALLERY = 1, CAMERA = 2;
    private Uri filePath;
    private ImageView imageView;
    private String mCurrentPhotoPath;

    private DatabaseReference recipeRefs,userRefs;
    private StorageReference fotoStorage;

    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recipe1_fragment, container, false);
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

        tv_hapus_foto = getActivity().findViewById(R.id.tv_hapus_foto_resep);
        imageView = getActivity().findViewById(R.id.iv_foto_resep);
        btn_addfoto = getActivity().findViewById(R.id.btn_add_foto_resep);

        if(filePath != null){
            btn_addfoto.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(filePath).fit().into(imageView);
        }else{
            btn_addfoto.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(null);
        }

        et_nama_masakan = getActivity().findViewById(R.id.et_nama_masakan);
        et_bahan = getActivity().findViewById(R.id.et_bahan);
        et_cara_masak = getActivity().findViewById(R.id.et_cara_masak);

        fotoStorage = FirebaseStorage.getInstance().getReference().child("RecipeFile");

        btnNext = getActivity().findViewById(R.id.btn_next_resep);

        btn_addfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhotoFromGallery();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInput();
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

    //Todo: checkInput itu buat ngecek apakah inputan ada yg kosong apa engga, kalo engga dia lanjut ke proses berikutnya
    private void checkInput(){
        if(TextUtils.isEmpty(et_nama_masakan.getText().toString()) && TextUtils.isEmpty(et_bahan.getText().toString()) && TextUtils.isEmpty(et_cara_masak.getText().toString())){
            Toast.makeText(getActivity(), "Data harus diisi terlebih dahulu", Toast.LENGTH_SHORT).show();
        }else if(filePath == null){
            Toast.makeText(getActivity(), "Silahkan pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
        }else{

            HashMap inputResep = new HashMap();
            inputResep.put("nama_masakan",et_nama_masakan.getText().toString());
            inputResep.put("bahan",et_bahan.getText().toString());
            inputResep.put("cara_masak",et_cara_masak.getText().toString());
            inputResep.put("imageURL",filePath);

            AddRecipe2Fragment addRecipe2Fragment = new AddRecipe2Fragment();
            //Todo: ini cara buat ngirim objek/variabel dari suatu fragment ke fragment lain, kalo yg di put itu serializeable, brarti objek yg dikirim adalah objek, kalo put String, objek yg dikirim String
            Bundle bundle = new Bundle();
            bundle.putSerializable("inputResep1",inputResep);
            addRecipe2Fragment.setArguments(bundle);
            setFragment(addRecipe2Fragment);
        }
    }

    private void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Photo"), GALLERY );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            imageView.setVisibility(View.VISIBLE);
            btn_addfoto.setVisibility(View.GONE);
            filePath = data.getData();

            if(requestCode == GALLERY && data != null && data.getData() != null){
                try {
                    Picasso.get().load(filePath).fit().into(imageView);
                }catch (Exception e){
                    e.printStackTrace();
                }
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
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }
}
