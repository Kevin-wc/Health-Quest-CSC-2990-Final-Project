package com.example.finalproject;

import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.finalproject.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView navView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);


        navView = findViewById(R.id.bottomNav);
        Fragment homeNavFragment = new HomeNavFragment();
        Fragment profileNavFragment = new ProfileNavFragment();

        setCurrentFragment(homeNavFragment);

        navView.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home) {
                setCurrentFragment(homeNavFragment);
            }
            else if(item.getItemId() == R.id.profile) {
                setCurrentFragment(profileNavFragment);
            }
            return true;
        });



    }


    public void setCurrentFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).commit();
    }
}