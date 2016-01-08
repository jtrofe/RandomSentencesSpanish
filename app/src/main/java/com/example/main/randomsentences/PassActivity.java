package com.example.main.randomsentences;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.main.randomsentences.Game.Fragments.CaptionFragment;
import com.example.main.randomsentences.Game.Fragments.DrawingFragment;
import com.example.main.randomsentences.Game.Fragments.PassGameOverFragment;
import com.example.main.randomsentences.Game.Fragments.SentenceChooserFragment;
import com.example.main.randomsentences.Game.GameInterface;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;


public class PassActivity extends ActionBarActivity implements GameInterface{

    private int mRoundLimit;
    private int mRoundCount;

    FragmentTransaction mFragmentTransaction;
    Fragment mCurrentFragment;

    List<String> captions;
    List<Bitmap> bitmaps;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        this.onGameStart();
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

    private void ReplaceFragment(Fragment f){
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.setCustomAnimations(R.anim.anim_scroll_in, R.anim.anim_scroll_out);

        mCurrentFragment = f;
        mFragmentTransaction.replace(R.id.fragment_container, mCurrentFragment).commit();
    }

    public void onGameStart(){
        mRoundCount = 0;
        mRoundLimit = 4;

        captions = new ArrayList<>();
        bitmaps = new ArrayList<>();

        SentenceChooserFragment f = new SentenceChooserFragment();
        f.SetContext(this);
        f.SetGame(this);
        f.SetTimerLength(60);

        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.fragment_container, f).commit();
    }

    public void onSentenceChosen(String sentence){
        captions.add(sentence);

        DrawingFragment f = new DrawingFragment();
        f.SetGame(this);
        f.SetCaption(sentence);
        f.SetTimerLength(120);

        ReplaceFragment(f);
    }


    public void onReceiveCaption(String sentence){}

    public void onStartDraw(){
        ((DrawingFragment) mCurrentFragment).StartTimer();
    }

    public void onDrawTimerEnd(Bitmap bitmap){
        bitmaps.add(bitmap);

        mRoundCount ++;

        if(mRoundCount == mRoundLimit){
            this.onGameEnd();
        }else{
            CaptionFragment f = new CaptionFragment();
            f.SetGame(this);
            f.SetBitmap(bitmap);
            f.SetTimerLength(120);

            ReplaceFragment(f);
        }
    }

    public void onReceiveDrawing(Bitmap img){
    }

    public void onStartCaption(){
        ((CaptionFragment) mCurrentFragment).StartTimer();
    }

    public void onCaptionTimerEnd(String caption){
        captions.add(caption);

        DrawingFragment f = new DrawingFragment();
        f.SetGame(this);
        f.SetCaption(caption);
        f.SetTimerLength(120);

        ReplaceFragment(f);
    }

    public void onReceiveCaption(){}

    public void onGameEnd(){
        System.out.println("Game over");
        PassGameOverFragment f = new PassGameOverFragment();

        f.SetContext(this);
        f.SetGameLists(captions, bitmaps);

        ReplaceFragment(f);
    }

    @Override
    public void onBackPressed(){
        System.out.println("Back pressed");
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
        private static AdView mAdView;

        public static void ShowAd(){
            if(mAdView != null) mAdView.resume();
        }

        public static void HideAd(){
            if(mAdView != null) mAdView.pause();
        }

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
