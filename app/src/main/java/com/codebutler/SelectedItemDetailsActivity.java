package com.codebutler;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codebutler.data.CodeButlerDbContract;

import java.util.Arrays;
import java.util.List;

public class SelectedItemDetailsActivity extends AppCompatActivity {

    private TextView mKeywordTextView;
    private TextView mTypeTextView;
    private TextView mLessonsTextView;
    private TextView mRelevantCodeTextView;
    private TextView mSourceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_item_details);

        Intent intentThatStartedThisActivity = getIntent();
        int item_id = 0;
        if (intentThatStartedThisActivity.hasExtra("RecyclerViewIndex")) {
            item_id = intentThatStartedThisActivity.getIntExtra("RecyclerViewIndex",0);
        }

        //Preparing the Uri
        String stringId = Integer.toString(item_id);
        Uri clickedItemQueryUri = CodeButlerDbContract.KeywordsDbEntry.CONTENT_URI;
        //clickedItemQueryUri = clickedItemQueryUri.buildUpon().appendPath(stringId).build();

        //Retrieving the values from the database
        Cursor cursor = getContentResolver().query(clickedItemQueryUri,
                MainActivity.KEYWORD_TABLE_ELEMENTS,
                CodeButlerDbContract.KeywordsDbEntry._ID +" = ?",
                new String[]{stringId},
                null);

        String keyword = "";
        String type = "";
        String lessons = "";
        String relevantCode = "";
        String source = "";

        if (cursor != null) {
            if (cursor.moveToFirst()){
                keyword = cursor.getString(cursor.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_KEYWORD));
                type = cursor.getString(cursor.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_TYPE));
                lessons = cursor.getString(cursor.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_LESSONS));
                relevantCode = cursor.getString(cursor.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_RELEVANT_CODE));
                source = cursor.getString(cursor.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_SOURCE));
            }
            cursor.close();
        }
        mKeywordTextView = findViewById(R.id.keywordTextView);
        mTypeTextView = findViewById(R.id.typeTextView);
        mLessonsTextView = findViewById(R.id.lessonsTextView);
        mRelevantCodeTextView = findViewById(R.id.relevantCodeTextView);
        mSourceTextView = findViewById(R.id.sourceTextView);

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


        mKeywordTextView.setText(keyword);
        mTypeTextView.setText(type);
        mLessonsTextView.setText(lessons);
        mRelevantCodeTextView.setText(relevantCode);
        mSourceTextView.setText(source);

    }
}
