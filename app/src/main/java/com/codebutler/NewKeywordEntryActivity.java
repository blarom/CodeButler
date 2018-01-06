package com.codebutler;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codebutler.data.CodeButlerDbContract;
import com.codebutler.data.KeywordsLessonsAndCodeDbHelper;

public class NewKeywordEntryActivity extends AppCompatActivity {

    private EditText mNewKeywordEditText;
    private EditText mNewTypeEditText;
    private EditText mNewLessonsEditText;
    private EditText mNewRelevantCodeEditText;
    Button mAddToKeywordlistButton;

    //SQL globals
    private SQLiteDatabase mSQLiteDatabase;
    private KeywordsLessonsAndCodeDbHelper dbHelper;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        //Getting the user inputs to update the SQLite database
        mNewKeywordEditText = findViewById(R.id.keywordEditText);
        mNewTypeEditText = findViewById(R.id.codeLanguageEditText);
        mNewLessonsEditText = findViewById(R.id.lessonsEditText);
        mNewRelevantCodeEditText = findViewById(R.id.relevantCodeEditText);

        //Getting the data from MainActivity
        Intent intentThatStartedThisActivity = getIntent();
        String userKeyword;
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            userKeyword = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            mNewKeywordEditText.setText(userKeyword);
        }

        //Adding the user's input when the Add button is pressed
        findViewById(R.id.add_to_keywordlist_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If no keyword is entered, don't add anything to the database
                if (mNewKeywordEditText.getText().length() == 0) return;

                // Put the user input into the a ContentValues object
                ContentValues contentValues = new ContentValues();
                contentValues.put(CodeButlerDbContract.KeywordsDbEntry.COLUMN_KEYWORD, mNewKeywordEditText.getText().toString());
                contentValues.put(CodeButlerDbContract.KeywordsDbEntry.COLUMN_TYPE, mNewTypeEditText.getText().toString());
                contentValues.put(CodeButlerDbContract.KeywordsDbEntry.COLUMN_LESSONS, mNewLessonsEditText.getText().toString());
                contentValues.put(CodeButlerDbContract.KeywordsDbEntry.COLUMN_RELEVANT_CODE, mNewRelevantCodeEditText.getText().toString());

                // Insert the content values via a ContentResolver
                Uri uri = getContentResolver().insert(CodeButlerDbContract.KeywordsDbEntry.CONTENT_URI, contentValues);

                if(uri != null) {
                    Toast.makeText(getBaseContext(), "Added " + mNewKeywordEditText.getText().toString() + " to the local database.", Toast.LENGTH_LONG).show();
                }

                //Cleaning things up visually
                mNewKeywordEditText.clearFocus();
                mNewKeywordEditText.getText().clear();
                mNewTypeEditText.getText().clear();
                mNewLessonsEditText.getText().clear();
                mNewRelevantCodeEditText.getText().clear();

                // Finish activity (this returns back to MainActivity)
                finish();
            }
        });
    }

}
