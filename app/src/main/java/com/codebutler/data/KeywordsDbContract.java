package com.codebutler.data;

import android.provider.BaseColumns;

public class KeywordsDbContract {

    public static final class KeywordsDbEntry implements BaseColumns {

        public static final String TABLE_NAME = "keywordsTable";
        public static final String COLUMN_KEYWORD = "keyword";
        public static final String COLUMN_LANGUAGE = "codeLanguageForKeyword";
        public static final String COLUMN_LESSONS = "lessonsForKeyword";
        public static final String COLUMN_RELEVANT_CODE = "relevantCodeForKeyword";

    }
}
