package com.example.main.randomsentences.Game.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.main.randomsentences.Game.CustomViews.DrawingView;
import com.example.main.randomsentences.Game.GameInterface;
import com.example.main.randomsentences.R;


public class DrawingFragment extends Fragment{
    //**Things for sliding
    public float getXFraction() {
        return mView.getX() / mView.getWidth(); // TODO: guard divide-by-zero
    }
    public void setXFraction(float xFraction) {
        // TODO: cache width
        final int width = mView.getWidth();
        mView.setX((width > 0) ? (xFraction * width) : -9999);
    }

    //**UI Elements
    private View mView;
    private TextView mTimerText;
    private DrawingView mDrawingView;
    private TextView mCaptionText;
    private Button mDoneButton;

    private ImageButton currPaint, currSize;

    //**Game variables
    private Activity mActivity;
    private GameInterface mGame;
    private String mCaption;

    //**Timer
    private long mStartTime = 0;
    private int mTimerLength;
    private boolean mPaused;
    private long mPausedTime = -1;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if(!mPaused){
                long millis = System.currentTimeMillis() - mStartTime;
                millis = mTimerLength - millis;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                mTimerText.setText(String.format("%02d:%02d", minutes, seconds));

                if (millis <= 0) {
                    FinishDrawing();
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
        mActivity = (Activity) game;
    }

    public void SetCaption(String caption){
        mCaption = caption;
        if(mCaptionText != null){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCaptionText.setText(mCaption);
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
        mView = inflater.inflate(R.layout.fragment_drawer, container, false);

        mTimerText = (TextView) mView.findViewById(R.id.drawing_timer_text);
        mDrawingView = (DrawingView) mView.findViewById(R.id.drawing_canvas);
        mDrawingView.setVisibility(View.INVISIBLE);
        mCaptionText = (TextView) mView.findViewById(R.id.drawing_caption_text);

        mDoneButton = (Button) mView.findViewById(R.id.drawing_done);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinishDrawing();
            }
        });
        mDoneButton.setVisibility(View.INVISIBLE);

        LinearLayout paintLayout = (LinearLayout) mView.findViewById(R.id.drawing_paint_colors);
        LinearLayout sizeLayout = (LinearLayout) mView.findViewById(R.id.drawing_brush_sizes);

        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        currSize = (ImageButton) sizeLayout.getChildAt(0);
        currSize.setImageDrawable(getResources().getDrawable(R.drawable.brush_button_small_pressed));


        for(int i=0;i<paintLayout.getChildCount();i++){
            ImageButton b = (ImageButton) paintLayout.getChildAt(i);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paintClicked(v);
                }
            });
        }

        for(int i=0;i<sizeLayout.getChildCount();i++){
            ImageButton b = (ImageButton) sizeLayout.getChildAt(i);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sizeClicked(v);
                }
            });
        }

        if(mCaption != null) mCaptionText.setText(mCaption);

        mDrawingView.setBrushSize(getResources().getInteger(R.integer.small_size));

        mGame.onStartDraw();
        return mView;
    }

    public void StartTimer(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDoneButton.setVisibility(View.VISIBLE);
                mDrawingView.setEnabled(true);
                mDrawingView.setVisibility(View.VISIBLE);
            }
        });
        mStartTime = System.currentTimeMillis();
        mTimerHandler = new Handler(Looper.getMainLooper());
        mTimerHandler.postDelayed(mTimerRunnable, 0);
    }


    private void FinishDrawing(){
        mTimerHandler.removeCallbacks(mTimerRunnable);
        mTimerHandler = null;
        mGame.onDrawTimerEnd(mDrawingView.GetImage());
    }



    //*Drawing controls
    public void paintClicked(View view){

        if(view != currPaint){
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            mDrawingView.setColor(color);

            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) view;
        }
    }

    public void sizeClicked(View view){

        if(view != currSize){
            ImageButton b = (ImageButton) view;
            String tag = b.getTag().toString();
            String lastTag = currSize.getTag().toString();

            switch(lastTag){
                case "small":
                    currSize.setImageDrawable(getResources().getDrawable(R.drawable.brush_button_small));
                    break;
                case "medium":
                    currSize.setImageDrawable(getResources().getDrawable(R.drawable.brush_button_medium));
                    break;
                case "large":
                    currSize.setImageDrawable(getResources().getDrawable(R.drawable.brush_button_large));

            }

            int size;
            Drawable selDraw;
            switch(tag){
                case "small":
                    size = getResources().getInteger(R.integer.small_size);
                    selDraw = getResources().getDrawable(R.drawable.brush_button_small_pressed);
                    break;
                case "medium":
                    size = getResources().getInteger(R.integer.medium_size);
                    selDraw = getResources().getDrawable(R.drawable.brush_button_medium_pressed);
                    break;
                case "large":
                    size = getResources().getInteger(R.integer.large_size);
                    selDraw = getResources().getDrawable(R.drawable.brush_button_large_pressed);
                    break;
                default:
                    size = getResources().getInteger(R.integer.small_size);
                    selDraw = getResources().getDrawable(R.drawable.brush_button_small_pressed);
            }

            mDrawingView.setBrushSize(size);

            b.setImageDrawable(selDraw);
            currSize = b;
        }
    }
}
