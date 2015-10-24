package com.simpleastudio.recommendbookapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;

import com.simpleastudio.recommendbookapp.service.RandomBookService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan on 15/10/2015.
 */
public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";
    private static final String PREF_CHECKBOX = "checkboxboolean";
    private static final int INPUT_BOOK_REQUEST = 1;
    @Bind(R.id.button_current_book)
    Button mTitleInputButton;
    @Bind(R.id.checkbox_daily_rec)
    CheckBox mDailyRecCheckbox;
    boolean checkBoxTick;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, v);

        //Making title as Setting
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.navigation_setting);


        String currentTitle = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(BookInputFragment.PREF_INITIAL_BOOK, null);

        if(currentTitle != null){
            mTitleInputButton.setText(currentTitle);
        }
        else {
            mTitleInputButton.setText(R.string.default_book_title);
        }

        mTitleInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                BookInputFragment fragment = new BookInputFragment();
                fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();


                NavigationView nv = (NavigationView)((AppCompatActivity) getActivity()).findViewById(R.id.navigation_view);
                nv.getMenu().getItem(2).setChecked(true);
            }
        });

        boolean checkboxState = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(PREF_CHECKBOX, false);
        mDailyRecCheckbox.setChecked(checkboxState);
        mDailyRecCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDailyRecCheckbox.isChecked()) {
                    RandomBookService.setServiceAlarm(getActivity(), true);
                } else {
                    RandomBookService.setServiceAlarm(getActivity(), false);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == INPUT_BOOK_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                String newTitle = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(BookInputFragment.PREF_INITIAL_BOOK, null);
                mTitleInputButton.setText(newTitle);
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        //Saves state of checkbox
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putBoolean(PREF_CHECKBOX, mDailyRecCheckbox.isChecked())
                .commit();
    }
}
