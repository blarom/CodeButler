package com.codebutler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.codebutler.data.DatabaseHelper;

public class MainActivity extends AppCompatActivity implements
        KeywordEntriesListAdapter.ListItemClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    //SQL globals
    public SQLiteDatabase mSQLiteDatabase;
    public DatabaseHelper dbHelper;
    Toast mToast;

    //RecyclerView globals
    private static final int NUM_RECYCLERVIEW_LIST_ITEMS = 100;
    private KeywordEntriesListAdapter mKeywordEntriesListAdapter;
    private RecyclerView mKeywordEntriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSharedPreferences();

        //Preparing the writing operation for the database
        dbHelper = new DatabaseHelper(this);
        mSQLiteDatabase = dbHelper.getWritableDatabase();
        Cursor keywordCursor = dbHelper.getAllKeywordEntries();
        Cursor lessonsCursor = dbHelper.getAllLessonEntries();
        Cursor codeReferenceCursor = dbHelper.getAllCodeReferenceEntries();

        //Preparing the RecyclerView
        mKeywordEntriesList = findViewById(R.id.keywords_list_view);
        mKeywordEntriesList.setLayoutManager(new LinearLayoutManager(this));
        mKeywordEntriesList.setHasFixedSize(true);
        mKeywordEntriesListAdapter = new KeywordEntriesListAdapter(this, keywordCursor, this);
        mKeywordEntriesList.setAdapter(mKeywordEntriesListAdapter);

        //Adding the swipe to remove entry functionality
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                long id = (long) viewHolder.itemView.getTag();

                dbHelper.removeKeywordEntry(id, mSQLiteDatabase);

                mKeywordEntriesListAdapter.swapCursor(dbHelper.getAllKeywordEntries());
            }
        })
                .attachToRecyclerView(mKeywordEntriesList);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        switch (itemThatWasClickedId) {
            case R.id.action_refresh:dbHelper = new DatabaseHelper(this);
                mSQLiteDatabase = dbHelper.getWritableDatabase();
                Cursor keywordCursor = dbHelper.getAllKeywordEntries();
                mKeywordEntriesListAdapter = new KeywordEntriesListAdapter(this, keywordCursor, this);
                mKeywordEntriesList.setAdapter(mKeywordEntriesListAdapter);
                return true;
            case R.id.action_search:
                Context context = MainActivity.this;
                String textToShow = "Search clicked";
                Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_add:
                Intent startChildActivityIntent = new Intent(MainActivity.this, NewEntryActivity.class);
                EditText userKeyword = findViewById(R.id.keywordEntryEditText);
                startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, userKeyword.getText().toString());
                startActivity(startChildActivityIntent);
                return true;
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
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

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setShowGDCADCourse(sharedPreferences.getBoolean(getString(R.string.pref_show_GDC_AD_course_key), getResources().getBoolean(R.bool.pref_show_GDC_AD_course_default)));
        setShowNDADCourse(sharedPreferences.getBoolean(getString(R.string.pref_show_ND_AD_course_key), getResources().getBoolean(R.bool.pref_show_ND_AD_course_default)));
        setPreferredResultType(sharedPreferences.getString(getString(R.string.pref_preferred_result_key), getString(R.string.pref_preferred_result_value_java)));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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
}
