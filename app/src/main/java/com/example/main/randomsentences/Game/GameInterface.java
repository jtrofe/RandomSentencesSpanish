package com.example.main.randomsentences.Game;

import android.graphics.Bitmap;

public interface GameInterface{
    public void onGameStart();

    public void onSentenceChosen(String sentence);

    public void onReceiveCaption(String sentence);

    public void onStartDraw();

    public void onDrawTimerEnd(Bitmap img);

    public void onReceiveDrawing(Bitmap img);

    public void onStartCaption();

    public void onCaptionTimerEnd(String caption);

    public void onReceiveCaption();

    public void onGameEnd();
}
