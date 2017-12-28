package com.codebutler;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.codebutler.data.KeywordsDbContract;


public class KeywordEntriesListAdapter extends RecyclerView.Adapter<KeywordEntriesListAdapter.KeywordEntryViewHolder> {

    private Context mContext;
    private Cursor mKeywordDatabaseCursor;
    final private ListItemClickHandler mOnClickHandler;

    public KeywordEntriesListAdapter(Context context, Cursor cursor, ListItemClickHandler listener) {
        this.mContext = context;
        this.mKeywordDatabaseCursor = cursor;
        this.mOnClickHandler = listener;
    }

    @Override
    public KeywordEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.keywords_list_item, parent, false);
        return new KeywordEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(KeywordEntryViewHolder holder, int position) {

        //Moving the cursor to the desired row and skipping the next calls if there is no such position
        if (!mKeywordDatabaseCursor.moveToPosition(position)) return;

        //Getting the texts at the appropriate columns in this row
        int current_column = mKeywordDatabaseCursor.getColumnIndex(KeywordsDbContract.KeywordsDbEntry.COLUMN_KEYWORD);
        String person_name = mKeywordDatabaseCursor.getString(current_column);
        holder.keywordTextViewInList.setText(person_name);

        current_column = mKeywordDatabaseCursor.getColumnIndex(KeywordsDbContract.KeywordsDbEntry.COLUMN_LANGUAGE);
        String company = mKeywordDatabaseCursor.getString(current_column);
        holder.codeLanguageTextViewInList.setText(company);
    }

    @Override
    public int getItemCount() {
        return mKeywordDatabaseCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mKeywordDatabaseCursor != null) mKeywordDatabaseCursor.close();
        mKeywordDatabaseCursor = newCursor;
        if (newCursor != null) this.notifyDataSetChanged();
    }

    class KeywordEntryViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        TextView keywordTextViewInList;
        TextView codeLanguageTextViewInList;
        TextView lessonsTextViewInList;
        TextView relevantCodeTextViewInList;

        public KeywordEntryViewHolder(View itemView) {
            super(itemView);
            keywordTextViewInList = itemView.findViewById(R.id.keywordTextViewInList);
            codeLanguageTextViewInList = itemView.findViewById(R.id.codeLanguageTextViewInList);
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
