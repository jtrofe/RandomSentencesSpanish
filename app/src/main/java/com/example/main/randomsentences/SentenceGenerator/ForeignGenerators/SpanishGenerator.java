package com.example.main.randomsentences.SentenceGenerator.ForeignGenerators;


public class SpanishGenerator{
    public static String MakeSentence(String baseText, String[] returnedWords){
        String sentence = baseText;

        //*Replace question marks in base text with respective words
        for(String word:returnedWords){
            sentence = sentence.replaceFirst(" \\?", " " + word);
        }

        while(sentence.contains("@")){
            sentence = MakeAgreements(sentence);
        }
        sentence = sentence.replaceAll("m\\|", "").replaceAll("f\\|", "");

        // Many adjectives don't have an @
        // Remove the < and > from these
        sentence = sentence.replaceAll("<", "").replaceAll(">", "");

        // Clean up any double spaces
        sentence = sentence.replaceAll("  ", " ");

        sentence = sentence.trim();

        //*Capitalize first letter
        String firstLetter = sentence.substring(0, 1).toUpperCase();
        sentence = firstLetter + sentence.substring(1);


        String lastChar = sentence.substring(sentence.length()-1);
        if(!lastChar.equals("¿") && !lastChar.equals("?") && !lastChar.equals("!") && !lastChar.equals(".") && !lastChar.equals("\"")){
            sentence = sentence + ".";
        }
        return sentence;
    }

    private static String MakeAgreements(String sentence){
        //*Finds the noun that is being pointed to after a @ symbol
        //*  replaces it with a/o/as/os depending on the gender specified
        //*  by the m/f| before the noun
        int articleIndex = sentence.indexOf("@");
        String direction = sentence.substring(articleIndex+1, articleIndex+2);
        String restOfSentence;
        int beginningOfVariable;
        int beginningOfNoun;
        int endOfNoun;
        String variable;
        String noun;
        String nounGender;
        String nounLastLetter;
        String baseVariable;

        if(direction.equals("<")){
            restOfSentence = sentence.substring(0, articleIndex);

            beginningOfVariable = restOfSentence.lastIndexOf(" ");

            String rest2 = restOfSentence.substring(0, beginningOfVariable-2);
            beginningOfNoun = rest2.lastIndexOf("|") - 1;
            if(beginningOfNoun == -1) beginningOfNoun = 0;
            endOfNoun = restOfSentence.indexOf(" ", beginningOfNoun);
            if(endOfNoun == -1) endOfNoun = restOfSentence.length();


            baseVariable = restOfSentence.substring(beginningOfVariable);
            noun = restOfSentence.substring(beginningOfNoun, endOfNoun);
        }else{
            restOfSentence = sentence.substring(sentence.indexOf("|")-1);
            beginningOfVariable = sentence.lastIndexOf(" ", articleIndex);
            if(beginningOfVariable == -1) beginningOfVariable = 0;
            baseVariable = sentence.substring(beginningOfVariable, articleIndex);
            endOfNoun = restOfSentence.indexOf(" ");
            if(endOfNoun == -1) endOfNoun = restOfSentence.length();

            noun = restOfSentence.substring(0, endOfNoun);
        }


        nounLastLetter = noun.substring(noun.length()-1);

        nounGender = noun.substring(0, 1);

        variable = baseVariable.trim().toLowerCase();
        if(nounGender.equals("f")){
            variable += "a";
        }else{
            variable += "o";
        }

        if(variable.equals("uno")) variable = variable.substring(0, 2);
        if(variable.equals("lo")) variable = "el";

        if(nounLastLetter.equals("s")) variable += "s";
        if(variable.equals("els")) variable = "los";

        if(variable.equals("uns")) variable = "unos";
        if(variable.equals("uno")) variable = "un";


        sentence = sentence.replace(baseVariable + "@" + direction, " " + variable);

        return sentence;
    }
}