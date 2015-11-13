package com.simpleastudio.recommendbookapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan on 12/11/2015.
 */
public class MyBooksFragment extends VisibleFragment implements SearchView.OnQueryTextListener {
    private final static String TAG = "MyBooksFragment";
    @Bind(R.id.book_recycler_view) protected RecyclerView mRecyclerView;
    protected List<Book> mBookList;
    protected RecyclerView.Adapter mAdapter;
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
                    final Book book = ((BookCardAdaptor)mAdapter).mList.get(position);
                    mTempList.clear();
                    if(mBookList.contains(book)){
                        int positionInBookList = mBookList.indexOf(book);
                        mBookList.remove(book);
                        mTempList.put(positionInBookList, book);
                        BookLab.get(getActivity())
                                .removeBookFromTable(BookLab.PAST_REC_TABLE, book.getmTitle());
                        //Displaying the new bookList
                        List<Book> newFilteredList;
                        if(currentQuery != null){
                            newFilteredList = filter(mBookList, currentQuery);
                        } else {
                            newFilteredList = mBookList;
                        }
                        ((BookCardAdaptor)mAdapter).animateTo(newFilteredList);
                        launchDeleteSnackBar(1);
                    }
                }
            };
    private ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
    private Snackbar mSnackbar;
    private LinkedHashMap<Integer, Book> mTempList;
    private String currentQuery;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mTempList = new LinkedHashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_book_list, container, false);
        ButterKnife.bind(this, v);

        //Making title as My Books
        ((AppCompatActivity)getActivity()).getSupportActionBar()
                .setTitle(R.string.navigation_my_books);
        NavigationView nv = (NavigationView)((AppCompatActivity) getActivity())
                .findViewById(R.id.navigation_view);
        nv.getMenu().getItem(2).setChecked(true);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Converting hashtable to array list for adaptor
        mBookList = tableToList(BookLab.get(getActivity()).getTable(BookLab.FAV_BOOKS_TABLE));

        mAdapter = new BookCardAdaptor(mBookList);
        mRecyclerView.setAdapter(mAdapter);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        //Hiding add button
        Button addButton = (Button) v.findViewById(R.id.card_button_add);
        addButton.setEnabled(false);
        addButton.setVisibility(View.GONE);

        return v;
    }

    public ArrayList<Book> tableToList(Hashtable<String, Book> table){
        ArrayList<Book> list = new ArrayList<Book>();
        Enumeration<Book> books = table.elements();
        while(books.hasMoreElements()){
            list.add(books.nextElement());
        }
        return list;
    }

    //Undoing deletion when undo button is clicked in the UndoSnackbar
    public void onUndoDelete(){
        Set set = mTempList.entrySet();
        Log.d(TAG, "Number of books in tempList: " + set.size());
        BookLab bookLab = BookLab.get(getActivity());
        for (Object aSet : set) {
            Map.Entry bookSet = (Map.Entry) aSet;
            int position = (int) bookSet.getKey();
            Book book = (Book) bookSet.getValue();
            mBookList.add(position, book);
            bookLab.addBookToTable(BookLab.PAST_REC_TABLE, book);
        }
        List<Book> newFilteredList;
        if(currentQuery != null){
            newFilteredList = filter(mBookList, currentQuery);
        } else {
            newFilteredList = mBookList;
        }
        ((BookCardAdaptor)mAdapter).animateTo(newFilteredList);
        mTempList.clear();
    }

    //Delete snackbar
    public void launchDeleteSnackBar(int itemCount){
        if(mSnackbar != null){
            mSnackbar.dismiss();
        }
        mSnackbar = Snackbar.make(getView(), String.format(getString(R.string.snackbar_delete_text), itemCount), Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onUndoDelete();
                    }
                });
        mSnackbar.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.booklist_actionbar_menu, menu);

        final MenuItem item = menu.findItem(R.id.booklist_search_bar);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        Log.d(TAG, "Query text has changed to: " + query);
        currentQuery = query;
        final List<Book> filteredBookList = filter(mBookList, query);
        ((BookCardAdaptor)mAdapter).animateTo(filteredBookList);
        mRecyclerView.scrollToPosition(0);
        Log.d(TAG, "Filtered book list size: " + filteredBookList.size());
        return true;
    }

    private List<Book> filter(List<Book> books, String query){
        query = query.toLowerCase();

        final List<Book> filteredModelList = new ArrayList<>();
        for(Book book: books){
            final String bookTitle = book.getmTitle().toLowerCase();
            if(bookTitle.contains(query)){
                filteredModelList.add(book);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_delete:
                onDeleteAll();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Deleting all visible book items in the adaptor
    public void onDeleteAll(){
        mTempList.clear();
        //Get all visible books in adaptor
        final List<Book> adaptorList = ((BookCardAdaptor)mAdapter).mList;
        int listSize = adaptorList.size();
        BookLab bookLab = BookLab.get(getActivity());
        Log.d(TAG, "Number of books in adaptorList: " + listSize);
        for(int position = 0; position < listSize; position++){
            Book book = adaptorList.get(position);
            Log.d(TAG, "Book title: " + book.getmTitle());
            if(mBookList.contains(book)){
                Log.d(TAG, "mBookList contains book in arrayList: " + mBookList.contains(book));
                int positionInBookList = mBookList.indexOf(book);
                Log.d(TAG, "Book at position " + positionInBookList + " is called " + mBookList.get(positionInBookList).getmTitle());
                mTempList.put(positionInBookList, book);
                Log.d(TAG, "Number of books in tempList: " + mTempList.size());
                bookLab.removeBookFromTable(BookLab.PAST_REC_TABLE, book.getmTitle());
            }
        }
        mBookList.removeAll(adaptorList);
        List<Book> newFilteredList;
        if(currentQuery != null){
            newFilteredList = filter(mBookList, currentQuery);
        } else {
            newFilteredList = mBookList;
        }
        ((BookCardAdaptor)mAdapter).animateTo(newFilteredList);
        launchDeleteSnackBar(listSize);
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
        if(mSnackbar != null){
            if(mSnackbar.isShown()){
                mSnackbar.dismiss();
            }
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
        public final List<Book> mList;
        ImageLoader imageLoader = SingRequestQueue.getInstance(getActivity()).getImageLoader();
        private LinkedHashMap<Integer, Book> tempRecord;

        public BookCardAdaptor(List<Book> books){
            mList = new ArrayList<>(books);
            tempRecord = new LinkedHashMap<>();
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

        public Book removeItem(int position){
            final Book book = mList.remove(position);
            notifyItemRemoved(position);
            return book;
        }

        public void addItem(int position, Book book){
            mList.add(position, book);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition){
            Log.d(TAG, "Adaptor list size: " + mList.size());
            final Book book = mList.remove(fromPosition);
            Log.d(TAG, "Adaptor list size after removal: " + mList.size());
            mList.add(toPosition, book);
            notifyItemMoved(fromPosition, toPosition);
        }

        public void animateTo(List<Book> newList){
            Log.d(TAG, "Animating to new list.");
            Log.d(TAG, "New list size: " + newList.size());
            applyAndAnimateRemovals(newList);
            applyAndAnimateAdditions(newList);
            applyAndAnimateMovedItems(newList);
            Log.d(TAG, "Current list size in adaptor: " + mList.size());
        }

        private void applyAndAnimateRemovals(List<Book> newList){
            for(int i = mList.size() - 1; i >= 0; i--){
                final Book book = mList.get(i);
                if(!newList.contains(book)){
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<Book> newList){
            for(int i = 0; i < newList.size(); i++){
                final Book book = newList.get(i);
                if(!mList.contains(book)){
                    addItem(i, book);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<Book> newList){
            for(int toPosition = newList.size() -1; toPosition >= 0; toPosition--){
                final Book book = newList.get(toPosition);
                final int fromPosition = mList.indexOf(book);
                if(fromPosition >= 0 && fromPosition != toPosition){
                    moveItem(fromPosition, toPosition);
                }
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
                Log.d(TAG, "Book title clicked in MyBooksFragment: " + b.getmTitle());
                Intent i = new Intent(getContext(), BookDetailActivity.class);
                i.putExtra(BookDetailFragment.ARGS_BOOK_TITLE, b.getmTitle());
                startActivity(i);
            }
        }
    }
}
