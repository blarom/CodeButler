package com.codebutler.data;

import android.provider.BaseColumns;

public class CodeDbContract {

    public static final class CodeReferenceDbEntry implements BaseColumns {

        public static final String TABLE_NAME = "codeTable";
        public static final String COLUMN_CODE_REFERENCE = "codeReference";
        public static final String COLUMN_LINK = "codeLink";

    }
}
