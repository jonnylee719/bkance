package com.simpleastudio.recommendbookapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

/**
 * Created by Jonathan on 22/10/2015.
 */
public class BookNavigationActivity extends NavigationActivityBase{
    private static final String TAG = "NavigationActivity";

    @Override
    protected Fragment setStartFragment() {
        return new BookInfoFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_navigation;
    }

    @Override
    protected int setNavigationDrawerMenu() {
        return R.menu.drawer;
    }

    @Override
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
