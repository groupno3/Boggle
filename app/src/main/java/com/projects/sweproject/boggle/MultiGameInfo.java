package com.projects.sweproject.boggle;

import java.util.ArrayList;

/**
 * Created by hrishikesh on 3/4/17.
 */

public class MultiGameInfo {

    public String PassCode;
    public boolean GameStarted;
    public boolean Player2Joined;
    public String level;
    public ArrayList<MultiPlayerBoard> Boards;

    public MultiGameInfo(){

        Boards = new ArrayList();
        GameStarted = false;
        Player2Joined = false;
        PassCode = String.valueOf((int)(Math.random()*10))+String.valueOf((int)(Math.random()*10))+String.valueOf((int)(Math.random()*10))
                +String.valueOf(((int)Math.random()*10));
        level ="";
    }
}
