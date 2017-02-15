package com.projects.sweproject.boggle;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by rPhilip on 2/12/17.
 */

public class BoggleDice {
    String[] diceSides = new String[6];
    BoggleDice(String[] sides) {
        this.diceSides = sides;
    }

    public String rollDice() {
        int randomSide = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            randomSide = ThreadLocalRandom.current().nextInt(0, 6);
        }
        return diceSides[randomSide];
    }

}
