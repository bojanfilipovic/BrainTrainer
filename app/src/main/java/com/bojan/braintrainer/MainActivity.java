package com.bojan.braintrainer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button startButton, button1, button2, button3, button4, playAgainButton;
    TextView resultTextView, sumTextView, pointsTextView, timerTextView, volumeTextView;
    MediaPlayer mPlayer, mPlayerStart;
    AudioManager audioManager;
    SeekBar volumeControl;
    RelativeLayout gameRelativeLayout;
    GridLayout gridLayout;
    ArrayList<Integer> answers = new ArrayList<Integer>();
    int locationOfCorrectAnswer;
    int score = 0;
    int numberOfQuestions = 0;

    public void playAgain(View view){

        score = 0;
        numberOfQuestions =0;
        mPlayerStart.start();

        timerTextView.setText(R.string.thirty);
        pointsTextView.setText(R.string.score);
        resultTextView.setText("");
        playAgainButton.setVisibility(View.INVISIBLE);


        //to disable buttons after game finishes
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            child.setEnabled(true);
        }

        generateQuestion();

        new CountDownTimer(10100, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished/1000)+"s");
            }

            @Override
            public void onFinish() {
                mPlayerStart.pause();
                timerTextView.setText(R.string.zero);
                resultTextView.setText("Your score: " +
                        Integer.toString(score) +
                        "/" +
                        Integer.toString(numberOfQuestions));
                playAgainButton.setVisibility(View.VISIBLE);

                //to disable buttons after game finishes
                for (int i = 0; i < gridLayout.getChildCount(); i++) {
                    View child = gridLayout.getChildAt(i);
                    child.setEnabled(false);
                }
            }
        }.start();
    }

    public void generateQuestion(){
        Random rand = new Random();

        int a = rand.nextInt(21);
        int b = rand.nextInt(21);
        int incorrectAnswer;

        sumTextView.setText(Integer.toString(a) + " + " + Integer.toString(b));
        locationOfCorrectAnswer = rand.nextInt(4);   //0 1 2 3
        answers.clear();

        for (int i=0; i<4; i++){
            if (i == locationOfCorrectAnswer){
                answers.add(a + b);
            } else {
                incorrectAnswer = rand.nextInt(41);
                while (incorrectAnswer == a+b){
                    incorrectAnswer = rand.nextInt(41);
                }
                answers.add(incorrectAnswer);
            }
        }

        button1.setText(Integer.toString(answers.get(0)));
        button2.setText(Integer.toString(answers.get(1)));
        button3.setText(Integer.toString(answers.get(2)));
        button4.setText(Integer.toString(answers.get(3)));
    }

    public void chooseAnswer(View view){
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            //catch tag of clicked button, and compare it to (converted) integer location of corr answer
            score++;
            resultTextView.setText(R.string.correct);
        } else {
            //add incorrect to score
            resultTextView.setText(R.string.wrong);

        }
        numberOfQuestions++;
        pointsTextView.setText(Integer.toString(score)+"/"+Integer.toString(numberOfQuestions));
        generateQuestion();
    }

    public void start(View view){
        startButton.setVisibility(View.INVISIBLE);
        volumeTextView.setVisibility(View.INVISIBLE);
        volumeControl.setVisibility(View.INVISIBLE);

        gameRelativeLayout.setVisibility(RelativeLayout.VISIBLE);
        playAgain(findViewById(R.id.playAgainButton));          //any view is ok
        mPlayer.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button)findViewById(R.id.startButton);
        playAgainButton = (Button)findViewById(R.id.playAgainButton);
        sumTextView = (TextView)findViewById(R.id.sumTextView);
        resultTextView = (TextView)findViewById(R.id.resultTextView);
        pointsTextView = (TextView) findViewById(R.id.pointsTextView);
        volumeTextView = (TextView)findViewById(R.id.volumeTextView);
        timerTextView = (TextView)findViewById(R.id.timerTextView);
        gameRelativeLayout = (RelativeLayout)findViewById(R.id.gameRelativeLayout);
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);

        volumeControl = (SeekBar) findViewById(R.id.seekBar);

        mPlayer = MediaPlayer.create(this, R.raw.before);
        mPlayerStart = MediaPlayer.create(this, R.raw.start);
        mPlayer.start();


        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);       //use audiomanager to get information about the volume
        final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  //generic stream for playing sound music in our app
        final int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        //set the max and current volume to the seekbar end
        volumeControl.setMax(maxVolume);
        volumeControl.setProgress(curVolume);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //we need to implement methods for other two user use cases even though we may not be using them
            //when user touches, when user moves, and when user releases the seekbar
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //when the user stopped
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //when the user started
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //we are adding our own code to a method that already exists
                //thats what @Override is for, when you add a code to a method that already exists
                //because when the app runs some methods already exists and do some work, we just expand them

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);       //set the volume
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
