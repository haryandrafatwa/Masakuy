package com.example.masakuy.Feature.Beranda;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.masakuy.Feature.Beranda.Recyclerview.RecipeModel;
import com.example.masakuy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IngredientsFragment extends Fragment {

    private TextView tv_edit_ingredients;
    private EditText et_ingredients;
    private Button btnUpdate;
    private BottomNavigationView bottomNavigationView;
    private DatabaseReference recipeRefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ingredients_fragment, container, false);
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

        Bundle bundleRecipe = this.getArguments();
        RecipeModel recipeModel = bundleRecipe.getParcelable("key");

        recipeRefs = FirebaseDatabase.getInstance().getReference().child("Recipe").child(recipeModel.getKey());

        et_ingredients = getActivity().findViewById(R.id.tv_ingredients_recipe_detail);
        tv_edit_ingredients = getActivity().findViewById(R.id.tv_edit_ingredients);
        btnUpdate = getActivity().findViewById(R.id.btn_update_ingredients_resep);

        if (recipeModel.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            tv_edit_ingredients.setVisibility(View.VISIBLE);
        }else{
            tv_edit_ingredients.setVisibility(View.INVISIBLE);
        }

        et_ingredients.setText(recipeModel.getBahan());

        tv_edit_ingredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_ingredients.setFocusableInTouchMode(true);
                btnUpdate.setVisibility(View.VISIBLE);
                tv_edit_ingredients.setVisibility(View.GONE);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deskripsi = et_ingredients.getText().toString();
                if(TextUtils.isEmpty(deskripsi)){
                    Toast.makeText(getActivity(), "Silahkan isi bahan terlebih dahulu!", Toast.LENGTH_SHORT).show();
                }else{
                    recipeRefs.child("bahan").setValue(deskripsi).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "Update berhasil!", Toast.LENGTH_SHORT).show();
                            et_ingredients.setFocusable(false);
                            btnUpdate.setVisibility(View.GONE);
                            tv_edit_ingredients.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void setStatusBar(){ // fungsi buat ubah warna status bar
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

}
