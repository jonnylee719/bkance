package com.simpleastudio.recommendbookapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.simpleastudio.recommendbookapp.service.RandomBookService;

/**
 * Created by Jonathan on 10/10/2015.
 */
public class VisibleFragment extends Fragment {
    public static final String TAG = "VisibleFragment";


    public void actionOnReceive(){};

    private BroadcastReceiver mOnRandomBookNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received a broadcast intent.");
            actionOnReceive();
            Snackbar.make(getView(), R.string.new_recommendation, Snackbar.LENGTH_LONG);
            Toast.makeText(getActivity(),
                    "Got a broadcast: " + intent.getAction(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter(RandomBookService.EVENT_NEW_RECOMMENDATION);
        getActivity().registerReceiver(mOnRandomBookNotification, filter,
                RandomBookService.PERM_PRIVATE, null);
    }

    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(mOnRandomBookNotification);
    }

}
