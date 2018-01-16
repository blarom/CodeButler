package com.codebutler;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codebutler.data.CodeButlerDbContract;

import java.util.Arrays;
import java.util.List;

public class SelectedItemDetailsActivity extends AppCompatActivity {

    private TextView mUserKeywordsTextView;
    private TextView mDatabaseKeywordsTextView;
    private TextView mTypeTextView;
    private TextView mSourceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_item_details);

        //Setting the title of the actionbar
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(getResources().getString(R.string.selectedItemDetails));

        //Getting the values from MainActivity
        Intent intentThatStartedThisActivity = getIntent();
        int item_id = 0;
        String userKeywords = "";
        if (intentThatStartedThisActivity.hasExtra("RecyclerViewIndex")) {
            item_id = intentThatStartedThisActivity.getIntExtra("RecyclerViewIndex",0);
        }
        if (intentThatStartedThisActivity.hasExtra("RecyclerViewIndex")) {
            userKeywords = intentThatStartedThisActivity.getStringExtra("Keywords");
            if (userKeywords.equals("")) userKeywords = getResources().getString(R.string.noKeywords);
        }

        //Retrieving the values from the Keywords database
        String stringId = Integer.toString(item_id);
        Cursor cursorKeywords = getContentResolver().query(
                CodeButlerDbContract.KeywordsDbEntry.CONTENT_URI,
                MainActivity.KEYWORD_TABLE_ELEMENTS,
                CodeButlerDbContract.KeywordsDbEntry._ID +" = ?",
                new String[]{stringId},
                null);

        String keywordKeyword = "";
        String keywordType = "";
        String keywordLessons = "";
        String keywordRelevantCode = "";
        String keywordSource = "";

        if (cursorKeywords != null) {
            if (cursorKeywords.moveToFirst()){
                keywordKeyword = cursorKeywords.getString(cursorKeywords.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_KEYWORD));
                keywordType = cursorKeywords.getString(cursorKeywords.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_TYPE));
                keywordLessons = cursorKeywords.getString(cursorKeywords.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_LESSONS));
                keywordRelevantCode = cursorKeywords.getString(cursorKeywords.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_RELEVANT_CODE));
                keywordSource = cursorKeywords.getString(cursorKeywords.getColumnIndex(CodeButlerDbContract.KeywordsDbEntry.COLUMN_SOURCE));
            }
            cursorKeywords.close();
        }
        List<String> lessonsList = Arrays.asList(keywordLessons.split(";"));
        List<String> relevantCodeList = Arrays.asList(keywordRelevantCode.split(";"));

        //Making the keywordSource and keywordType text more readable
        if (keywordSource.equals(MainActivity.KEYWORD_COURSE)) {
            if (keywordLessons.contains(MainActivity.KEYWORD_GDC_AD)) keywordSource = MainActivity.EXPLANATION_GDC_AD;
            else if (keywordLessons.contains(MainActivity.KEYWORD_ND_AD)) keywordSource = MainActivity.EXPLANATION_ND_AD;
            else if (keywordLessons.contains(MainActivity.KEYWORD_FIW)) keywordSource = MainActivity.EXPLANATION_FIW;
        }
        else if (keywordSource.equals(MainActivity.KEYWORD_FORUM)) keywordSource = MainActivity.EXPLANATION_FORUM;
        else if (keywordSource.equals(MainActivity.KEYWORD_USER)) keywordSource = MainActivity.EXPLANATION_USER;

        if (keywordType.equals(MainActivity.KEYWORD_JAVA)) keywordType = MainActivity.EXPLANATION_JAVA;
        else if (keywordType.equals(MainActivity.KEYWORD_LESSON)) keywordType = MainActivity.EXPLANATION_LESSON;
        else if (keywordType.equals(MainActivity.KEYWORD_RESOURCE_XML)) keywordType = MainActivity.EXPLANATION_RESOURCE_XML;
        else if (keywordType.equals(MainActivity.KEYWORD_MANIFEST_XML)) keywordType = MainActivity.EXPLANATION_MANIFEST_XML;
        else if (keywordType.equals(MainActivity.KEYWORD_GRADLE)) keywordType = MainActivity.EXPLANATION_GRADLE;
        else if (keywordType.equals(MainActivity.KEYWORD_JSON)) keywordType = MainActivity.EXPLANATION_JSON;

        //Updating the LinearLayouts used to display the lessons and relevant code
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.topMargin = (int) getResources().getDimension(R.dimen.selectedItemGroupTopMargin);

        LinearLayout lessonsListLinearLayout = findViewById(R.id.lessonsLinearLayout);
        lessonsListLinearLayout.setLayoutParams(params);
        lessonsListLinearLayout.removeAllViews();
        TextView newLessonTV = new TextView(getApplicationContext());
        newLessonTV.setText("Lessons:");
        newLessonTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.selectedItemGroupLabelTextSize));
        newLessonTV.setGravity(Gravity.CENTER_VERTICAL);
        newLessonTV.setTextColor(getResources().getColor(R.color.textColorPrimary));
        newLessonTV.setTypeface(Typeface.DEFAULT_BOLD);
        lessonsListLinearLayout.addView(newLessonTV);
        for (final String lessonsElement : lessonsList) {
            newLessonTV = new TextView(getApplicationContext());
            newLessonTV.setText(lessonsElement);
            newLessonTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.selectedItemSubgroupLabelTextSize));
            newLessonTV.setGravity(Gravity.CENTER_VERTICAL);
            newLessonTV.setTextColor(getResources().getColor(R.color.textColorPrimary));
            if (!lessonsElement.equals(getResources().getString(R.string.NotAvailable))) newLessonTV.setPaintFlags(newLessonTV.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            newLessonTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWebPage(getLessonLink(lessonsElement));
                }
            });
            lessonsListLinearLayout.addView(newLessonTV);
        }

        LinearLayout relevantCodeListLinearLayout = findViewById(R.id.relevantCodeLinearLayout);
        relevantCodeListLinearLayout.setLayoutParams(params);
        relevantCodeListLinearLayout.removeAllViews();
        TextView newRelevantCodeTV = new TextView(getApplicationContext());
        newRelevantCodeTV.setText("Relevant Code:");
        newRelevantCodeTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.selectedItemGroupLabelTextSize));
        newRelevantCodeTV.setGravity(Gravity.CENTER_VERTICAL);
        newRelevantCodeTV.setTextColor(getResources().getColor(R.color.textColorPrimary));
        newRelevantCodeTV.setTypeface(Typeface.DEFAULT_BOLD);
        relevantCodeListLinearLayout.addView(newRelevantCodeTV);
        for (final String relevantCodeElement : relevantCodeList) {
            newRelevantCodeTV = new TextView(getApplicationContext());
            newRelevantCodeTV.setText(relevantCodeElement);
            newRelevantCodeTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.selectedItemSubgroupLabelTextSize));
            newRelevantCodeTV.setGravity(Gravity.CENTER_VERTICAL);
            newRelevantCodeTV.setTextColor(getResources().getColor(R.color.textColorPrimary));
            if (!relevantCodeElement.equals(getResources().getString(R.string.NotAvailable))) newRelevantCodeTV.setPaintFlags(newLessonTV.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            newRelevantCodeTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWebPage(getCodeReferenceLink(relevantCodeElement));
                }
            });
            relevantCodeListLinearLayout.addView(newRelevantCodeTV);
        }

        //Updating the TextView values in the xml
        mUserKeywordsTextView = findViewById(R.id.userKeywordsTextView);
        mDatabaseKeywordsTextView = findViewById(R.id.databaseKeywordsTextView);
        mTypeTextView = findViewById(R.id.typeTextView);
        mSourceTextView = findViewById(R.id.sourceTextView);

        mUserKeywordsTextView.setText(userKeywords);
        mDatabaseKeywordsTextView.setText(keywordKeyword);
        mTypeTextView.setText(keywordType);
        mSourceTextView.setText(keywordSource);

    }
    private String getLessonLink(String reference) {

        //Retrieving the values from the Lessons database
        Cursor cursorLessons = getContentResolver().query(
                CodeButlerDbContract.LessonsDbEntry.CONTENT_URI,
                MainActivity.LESSONS_TABLE_ELEMENTS,
                CodeButlerDbContract.LessonsDbEntry.COLUMN_LESSON_NUMBER + " LIKE '%" + reference + "%'",
                null,
                null);

        String lessonNumber = "";
        String lessonTitle = "";
        String lessonLink = "";
        String lessonSource = "";

        if (cursorLessons != null) {
            if (cursorLessons.moveToFirst()){
                lessonNumber = cursorLessons.getString(cursorLessons.getColumnIndex(CodeButlerDbContract.LessonsDbEntry.COLUMN_LESSON_NUMBER));
                lessonTitle = cursorLessons.getString(cursorLessons.getColumnIndex(CodeButlerDbContract.LessonsDbEntry.COLUMN_LESSON_TITLE));
                lessonLink = cursorLessons.getString(cursorLessons.getColumnIndex(CodeButlerDbContract.LessonsDbEntry.COLUMN_LINK));
                lessonSource = cursorLessons.getString(cursorLessons.getColumnIndex(CodeButlerDbContract.LessonsDbEntry.COLUMN_SOURCE));
            }
            cursorLessons.close();
        }
        return lessonLink;
    }
    private String getCodeReferenceLink(String reference) {

        //Retrieving the values from the CodeReference database
        Cursor cursorCode = getContentResolver().query(
                CodeButlerDbContract.CodeReferenceDbEntry.CONTENT_URI,
                MainActivity.CODE_REFERENCES_TABLE_ELEMENTS,
                CodeButlerDbContract.CodeReferenceDbEntry.COLUMN_CODE_REFERENCE + " LIKE '%" + reference + "%'",
                null,
                null);

        String codeNumber = "";
        String codeLink = "";
        String codeSource = "";

        if (cursorCode != null) {
            if (cursorCode.moveToFirst()){
                codeNumber = cursorCode.getString(cursorCode.getColumnIndex(CodeButlerDbContract.CodeReferenceDbEntry.COLUMN_CODE_REFERENCE));
                codeLink = cursorCode.getString(cursorCode.getColumnIndex(CodeButlerDbContract.CodeReferenceDbEntry.COLUMN_LINK));
                codeSource = cursorCode.getString(cursorCode.getColumnIndex(CodeButlerDbContract.CodeReferenceDbEntry.COLUMN_SOURCE));
            }
            cursorCode.close();
        }
        return codeLink;
    }
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
    }
}
