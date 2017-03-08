package com.projects.sweproject.boggle;

import java.util.ArrayList;

/**
 * Created by hrishikesh on 3/4/17.
 */

public class MultiGameInfo {

    public String PassCode;
    public boolean BoardStarted;
    public boolean Player2Joined;
    public String level;

    public int Player1Score;
    public int Player2Score;

    public String Mode;
    public ArrayList<MultiPlayerBoard> Boards;
    public CutThroatMode CTMData;



    public MultiGameInfo(){

        Boards = new ArrayList();
        CTMData = new CutThroatMode();

        BoardStarted = false;
        Player2Joined = false;
        PassCode = String.valueOf((int)(Math.random()*10))+String.valueOf((int)(Math.random()*10))+String.valueOf((int)(Math.random()*10))
                +String.valueOf(((int)Math.random()*10));
        level ="";
        Player1Score=0;
        Player2Score=0;

        Mode="";

    }
}
