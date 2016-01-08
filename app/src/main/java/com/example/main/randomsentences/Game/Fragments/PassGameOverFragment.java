package com.example.main.randomsentences.Game.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.main.randomsentences.R;

import java.util.List;

public class PassGameOverFragment extends Fragment{
    //**UI Elements
    private View mView;
    private LinearLayout mLayout;

    public LinearLayout getLayout(){return mLayout;}



    //**Game elements
    private Context mContext;
    private Activity mActivity;

    private List<String> mCaptions;
    private List<Bitmap> mBitmaps;

    public void SetContext(Context context){mActivity = (Activity) context;mContext = context;}

    public void SetGameLists(List<String> captions, List<Bitmap> bitmaps){
        mCaptions = captions;
        mBitmaps = bitmaps;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.fragment_pass_game_over, container, false);

        mLayout = (LinearLayout) mView.findViewById(R.id.pass_game_over_layout);

        AddImages();
        return mView;
    }


    public void AddImages(){
        System.out.println("Layout: " + (mLayout == null));
        for(int i=0;i<mCaptions.size();i++){
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View cv = inflater.inflate(R.layout.caption_view_game_over, null);

            TextView textView = (TextView) cv.findViewById(R.id.caption_text);
            textView.setText(mCaptions.get(i));

            mLayout.addView(cv);

            View dv = inflater.inflate(R.layout.drawing_view_game_over, null);

            ImageView imageView = (ImageView) dv.findViewById(R.id.drawing_image);
            imageView.setImageBitmap(mBitmaps.get(i));

            mLayout.addView(dv);

        }
    }
}
