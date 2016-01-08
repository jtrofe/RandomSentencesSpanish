package com.example.main.randomsentences.Game;

import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class GamePaper{

    public List<String> captions;
    public List<Bitmap> bitmaps;

    public GamePaper(){
        captions = new ArrayList<>();
        bitmaps = new ArrayList<>();
    }
}