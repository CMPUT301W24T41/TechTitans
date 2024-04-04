package com.example.eventsigninapp;

// Import statements

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class AdminActivity extends AppCompatActivity {

    // Member variables
    FrameLayout frameLayout;
    TabLayout tabLayout;
    DatabaseController databaseController = new DatabaseController();
    UserController userController = new UserController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize views
        frameLayout = findViewById(R.id.adminMain);
        tabLayout = findViewById(R.id.adminTabs);

        // Select the first tab by default
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.select();
        }

        // Replace default fragment with AdminEventListFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.adminMain, new AdminEventListFragment())
                .addToBackStack(null)
                .commit();

        // Set listener for tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new AdminEventListFragment();
                        break;
                    case 1:
                        fragment = new AdminUserListFragment();
                        break;
                    case 2:
                        fragment = new AdminImageListFragment();
                        break;
                    case 3:
                        fragment = new AdminCodeGeneratorFragment();
                        break;
                    case 4:
                        // Redirect to MainActivity if the fourth tab is selected
                        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                }
                if (tab.getPosition() != 4) {
                    // Replace fragment only if the fifth tab is not selected
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.adminMain, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }
}
