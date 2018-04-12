package com.gmail.kol.c.arindam.wondersofworld;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int position = 0;
    private int hintNumber = 0;
    private float[][] viewTable = {{0.95f,0.95f,0.95f},{0.95f,0.95f,0.95f},{0.95f,0.95f,0.95f}};
    private ImageView mImageView;
    private TextView hintTextView;
    private LinearLayout rootLayout;
    private Button hintButton;
    private ArrayList <Wonder> wonders = new ArrayList<Wonder>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWondersData();

        rootLayout = findViewById(R.id.root_layout);
        mImageView = findViewById(R.id.image);
        hintTextView = findViewById(R.id.hint_textview);
        hintButton = findViewById(R.id.show_hint);
        mImageView.setImageResource(wonders.get(position).getImageId());
        position++;
    }

    public void nextImage (View view) {
        mImageView.setImageResource(wonders.get(position).getImageId());
        position++;
        hintButton.setClickable(true);
        hintButton.setAlpha(1f);
        hintNumber=0;
        if(position>9) {position=0;}
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                viewTable[i][j] = 0.95f;
                setChildViewVisibility(i,j,viewTable[i][j]);
            }
        }
    }

    public void showHint (View view) {
        int viewRow = (int) (Math.random()*3);
        int viewCol = (int) (Math.random()*3);
        while(viewTable[viewRow][viewCol]==0f) {
            viewRow = (int) (Math.random()*3);
            viewCol = (int) (Math.random()*3);
        }
        viewTable[viewRow][viewCol]=0f;
        setChildViewVisibility(viewRow, viewCol,viewTable[viewRow][viewCol]);
        hintTextView.setText(wonders.get(position).getHint(hintNumber));
        hintNumber++;
        if (hintNumber>8) {
            hintButton.setClickable(false);
            hintButton.setAlpha(.5f);
        }
    }

    public void setChildViewVisibility(int row, int col, float alphaValue) {
        LinearLayout linearLayout = (LinearLayout) rootLayout.getChildAt(row);
        View view = (View) linearLayout.getChildAt(col);
        view.setAlpha(alphaValue);
    }

    public void setWondersData () {

        Resources res = getResources();
        String [] wonder_name = res.getStringArray(R.array.wonder_names);
        TypedArray imageList = res.obtainTypedArray(R.array.image_list);
        TypedArray hintList = res.obtainTypedArray(R.array.hints);

        for (int i=0; i<imageList.length(); i++) {
            String [] tempHintArray = res.getStringArray(hintList.getResourceId(i,0));
            Wonder temp = new Wonder(imageList.getResourceId(i,R.drawable.ic_launcher_foreground),wonder_name[i],tempHintArray);
            wonders.add(temp);
        }
//        wonders.add(new Wonder(R.drawable.angkorwat));
//        wonders.add(new Wonder(R.drawable.colosseum));
//        wonders.add(new Wonder(R.drawable.greatwall));
//        wonders.add(new Wonder(R.drawable.leaningtower));
//        wonders.add(new Wonder(R.drawable.machupicchu));
//        wonders.add(new Wonder(R.drawable.petra));
//        wonders.add(new Wonder(R.drawable.pyramidsofgiza));
//        wonders.add(new Wonder(R.drawable.tajmahal));
//        wonders.add(new Wonder(R.drawable.teotihuacan));
//        wonders.add(new Wonder(R.drawable.tigersnest));
    }
}
