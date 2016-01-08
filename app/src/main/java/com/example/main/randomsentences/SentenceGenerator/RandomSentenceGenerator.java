package com.example.main.randomsentences.SentenceGenerator;

import android.content.Context;

import com.example.main.randomsentences.R;
import com.example.main.randomsentences.SentenceGenerator.ForeignGenerators.SpanishGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Random;


public class RandomSentenceGenerator {
    public static String BLANK_SENTENCE = "___________________";
    public static String BLANK_WORD = "_____";

    private Context mContext;
    //*Word lists
    private JSONArray mStructures;
    private JSONObject mVocab;

    //*Info about sentence
    private String subject_gender;
    private String mBaseText;
    private String[] mReturnedWords;

    public RandomSentenceGenerator(Context context) {
        mContext = context;
        LoadVocabulary();
    }

    public String GetSentence(int index){
        if(mStructures == null || mVocab == null){
            System.out.println("No structures or vocab loaded");
            return BLANK_SENTENCE;
        }

        //*Check the language of the vocab to determine if it
        //*  needs to be reloaded
        String language = "";

        try{
            language = mVocab.getString("language");
        }catch(JSONException e){
            System.out.println("Unable to get vocab language\n" + e.getMessage());
        }

        String phoneLanguage = Locale.getDefault().getLanguage();
        if(!language.equals(phoneLanguage)){
            System.out.println("Language loaded: " + language + "\nPhone language: " + phoneLanguage);
            LoadVocabulary();
        }

        //*Get the selected index
        JSONObject structure;
        int structureIndex;
        Random rnd = new Random();
        try{
            structureIndex = rnd.nextInt(index);
            structure = mStructures.getJSONObject(structureIndex);
        }catch(JSONException e){
            System.out.println("Error getting structure\n" + e.getMessage());
            return BLANK_SENTENCE;
        }

        //*Get parameters and base text
        JSONArray params;
        mBaseText = "";
        try{
            mBaseText = structure.getString("text");
            params = structure.getJSONArray("params");
        }catch(JSONException e){
            System.out.println("Getting params error\n" + e.getMessage());
            return BLANK_SENTENCE;
        }

        //*Verify that the base text and param count match
        int lastIndex = 0;
        int count = 0;
        int paramCount = params.length();
        while(lastIndex != -1){
            lastIndex = mBaseText.indexOf(" ?", lastIndex);
            if(lastIndex != -1){
                count ++;
                lastIndex += 2;
            }
        }
        if(count != paramCount){
            System.out.println("Parameter mismatch (" + count + ", " + paramCount + ")\n" + mBaseText);
            return BLANK_SENTENCE;
        }

        //*Loop through the parameters and see if the sentence
        //* has a subject. If there is, pick a random gender
        subject_gender = "m";
        for(int i=0;i<count;i++){
            try{
                String pType = params.getJSONObject(i).getString("pos");
                if(pType.equals("subject")){
                    String gender = params.getJSONObject(i).getString("gender");
                    if(gender.equals("?")){
                        if (rnd.nextInt(2) == 1) subject_gender = "f";
                    }else{
                        subject_gender = gender;
                    }
                }
            }catch(Exception e){
            }
        }

        //*Get random words
        mReturnedWords = new String[count];
        for(int i=0;i<count;i++) {
            try{
                mReturnedWords[i] = GetWord(params.getJSONObject(i));
            }catch(JSONException e){
                System.out.println("Error getting word\n" + e.getMessage());
                mReturnedWords[i] = BLANK_WORD;
            }
        }

        switch(language){
            case "en":
                return MakeSentence();
            case "es":
                return SpanishGenerator.MakeSentence(mBaseText, mReturnedWords);
        }
        return BLANK_SENTENCE;
    }

