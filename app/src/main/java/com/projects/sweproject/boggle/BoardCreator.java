package com.projects.sweproject.boggle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rPhilip on 2/12/17.
 */

public class BoardCreator {
    String[] boardLayout;
    ArrayList<String> allWordsInBoard;
    BoardCreator(WordDBHelper dbHelper, String level) {
        int requiredWordCount;
        if (level.equals("Easy")) {
            requiredWordCount = 2;
        } else if (level.equals("Medium")) {
            requiredWordCount = 5;
        } else {
            requiredWordCount = 7;
        }

        //real boggle dice possibilities
        String str0[] = {"r", "i", "f", "o", "b", "x"};
        String str1[] = {"i", "f", "e", "h", "e", "y"};
        String str2[] = {"d", "e", "n", "o", "w", "s"};
        String str3[] = {"u", "t", "o", "k", "n", "d"};
        String str4[] = {"h", "m", "s", "r", "a", "o"};
        String str5[] = {"l", "u", "p", "e", "t", "s"};
        String str6[] = {"a", "c", "i", "t", "o", "a"};
        String str7[] = {"y", "l", "g", "k", "u", "e"};
        String str8[] = {"qu", "b", "m", "j", "o", "a"};
        String str9[] = {"e", "h", "i", "s", "p", "n"};
        String str10[] = {"v", "e", "t", "i", "g", "n"};
        String str11[] = {"b", "a", "l", "i", "y", "t"};
        String str12[] = {"e", "z", "a", "v", "n", "d"};
        String str13[] = {"r", "a", "l", "e", "s", "c"};
        String str14[] = {"u", "w", "i", "l", "r", "g"};
        String str15[] = {"p", "a", "c", "e", "m", "d"};

        //put dice into a list
        List<BoggleDice> boggleList = new ArrayList<>();
        boggleList.add(new BoggleDice(str0));
        boggleList.add(new BoggleDice(str1));
        boggleList.add(new BoggleDice(str2));
        boggleList.add(new BoggleDice(str3));
        boggleList.add(new BoggleDice(str4));
        boggleList.add(new BoggleDice(str5));
        boggleList.add(new BoggleDice(str6));
        boggleList.add(new BoggleDice(str7));
        boggleList.add(new BoggleDice(str8));
        boggleList.add(new BoggleDice(str9));
        boggleList.add(new BoggleDice(str10));
        boggleList.add(new BoggleDice(str11));
        boggleList.add(new BoggleDice(str12));
        boggleList.add(new BoggleDice(str13));
        boggleList.add(new BoggleDice(str14));
        boggleList.add(new BoggleDice(str15));

        do {
            //shuffle the dice
            Collections.shuffle(boggleList);

            //"roll" the dice and put the result into an array
            String output[] = new String[16];
            for (int i = 0; i < 16; i++) {
                output[i] = (boggleList.get(i)).rollDice();
            }

            this.boardLayout = output;

            DFSearcher searcher = new DFSearcher(this.boardLayout, dbHelper);
            this.allWordsInBoard = searcher.depthFirstSearch();
        } while (allWordsInBoard.size() < requiredWordCount);

    }

    public String[] getBoardLayout() {
        return this.boardLayout;
    }

    public ArrayList<String> getWords() {
        return this.allWordsInBoard;
    }

    public String getAllWordsInString() {
        Set<String> uniqueWords = new LinkedHashSet<>(allWordsInBoard);
        String returnStr = "";

        for(String word : uniqueWords) {
            returnStr += word + " ";
        }

        return returnStr;
    }

}
