package com.codebutler;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codebutler.data.DatabaseHelper;
import com.codebutler.data.Keyword;

public class NewEntryActivity extends AppCompatActivity {

    private EditText mNewKeywordEditText;
    private EditText mNewCodeLanguageEditText;
    private EditText mNewLessonsEditText;
    private EditText mNewRelevantCodeEditText;
    Button mAddToKeywordlistButton;

    //SQL globals
    private SQLiteDatabase mSQLiteDatabase;
    private DatabaseHelper dbHelper;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        //Getting the user inputs to update the SQLite database
        mNewKeywordEditText = findViewById(R.id.keywordEditText);
        mNewCodeLanguageEditText = findViewById(R.id.codeLanguageEditText);
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

                //Adding the new entry to the database
                Keyword keyword = new Keyword(mNewKeywordEditText.getText().toString(),
                        mNewCodeLanguageEditText.getText().toString(),
                        mNewLessonsEditText.getText().toString(),
                        mNewRelevantCodeEditText.getText().toString());
                dbHelper.addKeyword(keyword);

                //Move the database cursor to the new entry
                //mKeywordEntriesListAdapter.swapCursor(dbHelper.getAllKeywordEntries());

                //Cleaning things up visually
                mNewKeywordEditText.clearFocus();
                mNewKeywordEditText.getText().clear();
                mNewCodeLanguageEditText.getText().clear();
                mNewLessonsEditText.getText().clear();
                mNewRelevantCodeEditText.getText().clear();
            }
        });
    }

}
