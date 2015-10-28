package com.simpleastudio.recommendbookapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.simpleastudio.recommendbookapp.api.GoodreadsFetcher;
import com.simpleastudio.recommendbookapp.api.GoogleBooksFetcher;
import com.simpleastudio.recommendbookapp.api.SingRequestQueue;
import com.simpleastudio.recommendbookapp.model.Book;
import com.simpleastudio.recommendbookapp.model.BookLab;
import com.simpleastudio.recommendbookapp.service.RandomBookService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan on 9/10/2015.
 */
public class BookListFragment extends VisibleFragment {
    private static final String TAG = "BookListFragment";
    @Bind(R.id.book_recycler_view)
    protected RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_book_list, container, false);
        ButterKnife.bind(this, v);

        //Making title as Book list
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.navigation_previous);
        NavigationView nv = (NavigationView)((AppCompatActivity) getActivity()).findViewById(R.id.navigation_view);
        nv.getMenu().getItem(1).setChecked(true);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new BookCardAdaptor(BookLab.get(getActivity()).getmPastRecTable());
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.booklist_actionbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_delete:
                BookLab.get(getActivity()).clearPastRecTable();
                ((BookCardAdaptor)mAdapter).clearList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void actionOnReceive(){
        //Putting the recommended title into current recommended title
        String recBookTitle = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(RandomBookService.PREF_RANDOM_REC, null);
        Log.d(TAG, "RecBookTitle: " + recBookTitle);

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

    public class BookCardAdaptor extends RecyclerView.Adapter<BookCardAdaptor.ViewHolder>{
        private ArrayList<Book> mList;
        ImageLoader imageLoader = SingRequestQueue.getInstance(getActivity()).getImageLoader();
        private Hashtable<String, Book> tempRecord;

        public ArrayList<Book> tableToList(Hashtable<String, Book> table){
            ArrayList<Book> list = new ArrayList<Book>();
            Enumeration<Book> books = table.elements();
            while(books.hasMoreElements()){
                list.add(books.nextElement());
            }
            return list;
        }

        public BookCardAdaptor(Hashtable<String, Book> bookTable){
            mList = tableToList(bookTable);
            tempRecord = new Hashtable<>();
        }

        private void saveToTemp(int positionStart, int itemCount){
            for(int i = positionStart; i < (positionStart+itemCount); i++){
                Book b = this.mList.get(i);
                this.tempRecord.put(b.getmTitle(), b);
            }
        }

        public void clearList(){
            //First clear tempHashTable, so that previously deleted will not appear back at undo
            tempRecord.clear();
            if(getItemCount() != 0){
                int itemCount = this.getItemCount();
                this.saveToTemp(0, itemCount);
                this.mList.clear();
                this.notifyItemRangeRemoved(0, itemCount);
                showUndoSnackbar(itemCount);
            }
        }

        public void undoDelete(){
            //Put all the books in temp record back to list
            Enumeration<Book> books = this.tempRecord.elements();
            int positionStart = getItemCount() - 1;
            int itemInserted = 0;
            while(books.hasMoreElements()){
                itemInserted++;
                Book b = books.nextElement();
                this.mList.add(b);
                BookLab.get(getActivity()).addPastRec(b);
            }
            this.notifyItemRangeInserted(positionStart, itemInserted);
        }

        public void showUndoSnackbar(int itemCount){
            Snackbar.make(getView(), String.format(getString(R.string.snackbar_delete_text), itemCount), Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_undo, snackbarClickListener)
                    .show();
        }

        final View.OnClickListener snackbarClickListener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Delete undone.");
                undoDelete();
            }
        };

        @Override
        public int getItemCount(){
            return mList.size();
        }

        @Override
        public BookCardAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.book_card, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(BookCardAdaptor.ViewHolder holder, int position) {
            Book b = mList.get(position);
            holder.mTextviewTitle.setText(b.getmTitle());
            holder.mTextviewAuthor.setText(b.getmAuthors());
            String ratingCount = NumberFormat.getInstance().format(b.getmRatingCount());
            String avgRating = String.format(getResources().getString(R.string.card_rating), b.getmAvgRating(), ratingCount);
            if(b.getmRatingCount() != 0 || b.getmAvgRating() != 0){
                holder.mTextviewRating.setText(avgRating);
            } else {
                holder.mTextviewRating.setText("");
            }

            //Getting the thumbnail url from hashtable
            //TODO consider situation where title does not exist in the thumbnail hashtable,
            //TODO perhaps create new GooglebooksFetcher request?
            String url = BookLab.get(getActivity()).getThumbnailUrl(b.getmTitle());
            holder.mNetworkImageView.setDefaultImageResId(R.drawable.default_book_cover);
            if(url == null){
                new GoogleBooksFetcher(getActivity()).setThumbnail(b.getmTitle(), holder.mNetworkImageView);
            } else {
                holder.mNetworkImageView.setErrorImageResId(R.drawable.default_book_cover);
                holder.mNetworkImageView.setImageUrl(url, imageLoader);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            protected NetworkImageView mNetworkImageView;
            protected TextView mTextviewTitle;
            protected TextView mTextviewAuthor;
            protected TextView mTextviewRating;
            protected Button mMoreButton;

            public ViewHolder(View itemView) {
                super(itemView);
                mNetworkImageView = (NetworkImageView) itemView.findViewById(R.id.card_imageview_thumbnail);
                mTextviewTitle = (TextView) itemView.findViewById(R.id.card_textview_title);
                mTextviewAuthor = (TextView) itemView.findViewById(R.id.card_textview_author);
                mTextviewRating = (TextView) itemView.findViewById(R.id.card_textview_rating);
                mMoreButton = (Button) itemView.findViewById(R.id.card_button_more);
                mMoreButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Book b = mList.get(position);
                Intent i = new Intent(getContext(), BookDetailActivity.class);
                i.putExtra(BookDetailFragment.ARGS_BOOK_TITLE, b.getmTitle());
                startActivity(i);
            }
        }
    }
}
