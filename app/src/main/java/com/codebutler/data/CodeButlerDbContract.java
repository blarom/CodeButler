package com.codebutler.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.codebutler.MainActivity;

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
        public static final String COLUMN_SOURCE = "databaseSource";

        public static String getSelectionForGivenKeywordsAndOperator(String keyword_list, String operator, Boolean[] showSources) {

            //Escaping special characters so that special keywords won't crash the SQLite search
            keyword_list = keyword_list.replaceAll("'","\'");

            //Making sure the user selected something to display
            String returnSQLCommand = null;
            Boolean atLeastOneSourceIsShown = false;
            for (Boolean showCurrentSource : showSources) { if (showCurrentSource) atLeastOneSourceIsShown = true; }
            if (!atLeastOneSourceIsShown || keyword_list.length()==0) return returnSQLCommand;

            returnSQLCommand = "";

            //Returning a valid filtering SQL command for the Source
            if (MainActivity.atLeastOneCourseIsShown) {
                returnSQLCommand = "("+ COLUMN_SOURCE + "='" + MainActivity.KEYWORD_COURSE + "'";
                if (showSources[MainActivity.SHOW_FORUM_THREADS_KEY]) {
                    returnSQLCommand += " OR " + COLUMN_SOURCE + "='" + MainActivity.KEYWORD_FORUM + "'";

                    if (showSources[MainActivity.SHOW_USER_VALUES_KEY]) {
                        returnSQLCommand += " OR " + COLUMN_SOURCE + "='" + MainActivity.KEYWORD_USER + "'";
                    }
                }
            }
            else {
                if (showSources[MainActivity.SHOW_FORUM_THREADS_KEY]) {
                    returnSQLCommand += "(" + COLUMN_SOURCE + "='" + MainActivity.KEYWORD_FORUM + "'";

                    if (showSources[MainActivity.SHOW_USER_VALUES_KEY]) {
                        returnSQLCommand += " OR " + COLUMN_SOURCE + "='" + MainActivity.KEYWORD_USER + "'";
                    }
                }
                else {
                    if (showSources[MainActivity.SHOW_USER_VALUES_KEY]) {
                        returnSQLCommand += "(" + COLUMN_SOURCE + "='" + MainActivity.KEYWORD_USER + "'";
                    }
                }
            }

            returnSQLCommand += ") AND (";

            //Returning a valid filtering SQL command for the keywords
            switch (operator) {
                case "EXACT":
                    //String returnSQLCommand = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_KEYWORD + "= '" + keyword + "'";
                    returnSQLCommand += COLUMN_KEYWORD + "='" + keyword_list + "'";
                    break;
                default: //operator is "AND" or "OR"
                    List<String> keywords = new ArrayList<>();
                    if (keyword_list.contains(" ")) keywords = Arrays.asList(keyword_list.split(" "));
                    else if (keyword_list.contains(",")) keywords = Arrays.asList(keyword_list.split(","));
                    else if (keyword_list.contains(";")) keywords = Arrays.asList(keyword_list.split(";"));
                    else if (keyword_list.contains(".")) keywords = Arrays.asList(keyword_list.split("."));
                    else keywords.add(keyword_list);

                    //returnSQLCommand += "'%" + keywords.get(0) + "%'";
                    if (keywords.size()>=1) {
                        returnSQLCommand += COLUMN_KEYWORD + " LIKE '%" + keywords.get(0) + "%'";
                        for (int i = 1; i < keywords.size(); i++) {
                            returnSQLCommand += " " + operator + " " + COLUMN_KEYWORD + " LIKE '%" + keywords.get(i) + "%'";
                        }
                    }
                    break;
            }

            returnSQLCommand += ")";

            //Returning a valid filtering SQL command for the course type
            Boolean alreadyHasAtLeastOneCourse = false;
            if (showSources[MainActivity.SHOW_USER_VALUES_KEY] && !(MainActivity.atLeastOneCourseIsShown || showSources[MainActivity.SHOW_FORUM_THREADS_KEY])) {
                //If the user values are shown but all other values are hidden, don't add an extra clause to the SQL query
                return returnSQLCommand;
            }
            else {
                returnSQLCommand += " AND (";
                if (showSources[MainActivity.SHOW_GDC_AD_COURSE_KEY]) {
                    returnSQLCommand += COLUMN_LESSONS + " LIKE '%" + MainActivity.KEYWORD_GDC_AD + "%'";
                    alreadyHasAtLeastOneCourse = true;
                }
                if (showSources[MainActivity.SHOW_ND_AD_COURSE_KEY]) {
                    if (alreadyHasAtLeastOneCourse) returnSQLCommand += " OR ";
                    returnSQLCommand += COLUMN_LESSONS + " LIKE '%" + MainActivity.KEYWORD_ND_AD + "%'";
                    alreadyHasAtLeastOneCourse = true;
                }
                if (showSources[MainActivity.SHOW_FIW_COURSE_KEY]) {
                    if (alreadyHasAtLeastOneCourse) returnSQLCommand += " OR ";
                    returnSQLCommand += COLUMN_LESSONS + " LIKE '%" + MainActivity.KEYWORD_FIW + "%'";
                    alreadyHasAtLeastOneCourse = true;
                }
                if (showSources[MainActivity.SHOW_FORUM_THREADS_KEY]) {
                    if (alreadyHasAtLeastOneCourse) returnSQLCommand += " OR ";
                    returnSQLCommand += COLUMN_LESSONS + " LIKE '%" + MainActivity.KEYWORD_UFT + "%' OR " + COLUMN_LESSONS + " LIKE '%" + MainActivity.KEYWORD_SO + "%'";
                    alreadyHasAtLeastOneCourse = true;
                }
                returnSQLCommand += ")";
            }


            return returnSQLCommand;
        }

        public static String getSelectionForGivenSource(String source) {

            String returnSQLCommand = null;
            if(source.length()==0) return returnSQLCommand;
            else {
                returnSQLCommand = COLUMN_SOURCE + "='" + source + "'";
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
        public static final String COLUMN_SOURCE = "databaseSource";

    }

    public static final class CodeReferenceDbEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CODE).build();

        public static final String TABLE_NAME = "codeTable";
        public static final String COLUMN_CODE_REFERENCE = "codeReference";
        public static final String COLUMN_LINK = "codeLink";
        public static final String COLUMN_SOURCE = "databaseSource";

    }
}
