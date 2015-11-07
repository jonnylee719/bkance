package com.simpleastudio.recommendbookapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.simpleastudio.recommendbookapp.api.GoogleBooksFetcher;
import com.simpleastudio.recommendbookapp.api.SingRequestQueue;
import com.simpleastudio.recommendbookapp.model.Book;
import com.simpleastudio.recommendbookapp.model.BookLab;
import com.simpleastudio.recommendbookapp.service.RandomBookService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
    ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    ((BookCardAdaptor)mAdapter).deleteBook(position);
                }
            };
    private ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
    private Snackbar mSnackbar;

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
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

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
                ((BookCardAdaptor)mAdapter).deleteAllBooks();
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
    public void onPause(){
        super.onPause();
        BookLab.get(getActivity()).save();
        if(mSnackbar.isShown()){
            mSnackbar.dismiss();
        }
    }

    @Override
    public void actionOnReceive(){
        //Putting the recommended title into current recommended title
        String recBookTitle = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getString(RandomBookService.PREF_RANDOM_REC, null);
        Log.d(TAG, "RecBookTitle: " + recBookTitle);

        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
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
        private LinkedHashMap<Integer, Book> tempRecord;

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
            tempRecord = new LinkedHashMap<>();
        }

        private void saveToTemp(int position){
            Book b = this.mList.get(position);
            this.tempRecord.put(position, b);
        }

        private void undoDelete(){
            Set set = this.tempRecord.entrySet();
            BookLab bookLab = BookLab.get(getActivity());
            for (Object aSet : set) {
                Map.Entry bookSet = (Map.Entry) aSet;
                int position = (int) bookSet.getKey();
                Book book = (Book) bookSet.getValue();
                this.mList.add(position, book);
                bookLab.addPastRec(book);
                notifyItemInserted(position);
            }
            this.tempRecord.clear();
        }

        private void deleteAllBooks(){
            int itemCount = getItemCount();
            for(int i = 0; i < itemCount; i++){
                saveToTemp(i);
            }
            this.mList.clear();
            BookLab.get(getActivity()).clearPastRecTable();
            Log.d(TAG, "All items deleted.");
            notifyItemRangeRemoved(0, itemCount);
            deleteSnackbar(itemCount);
        }

        private void deleteBook(int position){
            saveToTemp(position);
            Book b = this.mList.get(position);
            this.mList.remove(position);
            boolean deleted = BookLab.get(getActivity()).removeItemPastRec(b.getmTitle());
            Log.d(TAG, "Deleted item: " + deleted);
            notifyItemRemoved(position);
            deleteSnackbar(1);
        }

        public void deleteSnackbar(int itemCount){
            if(mSnackbar != null){
                mSnackbar.dismiss();
            }
            mSnackbar = Snackbar.make(getView(), String.format(getString(R.string.snackbar_delete_text), itemCount), Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            undoDelete();
                        }
                    });
            mSnackbar.show();
        }

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
            if(b.getmRatingCount() != -1 || b.getmAvgRating() != -1){
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

        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener{
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