    public String GetSentence(){
        if(mStructures == null || mVocab == null){
            System.out.println("No structures or vocab loaded");
            return BLANK_SENTENCE;
        }

        //*Check the language of the vocab to determine if it
        //*  needs to be reloaded
        String language = "";

        try{
            language = mVocab.getString("language");
        }catch(JSONException e){
            System.out.println("Unable to get vocab language\n" + e.getMessage());
        }

        String phoneLanguage = Locale.getDefault().getLanguage();
        if(!language.equals(phoneLanguage)){
            System.out.println("Language loaded: " + language + "\nPhone language: " + phoneLanguage);
            LoadVocabulary();
        }

        //*Pick random structure from the structures object
        JSONObject structure;
        int structureIndex;
        Random rnd = new Random();
        try{
            structureIndex = rnd.nextInt(mStructures.length());
            structure = mStructures.getJSONObject(structureIndex);
        }catch(JSONException e){
            System.out.println("Error getting structure\n" + e.getMessage());
            return BLANK_SENTENCE;
        }

        //*Get parameters and base text
        JSONArray params;
        mBaseText = "";
        try{
            mBaseText = structure.getString("text");
            params = structure.getJSONArray("params");
        }catch(JSONException e){
            System.out.println("Getting params error\n" + e.getMessage());
            return BLANK_SENTENCE;
        }

        //*Verify that the base text and param count match
        int lastIndex = 0;
        int count = 0;
        int paramCount = params.length();
        while(lastIndex != -1){
            lastIndex = mBaseText.indexOf(" ?", lastIndex);
            if(lastIndex != -1){
                count ++;
                lastIndex += 2;
            }
        }
        if(count != paramCount){
            System.out.println("Parameter mismatch (" + count + ", " + paramCount + ")\n" + mBaseText);
            return BLANK_SENTENCE;
        }

        //*Loop through the parameters and see if the sentence
        //* has a subject. If there is, pick a random gender
        subject_gender = "m";
        for(int i=0;i<count;i++){
            try{
                String pType = params.getJSONObject(i).getString("pos");
                if(pType.equals("subject")){
                    String gender = params.getJSONObject(i).getString("gender");
                    if(gender.equals("?")){
                        if (rnd.nextInt(2) == 1) subject_gender = "f";
                    }else{
                        subject_gender = gender;
                    }
                }
            }catch(Exception e){
            }
        }

        //*Get random words
        mReturnedWords = new String[count];
        for(int i=0;i<count;i++) {
            try{
                mReturnedWords[i] = GetWord(params.getJSONObject(i));
            }catch(JSONException e){
                System.out.println("Error getting word\n" + e.getMessage());
                mReturnedWords[i] = BLANK_WORD;
            }
        }

        switch(language){
            case "en":
                return MakeSentence();
            case "es":
                return SpanishGenerator.MakeSentence(mBaseText, mReturnedWords);
        }
        return BLANK_SENTENCE;
    }

    private String GetWord(JSONObject param) throws JSONException{
        Random rnd = new Random();
        String pos = param.getString("pos");

        JSONArray wordList;
        JSONArray wordArray;
        JSONObject nameObj;
        String word;
        switch (pos){
            case "subject":
                wordList = mVocab.getJSONObject("subject").getJSONArray(subject_gender);
                word = wordList.getString(rnd.nextInt(wordList.length()));
                break;
            case "hero":
                wordList = mVocab.getJSONArray("hero");
                nameObj = wordList.getJSONObject(rnd.nextInt(wordList.length()));
                subject_gender = nameObj.getString("gender");
                word = nameObj.getString("name");
                break;
            case "villain":
                wordList = mVocab.getJSONArray("villain");
                nameObj = wordList.getJSONObject(rnd.nextInt(wordList.length()));
                subject_gender = nameObj.getString("gender");
                word = nameObj.getString("name");
                break;
            case "abstract":
                wordList = mVocab.getJSONArray("abstract");
                word = wordList.getString(rnd.nextInt(wordList.length()));
                break;
            case "celeb":
                wordList = mVocab.getJSONArray("celeb");
                nameObj = wordList.getJSONObject(rnd.nextInt(wordList.length()));
                subject_gender = nameObj.getString("gender");
                word = nameObj.getString("name");
                break;
            case "noun":
                wordList = mVocab.getJSONArray("noun");
                wordArray = wordList.getJSONArray(rnd.nextInt(wordList.length()));
                word = wordArray.getString(0);
                if(wordArray.length() == 1) break;
                if (param.has("plural")) {
                    if (param.getBoolean("plural")) {
                        word = wordArray.getString(1);
                    }
                }
                break;
            case "verb-intransitive":
                wordList = mVocab.getJSONArray("verb-intransitive");
                wordArray = wordList.getJSONArray(rnd.nextInt(wordList.length()));
                word = wordArray.getString(param.getInt("form"));
                break;
            case "verb-transitive":
                wordList = mVocab.getJSONArray("verb-transitive");
                wordArray = wordList.getJSONArray(rnd.nextInt(wordList.length()));
                word = wordArray.getString(param.getInt("form"));
                break;
            case "adjective":
                wordList = mVocab.getJSONArray("adjective");
                word = wordList.getString(rnd.nextInt(wordList.length()));
                break;
            case "adverb":
                wordList = mVocab.getJSONArray("adverb");
                word = wordList.getString(rnd.nextInt(wordList.length()));
                break;
            case "substance":
                wordList = mVocab.getJSONArray("substance");
                word = wordList.getString(rnd.nextInt(wordList.length()));
                break;
            default:
                System.out.println("Unknown part of speech-" + pos);
                word = BLANK_WORD;
        }

        if(param.has("capital")){
            if (param.getBoolean("capital")) {
                word = toTitleCase(word);
            }

        }
        return word;
    }

