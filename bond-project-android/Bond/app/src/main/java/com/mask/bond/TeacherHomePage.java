package com.mask.bond;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.mask.bond.teacher.teacherScreen.ContactsFragment;
import com.mask.bond.teacher.teacherScreen.HomeFragment;
import com.mask.bond.teacher.teacherScreen.ProfileFragment;
import com.mask.bond.teacher.teacherScreen.RequestsFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TeacherHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        fragmentLoad(new HomeFragment());
    }

    private void fragmentLoad(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment,fragment);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id){
            case R.id.nav_home:
                fragmentLoad(new HomeFragment());
                break;
            case R.id.nav_profile:
                fragmentLoad(new ProfileFragment());
                break;
            case R.id.nav_requests:
                fragmentLoad(new RequestsFragment());
                break;
            case R.id.nav_contacts:
                fragmentLoad(new ContactsFragment());
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
