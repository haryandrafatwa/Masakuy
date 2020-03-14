package com.example.masakuy;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.masakuy.Feature.Beranda.BerandaFragment;
import com.example.masakuy.Feature.Artikel.ArtikelFragment;
import com.example.masakuy.Feature.Profile.ProfileFragment;
import com.example.masakuy.Feature.Recipe.RecipeFragment;
import com.example.masakuy.Feature.Search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    public static Context contextOfApplication;
    private DatabaseReference databaseReference;
    private String tBag="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("trashbag"))
                {
                    tBag = dataSnapshot.child("trashbag").getValue().toString();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        contextOfApplication = getApplicationContext();

        setContentView(R.layout.activity_main);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {

                int id = menuItem.getItemId();
                if (id == R.id.menuProfile) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    setFragment(profileFragment);
                    return true;
                } else if (id == R.id.menuBeranda) {
                    BerandaFragment berandaFragment = new BerandaFragment();
                    setFragment(berandaFragment);
                    return true;
                } else if (id == R.id.menuRecipe) {
                    RecipeFragment recipeFragment = new RecipeFragment();
                    setFragment(recipeFragment);
                    return true;
                } else if (id == R.id.menuSearch) {
                    SearchFragment searchFragment = new SearchFragment();
                    setFragment(searchFragment);
                    return true;
                } else if (id == R.id.menuFeeds) {
                    ArtikelFragment artikelFragment = new ArtikelFragment();
                    setFragment(artikelFragment);
                    return true;
                }
                return  false;
            }

        });
        bottomNavigationView.setSelectedItemId(R.id.menuBeranda);

    }
    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment);
        fragmentTransaction.commit();
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }
}
