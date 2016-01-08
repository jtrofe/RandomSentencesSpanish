package com.example.main.randomsentences.Game.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main.randomsentences.Game.GameInterface;
import com.example.main.randomsentences.R;
import com.example.main.randomsentences.SentenceGenerator.RandomSentenceGenerator;


public class CaptionFragment extends Fragment{
    //**UI Elements
    private View mView;
    private TextView mTimerText;
    private EditText mCaptionText;
    private ImageView mImageView;
    private Button mDoneButton;

    //**Game variables
    private Activity mActivity;
    private Context mContext;
    private GameInterface mGame;
    private Bitmap mBitmap;

    //**Timer
    private long mStartTime = 0;
    private int mTimerLength;
    private boolean mPaused;
    private long mPausedTime = -1;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if(!mPaused) {
                long millis = System.currentTimeMillis() - mStartTime;
                millis = mTimerLength - millis;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                mTimerText.setText(String.format("%02d:%02d", minutes, seconds));

                if (millis <= 0) {
                    FinishCaption();
                } else {
                    mTimerHandler.postDelayed(this, 500);
                }
            }else{
                mTimerHandler.postDelayed(this, 500);
            }
        }
    };


    public void SetGame(GameInterface game){
        mGame = game;
        mContext = (Context) game;
        mActivity = (Activity) game;
    }

    public void SetBitmap(Bitmap bitmap){
        mBitmap = bitmap;
        if(mImageView != null){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(mBitmap);
                }
            });
        }
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
        mView = inflater.inflate(R.layout.fragment_caption, container, false);

        mTimerText = (TextView) mView.findViewById(R.id.caption_timer_text);
        mImageView = (ImageView) mView.findViewById(R.id.caption_bitmap);
        mCaptionText = (EditText) mView.findViewById(R.id.caption_text);
        mCaptionText.setEnabled(false);

        mDoneButton = (Button) mView.findViewById(R.id.caption_done);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCaptionText.getText().toString().trim().equals("")){
                    Toast.makeText(mContext, R.string.warning_caption_blank, Toast.LENGTH_SHORT).show();
                    return;
                }
                FinishCaption();
            }
        });
        mDoneButton.setVisibility(View.INVISIBLE);

        if(mBitmap != null) mImageView.setImageBitmap(mBitmap);

        mGame.onStartCaption();

        return mView;
    }

    public void StartTimer(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDoneButton.setVisibility(View.VISIBLE);
                mCaptionText.setEnabled(true);
            }
        });
        mStartTime = System.currentTimeMillis();
        mTimerHandler = new Handler(Looper.getMainLooper());
        mTimerHandler.postDelayed(mTimerRunnable, 0);
    }

    private void FinishCaption(){
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mCaptionText.getWindowToken(), 0);

        mTimerHandler.removeCallbacks(mTimerRunnable);
        mTimerHandler = null;

        String caption = mCaptionText.getText().toString().trim();

        if(caption.equals("")){
            RandomSentenceGenerator rsg = new RandomSentenceGenerator(mContext);
            caption = rsg.GetSentence();
        }

        mGame.onCaptionTimerEnd(caption);
    }
}
