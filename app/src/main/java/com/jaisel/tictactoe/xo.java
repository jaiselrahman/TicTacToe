package com.jaisel.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class xo extends Activity implements Button.OnClickListener {
    private AlertDialog.Builder chooseXO;
    private AlertDialog.Builder gameClear;
    private static ttt t = new ttt();
    private char player = 'P';
    public static char player2 = 'C';
    boolean isReset = false;
    private String secondPlayerType, firstPlayerName, secondPlayerName;
    private Server S = new Server("http://0.0.0.0:8080/app.php");
    private Player P;
    private boolean isPlaying = false;
    private static Button board[] = new Button[10];
    private static Button reset, confirm;
    static int pos = 0;
    private landscape_fragment lf = new landscape_fragment();
    private portrait_fragment pf = new portrait_fragment();
    static View.OnClickListener OnClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager FM = getFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();
        Configuration newConfig = getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            FT.replace(android.R.id.content, lf);
        else
            FT.replace(android.R.id.content, pf);
        FT.commit();
        DialogInterface.OnClickListener chooseXOListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        t.playerLetter = 'O';
                        t.computerLetter = 'X';
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        t.playerLetter = 'X';
                        t.computerLetter = 'O';
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        finish();
                        return;
                }
                player = t.chooseFirst();
                isPlaying = true;
                S.gameStart();
                if (player2 == 'O') {
                    P = new Opponent();
                    secondPlayerType = "Opponent";
                } else {
                    P = new Computer(t);
                    secondPlayerType = "Computer";
                }
                if (player == 'Q') {
                    Toast.makeText(xo.this.getApplicationContext(), secondPlayerType + " goes first !", Toast.LENGTH_SHORT).show();
                    playSecondPlayer();
                } else if (player == 'P')
                    Toast.makeText(xo.this.getApplicationContext(), "You goes first !", Toast.LENGTH_SHORT).show();
            }
        };
        chooseXO = new AlertDialog.Builder(xo.this);
        chooseXO.setMessage("Select X or O")
                .setPositiveButton("O", chooseXOListener)
                .setNegativeButton("X", chooseXOListener)
                .setNeutralButton("Exit", chooseXOListener)
                .setCancelable(false);
        gameClear = new AlertDialog.Builder(xo.this);
        OnClickListener = this;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        FragmentManager FM = getFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            FT.replace(android.R.id.content, new landscape_fragment());
        else
            FT.replace(android.R.id.content, new portrait_fragment());
        FT.commit();
        super.onConfigurationChanged(newConfig);
    }

    public static class landscape_fragment extends Fragment {
        public landscape_fragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.xoboard_landscape, container, false);
            board[1] = (Button) v.findViewById(R.id.TL);
            board[2] = (Button) v.findViewById(R.id.TM);
            board[3] = (Button) v.findViewById(R.id.TR);
            board[4] = (Button) v.findViewById(R.id.ML);
            board[5] = (Button) v.findViewById(R.id.MM);
            board[6] = (Button) v.findViewById(R.id.MR);
            board[7] = (Button) v.findViewById(R.id.BL);
            board[8] = (Button) v.findViewById(R.id.BM);
            board[9] = (Button) v.findViewById(R.id.BR);
            for (int i = 1; i < 10; i++) {
                board[i].setOnClickListener(OnClickListener);
            }
            toggleText(board, 0);
            reset = (Button) v.findViewById(R.id.reset);
            reset.setOnClickListener(OnClickListener);
            confirm = (Button) v.findViewById(R.id.confirm);
            confirm.setOnClickListener(OnClickListener);
            return v;
        }
    }

    public static class portrait_fragment extends Fragment {
        public portrait_fragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.xoboard, container, false);
            board[1] = (Button) v.findViewById(R.id.TL);
            board[2] = (Button) v.findViewById(R.id.TM);
            board[3] = (Button) v.findViewById(R.id.TR);
            board[4] = (Button) v.findViewById(R.id.ML);
            board[5] = (Button) v.findViewById(R.id.MM);
            board[6] = (Button) v.findViewById(R.id.MR);
            board[7] = (Button) v.findViewById(R.id.BL);
            board[8] = (Button) v.findViewById(R.id.BM);
            board[9] = (Button) v.findViewById(R.id.BR);
            for (int i = 1; i < 10; i++) {
                board[i].setOnClickListener(OnClickListener);
            }
            toggleText(board, 0);
            reset = (Button) v.findViewById(R.id.reset);
            reset.setOnClickListener(OnClickListener);
            confirm = (Button) v.findViewById(R.id.confirm);
            confirm.setOnClickListener(OnClickListener);
            return v;
        }
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener gameExitListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        gameClear.setMessage("Are you sure?\nThe current game will be lost.")
                .setPositiveButton("Yes", gameExitListener)
                .setNegativeButton("No", gameExitListener);
        if (isPlaying)
            gameClear.show();
        else
            finish();
    }

    void playSecondPlayer() {
        int move = -1;
        while (move == -1) {
            move = P.getmove();
        }
        t.makeMove(t.board, move, t.computerLetter);
        if (move != 0) board[move].setText(String.valueOf(t.computerLetter));
        if (t.isWinner(t.board, t.computerLetter)) {
            Toast.makeText(xo.this.getApplicationContext(), secondPlayerType + " has beaten you\nYou loose!", Toast.LENGTH_SHORT).show();
            isPlaying = false;
            S.gameFinish();
        } else if (t.isFull()) {
            Toast.makeText(xo.this.getApplicationContext(), "The Game is a tie !", Toast.LENGTH_SHORT).show();
            isPlaying = false;
            S.gameFinish();
        } else
            player = 'P';
    }

    public static void toggleText(Button board[], int _pos) {
        if (t.isFree(_pos)) {
            if (board[_pos].getText().charAt(0) == '-') {
                board[_pos].setText(String.valueOf(t.playerLetter));
            } else {
                board[_pos].setText("-");
                pos = 0;
            }
        }
        for (int i = 1; i < 10; i++) {
            if (i != _pos)
                if (t.board[i] == ' ')
                    board[i].setText("-");
                else
                    board[i].setText(String.valueOf(t.board[i]));
        }
    }

    @Override
    public void onClick(View p1) {
        if (p1.getId() == R.id.reset) {
            if (isReset) {
                DialogInterface.OnClickListener gameResetListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                t.board = new char[]{'-', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
                                toggleText(board, 0);
                                reset.setText("Start");
                                isReset = false;
                                isPlaying = false;
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                if (isPlaying) {
                    gameClear.setMessage("Are you sure?\nThe current game will be lost.")
                            .setPositiveButton("Yes", gameResetListener)
                            .setNegativeButton("No", gameResetListener)
                            .show();
                    return;
                }
                t.board = new char[]{'-', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
                toggleText(board, 0);
                reset.setText("Start");
                isReset = false;
                isPlaying = false;
            } else {
                chooseXO.show();
                reset.setText("Reset");
                isReset = true;
            }
        }
        if (isPlaying && player == 'P') {
            switch (p1.getId()) {
                case R.id.TL:
                    pos = 1;
                    toggleText(board, pos);
                    break;
                case R.id.TM:
                    pos = 2;
                    toggleText(board, pos);
                    break;
                case R.id.TR:
                    pos = 3;
                    toggleText(board, pos);
                    break;
                case R.id.ML:
                    pos = 4;
                    toggleText(board, pos);
                    break;
                case R.id.MM:
                    pos = 5;
                    toggleText(board, pos);
                    break;
                case R.id.MR:
                    pos = 6;
                    toggleText(board, pos);
                    break;
                case R.id.BL:
                    pos = 7;
                    toggleText(board, pos);
                    break;
                case R.id.BM:
                    pos = 8;
                    toggleText(board, pos);
                    break;
                case R.id.BR:
                    pos = 9;
                    toggleText(board, pos);
                    break;
                case R.id.confirm:
                    if (t.isFree(pos)) {
                        t.makeMove(t.board, pos, t.playerLetter);
                        P.setmove(pos);
                        board[pos].setText(String.valueOf(t.playerLetter));
                        if (t.isWinner(t.board, t.playerLetter)) {
                            Toast.makeText(xo.this.getApplicationContext(), "You won the game !", Toast.LENGTH_SHORT).show();
                            isPlaying = false;
                            S.gameFinish();
                        } else if (t.isFull()) {
                            Toast.makeText(xo.this.getApplicationContext(), "The Game is a tie !", Toast.LENGTH_SHORT).show();
                            isPlaying = false;
                            S.gameFinish();
                        } else
                            player = 'Q';
                    }
                    break;
            }
        }
        if (isPlaying && player == 'Q') {
            playSecondPlayer();
        }
    }

    private abstract class Player {
        abstract int getmove();

        abstract void setmove(int m);
    }

    private class Computer extends Player {
        private ttt t;

        Computer(ttt _t) {
            t = _t;
        }

        int getmove() {
            return t.compMove();
        }

        void setmove(int m) {
        }
    }

    private class Opponent extends Player {
        int move;

        int getmove() {
            return move = S.getmove();
        }

        void setmove(int m) {
            S.setmove(m);
        }
    }

    static void init(View v) {
        Button board[] = new Button[10];
        board[1] = (Button) v.findViewById(R.id.TL);
        board[2] = (Button) v.findViewById(R.id.TM);
        board[3] = (Button) v.findViewById(R.id.TR);
        board[4] = (Button) v.findViewById(R.id.ML);
        board[5] = (Button) v.findViewById(R.id.MM);
        board[6] = (Button) v.findViewById(R.id.MR);
        board[7] = (Button) v.findViewById(R.id.BL);
        board[8] = (Button) v.findViewById(R.id.BM);
        board[9] = (Button) v.findViewById(R.id.BR);
        for (int i = 1; i < 10; i++) {
            board[i].setOnClickListener(OnClickListener);
        }
        toggleText(board, 0);
        Button reset = (Button) v.findViewById(R.id.reset);
        reset.setOnClickListener(OnClickListener);
        Button confirm = (Button) v.findViewById(R.id.confirm);
        confirm.setOnClickListener(OnClickListener);

    }

}
