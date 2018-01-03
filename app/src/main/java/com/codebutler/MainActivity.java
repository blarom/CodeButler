package com.codebutler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;
import com.codebutler.data.KeywordsLessonsAndCodeDbContract;
import com.codebutler.data.KeywordsLessonsAndCodeDbHelper;

public class MainActivity extends AppCompatActivity implements
        KeywordEntriesRecycleViewAdapter.ListItemClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    EditText mKeywordTeditText;

    //SQL globals
    public SQLiteDatabase mSQLiteDatabase;
    public KeywordsLessonsAndCodeDbHelper dbHelper;
    Toast mToast;
    public static final String[] KEYWORD_TABLE_ELEMENTS = {
            KeywordsLessonsAndCodeDbContract.KeywordsDbEntry._ID,
            KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.COLUMN_KEYWORD,
            KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.COLUMN_TYPE,
            KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.COLUMN_LESSONS,
            KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.COLUMN_RELEVANT_CODE
    };
    public static final int INDEX_COLUMN_KEYWORD = 0;
    public static final int INDEX_COLUMN_TYPE = 1;
    public static final int INDEX_COLUMN_LESSONS = 2;
    public static final int INDEX_COLUMN_RELEVANT_CODE = 3;
    private static final int ID_KEYWORD_DATABASE_LOADER = 666;

    //RecyclerView globals
    private static final int NUM_RECYCLERVIEW_LIST_ITEMS = 100;
    private KeywordEntriesRecycleViewAdapter mKeywordEntriesRecycleViewAdapter;
    private RecyclerView mKeywordEntriesRecylcleView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mLoadingIndicator;


    //Lifecycle methods
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Finding the layouts
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mKeywordEntriesRecylcleView = findViewById(R.id.keywords_list_view);
        mKeywordTeditText = findViewById(R.id.keywordEntryEditText);

        setupSharedPreferences();

        //Preparing the RecyclerView
        mKeywordEntriesRecylcleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mKeywordEntriesRecylcleView.setHasFixedSize(true);
        mKeywordEntriesRecycleViewAdapter = new KeywordEntriesRecycleViewAdapter(this, this);
        mKeywordEntriesRecylcleView.setAdapter(mKeywordEntriesRecycleViewAdapter);

        showLoadingIndicatorInsteadOfRecycleView();

        //Adding the swipe to remove entry functionality
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                String requested_keyword = mKeywordTeditText.getText().toString();

                //Getting the list id of the eleemnt the user wants to delete
                int id = (int) viewHolder.itemView.getTag();

                //Preparing the Uri
                String stringId = Integer.toString(id);
                Uri keywordQueryUri = KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.CONTENT_URI;
                keywordQueryUri = keywordQueryUri.buildUpon().appendPath(stringId).build();

                //Preparing the arguments the user wants to delete
                //String selection = KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.COLUMN_KEYWORD + " = ?";
                //String[] selectionArgs = {"'" + requested_keyword + "'"};
                String selection = null;
                String[] selectionArgs = null;

                // Deletes the words that match the selection criteria
                int mRowsDeleted = 0;
                mRowsDeleted = getContentResolver().delete(keywordQueryUri, selection, selectionArgs);

