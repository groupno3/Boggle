package com.projects.sweproject.boggle;

import java.util.ArrayList;

/**
 * Created by hrishikesh on 2/26/17.
 */

public class MultiPlayerBoard {

    ArrayList<String> BoardList;
    String AllWords;



    public MultiPlayerBoard(){}

    public MultiPlayerBoard (String[] Board, String allWords){
        BoardList = new ArrayList();
        AllWords = allWords;

        for (int i =0; i<Board.length; i++){
            this.BoardList.add(Board[i]);
        }
    }
}