    private String MakeSentence(){
        String sentence = mBaseText;
        String vowels = "aeiou";

        //*Replace question marks in base text with respective words
        for(String word:mReturnedWords){
            sentence = sentence.replaceFirst(" \\?", " " + word);
        }

        //*Replace quote followed by space with just quote
        sentence = sentence.replaceAll("\" ", "\"");

        //*Replace all the articles
        int articleIndex = sentence.indexOf("|a/n|");
        while(articleIndex >= 0){
            String nextLetter = sentence.substring(articleIndex+6, articleIndex + 7);
            if(nextLetter.equals("[")){
                nextLetter = sentence.substring(articleIndex+7, articleIndex + 8);
            }
            if(vowels.contains(nextLetter)){
                sentence = sentence.replace("|a/n|", "an");
            }else{
                sentence = sentence.replace("|a/n|", "a");
            }
            articleIndex = sentence.indexOf("|a/n|");
        }

        //*Replace all the possessives
        if(subject_gender.equals("m")){
            sentence = sentence.replaceAll("\\|pos\\|", "his");
        }else{
            sentence = sentence.replaceAll("\\|pos\\|", "her");
        }


        //*Replace all the pronouns
        if(subject_gender.equals("m")){
            sentence = sentence.replaceAll("\\|pro\\|", "he");
        }else{
            sentence = sentence.replaceAll("\\|pro\\|", "she");
        }


        //*Replace all the object pronouns
        if(subject_gender.equals("m")){
            sentence = sentence.replaceAll("\\|opr\\|", "him");
        }else{
            sentence = sentence.replaceAll("\\|opr\\|", "her");
        }

        sentence = sentence.trim();
        //*Capitalize first word
        String firstLetter = sentence.substring(0, 1).toUpperCase();
        if(firstLetter.equals("[")){
            firstLetter = sentence.substring(1, 2).toUpperCase();
            sentence = "[" + firstLetter + sentence.substring(2);
        }else {
            sentence = firstLetter + sentence.substring(1);
        }

        String lastChar = sentence.substring(sentence.length()-1);
        if(!lastChar.equals("?") && !lastChar.equals("!") && !lastChar.equals(".") && !lastChar.equals("\"")){
            sentence = sentence + ".";
        }

        return sentence;
    }

    private void LoadVocabulary(){
        //*Load the structures file from the MainActivity context
        InputStream structureFile = mContext.getResources().openRawResource(R.raw.structures);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(structureFile));
        String r = "";
        String line;
        try{
            while ((line = bufferedReader.readLine()) != null) {
                r += line;
            }
            mStructures = new JSONArray(r);
        }catch(Exception e){
            mStructures = null;
            System.out.println("Reading structures error\n" + e.toString());
        }

        try{
            System.out.println(mStructures.length() + " structures loaded");
        }catch(Exception e){}

        //*Load vocab
        InputStream vocabFile = mContext.getResources().openRawResource(R.raw.vocabulary);
        bufferedReader = new BufferedReader(new InputStreamReader(vocabFile));
        r = "";
        try{
            while ((line = bufferedReader.readLine()) != null) {
                r += line;
            }
            mVocab = new JSONObject(r);
        }catch(Exception e){
            mVocab = null;
            System.out.println("Reading vocab error\n" + e.toString());
        }
    }

    private String toTitleCase(String noun){
        String[] arr = noun.split(" ");
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < arr.length; i++){
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
