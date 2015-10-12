package com.simpleastudio.recommendbookapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

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
        getActivity().registerReceiver(mOnRandomBookNotification, filter);
    }

    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(mOnRandomBookNotification);
    }
}
