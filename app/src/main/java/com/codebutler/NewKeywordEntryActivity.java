package com.codebutler;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codebutler.data.CodeButlerDbContract;
import com.codebutler.utilities.SharedMethods;

public class NewKeywordEntryActivity extends AppCompatActivity {

    private EditText mNewKeywordEditText;
    private String mNewTypeString;
    private EditText mNewLessonsReferenceEditText;
    private EditText mNewLessonsNameEditText;
    private EditText mNewLessonsLinkEditText;
    private EditText mNewRelevantCodeReferenceEditText;
    private EditText mNewRelevantCodeLinkEditText;
    private Button mAddToKeywordlistButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        //Setting the title of the actionbar
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(getResources().getString(R.string.newEntry));

        //Getting the user inputs to update the SQLite database
        mNewKeywordEditText = findViewById(R.id.keywordEditText);
        mNewLessonsReferenceEditText = findViewById(R.id.lessonsReferenceEditText);
        mNewLessonsNameEditText = findViewById(R.id.lessonsTitleEditText);
        mNewLessonsLinkEditText = findViewById(R.id.lessonsLinkEditText);
        mNewRelevantCodeReferenceEditText = findViewById(R.id.relevantCodeReferenceEditText);
        mNewRelevantCodeLinkEditText = findViewById(R.id.relevantCodeLinkEditText);

        fixSoftKeyboardBehavior(mNewKeywordEditText);
        fixSoftKeyboardBehavior(mNewLessonsReferenceEditText);
        fixSoftKeyboardBehavior(mNewLessonsNameEditText);
        fixSoftKeyboardBehavior(mNewLessonsLinkEditText);
        fixSoftKeyboardBehavior(mNewRelevantCodeReferenceEditText);
        fixSoftKeyboardBehavior(mNewRelevantCodeLinkEditText);

        findViewById(R.id.keywordEditTextClearButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewKeywordEditText.setText("");
            }
        });
        findViewById(R.id.lessonsReferenceEditTextClearButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewLessonsReferenceEditText.setText("");
            }
        });
        findViewById(R.id.lessonsTitleEditTextClearButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewLessonsNameEditText.setText("");
            }
        });
        findViewById(R.id.lessonsLinkEditTextClearButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewLessonsLinkEditText.setText("");
            }
        });
        findViewById(R.id.relevantCodeReferenceEditTextClearButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewRelevantCodeReferenceEditText.setText("");
            }
        });
        findViewById(R.id.relevantCodeLinkEditTextClearButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewRelevantCodeLinkEditText.setText("");
            }
        });

        findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Remove the software keyboard if the EditText is not in focus
                SharedMethods.hideSoftKeyboard(NewKeywordEntryActivity.this);
                return false;
            }
        });

        Spinner dropdown = findViewById(R.id.typeDropDownSpinner);
        final String[] items = new String[]{
                getResources().getString(R.string.type_label_lesson),
                getResources().getString(R.string.type_label_java),
                getResources().getString(R.string.type_label_resource_xml),
                getResources().getString(R.string.type_label_manifest_xml),
                getResources().getString(R.string.type_label_gradle),
                getResources().getString(R.string.type_label_json)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                mNewTypeString = items[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mNewTypeString = getResources().getString(R.string.type_label_lesson);
            }
        });

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

                // Put the Keywords input into the a ContentValues object and insert the content values via a ContentResolver
                Uri uri;
                ContentValues contentValues = new ContentValues();
                contentValues.put(CodeButlerDbContract.KeywordsDbEntry.COLUMN_KEYWORD, mNewKeywordEditText.getText().toString());
                contentValues.put(CodeButlerDbContract.KeywordsDbEntry.COLUMN_TYPE, mNewTypeString);
                contentValues.put(CodeButlerDbContract.KeywordsDbEntry.COLUMN_LESSONS, mNewLessonsReferenceEditText.getText().toString());
                contentValues.put(CodeButlerDbContract.KeywordsDbEntry.COLUMN_RELEVANT_CODE, mNewRelevantCodeReferenceEditText.getText().toString());
                contentValues.put(CodeButlerDbContract.KeywordsDbEntry.COLUMN_SOURCE, getResources().getString(R.string.userIsTheSource));
                uri = getContentResolver().insert(CodeButlerDbContract.KeywordsDbEntry.CONTENT_URI, contentValues);


                // Put the Lessons input into the a ContentValues object and insert the content values via a ContentResolver
                if (mNewLessonsReferenceEditText.getText().toString().length() != 0) {
                    if (mNewLessonsLinkEditText.getText().toString().length() == 0) mNewLessonsLinkEditText.setText(getResources().getString(R.string.NotAvailable));
                    if (mNewLessonsNameEditText.getText().toString().length() == 0) mNewLessonsNameEditText.setText(getResources().getString(R.string.NotAvailable));
                    contentValues = new ContentValues();
                    contentValues.put(CodeButlerDbContract.LessonsDbEntry.COLUMN_LESSON_NUMBER, mNewLessonsReferenceEditText.getText().toString());
                    contentValues.put(CodeButlerDbContract.LessonsDbEntry.COLUMN_LESSON_TITLE, mNewLessonsNameEditText.getText().toString());
                    contentValues.put(CodeButlerDbContract.LessonsDbEntry.COLUMN_LINK, mNewLessonsLinkEditText.getText().toString());
                    contentValues.put(CodeButlerDbContract.LessonsDbEntry.COLUMN_SOURCE, getResources().getString(R.string.userIsTheSource));
                    uri = getContentResolver().insert(CodeButlerDbContract.LessonsDbEntry.CONTENT_URI, contentValues);
                }


                // Put the RelevantCode input into the a ContentValues object and insert the content values via a ContentResolver
                if (mNewRelevantCodeReferenceEditText.getText().toString().length() != 0) {
                    if (mNewRelevantCodeLinkEditText.getText().toString().length() == 0) mNewRelevantCodeLinkEditText.setText(getResources().getString(R.string.NotAvailable));
                    contentValues = new ContentValues();
                    contentValues.put(CodeButlerDbContract.CodeReferenceDbEntry.COLUMN_CODE_REFERENCE, mNewRelevantCodeReferenceEditText.getText().toString());
                    contentValues.put(CodeButlerDbContract.CodeReferenceDbEntry.COLUMN_LINK, mNewRelevantCodeLinkEditText.getText().toString());
                    contentValues.put(CodeButlerDbContract.CodeReferenceDbEntry.COLUMN_SOURCE, getResources().getString(R.string.userIsTheSource));
                    uri = getContentResolver().insert(CodeButlerDbContract.CodeReferenceDbEntry.CONTENT_URI, contentValues);
                }


                if(uri != null) {
                    Toast.makeText(getBaseContext(), "Added " + mNewKeywordEditText.getText().toString() + " to the local database.", Toast.LENGTH_LONG).show();
                }

                //Cleaning things up visually
                //mNewKeywordEditText.clearFocus();
                //findViewById(android.R.id.content).clearFocus();
                mNewKeywordEditText.getText().clear();
                mNewLessonsReferenceEditText.getText().clear();
                mNewLessonsNameEditText.getText().clear();
                mNewLessonsLinkEditText.getText().clear();
                mNewRelevantCodeReferenceEditText.getText().clear();
                mNewRelevantCodeLinkEditText.getText().clear();

                // Finish activity (this returns back to MainActivity)
                finish();
            }
        });
    }

    private void fixSoftKeyboardBehavior(final EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.requestFocus();
                SharedMethods.showSoftKeyboard(NewKeywordEntryActivity.this, editText);
            }
        });
    }

}
