package me.kindeep.fahadstimulator;

import java.util.Random;

public class Fahad {

    public Fahad() {

        System.out.println(fahadifySentence("What is phone number" ));
        //does nothing right now.
    }

    /**
     * Converts given word, has to be a single word to Fahadian tounge
     *
     * @param word word to convert
     * @return fahadified word
     */
    public static String fahadifyWord(String word) {

        String result = "";

        if(word.length()>3) {
            StringBuilder sb = new StringBuilder(word);

            Random methodselection = new Random();
            Random finalSelection = new Random();
            Random index = new Random();

            int method = methodselection.nextInt(2) + 1;

            int indexToReplace = 0;

            int select = finalSelection.nextInt(3) + 1;

            if (method == 1) {
                for (int i = 0; i <= 3; i++) {
                    indexToReplace = index.nextInt((word.length()-1));
                    if (select == i) {
                        if (word.charAt(indexToReplace) == 'a' | word.charAt(indexToReplace) == 'A' |
                                word.charAt(indexToReplace) == 'e' | word.charAt(indexToReplace) == 'E' |
                                word.charAt(indexToReplace) == 'i' | word.charAt(indexToReplace) == 'I' |
                                word.charAt(indexToReplace) == 'O' | word.charAt(indexToReplace) == 'o' |
                                word.charAt(indexToReplace) == 'U' | word.charAt(indexToReplace) == 'u') {
                            indexToReplace = updateIndex(indexToReplace, word.length());
                        }
                        result = method1(sb, indexToReplace, word);
                        if (doubleExists(result)) {
                            indexToReplace = updateIndex(indexToReplace, word.length()-1);
                            result = method1(sb, indexToReplace, word);
                        }
                    }
                }
            } else if (method == 3) {
                for (int i = 0; i <= 3; i++) {
                    indexToReplace = index.nextInt((word.length()-1));
                    if (select == i) {
                        if (word.charAt(indexToReplace) == 'a' | word.charAt(indexToReplace) == 'A' |
                                word.charAt(indexToReplace) == 'e' | word.charAt(indexToReplace) == 'E' |
                                word.charAt(indexToReplace) == 'i' | word.charAt(indexToReplace) == 'I' |
                                word.charAt(indexToReplace) == 'O' | word.charAt(indexToReplace) == 'o' |
                                word.charAt(indexToReplace) == 'U' | word.charAt(indexToReplace) == 'u') {
                            indexToReplace = updateIndex(indexToReplace, word.length());
                        }
                        result = method2(sb, word);
                        if (doubleExists(result)) {
                            indexToReplace = updateIndex(indexToReplace, word.length()-1);
                            result = method2(sb, word);
                        }
                    }
                }
            } else if (method == 2) {
                for (int i = 0; i <= 3; i++) {
                    indexToReplace = index.nextInt((word.length()-1));
                    if (select == i) {
                        if (word.charAt(indexToReplace) == 'a' | word.charAt(indexToReplace) == 'A' |
                                word.charAt(indexToReplace) == 'e' | word.charAt(indexToReplace) == 'E' |
                                word.charAt(indexToReplace) == 'i' | word.charAt(indexToReplace) == 'I' |
                                word.charAt(indexToReplace) == 'O' | word.charAt(indexToReplace) == 'o' |
                                word.charAt(indexToReplace) == 'U' | word.charAt(indexToReplace) == 'u') {
                            indexToReplace = updateIndex(indexToReplace, word.length());
                        }
                        result = method3(sb, indexToReplace, word);
                        if (doubleExists(result)) {
                            indexToReplace = updateIndex(indexToReplace, word.length()-1);
                            result = method3(sb, indexToReplace, word);
                        }
                    }
                }
            }

            if(result.equals(word)){

            }
        }else {
            result = word;
        }
           return result;
    }


    private static int updateIndex(int i, int word){
        if(i==word){
            i--;
        }else{
            i++;
        }
        return i;
    }



    private static boolean doubleExists(String s){
        boolean result = false;
        for (int i = 0; i <s.length()-1 ; i++) {
           if(s.charAt(i)==s.charAt(i+1)) {
               result = true;
           }
        }
        return result;
    }

    /**
     * Changes the word with first method
     *
     * @param word word to convert,  string builder with word to convert and index of word to swap
     * @return fahadified word
     */

    private static String method1(StringBuilder sb, int index, String word) {
          char temp = word.charAt(index);
          char zeroIndex = word.charAt(0);
          sb.setCharAt(0,temp);
          sb.setCharAt(index,zeroIndex);
          return sb.toString();
    }

    /**
     * Changes the word with third method
     *
     * @param word word to convert,  string builder with word to convert and index of word to swap
     * @return fahadified word
     */
    private static String method3(StringBuilder sb, int index, String word) {
        String result = "";

        if(index<word.length() && index+1<word.length()-1){
            sb.setCharAt(0,word.charAt(index));
            sb.setCharAt(1,word.charAt(index+1));
            sb.setCharAt(index,word.charAt(0));
            sb.setCharAt(index+1,word.charAt(1));
            result = sb.toString();
        }else{
            index--;
            result = method3(sb,index,word);
        }
        return result;
    }


   /**
     * Changes the word with second method
     *
     * @param word word to convert  string builder with word to conver
     * @return fahadified word
     */
    private static String method2(StringBuilder sb,String word) {
        char temp = word.charAt(word.length()-1);
        char zeroIndex = word.charAt(0);
        sb.setCharAt(0,temp);
        sb.setCharAt(word.length()-1,zeroIndex);
        return sb.toString();
    }



    /**
     * Uses fahadifyWord for each word in the sentence to make a new sentence in Fahadian tounge
     *
     * @param sentence sentence to work with
     * @return fahadified sentence
     */
    public static String fahadifySentence(String sentence) {
      String result = "";
      String s = sentence;
      int skipper = 0;
        for (int i = 0; i <s.length() ; i++) {
            if(Character.isLetter(s.charAt(i))){
                String temp = "";
                while( s.charAt(i)!=' ' ){
                    temp = temp + s.charAt(i);
                    if(i==s.length()-1){
                        break;
                    }else{
                        i++;
                    }
                }


                result =  result + fahadifyWord(temp);
                if(i!=s.length()-1) {
                    i--;
                }
            }else{
                result = result + s.charAt(i);
            }
        }

      return result;
    }


}
