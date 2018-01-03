package com.codebutler;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.codebutler.data.KeywordsLessonsAndCodeDbContract;


public class KeywordEntriesRecycleViewAdapter extends RecyclerView.Adapter<KeywordEntriesRecycleViewAdapter.KeywordEntryViewHolder> {

    private Context mContext;
    private Cursor mKeywordDatabaseCursor;
    final private ListItemClickHandler mOnClickHandler;

    public KeywordEntriesRecycleViewAdapter(Context context, ListItemClickHandler listener) {
        this.mContext = context;
        this.mOnClickHandler = listener;
    }

    @Override public KeywordEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.keywords_list_item, parent, false);
        view.setFocusable(true);
        return new KeywordEntryViewHolder(view);
    }
    @Override public void onBindViewHolder(KeywordEntryViewHolder holder, int position) {

        //Moving the cursor to the desired row and skipping the next calls if there is no such position
        if (!mKeywordDatabaseCursor.moveToPosition(position)) return;

        // Indices for the _id, description, and priority columns
        int idIndex = mKeywordDatabaseCursor.getColumnIndex(KeywordsLessonsAndCodeDbContract.KeywordsDbEntry._ID);
        int keywordIndex = mKeywordDatabaseCursor.getColumnIndex(KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.COLUMN_KEYWORD);
        int typeIndex = mKeywordDatabaseCursor.getColumnIndex(KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.COLUMN_TYPE);

        // Determine the values of the wanted data
        final int id = mKeywordDatabaseCursor.getInt(idIndex);
        String keyword = mKeywordDatabaseCursor.getString(keywordIndex);
        String type = mKeywordDatabaseCursor.getString(typeIndex);

        //Set values
        holder.itemView.setTag(id);
        holder.keywordTextViewInRecycleView.setText(keyword);
        holder.typeTextViewInRecycleView.setText(type);
    }
    @Override public int getItemCount() {
        if (mKeywordDatabaseCursor == null) return 0;
        return mKeywordDatabaseCursor.getCount();
    }
    public void swapCursor(Cursor newCursor) {
        if (mKeywordDatabaseCursor != null) mKeywordDatabaseCursor.close();
        mKeywordDatabaseCursor = newCursor;
        if (newCursor != null) this.notifyDataSetChanged();
    }

    class KeywordEntryViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        TextView keywordTextViewInRecycleView;
        TextView typeTextViewInRecycleView;
        TextView lessonsTextViewInList;
        TextView relevantCodeTextViewInList;

        public KeywordEntryViewHolder(View itemView) {
            super(itemView);
            keywordTextViewInRecycleView = itemView.findViewById(R.id.keywordTextViewInList);
            typeTextViewInRecycleView = itemView.findViewById(R.id.typeTextViewInList);
            lessonsTextViewInList = itemView.findViewById(R.id.lessonsTextViewInList);
            relevantCodeTextViewInList = itemView.findViewById(R.id.relevantCodeTextViewInList);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickHandler.onListItemClick(clickedPosition);
        }
    }

    public interface ListItemClickHandler {
        void onListItemClick(int clickedItemIndex);
    }
}
