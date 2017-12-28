package com.codebutler.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.codebutler.data.KeywordsDbContract.*;
import com.codebutler.data.LessonsDbContract.*;
import com.codebutler.data.CodeDbContract.*;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "codebutler.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_KEYWORDS_TABLE = "CREATE TABLE " + KeywordsDbEntry.TABLE_NAME +
                " (" +
                KeywordsDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KeywordsDbEntry.COLUMN_KEYWORD + " TEXT NOT NULL, " +
                KeywordsDbEntry.COLUMN_LANGUAGE + " TEXT NOT NULL, " +
                KeywordsDbEntry.COLUMN_LESSONS + " TEXT NOT NULL, " +
                KeywordsDbEntry.COLUMN_RELEVANT_CODE + " TEXT NOT NULL" +
                "); ";

        final String SQL_CREATE_LESSONS_TABLE = "CREATE TABLE " + LessonsDbEntry.TABLE_NAME +
                " (" +
                LessonsDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LessonsDbEntry.COLUMN_LESSON_NUMBER + " TEXT NOT NULL, " +
                LessonsDbEntry.COLUMN_LESSON_TITLE + " TEXT NOT NULL, " +
                LessonsDbEntry.COLUMN_LINK + " TEXT NOT NULL" +
                "); ";

        final String SQL_CREATE_CODE_TABLE = "CREATE TABLE " + CodeReferenceDbEntry.TABLE_NAME +
                " (" +
                CodeReferenceDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CodeReferenceDbEntry.COLUMN_CODE_REFERENCE + " TEXT NOT NULL, " +
                CodeReferenceDbEntry.COLUMN_LINK + " TEXT NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_KEYWORDS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LESSONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CODE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KeywordsDbEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LessonsDbEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CodeReferenceDbEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    // CRUD (Create, Read, Update, Delete) Operations
    public void addKeyword(Keyword keyword) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KeywordsDbEntry.COLUMN_KEYWORD, keyword.getKeyword());
        values.put(KeywordsDbEntry.COLUMN_LANGUAGE, keyword.getCodeLanguage());
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
        boolean successfullyRemoved = sqLiteDatabase.delete(KeywordsDbContract.KeywordsDbEntry.TABLE_NAME, whereClause, null) > 0;
        return successfullyRemoved;
    }
    public boolean removeLessonEntry(long id, SQLiteDatabase sqLiteDatabase) {
        String whereClause = LessonsDbEntry._ID + "=" + id;
        boolean successfullyRemoved = sqLiteDatabase.delete(KeywordsDbContract.KeywordsDbEntry.TABLE_NAME, whereClause, null) > 0;
        return successfullyRemoved;
    }
    public boolean removeCodeReferenceEntry(long id, SQLiteDatabase sqLiteDatabase) {
        String whereClause = CodeReferenceDbEntry._ID + "=" + id;
        boolean successfullyRemoved = sqLiteDatabase.delete(KeywordsDbContract.KeywordsDbEntry.TABLE_NAME, whereClause, null) > 0;
        return successfullyRemoved;
    }
}
