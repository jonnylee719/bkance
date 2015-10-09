package com.simpleastudio.recommendbookapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.zip.Inflater;

/**
 * Created by Jonathan on 9/10/2015.
 */
public class BookInputFragment extends Fragment {
    private static final String TAG = "BookInputFragment";
    private EditText bookInput;
    private Button enterBook;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_book_input, container, false);

        bookInput = (EditText) v.findViewById(R.id.editText_book);
        enterBook = (Button) v.findViewById(R.id.button_input_book);

        return v;
    }
}
