package com.projects.sweproject.boggle;

/**
 * Created by rPhilip on 2/26/17.
 */

public class Node {
    String value;
    boolean isVisited;
    int index;

    public Node(String value, int index) {
        this.value = value;
        this.index = index;
    }
}
