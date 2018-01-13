package com.codebutler;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.codebutler.data.CodeButlerDbContract;

public class KeywordEntriesRecycleViewAdapter extends RecyclerView.Adapter<KeywordEntriesRecycleViewAdapter.KeywordEntryViewHolder>  {

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
        int idIndex = mKeywordDatabaseCursor.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry._ID);
        int keywordIndex = mKeywordDatabaseCursor.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_KEYWORD);
        int typeIndex = mKeywordDatabaseCursor.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_TYPE);
        int lessonsIndex = mKeywordDatabaseCursor.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_LESSONS);
        int relevantCodeIndex = mKeywordDatabaseCursor.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_RELEVANT_CODE);
        int sourceIndex = mKeywordDatabaseCursor.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_SOURCE);

        // Determine the values of the wanted data
        final int id = mKeywordDatabaseCursor.getInt(idIndex);
        String keyword = mKeywordDatabaseCursor.getString(keywordIndex);
        String type = mKeywordDatabaseCursor.getString(typeIndex);
        String lessons = mKeywordDatabaseCursor.getString(lessonsIndex);
        String relevantCode = mKeywordDatabaseCursor.getString(relevantCodeIndex);
        String source = mKeywordDatabaseCursor.getString(sourceIndex);

        //Making the text more readable
        if (source.equals(MainActivity.KEYWORD_COURSE)) {
            if (lessons.contains(MainActivity.KEYWORD_GDC_AD)) source = MainActivity.EXPLANATION_GDC_AD;
            else if (lessons.contains(MainActivity.KEYWORD_ND_AD)) source = MainActivity.EXPLANATION_ND_AD;
            else if (lessons.contains(MainActivity.KEYWORD_FIW)) source = MainActivity.EXPLANATION_FIW;
        }
        else if (source.equals(MainActivity.KEYWORD_FORUM)) source = MainActivity.EXPLANATION_FORUM;
        else if (source.equals(MainActivity.KEYWORD_USER)) source = MainActivity.EXPLANATION_USER;

        if (type.equals(MainActivity.KEYWORD_JAVA)) type = MainActivity.EXPLANATION_JAVA;
        else if (type.equals(MainActivity.KEYWORD_LESSON)) type = MainActivity.EXPLANATION_LESSON;
        else if (type.equals(MainActivity.KEYWORD_RESOURCE_XML)) type = MainActivity.EXPLANATION_RESOURCE_XML;
        else if (type.equals(MainActivity.KEYWORD_MANIFEST_XML)) type = MainActivity.EXPLANATION_MANIFEST_XML;
        else if (type.equals(MainActivity.KEYWORD_GRADLE)) type = MainActivity.EXPLANATION_GRADLE;
        else if (type.equals(MainActivity.KEYWORD_JSON)) type = MainActivity.EXPLANATION_JSON;

        //Set values
        holder.itemView.setTag(id);
        holder.keywordTextViewInRecycleView.setText(keyword);
        holder.typeTextViewInRecycleView.setText(type);
        holder.lessonsTextViewInRecycleView.setText(lessons);
        holder.relevantCodeTextViewInRecycleView.setText(relevantCode);
        holder.sourceTextViewInRecycleView.setText(source);


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
        TextView lessonsTextViewInRecycleView;
        TextView relevantCodeTextViewInRecycleView;
        TextView sourceTextViewInRecycleView;
        int element_id;

        public KeywordEntryViewHolder(View itemView) {
            super(itemView);
            keywordTextViewInRecycleView = itemView.findViewById(R.id.keywordTextViewInList);
            typeTextViewInRecycleView = itemView.findViewById(R.id.typeTextViewInList);
            lessonsTextViewInRecycleView = itemView.findViewById(R.id.lessonsTextViewInList);
            relevantCodeTextViewInRecycleView = itemView.findViewById(R.id.relevantCodeTextViewInList);
            sourceTextViewInRecycleView = itemView.findViewById(R.id.sourceTextViewInList);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //int clickedPosition = getAdapterPosition();
            mOnClickHandler.onListItemClick((int) view.getTag());
        }
    }

    public interface ListItemClickHandler {
        void onListItemClick(int clickedItemIndex);
    }
}
