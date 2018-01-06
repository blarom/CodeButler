package com.codebutler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class SelectedItemDetailsActivity extends AppCompatActivity {

    private TextView mKeywordTextView;
    private TextView mTypeTextView;
    private String mRelevantLessons;
    private String mRelevantCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_item_details);

        mKeywordTextView = findViewById(R.id.keywordTextView);
        mRelevantLessons = "N/A";
        mRelevantCode = "N/A";

        Intent intentThatStartedThisActivity = getIntent();
        String userKeyword;
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            userKeyword = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            //TODO modify to get bundle from MainActivity
            mKeywordTextView.setText(userKeyword);
        }

        populateLinearLayout(mRelevantLessons, "LESSON");
        populateLinearLayout(mRelevantCode, "CODE");
    }
    public void populateLinearLayout(String relevantItems, String itemType) {

        LinearLayout selectedItemViewsLinearLayout = findViewById(R.id.selectedItemViewsLinearLayout);
        String itemLink;

        if (!relevantItems.equals("N/A")) {
            List<String> parsedList = Arrays.asList(relevantItems.split(","));
            for (String item : parsedList) {
                item = item.trim();
                switch(itemType) {
                    case "LESSON": itemLink = getLessonLinkFromDatabase(item); break;
                    case "CODE": itemLink = getCodeLinkFromDatabase(item); break;
                    default: itemLink = getLessonLinkFromDatabase(item); break;
                }

                TextView currentItemTextView = new TextView(this);
                currentItemTextView.setText(item);

                selectedItemViewsLinearLayout.addView(currentItemTextView);
            }
        }
    }
    public String getLessonLinkFromDatabase(String lesson) {
        return "";
    }
    public String getCodeLinkFromDatabase(String lesson) {
        return "";
    }
}
