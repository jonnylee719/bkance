package com.simpleastudio.recommendbookapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by Jonathan on 20/10/2015.
 */
public class NavigationFragment extends Fragment {
    private static final String TAG = "NavigationFragment";
    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initToolbar();
        setUpDrawerLayout();
    }

    private void initToolbar(){
        final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

    }

    private void setUpDrawerLayout(){
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);

        NavigationView view = (NavigationView) getActivity().findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationItemListener());
    }


    private class NavigationItemListener implements NavigationView.OnNavigationItemSelectedListener {
        Activity currentActivity = getActivity();
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.navigation_main:
                    if(currentActivity instanceof BookInfoActivity){}
                    else {
                        Intent i = new Intent(getActivity(), BookInfoActivity.class);
                        startActivity(i);
                    }
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                case R.id.navigation_past:
                    if(currentActivity instanceof BookListActivity){}
                    else {
                        Intent i = new Intent(getActivity(), BookListActivity.class);
                        startActivity(i);
                    }
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                case R.id.navigation_saved:
                    if(currentActivity instanceof BookListActivity){}
                    else {
                        Intent i = new Intent(getActivity(), BookListActivity.class);
                        startActivity(i);
                    }
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                case R.id.navigation_input:
                    if(currentActivity instanceof BookInputActivity){}
                    else {
                        Intent i = new Intent(getActivity(), BookInputActivity.class);
                        startActivity(i);
                    }
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                case R.id.navigation_setting:
                    if(currentActivity instanceof SettingActivity){}
                    else {
                        Intent i = new Intent(getActivity(), SettingActivity.class);
                        startActivity(i);
                    }
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                default:
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return this.onNavigationItemSelected(menuItem);
            }
        }
    }
}
