package com.codebutler.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeButlerDbContract {

    public static final String AUTHORITY = "com.codebutler";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_KEYWORDS = "keywords";
    public static final String PATH_LESSONS = "lessons";
    public static final String PATH_CODE = "code";

    public static final class KeywordsDbEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_KEYWORDS).build();

        public static final String TABLE_NAME = "keywordsTable";
        public static final String COLUMN_KEYWORD = "keyword";
        public static final String COLUMN_TYPE = "typeForKeyword";
        public static final String COLUMN_LESSONS = "lessonsForKeyword";
        public static final String COLUMN_RELEVANT_CODE = "relevantCodeForKeyword";

        public static String getSelectionForGivenKeywordsAndOperator(String keyword_list, String operator) {

            String returnSQLCommand = null;
            if(keyword_list.length()==0) return returnSQLCommand;

            switch (operator) {
                case "EXACT":
                    //String returnSQLCommand = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_KEYWORD + "= '" + keyword + "'";
                    returnSQLCommand = COLUMN_KEYWORD + "='" + keyword_list + "'";
                    break;
                default: //operator is "AND" or "OR"
                    List<String> keywords = new ArrayList<>();
                    if (keyword_list.contains(" ")) keywords = Arrays.asList(keyword_list.split(" "));
                    else if (keyword_list.contains(",")) keywords = Arrays.asList(keyword_list.split(","));
                    else if (keyword_list.contains(";")) keywords = Arrays.asList(keyword_list.split(";"));
                    else if (keyword_list.contains(".")) keywords = Arrays.asList(keyword_list.split("."));
                    else keywords.add(keyword_list);

                    returnSQLCommand = "'%" + keywords.get(0) + "%'";
                    if (keywords.size()>=1) {
                        returnSQLCommand = COLUMN_KEYWORD + " LIKE '%" + keywords.get(0) + "%'";
                        for (int i = 1; i < keywords.size(); i++) {
                            returnSQLCommand = returnSQLCommand + " " + operator + " " + COLUMN_KEYWORD + " LIKE '%" + keywords.get(i) + "%'";
                        }
                    }
                    break;
            }
            return returnSQLCommand;
        }
    }

    public static final class LessonsDbEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LESSONS).build();

        public static final String TABLE_NAME = "lessonsTable";
        public static final String COLUMN_LESSON_NUMBER = "lessonNumber";
        public static final String COLUMN_LESSON_TITLE = "lessonTitle";
        public static final String COLUMN_LINK = "lessonsLink";

    }

    public static final class CodeReferenceDbEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CODE).build();

        public static final String TABLE_NAME = "codeTable";
        public static final String COLUMN_CODE_REFERENCE = "codeReference";
        public static final String COLUMN_LINK = "codeLink";

    }
}
