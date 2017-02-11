package com.projects.sweproject.boggle;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class spNewGame extends AppCompatActivity {

    int[][] matrix = {{R.id.Point00, R.id.Point01, R.id.Point02, R.id.Point03},
            {R.id.Point10, R.id.Point11, R.id.Point12, R.id.Point13},
            {R.id.Point20, R.id.Point21, R.id.Point22, R.id.Point23},
            {R.id.Point30, R.id.Point31, R.id.Point32, R.id.Point33}};

    Point[][] lMatrix;
    int[][] touchPath;
    int gridX,gridY;
    int viewHeight;
    int viewWidth;
    int offset;


    String [][] board;

    private LinearLayout main;
    private SquareTextView sq;
    private TextView wordIn;
    private String letter, word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        //init
        board = new String[4][4];
        lMatrix = new Point[4][4];
        touchPath = new int[4][4];
        viewHeight = 0;
        viewWidth = 0;
        wordIn = (TextView)findViewById(R.id.WordView);

        //timer

        final TextView a = (TextView) findViewById(R.id.timer);

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                a.setText("Time remaining: " + ((millisUntilFinished/1000)/60)  + ":"+ String.format("%02d", ((millisUntilFinished/1000)%60)));
            }

            public void onFinish() {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(spNewGame.this);
                alertDialog.setTitle("GAME OVER");
                alertDialog.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
                alertDialog.setNegativeButton("Play Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                alertDialog.create();
                alertDialog.show();

            }
        }.start();

        //Touch grid
        main = (LinearLayout) findViewById(R.id.MainLayout);
        main.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        SquareLayout grid = (SquareLayout) findViewById(R.id.SquareLayout);
                        gridX = (int)grid.getX();

                        sq = (SquareTextView) findViewById(matrix[0][0]);
                        viewWidth = sq.getWidth();
                        viewHeight = sq.getHeight();
                        offset = (viewWidth * 2) / 6;

                        int x;
                        int y = 0;
                        for(int i = 0; i < 4; ++i){
                            x = 0;
                            for(int j = 0; j < 4; ++j){
                                lMatrix[i][j] = new Point(x, y);
                                x = x + viewWidth;
                            }
                            y = y + viewHeight;
                        }
                    }
                });

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            gridY =
                    TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        gridY += getResources().getIdentifier("status_bar_height", "dimen", "android");

        //start
        startGame();
    }

    private void startGame(){
        //clear
        word = "";
        wordIn.setText(word);
        //gen board
        for(int i=0;i<4;++i){
            for(int j=0;j<4;++j){
                sq = (SquareTextView) findViewById(matrix[i][j]);
                touchPath[i][j] = 0;
                //This can be changed anytime.
                board[i][j] = "a";

                sq.setText(board[i][j], TextView.BufferType.EDITABLE);
            }
        }

    }

    private void track(int x, int y){
        int pointX;
        int pointY;

        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                pointX = lMatrix[i][j].x + gridX;
                pointY = lMatrix[i][j].y + gridY;

                if(x > pointX + offset && x < pointX + viewWidth - offset){
                    if(y > pointY + offset && y < pointY + viewHeight -offset){
                        if(touchPath[i][j] == 0){

                            sq = (SquareTextView) findViewById(matrix[i][j]);
                            letter = sq.getText().toString();

                            //highlight

                            word = word + letter;
                            wordIn.setText(word);
                            wordIn.setGravity(Gravity.LEFT);

                            //un highlight

                            touchPath[i][j] = 1;
                        }
                    }
                }
            }
        }
    }

    public void submit(){
        //do something with input word here.
    }

    public boolean onTouchEvent(MotionEvent event) {

        int X = (int) event.getX();
        int Y = (int) event.getY();
        int EA = event.getAction();
        switch (EA) {
            case MotionEvent.ACTION_DOWN:
                track(X, Y);
                Toast.makeText(this, "ACTION_DOWN "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                break;

            case MotionEvent.ACTION_MOVE:
                track(X, Y);
                Toast.makeText(this, "ACTION_MOVE "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                break;

            case MotionEvent.ACTION_UP:
                submit();
                break;
        }

        return true;
    }
}
