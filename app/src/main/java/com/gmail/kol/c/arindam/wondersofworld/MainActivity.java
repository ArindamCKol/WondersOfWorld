package com.gmail.kol.c.arindam.wondersofworld;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static final String STATE_POSITION = "current_position";
    static final String STATE_HINT = "current_hint";
    static final String STATE_SCORE = "current_score";
    static final String STATE_OPTION = "selected_radio_button";
    static final String STATE_ALPHA_ROW1 = "current_alpha_value_array1";
    static final String STATE_ALPHA_ROW2 = "current_alpha_value_array2";
    static final String STATE_ALPHA_ROW3 = "current_alpha_value_array3";

    private int position = 0; //hold current position of wonder arraylist
    private int hintNumber = -1; // hold current position of hint array of Wonder class
    private int score = 0; // score for quiz
    private int indexOfSelectedOption = -1;
    static final float BLURRED = 0.95f; // alpha value for blurred view widget
    static final float TRANSPARENT = 0f; // alpha value for transparent view widget
    // array to store current alpha value of 3X3 view widget
    private float[][] viewTable = {{BLURRED,BLURRED,BLURRED},{BLURRED,BLURRED,BLURRED},{BLURRED,BLURRED,BLURRED}};
    private RadioGroup optionRadioGroup;
    private ImageView mImageView;
    private TextView hintTextView;
    private TextView scoreTextView;
    private LinearLayout rootLayout;
    private Button hintButton;
    private Button submitButton;
    private ArrayList <Wonder> wonders = new ArrayList<Wonder>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWondersData();

        rootLayout = findViewById(R.id.root_layout);
        optionRadioGroup = findViewById(R.id.options_radio_group);
        hintTextView = findViewById(R.id.hint_textview);
        scoreTextView = findViewById(R.id.score_text);
        hintButton = findViewById(R.id.show_hint);
        submitButton = findViewById(R.id.submit_button);
        mImageView = findViewById(R.id.image);

        submitButton.setClickable(false);
        submitButton.setAlpha(0.5f);

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(STATE_POSITION);
            hintNumber = savedInstanceState.getInt(STATE_HINT);
            score = savedInstanceState.getInt(STATE_SCORE);
            indexOfSelectedOption = savedInstanceState.getInt(STATE_OPTION);
            viewTable[0] = savedInstanceState.getFloatArray(STATE_ALPHA_ROW1);
            viewTable[1] = savedInstanceState.getFloatArray(STATE_ALPHA_ROW2);
            viewTable[2] = savedInstanceState.getFloatArray(STATE_ALPHA_ROW3);

            if (hintNumber>=8) {
                hintButton.setClickable(false);
                hintButton.setAlpha(.5f);
            }

            if (hintNumber>=0) {
                hintTextView.setText(wonders.get(position).getHint(hintNumber));
            }

            if (indexOfSelectedOption>=0) {
                RadioButton tempRadioButton = (RadioButton) optionRadioGroup.getChildAt(indexOfSelectedOption);
                tempRadioButton.setChecked(true);
                submitButton.setClickable(true);
                submitButton.setAlpha(1f);
            }

            for (int i=0; i<3; i++) {
                for (int j=0; j<3; j++) {
                    setChildViewVisibility(i,j,viewTable[i][j]);
                }
            }
        }

        setOption();

        mImageView.setImageResource(wonders.get(position).getImageId());
        scoreTextView.setText(getString(R.string.score_text,score));

        optionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                indexOfSelectedOption = optionRadioGroup.indexOfChild(findViewById(i));
                submitButton.setClickable(true);
                submitButton.setAlpha(1f);
            }
        });
        hintButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_button :
                checkResult();
                break;

            case R.id.show_hint :
                showHint();
                break;

            default: break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, position);
        outState.putInt(STATE_HINT,hintNumber);
        outState.putInt(STATE_SCORE,score);
        outState.putInt(STATE_OPTION,indexOfSelectedOption);
        outState.putFloatArray(STATE_ALPHA_ROW1,viewTable[0]);
        outState.putFloatArray(STATE_ALPHA_ROW2,viewTable[1]);
        outState.putFloatArray(STATE_ALPHA_ROW3,viewTable[2]);

        super.onSaveInstanceState(outState);
    }

    public void nextImage () {
        position++;
        if(position>9) {position=0;}
        mImageView.setImageResource(wonders.get(position).getImageId());
        setOption();

        hintButton.setClickable(true);
        hintButton.setAlpha(1f);
        hintNumber=-1;

        hintTextView.setText(R.string.hintText);
        optionRadioGroup.clearCheck();

        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                viewTable[i][j] = BLURRED;
                setChildViewVisibility(i,j,viewTable[i][j]);
            }
        }
    }

    //show hints and randomly choose view to set transparent
    public void showHint ( ) {
        hintNumber++;

        int viewRow = (int) (Math.random()*3);
        int viewCol = (int) (Math.random()*3);
        while(viewTable[viewRow][viewCol]==0f) {
            viewRow = (int) (Math.random()*3);
            viewCol = (int) (Math.random()*3);
        }
        viewTable[viewRow][viewCol]=TRANSPARENT;
        setChildViewVisibility(viewRow, viewCol,viewTable[viewRow][viewCol]);
        hintTextView.setText(wonders.get(position).getHint(hintNumber));


        // disable showhint button if all hint are shown
        if (hintNumber>=8) {
            hintButton.setClickable(false);
            hintButton.setAlpha(.5f);
        }
    }

    //set visibility of view widget
    public void setChildViewVisibility(int row, int col, float alphaValue) {
        LinearLayout linearLayout = (LinearLayout) rootLayout.getChildAt(row);
        View view = (View) linearLayout.getChildAt(col);
        view.setAlpha(alphaValue);
    }

    // load wonder array list by collecting information from xml files
    public void setWondersData () {
        Resources res = getResources();
        String [] wonder_name = res.getStringArray(R.array.wonder_names);
        TypedArray imageList = res.obtainTypedArray(R.array.image_list); // load image ID
        TypedArray hintList = res.obtainTypedArray(R.array.hints);
        TypedArray optionList = res.obtainTypedArray(R.array.radiogroup_options);

        for (int i=0; i<10; i++) {
            String [] tempHintArray = res.getStringArray(hintList.getResourceId(i,0)); // load hint array
            String [] tempOptionArray = res.getStringArray(optionList.getResourceId(i,0)); // load option array
            Wonder temp = new Wonder(imageList.getResourceId(i,R.drawable.ic_launcher_foreground),wonder_name[i],tempHintArray,tempOptionArray);
            wonders.add(temp);
        }
        imageList.recycle();
        hintList.recycle();
        optionList.recycle();
    }

    // set radio button text
    public void setOption () {
        for (int i=0; i<4; i++) {
            RadioButton tempRadioButton = (RadioButton) optionRadioGroup.getChildAt(i);
            tempRadioButton.setText(wonders.get(position).getOption(i));

        }
    }

    public void checkResult () {
        boolean isCorrect = (wonders.get(position).getOption(indexOfSelectedOption)).equals(wonders.get(position).getName());
        if (isCorrect) {
            score = score + (10-hintNumber-1);
            scoreTextView.setText(getString(R.string.score_text,score));
            nextImage();
            indexOfSelectedOption=-1;
            submitButton.setClickable(false);
            submitButton.setAlpha(0.5f);
       }
    }
}
