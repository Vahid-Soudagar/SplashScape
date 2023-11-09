/*
    The MainActivity class represents the main activity of the wallpaper application.
    It utilizes the Android Navigation component to handle navigation between different fragments.
    Data binding is employed to bind UI components in the layout.
    The onCreate method initializes the layout, sets up the custom toolbar, and configures the navigation drawer.
    The onCreateOptionsMenu method inflates the menu for the action bar.
    The onSupportNavigateUp method handles the Up button's behavior, delegating navigation to the NavController.
 */

package com.example.wallpaper.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.wallpaper.R;
import com.example.wallpaper.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the data binding for the main activity
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the custom toolbar as the action bar
        setSupportActionBar(binding.appBarMain.toolbar);

        // Initialize the navigation drawer and navigation view
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Configure the AppBar with the list of top-level destinations and the DrawerLayout
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_photos, R.id.nav_collection, R.id.nav_favourites)
                .setOpenableLayout(drawer)
                .build();

        // Set up navigation with the NavController and tie it to the action bar
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle navigation up events by delegating them to the NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
