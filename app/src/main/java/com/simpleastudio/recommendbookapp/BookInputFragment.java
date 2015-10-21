package com.simpleastudio.recommendbookapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.simpleastudio.recommendbookapp.service.BookSearchService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan on 9/10/2015.
 */
public class BookInputFragment extends NavigationFragment {
    private static final String TAG = "BookInputFragment";
    public static final String PREF_INITIAL_BOOK = "initialBook";
    @Bind(R.id.editText_book) protected EditText bookInput;
    @Bind(R.id.button_input_book) protected Button enterBook;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_book_input, container, false);
        ButterKnife.bind(this, v);

        enterBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(PREF_INITIAL_BOOK, bookInput.getText().toString())
                        .commit();

                //Start service to search recommendations for the new title
                Intent iService = new Intent(getActivity(), BookSearchService.class);
                getActivity().startService(iService);

                //InputActivity can be started from SettingFragment as well,
                //if it's by SettingFragment, it should return to the SettingFragment
                if (getActivity().getCallingActivity() != null) {
                    Intent i = new Intent();
                    getActivity().setResult(Activity.RESULT_OK, i);
                    getActivity().finish();
                } else {
                    Intent i = new Intent(getActivity(), BookInfoActivity.class);
                    startActivity(i);
                }

            }
        });

        return v;
    }
}
