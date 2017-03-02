package com.projects.sweproject.boggle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rPhilip on 2/28/17.
 */

public class HighScoreKeeper {
    public ArrayList<String> easyNames = new ArrayList<>();
    public ArrayList<String> easyScores = new ArrayList<>();
    public ArrayList<String> mediumNames = new ArrayList<>();
    public ArrayList<String> mediumScores = new ArrayList<>();
    public ArrayList<String> hardNames = new ArrayList<>();
    public ArrayList<String> hardScores = new ArrayList<>();

    public HighScoreKeeper() {

    }


    public String displayEasyNames() {


        String players = "";
        for (String elem : easyNames) {
            players += elem + '\n';

        }

        return players;

    }


    public String displayEasyScores(){
        String scores = "";
        for (String elem : easyScores) {
            scores += elem + '\n';

        }

        return scores;

    }

    public String displayMediumNames() {


        String players = "";
        for (String elem : mediumNames) {
            players += elem + '\n';

        }

        return players;

    }


    public String displayMediumScores(){
        String scores = "";
        for (String elem : mediumScores) {
            scores += elem + '\n';
        }

        return scores;

    }
    public String displayHardNames() {
        String players = "";
        for (String elem : hardNames) {
            players += elem + '\n';

        }
        return players;
    }

    public String displayHardScores(){
        String scores = "";
        for (String elem : hardScores) {
            scores += elem + '\n';

        }

        return scores;

    }
}
