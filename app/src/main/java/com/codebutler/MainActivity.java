package com.codebutler;

//TODO set database initializ
//TODO adjust recyclerview colors
//TODO clean selected item results on start
//TODO add explanations in preferences, incl legend
//TODO enter performs search
//TODO hidesoftkeyboard
//TODO Selecteditems add button on top of scrolllayout
//TODO large text in recycleview is cut by textview height + need to implement multiple rows
//TODO title of actionbar in newitemsactivity should be "new item details"
//TODO title of actionbar in selecteditemsactivity should be "selected item details"
//TODO create icons for app

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;

import com.codebutler.data.CodeButlerDbContract;
import com.codebutler.data.CodeButlerDbHelper;

public class MainActivity extends AppCompatActivity implements
        KeywordEntriesRecycleViewAdapter.ListItemClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    //Locals
    EditText mKeywordEditText;
    Toast mToast;
    String mSearchType;
    Boolean[] mShowSources;
    Button mClearButton;

    //Preferences Globals
    public static final int SHOW_USER_VALUES_KEY = 0;
    public static final int SHOW_GDC_AD_COURSE_KEY = 1;
    public static final int SHOW_ND_AD_COURSE_KEY = 2;
    public static final int SHOW_FIW_COURSE_KEY = 3;
    public static final int SHOW_FORUM_THREADS_KEY = 4;
    public static Boolean atLeastOneCourseIsShown;

    //SQL globals
    public CodeButlerDbHelper dbHelper;
    public static final String[] KEYWORD_TABLE_ELEMENTS = {
            CodeButlerDbContract.KeywordsDbEntry._ID,
            CodeButlerDbContract.KeywordsDbEntry.COLUMN_KEYWORD,
            CodeButlerDbContract.KeywordsDbEntry.COLUMN_TYPE,
            CodeButlerDbContract.KeywordsDbEntry.COLUMN_LESSONS,
            CodeButlerDbContract.KeywordsDbEntry.COLUMN_RELEVANT_CODE,
            CodeButlerDbContract.KeywordsDbEntry.COLUMN_SOURCE
    };
    public static final String[] LESSONS_TABLE_ELEMENTS = {
            CodeButlerDbContract.LessonsDbEntry._ID,
            CodeButlerDbContract.LessonsDbEntry.COLUMN_LESSON_NUMBER,
            CodeButlerDbContract.LessonsDbEntry.COLUMN_LESSON_TITLE,
            CodeButlerDbContract.LessonsDbEntry.COLUMN_LINK,
            CodeButlerDbContract.LessonsDbEntry.COLUMN_SOURCE
    };
    public static final String[] CODE_REFERENCES_TABLE_ELEMENTS = {
            CodeButlerDbContract.CodeReferenceDbEntry._ID,
            CodeButlerDbContract.CodeReferenceDbEntry.COLUMN_CODE_REFERENCE,
            CodeButlerDbContract.CodeReferenceDbEntry.COLUMN_LINK,
            CodeButlerDbContract.CodeReferenceDbEntry.COLUMN_SOURCE
    };
    public static final int INDEX_COLUMN_KEYWORD = 1;
    public static final int INDEX_COLUMN_TYPE = 2;
    public static final int INDEX_COLUMN_LESSONS = 3;
    public static final int INDEX_COLUMN_RELEVANT_CODE = 4;
    public static final int INDEX_COLUMN_SOURCE = 5;
    private static final int ID_KEYWORD_DATABASE_LOADER = 666;

    //RecyclerView globals
    public static String KEYWORD_GDC_AD;
    public static String KEYWORD_ND_AD;
    public static String KEYWORD_FIW;
    public static String KEYWORD_COURSE;
    public static String KEYWORD_FORUM;
    public static String KEYWORD_USER;
    public static String KEYWORD_UFT;
    public static String KEYWORD_SO;
    public static String EXPLANATION_USER;
    public static String EXPLANATION_GDC_AD;
    public static String EXPLANATION_ND_AD;
    public static String EXPLANATION_FIW;
    public static String EXPLANATION_FORUM;
    public static String KEYWORD_JAVA;
    public static String KEYWORD_LESSON;
    public static String KEYWORD_RESOURCE_XML;
    public static String KEYWORD_MANIFEST_XML;
    public static String KEYWORD_GRADLE;
    public static String KEYWORD_JSON;
    public static String EXPLANATION_JAVA;
    public static String EXPLANATION_LESSON;
    public static String EXPLANATION_RESOURCE_XML;
    public static String EXPLANATION_MANIFEST_XML;
    public static String EXPLANATION_GRADLE;
    public static String EXPLANATION_JSON;
    private static final int NUM_RECYCLERVIEW_LIST_ITEMS = 100;
    private KeywordEntriesRecycleViewAdapter mKeywordEntriesRecycleViewAdapter;
    private RecyclerView mKeywordEntriesRecylcleView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mLoadingIndicator;

    //Lifecycle methods
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing the layout values
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mKeywordEntriesRecylcleView = findViewById(R.id.keywords_list_view);
        mClearButton = findViewById(R.id.button_clear);
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mKeywordEditText.setText("");
            }
        });
        mKeywordEditText = findViewById(R.id.keywordEntryEditText);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.CodeButlerSharedPrefs), 0);
        mKeywordEditText.setText(pref.getString(getResources().getString(R.string.user_keywords), null));
        mShowSources = new Boolean[]{
                getResources().getBoolean(R.bool.pref_show_user_values_default),
                getResources().getBoolean(R.bool.pref_show_GDC_AD_course_default),
                getResources().getBoolean(R.bool.pref_show_GDC_AD_course_default),
                getResources().getBoolean(R.bool.pref_show_FIW_course_default),
                getResources().getBoolean(R.bool.pref_show_forum_threads_default)};
        atLeastOneCourseIsShown = atLeastOneCourseIsShown();

        //Initializing the global constants
        KEYWORD_GDC_AD = getResources().getString(R.string.courseCode_GDCAD);
        KEYWORD_ND_AD = getResources().getString(R.string.courseCode_NDAD);
        KEYWORD_FIW = getResources().getString(R.string.courseCode_FIW);
        KEYWORD_COURSE = getResources().getString(R.string.courseIsTheSource);
        KEYWORD_FORUM = getResources().getString(R.string.forumIsTheSource);
        KEYWORD_USER = getResources().getString(R.string.userIsTheSource);
        KEYWORD_UFT = getResources().getString(R.string.udacity_thread);
        KEYWORD_SO = getResources().getString(R.string.stackoverflow_thread);

        EXPLANATION_USER = getResources().getString(R.string.recyclerViewSourceExplanationUSER);
        EXPLANATION_GDC_AD = getResources().getString(R.string.recyclerViewSourceExplanationGDCAD);
        EXPLANATION_ND_AD = getResources().getString(R.string.recyclerViewSourceExplanationNDAD);
        EXPLANATION_FIW = getResources().getString(R.string.recyclerViewSourceExplanationFIW);
        EXPLANATION_FORUM = getResources().getString(R.string.recyclerViewSourceExplanationFORUM);

        KEYWORD_JAVA = getResources().getString(R.string.pref_preferred_result_value_java);
        KEYWORD_LESSON= getResources().getString(R.string.pref_preferred_result_value_lesson);
        KEYWORD_RESOURCE_XML= getResources().getString(R.string.pref_preferred_result_value_resource_xml);
        KEYWORD_MANIFEST_XML = getResources().getString(R.string.pref_preferred_result_value_manifest_xml);
        KEYWORD_GRADLE = getResources().getString(R.string.pref_preferred_result_value_gradle);
        KEYWORD_JSON = getResources().getString(R.string.pref_preferred_result_value_json);

        EXPLANATION_JAVA = getResources().getString(R.string.type_label_java);
        EXPLANATION_LESSON= getResources().getString(R.string.type_label_lesson);
        EXPLANATION_RESOURCE_XML= getResources().getString(R.string.type_label_resource_xml);
        EXPLANATION_MANIFEST_XML = getResources().getString(R.string.type_label_manifest_xml);
        EXPLANATION_GRADLE = getResources().getString(R.string.type_label_gradle);
        EXPLANATION_JSON = getResources().getString(R.string.type_label_json);

        //Initialization methods
        getSearchType();
        setupSharedPreferences();

        //Preparing the RecyclerView
        mKeywordEntriesRecylcleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mKeywordEntriesRecylcleView.setHasFixedSize(true);
        mKeywordEntriesRecycleViewAdapter = new KeywordEntriesRecycleViewAdapter(this, this);
        mKeywordEntriesRecylcleView.setAdapter(mKeywordEntriesRecycleViewAdapter);
        mKeywordEntriesRecylcleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        showLoadingIndicatorInsteadOfRecycleView();

        //Adding the swipe-to-remove-entry functionality
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                //Getting the list id of the element the user wants to delete
                int id = (int) viewHolder.itemView.getTag();
                TextView sourceTV = mKeywordEntriesRecylcleView.findViewHolderForAdapterPosition(viewHolder.getLayoutPosition()).itemView.findViewById(R.id.sourceTextViewInList);
                String sourceValue = sourceTV.getText().toString();

                //Preparing the Uri
                String stringId = Integer.toString(id);
                Uri keywordQueryUri = CodeButlerDbContract.KeywordsDbEntry.CONTENT_URI;
                keywordQueryUri = keywordQueryUri.buildUpon().appendPath(stringId).build();

                // Deletes the words that match the selection criteria
                int mRowsDeleted = 0;
                String selection = null;
                String[] selectionArgs = null;
                if (sourceValue.equals(getResources().getString(R.string.userIsTheSource))) mRowsDeleted = getContentResolver().delete(keywordQueryUri, selection, selectionArgs);

