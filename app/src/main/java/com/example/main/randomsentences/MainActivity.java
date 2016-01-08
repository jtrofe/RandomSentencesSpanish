package com.example.main.randomsentences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OpenSentences(View view){
        Intent intent = new Intent(this, SentenceActivity.class);
        startActivity(intent);
    }

    public void OpenPass(View view){
        Intent intent = new Intent(this, PassActivity.class);
        startActivity(intent);
    }
}