//                long id = (long) viewHolder.itemView.getTag();
//
//                // Building the appropriate uri with String row id appended
//                String stringId = Long.toString(id);
//                Uri uri = KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.CONTENT_URI;
//                uri = uri.buildUpon().appendPath(stringId).build();
//
//                // Deleting a single row of data using a ContentResolver
//                getContentResolver().delete(uri, null, null);

                //Restarting the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(ID_KEYWORD_DATABASE_LOADER, null, MainActivity.this);
            }
        }).attachToRecyclerView(mKeywordEntriesRecylcleView);

        //Adding the Add Keyowrd Entry functionality in a FloatingActionButton
        FloatingActionButton fabButton = findViewById(R.id.floatingActionButton);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText userKeyword = findViewById(R.id.keywordEntryEditText);
                startNewKeywordEntryActivity(userKeyword.getText().toString());
            }
        });

        getSupportLoaderManager().initLoader(ID_KEYWORD_DATABASE_LOADER, null, this);

    }
    @Override protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(ID_KEYWORD_DATABASE_LOADER, null, this);
    }

    //Options Menu methods
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        switch (itemThatWasClickedId) {
            case R.id.action_refresh:dbHelper = new KeywordsLessonsAndCodeDbHelper(this);
                getSupportLoaderManager().restartLoader(ID_KEYWORD_DATABASE_LOADER, null, this);
                mKeywordEntriesRecycleViewAdapter = new KeywordEntriesRecycleViewAdapter(this,  this);
                mKeywordEntriesRecylcleView.setAdapter(mKeywordEntriesRecycleViewAdapter);
                return true;
            case R.id.action_search:
                Context context = MainActivity.this;
                String textToShow = "Search clicked";
                Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_add:
                EditText userKeyword = findViewById(R.id.keywordEntryEditText);
                startNewKeywordEntryActivity(userKeyword.getText().toString());
                return true;
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void startNewKeywordEntryActivity(String keyword) {
        Intent startChildActivityIntent = new Intent(MainActivity.this, NewKeywordEntryActivity.class);
        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, keyword);
        startActivity(startChildActivityIntent);
    }
    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setShowGDCADCourse(sharedPreferences.getBoolean(getString(R.string.pref_show_GDC_AD_course_key), getResources().getBoolean(R.bool.pref_show_GDC_AD_course_default)));
        setShowNDADCourse(sharedPreferences.getBoolean(getString(R.string.pref_show_ND_AD_course_key), getResources().getBoolean(R.bool.pref_show_ND_AD_course_default)));
        setPreferredResultType(sharedPreferences.getString(getString(R.string.pref_preferred_result_key), getString(R.string.pref_preferred_result_value_java)));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
    @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_show_GDC_AD_course_key))) {
            setShowGDCADCourse(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_GDC_AD_course_default)));
        }
        else if (key.equals(getString(R.string.pref_show_ND_AD_course_key))) {
            setShowNDADCourse(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_ND_AD_course_default)));
        }

    }
    public void setShowGDCADCourse (boolean showCourse) {

    }
    public void setShowNDADCourse (boolean showCourse) {

    }
    public void setPreferredResultType(String preferred_type) {
        switch (preferred_type) {
            case "xml":
                break;
            case "java":
                break;
            case "explanations":
                break;
        }
    }

    //RecycleView methods
    @Override public void onListItemClick(int clickedItemIndex) {
        if (mToast != null) mToast.cancel();
        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
        mToast.show();
    }
    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
    }
    private void showRecycleViewInsteadOfLoadingIndicator() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mKeywordEntriesRecylcleView.setVisibility(View.VISIBLE);
    }
    private void showLoadingIndicatorInsteadOfRecycleView() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mKeywordEntriesRecylcleView.setVisibility(View.INVISIBLE);
    }

    //Database methods
    @Override public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            case ID_KEYWORD_DATABASE_LOADER:
                showLoadingIndicatorInsteadOfRecycleView();
                Uri keywordQueryUri = KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.CONTENT_URI;
                String requested_keyword = mKeywordTeditText.getText().toString();
                String selection = KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.getSelectionForGivenKeywordsAndOperator(requested_keyword, "AND");
                String sortOrder = KeywordsLessonsAndCodeDbContract.KeywordsDbEntry.COLUMN_KEYWORD;
                return new CursorLoader(this, keywordQueryUri, KEYWORD_TABLE_ELEMENTS, selection, null, sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }
    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mKeywordEntriesRecycleViewAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mKeywordEntriesRecylcleView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showRecycleViewInsteadOfLoadingIndicator();
    }
    @Override public void onLoaderReset(Loader<Cursor> loader) {
        mKeywordEntriesRecycleViewAdapter.swapCursor(null);
    }
}
