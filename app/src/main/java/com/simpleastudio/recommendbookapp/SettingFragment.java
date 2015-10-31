package com.simpleastudio.recommendbookapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.simpleastudio.recommendbookapp.model.BookLab;
import com.simpleastudio.recommendbookapp.service.RandomBookService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan on 15/10/2015.
 */
public class SettingFragment extends VisibleFragment {
    private static final String TAG = "SettingFragment";
    private static final String PREF_CHECKBOX = "checkboxboolean";
    private static final int INPUT_BOOK_REQUEST = 1;
    @Bind(R.id.button_current_book)
    Button mTitleInputButton;
    @Bind(R.id.checkbox_daily_rec)
    CheckBox mDailyRecCheckbox;
    @Bind(R.id.linearlayout_rate_app)
    LinearLayout linearLayoutRateApp;
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
        NavigationView nv = (NavigationView)((AppCompatActivity) getActivity()).findViewById(R.id.navigation_view);
        nv.getMenu().getItem(3).setChecked(true);

        String currentTitle = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
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

            }
        });

        boolean checkboxState = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
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

        linearLayoutRateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateApp();
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

    @Override
    public void actionOnReceive(){
        //Putting the recommended title into current recommended title
        String recBookTitle = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getString(RandomBookService.PREF_RANDOM_REC, null);
        //Log.d(TAG, "RecBookTitle: " + recBookTitle);

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putString(BookLab.PREF_REC, recBookTitle)
                .commit();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        BookInfoFragment fragment = new BookInfoFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void rateApp(){
        Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try{
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
        }

    }
}
