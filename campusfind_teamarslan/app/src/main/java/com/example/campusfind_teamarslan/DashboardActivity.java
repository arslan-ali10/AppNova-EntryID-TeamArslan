
package com.example.campusfind_teamarslan;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigation =
                findViewById(R.id.bottomNavigation);

        // Open Home by default
        loadFragment(new HomeFragment());

        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                selectedFragment = new HomeFragment();

            } else if (id == R.id.nav_nearby) {

                selectedFragment = new NearbyFragment();

            } else if (id == R.id.nav_report) {

                selectedFragment = new ReportFragment();

            } else if (id == R.id.nav_profile) {

                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container,
                        fragment
                )
                .commit();
    }
}
