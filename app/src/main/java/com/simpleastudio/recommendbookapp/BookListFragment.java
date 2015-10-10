package com.simpleastudio.recommendbookapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jonathan on 9/10/2015.
 */
public class BookListFragment extends Fragment {
    private static final String TAG = "BookListFragment";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_book_list, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.book_recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new BookCardAdaptor(BookLab.get(getActivity()).getBooks());
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    public class BookCardAdaptor extends RecyclerView.Adapter<BookCardAdaptor.ViewHolder>{
        private ArrayList<Book> mList;

        public BookCardAdaptor(ArrayList<Book> mBookList){
            mList = mBookList;
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
            String avgRating = String.format(getResources().getString(R.string.book_rating), b.getmAvgRating());
            holder.mTextviewRating.setText(avgRating);
            //holder.mTextviewDescription.setText(b.getmDescription());
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            protected ImageView mImageView;
            protected TextView mTextviewTitle;
            protected TextView mTextviewAuthor;
            protected TextView mTextviewRating;
            protected TextView mTextviewDescription;

            public ViewHolder(View itemView) {
                super(itemView);
                mImageView = (ImageView) itemView.findViewById(R.id.card_imageview_thumbnail);
                mTextviewTitle = (TextView) itemView.findViewById(R.id.card_textview_title);
                mTextviewAuthor = (TextView) itemView.findViewById(R.id.card_textview_author);
                mTextviewRating = (TextView) itemView.findViewById(R.id.card_textview_rating);
                mTextviewDescription = (TextView) itemView.findViewById(R.id.card_textview_description);
            }
        }
    }

}
