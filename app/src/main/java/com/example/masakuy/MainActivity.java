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
import com.example.masakuy.Feature.Tambah.PilihInputFragment;
import com.example.masakuy.Feature.Search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    public static Context contextOfApplication;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contextOfApplication = getApplicationContext();

        setContentView(R.layout.activity_main);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavBar);

        final ArtikelFragment artikelFragment = new ArtikelFragment();
        final SearchFragment searchFragment = new SearchFragment();
        final BerandaFragment berandaFragment = new BerandaFragment();
        final PilihInputFragment pilihInputFragment = new PilihInputFragment();
        final ProfileFragment profileFragment = new ProfileFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {

                int id = menuItem.getItemId();
                if (id == R.id.menuProfile) {
                    setFragment(profileFragment);
                    return true;
                } else if (id == R.id.menuBeranda) {
                    setFragment(berandaFragment);
                    return true;
                } else if (id == R.id.menuRecipe) {
                    setFragment(pilihInputFragment);
                    return true;
                } else if (id == R.id.menuSearch) {
                    setFragment(searchFragment);
                    return true;
                } else if (id == R.id.menuFeeds) {
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
