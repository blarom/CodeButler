package com.codebutler.data;

import android.provider.BaseColumns;

public class LessonsDbContract {

    public static final class LessonsDbEntry implements BaseColumns {

        public static final String TABLE_NAME = "lessonsTable";
        public static final String COLUMN_LESSON_NUMBER = "lessonNumber";
        public static final String COLUMN_LESSON_TITLE = "lessonTitle";
        public static final String COLUMN_LINK = "lessonsLink";

    }
}
