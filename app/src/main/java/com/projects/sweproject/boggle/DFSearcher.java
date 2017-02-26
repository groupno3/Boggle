package com.projects.sweproject.boggle;

import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by rPhilip on 2/26/17.
 */

public class DFSearcher {
    ArrayList<Node> nodeList = new ArrayList<>();
    ArrayList<String> wordList = new ArrayList<>();

    private WordDBHelper dbHelper;
    SQLiteDatabase db;

    int[][] adjMatrix = {
            {0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0},
            {0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0},
            {0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0}
    };

    public DFSearcher(String[] str) {
        int len = str.length;
        for (int i = 0; i < len; i++) {
            nodeList.add(new Node(str[i], i));
        }
    }

    public ArrayList<String> depthFirstSearch() {
        //for each node...
        for (int i = 0; i < nodeList.size(); i++) {
            dfsRecursion(nodeList.get(i).value, nodeList.get(i), new ArrayList<Node>());
        }

        return wordList;
    }

    public void dfsRecursion(String str, Node node, ArrayList<Node> pastVisitedNodes) {
        ArrayList<Node> currentVisitedNodes = new ArrayList<>(pastVisitedNodes);
        currentVisitedNodes.add(node);
        ArrayList<Node> neighbors = getNeighbors(node);

        for (Node neighbor : neighbors) {
            String currStr = str + neighbor.value;
            if (currentVisitedNodes.contains(neighbor)) {
                continue;
            }

            if (isWord(currStr)) {
                if (currStr.length() > 2) {
                    wordList.add(currStr);
                }
            }

            if (isPrefixToWord(currStr)) {
                dfsRecursion(currStr, neighbor, currentVisitedNodes);
            }
        }

    }


    public ArrayList<Node> getNeighbors(Node node) {
        ArrayList<Node> neighbors = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            if (adjMatrix[node.index][i] == 1) {
                neighbors.add(nodeList.get(i));
            }
        }
        return neighbors;
    }


    public String nodesToString(ArrayList<Node> nodeList) {
        String str = "";
        for (int i = 0; i < nodeList.size(); i++) {
            str += nodeList.get(i).value;
        }
        return str;
    }

    public boolean isWord(String str) {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader("words.txt");
            br = new BufferedReader(fr);
            String currLine;
            while((currLine = br.readLine()) != null) {
                if (currLine.equals(str)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPrefixToWord(String str) {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader("words.txt");
            br = new BufferedReader(fr);
            String currLine;
            while((currLine = br.readLine()) != null) {
                if (currLine.substring(0, Math.min(currLine.length(), str.length())).equals(str)) {
                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
