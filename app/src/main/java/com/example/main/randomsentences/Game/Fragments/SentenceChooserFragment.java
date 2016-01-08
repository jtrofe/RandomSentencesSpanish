package com.example.main.randomsentences.Game.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.main.randomsentences.Game.GameInterface;
import com.example.main.randomsentences.R;
import com.example.main.randomsentences.SentenceGenerator.RandomSentenceGenerator;

public class SentenceChooserFragment extends Fragment{
    //**UI Elements
    private View mView;
    private TextView mTimerText;

    //**Game variables
    private Context mContext;
    private GameInterface mGame;

    //**Timer
    private boolean mEnded;
    private long mStartTime = 0;
    private int mTimerLength;
    private boolean mPaused;
    private long mPausedTime = -1;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run(){
            if(!mPaused){
                long millis = System.currentTimeMillis() - mStartTime;
                millis = mTimerLength - millis;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                mTimerText.setText(String.format("%02d:%02d", minutes, seconds));

                if(millis <= 0 && !mEnded){
                    System.out.println("Sentence choose runnable end");

                    mEnded = true;
                    LinearLayout linearLayout = (LinearLayout) mView.findViewById(R.id.layout_sentences);
                    Button textView = (Button) linearLayout.findViewWithTag("sentence_0");

                    SelectSentence(textView.getText().toString());
                }else if (!mEnded) {
                    mTimerHandler.postDelayed(this, 500);
                }
            }else{
                mTimerHandler.postDelayed(this, 500);
            }
        }
    };


    public void SetContext(Context context){mContext = context;}
    public void SetGame(GameInterface game){
        mGame = game;
    }

    /**
     * Sets the length of the timer
     * @param length timer length in seconds
     */
    public void SetTimerLength(int length){
        mTimerLength = length * 1000;
        mPausedTime = -1;
    }

    @Override
    public void onPause(){
        mPaused = true;
        mPausedTime = System.currentTimeMillis();
        super.onPause();
    }

    @Override
    public void onResume(){
        if(mPausedTime != -1){
            mStartTime += System.currentTimeMillis() - mPausedTime;
            mPaused = false;
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.fragment_sentence_chooser, container, false);

        mTimerText = (TextView) mView.findViewById(R.id.pass_timer_text);

        RandomSentenceGenerator generator = new RandomSentenceGenerator(mContext);

        LinearLayout linearLayout = (LinearLayout) mView.findViewById(R.id.layout_sentences);
        int sentenceCount = linearLayout.getChildCount();
        for(int i=0;i<sentenceCount;i++){
            Button textView = (Button) linearLayout.findViewWithTag("sentence_" + i);
            textView.setText(generator.GetSentence());
            textView.invalidate();

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectSentence(((Button) v).getText().toString());
                }
            });
        }

        mStartTime = System.currentTimeMillis();
        mTimerHandler = new Handler();
        mTimerHandler.postDelayed(mTimerRunnable, 0);
        mEnded = false;
        return mView;
    }

    private void SelectSentence(String sentence){
        mTimerHandler.removeCallbacks(mTimerRunnable);
        mTimerHandler = null;
        mGame.onSentenceChosen(sentence);
    }
}
