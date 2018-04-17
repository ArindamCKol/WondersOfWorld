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

public class MainActivity extends AppCompatActivity {

    private int position = 0; //hold current position of wonder arraylist
    private int hintNumber = 0; // hold current position of hint array of Wonder class
    private int score = 0; // score for quiz
    static final float BLURRED = 0.95f; // alpha value for blurred view widget
    static final float TRANSPARENT = 0f; // alpha value for transparent view widget
    // array to store current alpha value of 3X3 view widget
    private float[][] viewTable = {{BLURRED,BLURRED,BLURRED},{BLURRED,BLURRED,BLURRED},{BLURRED,BLURRED,BLURRED}};
    private RadioGroup optionRadioGroup;
    private ImageView mImageView;
    private TextView hintTextView;
    private TextView scoreText;
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
        scoreText = findViewById(R.id.score_text);
        hintButton = findViewById(R.id.show_hint);
        submitButton = findViewById(R.id.submit_button);
        mImageView = findViewById(R.id.image);

        mImageView.setImageResource(wonders.get(position).getImageId());
        scoreText.setText("Score : " + score);

        setOption();

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHint();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int optionSelected = (int) optionRadioGroup.getCheckedRadioButtonId()-1;
                boolean isCorrect = (wonders.get(position).getOption(optionSelected)).equals(wonders.get(position).getName());
                if (isCorrect) {
                    nextImage();
                }
            }
        });

    }

    public void nextImage () {
        position++;
        if(position>9) {position=0;}
        mImageView.setImageResource(wonders.get(position).getImageId());
        setOption();
        score = score + (10-hintNumber);
        scoreText.setText("Score : " + score);

        hintButton.setClickable(true);
        hintButton.setAlpha(1f);
        hintNumber=0;
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
        int viewRow = (int) (Math.random()*3);
        int viewCol = (int) (Math.random()*3);
        while(viewTable[viewRow][viewCol]==0f) {
            viewRow = (int) (Math.random()*3);
            viewCol = (int) (Math.random()*3);
        }
        viewTable[viewRow][viewCol]=TRANSPARENT;
        setChildViewVisibility(viewRow, viewCol,viewTable[viewRow][viewCol]);
        hintTextView.setText(wonders.get(position).getHint(hintNumber));
        hintNumber++;

        // disable showhint button if all hint are shown
        if (hintNumber>8) {
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
    }

    // set radio button text
    public void setOption () {
        for (int i=0; i<4; i++) {
            RadioButton tempRadioButton = (RadioButton) optionRadioGroup.getChildAt(i);
            tempRadioButton.setText(wonders.get(position).getOption(i));
        }
    }
}
