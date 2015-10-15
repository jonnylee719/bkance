package com.simpleastudio.recommendbookapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan on 15/10/2015.
 */
public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";
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
                Intent i = new Intent(getActivity(), BookInputActivity.class);
                startActivityForResult(i, INPUT_BOOK_REQUEST);
            }
        });
        return v;
    }
}
