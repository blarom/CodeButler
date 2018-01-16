package com.codebutler.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codebutler.MainActivity;
import com.codebutler.R;
import com.codebutler.data.CodeButlerDbContract.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CodeButlerDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "codebutler.db";
    public static final int DATABASE_VERSION = 34;
    private int mOriginalNumberOfColumnsInKeywordsDatabase = 6; //Don't touch these if you want to keep the user values!
    private int mNewNumberOfColumnsInKeywordsDatabase = 6; //Don't touch these if you want to keep the user values!
    private Context mContext;
    private Boolean mDatabaseAlreadyExists;

    public CodeButlerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_KEYWORDS_TABLE = "CREATE TABLE " + KeywordsDbEntry.TABLE_NAME +
                " (" +
                KeywordsDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KeywordsDbEntry.COLUMN_KEYWORD + " TEXT NOT NULL, " +
                KeywordsDbEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                KeywordsDbEntry.COLUMN_LESSONS + " TEXT NOT NULL, " +
                KeywordsDbEntry.COLUMN_RELEVANT_CODE + " TEXT NOT NULL, " +
                KeywordsDbEntry.COLUMN_SOURCE + " TEXT NOT NULL" +
                "); ";

        final String SQL_CREATE_LESSONS_TABLE = "CREATE TABLE " + LessonsDbEntry.TABLE_NAME +
                " (" +
                LessonsDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LessonsDbEntry.COLUMN_LESSON_NUMBER + " TEXT NOT NULL, " +
                LessonsDbEntry.COLUMN_LESSON_TITLE + " TEXT NOT NULL, " +
                LessonsDbEntry.COLUMN_LINK + " TEXT NOT NULL, " +
                KeywordsDbEntry.COLUMN_SOURCE + " TEXT NOT NULL" +
                "); ";

        final String SQL_CREATE_CODE_TABLE = "CREATE TABLE " + CodeReferenceDbEntry.TABLE_NAME +
                " (" +
                CodeReferenceDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CodeReferenceDbEntry.COLUMN_CODE_REFERENCE + " TEXT NOT NULL, " +
                CodeReferenceDbEntry.COLUMN_LINK + " TEXT NOT NULL, " +
                KeywordsDbEntry.COLUMN_SOURCE + " TEXT NOT NULL" +
                "); ";

        if (mDatabaseAlreadyExists == null) { mDatabaseAlreadyExists = false; }
        if (mOriginalNumberOfColumnsInKeywordsDatabase != mNewNumberOfColumnsInKeywordsDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_KEYWORDS_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_LESSONS_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_CODE_TABLE);
        }
        else {
            if (!mDatabaseAlreadyExists) {
                sqLiteDatabase.execSQL(SQL_CREATE_KEYWORDS_TABLE);
                sqLiteDatabase.execSQL(SQL_CREATE_LESSONS_TABLE);
                sqLiteDatabase.execSQL(SQL_CREATE_CODE_TABLE);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        mDatabaseAlreadyExists = false;
        String selection;
        String[] selectionArgs;
        if (mOriginalNumberOfColumnsInKeywordsDatabase != mNewNumberOfColumnsInKeywordsDatabase) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KeywordsDbEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LessonsDbEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CodeReferenceDbEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
        else {
            try {
                //Delete all values from the Udacity Mapping database rows
                selection = KeywordsDbEntry.COLUMN_SOURCE + "='" + MainActivity.KEYWORD_COURSE + "'" +
                        " OR " + KeywordsDbEntry.COLUMN_SOURCE + "='" + MainActivity.KEYWORD_FORUM + "'";
                selectionArgs = null;//{"COURSE"};
                sqLiteDatabase.delete(KeywordsDbEntry.TABLE_NAME, selection, selectionArgs);

                selection = LessonsDbEntry.COLUMN_SOURCE + "='" + MainActivity.KEYWORD_COURSE + "'" +
                        " OR " + LessonsDbEntry.COLUMN_SOURCE + "='" + MainActivity.KEYWORD_FORUM + "'";
                selectionArgs = null;//{"COURSE"};
                sqLiteDatabase.delete(LessonsDbEntry.TABLE_NAME, selection, selectionArgs);

                selection = LessonsDbEntry.COLUMN_SOURCE + "='" + MainActivity.KEYWORD_COURSE + "'" +
                        " OR " + LessonsDbEntry.COLUMN_SOURCE + "='" + MainActivity.KEYWORD_FORUM + "'";
                selectionArgs = null;//{"COURSE"};
                sqLiteDatabase.delete(CodeReferenceDbEntry.TABLE_NAME, selection, selectionArgs);

                mDatabaseAlreadyExists = true;
                onCreate(sqLiteDatabase);

            } catch (SQLException e) {
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KeywordsDbEntry.TABLE_NAME);
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LessonsDbEntry.TABLE_NAME);
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CodeReferenceDbEntry.TABLE_NAME);

                mDatabaseAlreadyExists = false;
                onCreate(sqLiteDatabase);
            }
        }
    }

    public void initializeKeywordsDatabase() {

        //Inspiration from: https://stackoverflow.com/questions/2887119/populate-android-database-from-csv-file
        BufferedReader fileReader = null;
        try {
            InputStream in = mContext.getAssets().open("UdacityMapper-Keywords.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            fileReader = new BufferedReader(inputStreamReader);

            String reader = "";
            fileReader.readLine(); //Skips the first row
            while ((reader = fileReader.readLine()) != null){
                String[] RowData = reader.split(",");
                ContentValues values = new ContentValues();
                values.put(KeywordsDbEntry.COLUMN_KEYWORD, RowData[0]);
                values.put(KeywordsDbEntry.COLUMN_TYPE, RowData[1]);
                values.put(KeywordsDbEntry.COLUMN_LESSONS, RowData[2]);
                values.put(KeywordsDbEntry.COLUMN_RELEVANT_CODE, RowData[3]);
                values.put(KeywordsDbEntry.COLUMN_SOURCE, RowData[4]);

                mContext.getContentResolver().insert(CodeButlerDbContract.KeywordsDbEntry.CONTENT_URI, values);
            }
            fileReader.close();
        } catch (Exception e) {
            System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {fileReader.close();}
            } catch (IOException e) {
                System.out.println("Error while closing fileReader !!!");
                e.printStackTrace();
            }
        }
    }
    public void initializeLessonsDatabase() {

        //Inspiration from: https://stackoverflow.com/questions/2887119/populate-android-database-from-csv-file
        BufferedReader fileReader = null;
        try {
            InputStream in = mContext.getAssets().open("UdacityMapper-Lessons.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            fileReader = new BufferedReader(inputStreamReader);

            String reader = "";
            fileReader.readLine(); //Skips the first row
            while ((reader = fileReader.readLine()) != null){
                String[] RowData = reader.split(",");
                ContentValues values = new ContentValues();
                values.put(LessonsDbEntry.COLUMN_LESSON_NUMBER, RowData[0]);
                values.put(LessonsDbEntry.COLUMN_LESSON_TITLE, RowData[1]);
                values.put(LessonsDbEntry.COLUMN_LINK, RowData[2]);
                values.put(LessonsDbEntry.COLUMN_SOURCE, RowData[3]);

                mContext.getContentResolver().insert(CodeButlerDbContract.LessonsDbEntry.CONTENT_URI, values);
            }
            fileReader.close();
        } catch (Exception e) {
            System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {fileReader.close();}
            } catch (IOException e) {
                System.out.println("Error while closing fileReader !!!");
                e.printStackTrace();
            }
        }
    }
    public void initializeCodeReferencesDatabase() {

        //Inspiration from: https://stackoverflow.com/questions/2887119/populate-android-database-from-csv-file
        BufferedReader fileReader = null;
        try {
            InputStream in = mContext.getAssets().open("UdacityMapper-CodeReferences.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            fileReader = new BufferedReader(inputStreamReader);

            String reader = "";
            fileReader.readLine(); //Skips the first row
            while ((reader = fileReader.readLine()) != null){
                String[] RowData = reader.split(",");
                ContentValues values = new ContentValues();
                values.put(CodeReferenceDbEntry.COLUMN_CODE_REFERENCE, RowData[0]);
                values.put(CodeReferenceDbEntry.COLUMN_LINK, RowData[1]);
                values.put(CodeReferenceDbEntry.COLUMN_SOURCE, RowData[2]);

                mContext.getContentResolver().insert(CodeButlerDbContract.CodeReferenceDbEntry.CONTENT_URI, values);
            }
            fileReader.close();
        } catch (Exception e) {
            System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {fileReader.close();}
            } catch (IOException e) {
                System.out.println("Error while closing fileReader !!!");
                e.printStackTrace();
            }
        }
    }

    // CRUD (Create, Read, Update, Delete) Operations
    public void addKeyword(Keyword keyword) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KeywordsDbEntry.COLUMN_KEYWORD, keyword.getKeyword());
        values.put(KeywordsDbEntry.COLUMN_TYPE, keyword.getCodeLanguage());
        values.put(KeywordsDbEntry.COLUMN_LESSONS, keyword.getLessons());
        values.put(KeywordsDbEntry.COLUMN_RELEVANT_CODE, keyword.getRelevantCode());

        // Inserting Row
        db.insert(KeywordsDbEntry.TABLE_NAME, null, values);
        db.close();
    }
    public void addLesson(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LessonsDbEntry.COLUMN_LESSON_NUMBER, lesson.getLessonNumber());
        values.put(LessonsDbEntry.COLUMN_LESSON_TITLE, lesson.getLessonTitle());
        values.put(LessonsDbEntry.COLUMN_LINK, lesson.getLink());

        // Inserting Row
        db.insert(LessonsDbEntry.TABLE_NAME, null, values);
        db.close();
    }
    public void addCodeReference(CodeReference codeReference) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CodeReferenceDbEntry.COLUMN_CODE_REFERENCE, codeReference.getCodeReference());
        values.put(CodeReferenceDbEntry.COLUMN_LINK, codeReference.getLink());

        // Inserting Row
        db.insert(CodeReferenceDbEntry.TABLE_NAME, null, values);
        db.close();
    }

    public Cursor getAllKeywordEntries() {
        SQLiteDatabase db = this.getWritableDatabase();

        String tableName = KeywordsDbEntry.TABLE_NAME;
        String orderBy = KeywordsDbEntry.COLUMN_KEYWORD;

        return db.query(tableName, null, null, null, null, null, orderBy);
    }
    public Cursor getAllLessonEntries() {
        SQLiteDatabase db = this.getWritableDatabase();

        String tableName = LessonsDbEntry.TABLE_NAME;
        String orderBy = LessonsDbEntry.COLUMN_LESSON_NUMBER;

        return db.query(tableName, null, null, null, null, null, orderBy);
    }
    public Cursor getAllCodeReferenceEntries() {
        SQLiteDatabase db = this.getWritableDatabase();

        String tableName = CodeReferenceDbEntry.TABLE_NAME;
        String orderBy = CodeReferenceDbEntry.COLUMN_CODE_REFERENCE;

        return db.query(tableName, null, null, null, null, null, orderBy);
    }

    public boolean removeKeywordEntry(long id, SQLiteDatabase sqLiteDatabase) {
        String whereClause = KeywordsDbEntry._ID + "=" + id;
        boolean successfullyRemoved = sqLiteDatabase.delete(CodeButlerDbContract.KeywordsDbEntry.TABLE_NAME, whereClause, null) > 0;
        return successfullyRemoved;
    }
    public boolean removeLessonEntry(long id, SQLiteDatabase sqLiteDatabase) {
        String whereClause = LessonsDbEntry._ID + "=" + id;
        boolean successfullyRemoved = sqLiteDatabase.delete(CodeButlerDbContract.KeywordsDbEntry.TABLE_NAME, whereClause, null) > 0;
        return successfullyRemoved;
    }
    public boolean removeCodeReferenceEntry(long id, SQLiteDatabase sqLiteDatabase) {
        String whereClause = CodeReferenceDbEntry._ID + "=" + id;
        boolean successfullyRemoved = sqLiteDatabase.delete(CodeButlerDbContract.KeywordsDbEntry.TABLE_NAME, whereClause, null) > 0;
        return successfullyRemoved;
    }
}
