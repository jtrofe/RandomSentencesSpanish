package com.example.main.randomsentences;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.main.randomsentences.SentenceGenerator.RandomSentenceGenerator;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;


public class SentenceActivity extends ActionBarActivity{
    private int mSentenceCount;
    private RandomSentenceGenerator sentenceGenerator;
    private boolean sentencesLoaded;
    private String mLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);

        sentenceGenerator = new RandomSentenceGenerator(this);
        mLanguage = Locale.getDefault().getLanguage();

        //*Count the children of the linear layout and set
        //*  that as the number of sentences to create
        mSentenceCount = ((LinearLayout) findViewById(R.id.layout_sentences)).getChildCount();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(!sentencesLoaded) GetNewSentences();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        LinearLayout layout = ((LinearLayout) findViewById(R.id.layout_sentences));
        outState.putInt("SentenceCount", mSentenceCount);
        outState.putBoolean("SentencesLoaded", sentencesLoaded);
        outState.putString("Language", mLanguage);

        for(int i=0;i<mSentenceCount;i++){
            TextView textView = (TextView) layout.findViewWithTag("sentence_" + i);
            String sentence = textView.getText().toString();
            outState.putString("sentence_" + i, sentence);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedState){
        super.onRestoreInstanceState(savedState);
        mSentenceCount = savedState.getInt("SentenceCount");
        sentencesLoaded = savedState.getBoolean("SentencesLoaded");

        if(!savedState.getString("Language").equals(mLanguage)){
            System.out.println("Language changed from " + savedState.getString("Language") + " to " + mLanguage);
            sentencesLoaded = false;
        }

        LinearLayout layout = ((LinearLayout) findViewById(R.id.layout_sentences));

        for(int i=0;i<mSentenceCount;i++){
            TextView textView = (TextView) layout.findViewWithTag("sentence_" + i);
            String txt = savedState.getString("sentence_" + i);
            textView.setText(txt);
        }
    }

    //*UI Event Handlers
    public void OnNewSentencesClicked(View view){
        GetNewSentences();
    }

    public void OnClearSentencesClicked(View view){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_sentences);
        for(int i=0;i<mSentenceCount;i++){
            TextView textView = (TextView) linearLayout.findViewWithTag("sentence_" + i);
            textView.setText("");
            textView.invalidate();
        }
        sentencesLoaded = false;
    }

    //*Sentence getter
    private void GetNewSentences(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_sentences);
        for(int i=0;i<mSentenceCount;i++){
            TextView textView = (TextView) linearLayout.findViewWithTag("sentence_" + i);
            textView.setText(sentenceGenerator.GetSentence());
            textView.invalidate();
        }
        sentencesLoaded = true;
        ((ScrollView) findViewById(R.id.scrollView_sentences)).scrollTo(0, 0);
    }


    //*ADS ADS ADS ADS ADS ADS ADS ADS ADS

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ad, container, false);
            return rootView;
        }
    }

    public static class AdFragment extends Fragment{
        private AdView mAdView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        @Override
        public void onActivityCreated(Bundle bundle){
            super.onActivityCreated(bundle);

            mAdView = (AdView) getView().findViewById(R.id.adView);

            AdRequest adRequest = new AdRequest.Builder().build();

            mAdView.loadAd(adRequest);
        }

        @Override
        public void onPause(){
            if(mAdView != null){
                mAdView.pause();
            }
            super.onPause();
        }

        @Override
        public void onResume(){
            super.onResume();
            if(mAdView != null) mAdView.resume();
        }

        @Override
        public void onDestroy(){
            if(mAdView != null) mAdView.destroy();
            super.onDestroy();
        }

    }
}
