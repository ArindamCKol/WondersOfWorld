package com.gmail.kol.c.arindam.wondersofworld;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // identifier for values to save for instance change
    static final String STATE_POSITION = "current_position";
    static final String STATE_HINT = "current_hint";
    static final String STATE_SCORE = "current_score";
    static final String STATE_OPTION = "selected_radio_button";
    static final String STATE_LOGIN = "login_dialog_shown";
    static final String STATE_GAMEOVER = "gameover_dialog_shown";
    static final String STATE_GAMEOVER_MESSAGE = "gameover_message";
    static final String STATE_PLAYERNAME = "playername";
    static final String STATE_ALPHA_ROW1 = "current_alpha_value_array1";
    static final String STATE_ALPHA_ROW2 = "current_alpha_value_array2";
    static final String STATE_ALPHA_ROW3 = "current_alpha_value_array3";

    private boolean isLoginShown = false;
    private boolean isGameOver = false;
    private int position = 0; //hold current position of wonder arraylist
    private int hintNumber = -1; // hold current position of hint array of Wonder class
    private int score = 0; // score for quiz
    private int indexOfSelectedOption = -1; // radio button index
    static final float BLURRED = 0.92f; // alpha value for blurred view widget
    static final float TRANSPARENT = 0f; // alpha value for transparent view widget
    // array to store current alpha value of 3X3 view widget
    private float[][] viewTable = {{BLURRED,BLURRED,BLURRED},{BLURRED,BLURRED,BLURRED},{BLURRED,BLURRED,BLURRED}};
    private String playerName = ""; // player name
    private String gameoverMessage = "";
    //variables for views
    private RadioGroup optionRadioGroup;
    private ImageView mImageView;
    private TextView hintTextView;
    private TextView scoreTextView;
    private TextView instructionTextView;
    private TextView checkBoxAnswerHint;
    private LinearLayout rootLayout;
    private LinearLayout checkboxGroup;
    private LinearLayout textviewGroup;
    private EditText textAnswer;
    private Button hintButton;
    private Button submitButton;

    private SharedPreferences mSharedPreferences; // to save player name in app
    //login dialog box views
    public EditText nameText;
    public CheckBox saveUserCheck;

    private ArrayList <Wonder> wonders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //put data from string arrays to wonder array list
        setWondersData();
        // initialise views
        rootLayout = findViewById(R.id.root_layout);
        checkboxGroup = findViewById(R.id.checkbox_group);
        textviewGroup = findViewById(R.id.text_answer_group);
        optionRadioGroup = findViewById(R.id.options_radio_group);
        hintTextView = findViewById(R.id.hint_textview);
        scoreTextView = findViewById(R.id.score_text);
        instructionTextView = findViewById(R.id.instruction);
        checkBoxAnswerHint = findViewById(R.id.checkbox_answer_hint);
        hintButton = findViewById(R.id.show_hint);
        submitButton = findViewById(R.id.submit_button);
        mImageView = findViewById(R.id.image);
        textAnswer = findViewById(R.id.text_answer);

        //if instance changed load data
        if (savedInstanceState != null) {
            isLoginShown = savedInstanceState.getBoolean(STATE_LOGIN);
            isGameOver = savedInstanceState.getBoolean(STATE_GAMEOVER);
            gameoverMessage = savedInstanceState.getString(STATE_GAMEOVER_MESSAGE);
            playerName = savedInstanceState.getString(STATE_PLAYERNAME);
            position = savedInstanceState.getInt(STATE_POSITION);
            hintNumber = savedInstanceState.getInt(STATE_HINT);
            score = savedInstanceState.getInt(STATE_SCORE);
            indexOfSelectedOption = savedInstanceState.getInt(STATE_OPTION);
            viewTable[0] = savedInstanceState.getFloatArray(STATE_ALPHA_ROW1);
            viewTable[1] = savedInstanceState.getFloatArray(STATE_ALPHA_ROW2);
            viewTable[2] = savedInstanceState.getFloatArray(STATE_ALPHA_ROW3);

            if (isGameOver) {
                gameOverAlert(gameoverMessage);
            }

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

        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);
        boolean isLoginSaved = mSharedPreferences.getBoolean("isLoginSaved", false);

        if (isLoginSaved) {
            playerName = mSharedPreferences.getString("player_name", "No Name");
        } else if (isLoginShown == false) {
            loginDialog();
        }

        //load options in radio group
        setOption();

        mImageView.setImageResource(wonders.get(position).getImageId());
        scoreTextView.setText(getString(R.string.score_text,score));
        hintTextView.setText(R.string.hintText);

        // handle radio group selection
        optionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                indexOfSelectedOption = optionRadioGroup.indexOfChild(findViewById(i));
                submitButton.setClickable(true);
                submitButton.setAlpha(1f);
            }
        });

        textAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

            @Override
            public void onTextChanged(CharSequence answer, int i, int i1, int i2) {
                if(answer.toString().trim().length()>0) {
                    submitButton.setClickable(true);
                    submitButton.setAlpha(1f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //attach click listener to views
        instructionTextView.setOnClickListener(this);
        hintButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        //disable submit button
        submitButton.setClickable(false);
        submitButton.setAlpha(0.5f);
    }

    public void checkBoxClicked (View view) {
        submitButton.setClickable(true);
        submitButton.setAlpha(1f);
    }

    //handle button click
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_button :
                checkResult();
                break;

            case R.id.show_hint :
                showHint();
                break;

            case R.id.instruction :
                showInstruction();
                break;

            default: break;
        }
    }

    //save data during instance change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_LOGIN,isLoginShown);
        outState.putBoolean(STATE_GAMEOVER,isGameOver);
        outState.putString(STATE_GAMEOVER_MESSAGE,gameoverMessage);
        outState.putString(STATE_PLAYERNAME,playerName);
        outState.putInt(STATE_POSITION, position);
        outState.putInt(STATE_HINT,hintNumber);
        outState.putInt(STATE_SCORE,score);
        outState.putInt(STATE_OPTION,indexOfSelectedOption);
        outState.putFloatArray(STATE_ALPHA_ROW1,viewTable[0]);
        outState.putFloatArray(STATE_ALPHA_ROW2,viewTable[1]);
        outState.putFloatArray(STATE_ALPHA_ROW3,viewTable[2]);

        super.onSaveInstanceState(outState);
    }

    //show next image & change all the required variables
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
        String [] answer_type = res.getStringArray(R.array.answer_type);
        TypedArray imageList = res.obtainTypedArray(R.array.image_list); // load image ID
        TypedArray hintList = res.obtainTypedArray(R.array.hints);
        TypedArray optionList = res.obtainTypedArray(R.array.radiogroup_options);

        for (int i=0; i<10; i++) {
            String [] tempHintArray = res.getStringArray(hintList.getResourceId(i,0)); // load hint array
            String [] tempOptionArray = res.getStringArray(optionList.getResourceId(i,0)); // load option array
            Wonder temp = new Wonder(imageList.getResourceId(i,R.drawable.ic_launcher_foreground),wonder_name[i],answer_type[i],tempHintArray,tempOptionArray);
            wonders.add(temp);
        }
        imageList.recycle();
        hintList.recycle();
        optionList.recycle();
    }

    // set radio button text
    public void setOption () {
        switch (wonders.get(position).getAnswerType()) {
            case "radio_button" :
                for (int i=0; i<4; i++) {
                    RadioButton tempRadioButton = (RadioButton) optionRadioGroup.getChildAt(i);
                    tempRadioButton.setText(wonders.get(position).getOption(i));
                }
                optionRadioGroup.setVisibility(View.VISIBLE);
                textviewGroup.setVisibility(View.GONE);
                checkboxGroup.setVisibility(View.GONE);
                checkBoxAnswerHint.setVisibility(View.GONE);
                break;
            case "text_view" :
                for (int i=0; i<4; i++) {
                    TextView tempTextView = (TextView) textviewGroup.getChildAt(i);
                    tempTextView.setText(wonders.get(position).getOption(i));
                }
                optionRadioGroup.setVisibility(View.GONE);
                textviewGroup.setVisibility(View.VISIBLE);
                checkboxGroup.setVisibility(View.GONE);
                checkBoxAnswerHint.setVisibility(View.GONE);
                break;
            case "check_box" :
                for (int i=0; i<4; i++) {
                    CheckBox tempCheckBox = (CheckBox) checkboxGroup.getChildAt(i);
                    tempCheckBox.setText(wonders.get(position).getOption(i));
                }
                optionRadioGroup.setVisibility(View.GONE);
                textviewGroup.setVisibility(View.GONE);
                checkboxGroup.setVisibility(View.VISIBLE);
                checkBoxAnswerHint.setVisibility(View.VISIBLE);
                break;
        }
    }

    //check answer
    public void checkResult () {
        boolean isCorrect = false;

        switch (wonders.get(position).getAnswerType()) {
            case "radio_button" :
                isCorrect = (wonders.get(position).getOption(indexOfSelectedOption)).equals(wonders.get(position).getName());
                break;
            case "text_view" :
                String answerText = textAnswer.getText().toString().trim().toLowerCase();
                String questionText = wonders.get(position).getName().toLowerCase();
                isCorrect = answerText.equals(questionText);
                break;
            case "check_box" :
                CheckBox answer1CheckBox = (CheckBox) checkboxGroup.getChildAt(0);
                CheckBox answer2CheckBox = (CheckBox) checkboxGroup.getChildAt(1);
                CheckBox answer3CheckBox = (CheckBox) checkboxGroup.getChildAt(2);
                CheckBox answer4CheckBox = (CheckBox) checkboxGroup.getChildAt(3);
                if (answer1CheckBox.isChecked() && answer2CheckBox.isChecked() && !answer3CheckBox.isChecked() && !answer4CheckBox.isChecked()) { isCorrect=true;}
                break;
        }

        if (isCorrect) {
            score = score + (10 - hintNumber - 1);
            if (position>=9) {
                isGameOver = true;
                Toast.makeText(MainActivity.this , getString(R.string.quiz_complete, playerName,score), Toast.LENGTH_SHORT).show();
                gameoverMessage = getString(R.string.quiz_complete, playerName,score);
                gameOverAlert(gameoverMessage);
            } else {
                scoreTextView.setText(getString(R.string.score_text, score));
                nextImage();
                indexOfSelectedOption = -1;
                submitButton.setClickable(false);
                submitButton.setAlpha(0.5f);
                Toast.makeText(MainActivity.this , (playerName + ", " + getString(R.string.correct_answer) + ", " + getString(R.string.score_text,score)), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            isGameOver = true;
            Toast.makeText(MainActivity.this , getString(R.string.gameover_text, playerName,position,score), Toast.LENGTH_SHORT).show();
            gameoverMessage = getString(R.string.gameover_text, playerName,position,score);
            gameOverAlert(gameoverMessage);
        }
    }

    //game over dialog box
    public void gameOverAlert (final String message) {
        AlertDialog.Builder gameoverBuilder = new AlertDialog.Builder(MainActivity.this);
        gameoverBuilder.setMessage(message).setCancelable(false);
        gameoverBuilder.setTitle(R.string.app_name);
        gameoverBuilder.setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                score = 0;
                scoreTextView.setText(getString(R.string.score_text,score));
                position = -1;
                nextImage();
                indexOfSelectedOption=-1;
                submitButton.setClickable(false);
                submitButton.setAlpha(0.5f);
                isGameOver = false;
                gameoverMessage = "";
            }
        });
        gameoverBuilder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog gameoverDialog = gameoverBuilder.create();
        gameoverDialog.show();
    }

    //login dialog box
    public void loginDialog () {
        final AlertDialog.Builder loginBuilder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_name,null);
        nameText = view.findViewById(R.id.username);
        saveUserCheck =view.findViewById(R.id.save_username);
        loginBuilder.setView(view).setCancelable(false);
        loginBuilder.setTitle(getString(R.string.app_name) + " - " + getString(R.string.login_text));
        loginBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                playerName = nameText.getText().toString().trim();
                int nameLength = playerName.length();
                isLoginShown = true;

                if(saveUserCheck.isChecked() && nameLength>0) {
                    SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                    mEditor.putBoolean("isLoginSaved", true);
                    mEditor.putString("player_name", playerName);
                    mEditor.commit();
                }

                if (nameLength == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.login_fail), Toast.LENGTH_SHORT ).show();
                    playerName = "X";
                }
            }
        });
        AlertDialog getnameDialog = loginBuilder.create();
        getnameDialog.show();
    }

    //dialog box for how to play
    public void showInstruction () {
        AlertDialog.Builder instructionBuilder = new AlertDialog.Builder(MainActivity.this);
        instructionBuilder.setTitle(getString(R.string.app_name) + " - " + getString(R.string.quiz_instruction));
        instructionBuilder.setMessage(getText(R.string.instruction_message)).setCancelable(false);
        instructionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog instructionDialog = instructionBuilder.create();
        instructionDialog.show();
    }
}
