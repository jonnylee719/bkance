package com.simpleastudio.recommendbookapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.simpleastudio.recommendbookapp.service.BookSearchService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan on 9/10/2015.
 */
public class BookInputFragment extends Fragment {
    private static final String TAG = "BookInputFragment";
    public static final String PREF_INITIAL_BOOK = "initialBook";
    @Bind(R.id.editText_book) protected EditText bookInput;
    @Bind(R.id.button_input_book) protected Button enterBook;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_book_input, container, false);
        ButterKnife.bind(this, v);

        //Hiding the floating action button
        ((FloatingActionButton)getActivity().findViewById(R.id.fab)).hide();

        //Making title as Book input
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.navigation_input);
        NavigationView nv = (NavigationView)((AppCompatActivity) getActivity()).findViewById(R.id.navigation_view);
        nv.getMenu().getItem(2).setChecked(true);

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

                closeKeyboard(getActivity(), bookInput.getWindowToken());

                NavigationView nv = (NavigationView)((AppCompatActivity) getActivity()).findViewById(R.id.navigation_view);
                nv.getMenu().getItem(0).setChecked(true);


                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                BookInfoFragment fragment = new BookInfoFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bookinput_actionbar_menu, menu);
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }
}
