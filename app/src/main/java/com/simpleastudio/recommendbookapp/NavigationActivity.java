package com.simpleastudio.recommendbookapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.Set;

/**
 * Created by Jonathan on 22/10/2015.
 */
public class NavigationActivity extends SingleFragmentActivity{
    private static final String TAG = "NavigationActivity";
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected Fragment createFragment() {
        return new BookInfoFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        nvDrawer = (NavigationView) findViewById(R.id.navigation_view);
        setUpDrawerContent(nvDrawer);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setUpDrawerToggle();

        //Tie DrawerLayout events to the ActionToggle
        mDrawer.setDrawerListener(drawerToggle);

    }

    private ActionBarDrawerToggle setUpDrawerToggle(){
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setUpDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem item){
        Fragment fragment = null;

        Class fragmentClass;
        switch (item.getItemId()){
            case R.id.navigation_main:
                fragmentClass = BookInfoFragment.class;
                break;
            case R.id.navigation_previous:
                fragmentClass = BookListFragment.class;
                break;
            case R.id.navigation_input:
                fragmentClass = BookInputFragment.class;
                break;
            case R.id.navigation_setting:
                fragmentClass = SettingFragment.class;
                break;
            default:
                fragmentClass = BookInfoFragment.class;
        }

        try{
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e){
            e.printStackTrace();
        }

        // Replacing the current fragment in container with the new fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        item.setChecked(true);
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        //Sync the toggle state after onRestoreInstanceState has occurred
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        //Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
