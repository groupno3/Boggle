package com.projects.sweproject.boggle;

import java.util.ArrayList;

/**
 * Created by hrishikesh on 3/7/17.
 */

public class CutThroatMode {

    public ArrayList<String> Player1TraceList;
    public ArrayList<String> Player2TraceList;

    public ArrayList<String> Player1WordList;
    public ArrayList<String> Player2WordList;

    public CutThroatMode(){

        Player1TraceList = new ArrayList();
        Player2TraceList = new ArrayList();

        Player1WordList = new ArrayList();
        Player2WordList = new ArrayList();

        Player1TraceList.add("-1");
        Player2TraceList.add("-1");

        Player1WordList.add("-1");
        Player2WordList.add("-1");


    }
}
