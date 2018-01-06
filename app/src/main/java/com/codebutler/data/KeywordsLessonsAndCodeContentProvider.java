package com.codebutler.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class KeywordsLessonsAndCodeContentProvider extends ContentProvider {

    private KeywordsLessonsAndCodeDbHelper mDbHelper;

    public static final int KEYWORDS = 100;
    public static final int KEYWORD_WITH_ID = 101;

    public static final int LESSONS = 200;
    public static final int LESSON_WITH_ID = 201;

    public static final int CODE_REFERENCES = 300;
    public static final int CODE_REFERENCE_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CodeButlerDbContract.AUTHORITY, CodeButlerDbContract.PATH_KEYWORDS, KEYWORDS);
        uriMatcher.addURI(CodeButlerDbContract.AUTHORITY, CodeButlerDbContract.PATH_KEYWORDS + "/#", KEYWORD_WITH_ID);

        uriMatcher.addURI(CodeButlerDbContract.AUTHORITY, CodeButlerDbContract.PATH_LESSONS, LESSONS);
        uriMatcher.addURI(CodeButlerDbContract.AUTHORITY, CodeButlerDbContract.PATH_LESSONS + "/#", LESSON_WITH_ID);

        uriMatcher.addURI(CodeButlerDbContract.AUTHORITY, CodeButlerDbContract.PATH_CODE, CODE_REFERENCES);
        uriMatcher.addURI(CodeButlerDbContract.AUTHORITY, CodeButlerDbContract.PATH_CODE + "/#", CODE_REFERENCE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        mDbHelper = new KeywordsLessonsAndCodeDbHelper(getContext());

        return false;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        //Get the database
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Get the matching Uri
        int match = sUriMatcher.match(uri);

        //Initialize the Uri that will be returned when the element is inserted into the database
        Uri returnUri;

        //Insert the element into the database if the Uri matches the relevant table
        switch(match) {
            case KEYWORDS:
                long id = db.insert(CodeButlerDbContract.KeywordsDbEntry.TABLE_NAME, null, contentValues);
                if (id > 0) returnUri = ContentUris.withAppendedId(CodeButlerDbContract.KeywordsDbEntry.CONTENT_URI, id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case KEYWORDS:
                return insertValuesIntoTable(uri, db, values, CodeButlerDbContract.KeywordsDbEntry.TABLE_NAME);
            case LESSONS:
                return insertValuesIntoTable(uri, db, values, CodeButlerDbContract.LessonsDbEntry.TABLE_NAME);
            case CODE_REFERENCES:
                return insertValuesIntoTable(uri, db, values, CodeButlerDbContract.CodeReferenceDbEntry.TABLE_NAME);
            default:
                return super.bulkInsert(uri, values);
        }
    }
    private int insertValuesIntoTable(Uri uri, SQLiteDatabase db, ContentValues[] values, String table) {
        db.beginTransaction();
        int rowsInserted = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(table, null, value);
                if (_id != -1)  rowsInserted++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsInserted;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //Get the database
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Get the matching Uri
        int match = sUriMatcher.match(uri);

        //Initialize the cursor that will be returned when the query is completed
        Cursor retCursor;

        //Query the database if the Uri matches the relevant table
        switch (match) {
            case KEYWORDS:
                retCursor =  db.query(CodeButlerDbContract.KeywordsDbEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //Set a notification URI on the Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        //Get the database
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Get the matching Uri
        int match = sUriMatcher.match(uri);

        //Number of deleted elements in the database
        int tasksDeleted;

        //Delete the element in the database with the relevant id if the Ur matches the relevant table
        switch (match) {
            case KEYWORD_WITH_ID:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(CodeButlerDbContract.KeywordsDbEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //Notify the resolver of a change
        if (tasksDeleted != 0)  getContext().getContentResolver().notifyChange(uri, null);

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        //Get the database
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Get the matching Uri
        int match = sUriMatcher.match(uri);

        //Initialize the Uri that will be returned when the element is inserted into the database
        int hasBeenUpdated = 0;

        //Insert the element into the database if the Uri matches the relevant table
        switch(match) {
            case KEYWORDS:
                long id = db.update(CodeButlerDbContract.KeywordsDbEntry.TABLE_NAME, contentValues, null, selectionArgs);
                //if (id > 0) hasBeenUpdated = id;
                //else throw new android.database.SQLException("Failed to update row with uri " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return hasBeenUpdated;
    }
}