//                long id = (long) viewHolder.itemView.getTag();
//
//                // Building the appropriate uri with String row id appended
//                String stringId = Long.toString(id);
//                Uri uri = CodeButlerDbContract.KeywordsDbEntry.CONTENT_URI;
//                uri = uri.buildUpon().appendPath(stringId).build();
//
//                // Deleting a single row of data using a ContentResolver
//                getContentResolver().delete(uri, null, null);

                //Restarting the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(ID_KEYWORD_DATABASE_LOADER, null, MainActivity.this);
            }

        }).attachToRecyclerView(mKeywordEntriesRecylcleView);


        //Adding the Add Keyword Entry functionality in a FloatingActionButton
        FloatingActionButton fabButton = findViewById(R.id.floatingActionButton);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText userKeyword = findViewById(R.id.keywordEntryEditText);
                startNewKeywordEntryActivity(userKeyword.getText().toString());
            }
        });

        //Initializing the database and Loader
        initializeDatabasesForNewDatabaseVersion();
        getSupportLoaderManager().initLoader(ID_KEYWORD_DATABASE_LOADER, null, this);

    }
    @Override protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(ID_KEYWORD_DATABASE_LOADER, null, this);
        //mKeywordEntriesRecycleViewAdapter.notifyDataSetChanged();
    }

    //Options Menu methods
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        switch (itemThatWasClickedId) {
            case R.id.action_search:dbHelper = new CodeButlerDbHelper(this);
                //Saving the current value of the EditText
                SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.CodeButlerSharedPrefs), 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(getResources().getString(R.string.user_keywords), mKeywordEditText.getText().toString());
                editor.apply();

                //Loading the RecycleView adapter with the Loader values
                getSupportLoaderManager().restartLoader(ID_KEYWORD_DATABASE_LOADER, null, this);
                mKeywordEntriesRecycleViewAdapter = new KeywordEntriesRecycleViewAdapter(this,  this);
                mKeywordEntriesRecylcleView.setAdapter(mKeywordEntriesRecycleViewAdapter);
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
        setShowUserValues(sharedPreferences.getBoolean(getString(R.string.pref_show_user_values_key), getResources().getBoolean(R.bool.pref_show_user_values_default)));
        setShowGDCADCourse(sharedPreferences.getBoolean(getString(R.string.pref_show_GDC_AD_course_key), getResources().getBoolean(R.bool.pref_show_GDC_AD_course_default)));
        setShowNDADCourse(sharedPreferences.getBoolean(getString(R.string.pref_show_ND_AD_course_key), getResources().getBoolean(R.bool.pref_show_ND_AD_course_default)));
        setShowForumThreads(sharedPreferences.getBoolean(getString(R.string.pref_show_forum_threads_key), getResources().getBoolean(R.bool.pref_show_forum_threads_default)));
        setShowFIWCourse(sharedPreferences.getBoolean(getString(R.string.pref_show_FIW_course_key), getResources().getBoolean(R.bool.pref_show_FIW_course_default)));
        setPreferredResultType(sharedPreferences.getString(getString(R.string.pref_preferred_result_key), getString(R.string.pref_preferred_result_value_java)));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
    @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        atLeastOneCourseIsShown = atLeastOneCourseIsShown();
        if (key.equals(getString(R.string.pref_show_user_values_key))) {
            setShowUserValues(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_user_values_default)));
        }
        else if (key.equals(getString(R.string.pref_show_GDC_AD_course_key))) {
            setShowGDCADCourse(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_GDC_AD_course_default)));
        }
        else if (key.equals(getString(R.string.pref_show_ND_AD_course_key))) {
            setShowNDADCourse(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_ND_AD_course_default)));
        }
        else if (key.equals(getString(R.string.pref_show_FIW_course_key))) {
            setShowFIWCourse(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_FIW_course_default)));
        }
        else if (key.equals(getString(R.string.pref_show_forum_threads_key))) {
            setShowForumThreads(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_forum_threads_default)));
        }
    }
    public void setShowUserValues (boolean showCourse) {
        mShowSources[SHOW_USER_VALUES_KEY] = showCourse;
        atLeastOneCourseIsShown = atLeastOneCourseIsShown();
    }
    public void setShowGDCADCourse (boolean showCourse) {
        mShowSources[SHOW_GDC_AD_COURSE_KEY] = showCourse;
        atLeastOneCourseIsShown = atLeastOneCourseIsShown();
    }
    public void setShowNDADCourse (boolean showCourse) {
        mShowSources[SHOW_ND_AD_COURSE_KEY] = showCourse;
        atLeastOneCourseIsShown = atLeastOneCourseIsShown();
    }
    public void setShowFIWCourse (boolean showCourse) {
        mShowSources[SHOW_FIW_COURSE_KEY] = showCourse;
        atLeastOneCourseIsShown = atLeastOneCourseIsShown();
    }
    public void setShowForumThreads (boolean showCourse) {
        mShowSources[SHOW_FORUM_THREADS_KEY] = showCourse;
        atLeastOneCourseIsShown = atLeastOneCourseIsShown();
    }
    public Boolean atLeastOneCourseIsShown() {
        return (mShowSources[SHOW_GDC_AD_COURSE_KEY] || mShowSources[SHOW_ND_AD_COURSE_KEY] || mShowSources[SHOW_FIW_COURSE_KEY]);
    }
    public void setPreferredResultType(String preferred_type) {
        if (preferred_type.equals(getResources().getString(R.string.pref_preferred_result_value_resource_xml))) {

        }
        if (preferred_type.equals(getResources().getString(R.string.pref_preferred_result_value_manifest_xml))) {

        }
        else if (preferred_type.equals(getResources().getString(R.string.pref_preferred_result_value_java))) {

        }
        else if (preferred_type.equals(getResources().getString(R.string.pref_preferred_result_value_gradle))) {

        }
        else if (preferred_type.equals(getResources().getString(R.string.pref_preferred_result_value_lesson))) {

        }
        else if (preferred_type.equals(getResources().getString(R.string.pref_preferred_result_value_json))) {

        }
    }

    //RecycleView methods
    @Override public void onListItemClick(int clickedItemIndex) {
        if (mToast != null) mToast.cancel();

        startNewSelectedItemDetailsActivity(clickedItemIndex);
    }
    private void startNewSelectedItemDetailsActivity(int clickedItemIndex) {
        Intent startChildActivityIntent = new Intent(MainActivity.this, SelectedItemDetailsActivity.class);
        startChildActivityIntent.putExtra("RecyclerViewIndex", clickedItemIndex);
        startChildActivityIntent.putExtra("Keywords", mKeywordEditText.getText().toString());
        startActivity(startChildActivityIntent);
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
                Uri keywordQueryUri = CodeButlerDbContract.KeywordsDbEntry.CONTENT_URI;
                String requested_keyword = mKeywordEditText.getText().toString();
                String selection = CodeButlerDbContract.KeywordsDbEntry.getSelectionForGivenKeywordsAndOperator(requested_keyword, mSearchType, mShowSources);
                String sortOrder = CodeButlerDbContract.KeywordsDbEntry.COLUMN_KEYWORD;
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
    private void initializeDatabasesForNewDatabaseVersion() {

        //Reading the previous database version from SharedPreferences
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int defaultValueIfSharedPrefDoesNotExist = Integer.parseInt(getResources().getString(R.string.defaultDatabaseVersion));
        long databaseVersionInSharedPreferences = 0;
        try {
            databaseVersionInSharedPreferences = sharedPref.getInt(getString(R.string.databaseVersionKey), defaultValueIfSharedPrefDoesNotExist);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //If the database version was incremented, clear and re-initialize the database and register the new database version
        if (databaseVersionInSharedPreferences < CodeButlerDbHelper.DATABASE_VERSION) {

            //create Udacity Mapping database rows from the csv in assets
            CodeButlerDbHelper dbHelper = new CodeButlerDbHelper(this);
            dbHelper.initializeKeywordsDatabase();
            dbHelper.initializeLessonsDatabase();
            dbHelper.initializeCodeReferencesDatabase();

            //save the current database version
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.databaseVersionKey), CodeButlerDbHelper.DATABASE_VERSION);
            editor.apply();
        }

        getSupportLoaderManager().restartLoader(ID_KEYWORD_DATABASE_LOADER, null, this);
    }

    //Helper methods
    private void getSearchType() {
        RadioGroup search_type = findViewById(R.id.search_type);
        mSearchType = getValueFromRadioButtons();
        search_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mSearchType = getValueFromRadioButtons();
            }
        });
    }
    @NonNull private String getValueFromRadioButtons() {
        RadioGroup search_type = findViewById(R.id.search_type);
        int selectedId = search_type.getCheckedRadioButtonId();
        switch (selectedId) {
            case R.id.search_exact: return "EXACT";
            case R.id.search_and: return "AND";
            case R.id.search_or: return "OR";
            default: return  "OR";
        }
    }
}
