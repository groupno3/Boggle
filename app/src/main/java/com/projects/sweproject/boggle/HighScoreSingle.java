package com.projects.sweproject.boggle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by emenp on 3/10/2017.
 */

public class HighScoreSingle extends Activity {


    TextView easyHighScoresNames;
    TextView easyHighScores;
    TextView mediumHighScoresNames;
    TextView mediumHighScores;
    TextView hardHighScoresNames;
    TextView hardHighScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.highscore_single);


    HighScoreDBHelper scoreDBHelper = new HighScoreDBHelper(getApplicationContext());
    HighScoreKeeper keeper = scoreDBHelper.getTopFiveScoreboard();



    easyHighScoresNames = (TextView) findViewById(R.id.easyNames);
    easyHighScores = (TextView) findViewById(R.id.easyScores);

    easyHighScoresNames.setText(keeper.displayEasyNames());
    easyHighScores.setText(keeper.displayEasyScores());

    mediumHighScoresNames = (TextView) findViewById(R.id.mediumNames);
    mediumHighScores = (TextView) findViewById(R.id.mediumScores);

    mediumHighScoresNames.setText(keeper.displayMediumNames());
    mediumHighScores.setText(keeper.displayMediumScores());

    hardHighScoresNames = (TextView) findViewById(R.id.hardNames);
    hardHighScores = (TextView) findViewById(R.id.hardScores);

    hardHighScoresNames.setText(keeper.displayHardNames());
    hardHighScores.setText(keeper.displayHardScores());


    }

}
