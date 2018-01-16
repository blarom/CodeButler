package com.codebutler;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codebutler.utilities.SharedMethods;

import java.util.List;

public class AboutActivity extends AppCompatActivity {

    ConstraintSet mConstraintSet;
    LinearLayout legendItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

//        List<String[]> legendItemsArray = SharedMethods.readCSVFile("UdacityMapper-Legend.csv", this,",");
//        ConstraintLayout constraintLayout = findViewById(R.id.about_contraintLayout);
//        mConstraintSet = new ConstraintSet();
//        mConstraintSet.clone(constraintLayout);
//
//        //Creating the first row for alignment
//        ViewGroup.LayoutParams tv_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        String[] legendItem = legendItemsArray.get(0);
//        TextView legendTextView = findViewById(R.id.legendTextView);
//
//
//        TextView abbreviationTitle = new TextView(this);
//        abbreviationTitle.setLayoutParams(tv_params);
//        abbreviationTitle.setText(legendItem[0]);
//        abbreviationTitle.setId(View.generateViewId());
//        constraintLayout.addView(abbreviationTitle);
//        mConstraintSet.connect(abbreviationTitle.getId(), ConstraintSet.TOP, legendTextView.getId(), ConstraintSet.BOTTOM, 0);
//        mConstraintSet.connect(abbreviationTitle.getId(), ConstraintSet.START, legendTextView.getId(), ConstraintSet.START, 0);
//
////        TextView explanationTitle = new TextView(this);
////        explanationTitle.setLayoutParams(tv_params);
////        explanationTitle.setText(legendItem[1]);
////        explanationTitle.setId(View.generateViewId());
////        constraintLayout.addView(explanationTitle);
////        mConstraintSet.connect(explanationTitle.getId(), ConstraintSet.TOP, legendTextView.getId(), ConstraintSet.BOTTOM, 0);
////        mConstraintSet.connect(explanationTitle.getId(), ConstraintSet.START, abbreviationTitle.getId(), ConstraintSet.END, 0);
//
//        //legendItems.addView(legendItemsRowTitle);
//
//        //Creating the ubsequent rows and aligning their elements to the first row
//        for (int i=1; i<legendItemsArray.size(); i++) {
//
//            legendItem = legendItemsArray.get(i);
//
//            TextView abbreviation = new TextView(this);
//            abbreviation.setLayoutParams(tv_params);
//            abbreviation.setText(legendItem[0]);
//            abbreviation.setId(View.generateViewId());
//            constraintLayout.addView(abbreviation);
//            mConstraintSet.connect(abbreviation.getId(), ConstraintSet.START, abbreviationTitle.getId(), ConstraintSet.START, 0);
//            mConstraintSet.connect(abbreviation.getId(), ConstraintSet.TOP, abbreviationTitle.getId(), ConstraintSet.BOTTOM, 0);
//            //if (i==legendItemsArray.size()-1) mConstraintSet.connect(abbreviation.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
//
////            TextView explanation = new TextView(this);
////            explanation.setLayoutParams(tv_params);
////            explanation.setText(legendItem[1]);
////            explanation.setId(View.generateViewId());
////            constraintLayout.addView(explanation);
////            mConstraintSet.connect(explanation.getId(), ConstraintSet.START, explanationTitle.getId(), ConstraintSet.START, 0);
////            mConstraintSet.connect(explanation.getId(), ConstraintSet.TOP, explanationTitle.getId(), ConstraintSet.BOTTOM, 0);
////            if (i==legendItemsArray.size()-1) mConstraintSet.connect(explanation.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
//        }
//        mConstraintSet.applyTo(constraintLayout);
//        setContentView(R.layout.activity_about);
    }
}
