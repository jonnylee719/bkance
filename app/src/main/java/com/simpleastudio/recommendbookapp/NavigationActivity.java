package com.simpleastudio.recommendbookapp;

import android.content.res.Configuration;
import android.graphics.drawable.DrawableWrapper;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.Set;

/**
 * Created by Jonathan on 22/10/2015.
 */
public class NavigationActivity extends NavigationActivityBase{
    private static final String TAG = "NavigationActivity";

    @Override
    protected Fragment setStartFragment() {
        return new BookInfoFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected int setNavigationDrawerMenu() {
        return R.menu.drawer;
    }

    @Override
    protected void setUpDrawerContent(NavigationView navigationView){
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
        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.closeDrawers();
    }

}
