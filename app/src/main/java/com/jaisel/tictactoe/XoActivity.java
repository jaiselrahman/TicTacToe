package com.jaisel.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.jaisel.tictactoe.Utils.Job;
import com.jaisel.tictactoe.Utils.OnJobDoneListener;
import com.jaisel.tictactoe.app.TicTacToeController;
import com.jaisel.tictactoe.opponent.Computer;
import com.jaisel.tictactoe.opponent.Opponent;
import com.jaisel.tictactoe.opponent.Player;

import static com.jaisel.tictactoe.TicTacToe.NowPlaying.NONE;
import static com.jaisel.tictactoe.TicTacToe.NowPlaying.OPPONENT;
import static com.jaisel.tictactoe.TicTacToe.NowPlaying.PLAYER;

public class XoActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PLAYER_TYPE = "PLAYER_TYPE";
    public static final String PLAYER_ID = "PLAYER_ID";
    public static final String PLAYER_NAME = "PLAYER_NAME";
    public static final String PLAYER_TURN = "PLAYER_TURN";
    public static final String TYPE_PLAYER = "PLAYER";
    public static final String TYPE_COMPUTER = "COMPUTER";

    private static final String TAG = XoActivity.class.getSimpleName();
    private static TicTacToe.NowPlaying nowPlaying = NONE;

    private TicTacToe ticTacToe = new TicTacToe();
    private AlertDialog.Builder chooseXO;
    private int position = 0;
    private Opponent opponent;
    private Button board[] = new Button[10];
    private Button reset;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xoboard);

        TextView statusHeader = findViewById(R.id.xo_status_header);
        statusText = findViewById(R.id.xo_status_text);

        Bundle b = getIntent().getExtras();
        if (b != null && b.getString(PLAYER_TYPE, TYPE_COMPUTER).equals(TYPE_PLAYER)) {
            statusHeader.setText(R.string.status);
            opponent = initPlayer(b);
        } else {
            statusHeader.setText("");
            opponent = initComputer();
        }

        opponent.getMove().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer move) {
                if (isPlaying()) {
                    makeOpponentMove(move);
                    statusText.setText(getString(R.string.your_turn));
                }
            }
        });

        opponent.getStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String status) {
                if (status.equals(TicTacToeController.RESET_GAME)) {
                    statusText.setText(String.format(getString(R.string.resetted_game), opponent.getName()));
                    resetGame();
                    reset.setText(getString(R.string.start));
                } else if (status.equals(TicTacToeController.START_GAME)) {
                    resetGame();
                    reset.setText(getString(R.string.reset));
                    String gameStatus = String.format(getString(R.string.started_game), opponent.getName());
                    if (nowPlaying == TicTacToe.NowPlaying.PLAYER)
                        statusText.setText(gameStatus + "\n" + getString(R.string.your_turn));
                    else if (nowPlaying == TicTacToe.NowPlaying.OPPONENT)
                        statusText.setText(gameStatus + "\n" + String.format(getString(R.string.turn), opponent.getName()));
                }
            }
        });

        board[1] = findViewById(R.id.TL);
        board[2] = findViewById(R.id.TM);
        board[3] = findViewById(R.id.TR);
        board[4] = findViewById(R.id.ML);
        board[5] = findViewById(R.id.MM);
        board[6] = findViewById(R.id.MR);
        board[7] = findViewById(R.id.BL);
        board[8] = findViewById(R.id.BM);
        board[9] = findViewById(R.id.BR);

        for (int i = 1; i < 10; i++) {
            board[i].setOnClickListener(this);
            board[i].setText(String.valueOf(ticTacToe.getSymbolAt(i)));
        }

        reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetStartClick();
            }
        });

        Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClick();
            }
        });
    }

    public Opponent initComputer() {
        DialogInterface.OnClickListener chooseXOListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ticTacToe.setSymbol('O', 'X');
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        ticTacToe.setSymbol('X', 'O');
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        finish();
                        return;
                }
                if (ticTacToe.chooseFirst() == TicTacToe.NowPlaying.OPPONENT) {
                    Toast.makeText(XoActivity.this.getApplicationContext(), getString(R.string.opp_goes_first), Toast.LENGTH_SHORT).show();
                    nowPlaying = TicTacToe.NowPlaying.OPPONENT;
                    //To initiate Opponent move
                    opponent.setOpponentMove(0, null);
                } else {
                    Toast.makeText(XoActivity.this.getApplicationContext(), getString(R.string.you_goes_first), Toast.LENGTH_SHORT).show();
                    nowPlaying = TicTacToe.NowPlaying.PLAYER;
                }
            }
        };

        chooseXO = new AlertDialog.Builder(XoActivity.this);
        chooseXO.setMessage(getString(R.string.select_x_o))
                .setPositiveButton(getString(R.string.o), chooseXOListener)
                .setNegativeButton(getString(R.string.x), chooseXOListener)
                .setNeutralButton(getString(R.string.exit), chooseXOListener)
                .setCancelable(false)
                .show();
        return new Computer(ticTacToe);
    }

    public Opponent initPlayer(Bundle b) {
        final String id = b.getString(PLAYER_ID, "");
        if (TextUtils.isEmpty(id)) {
            Log.d(TAG, "PLAYER_ID Empty");
            finish();
        }

        final String name = b.getString(PLAYER_NAME);
        final int turn = b.getInt(PLAYER_TURN);

        opponent = new Player(this, id, name);

        if (turn == 2) {
            Toast.makeText(getApplication(), String.format(getString(R.string.goes_first), opponent.getName()), Toast.LENGTH_SHORT).show();
            statusText.setText(String.format(getString(R.string.turn), name));
            nowPlaying = TicTacToe.NowPlaying.OPPONENT;
            ticTacToe.setSymbol('O', 'X');
        } else {
            Toast.makeText(getApplication(), R.string.you_goes_first, Toast.LENGTH_SHORT).show();
            statusText.setText(getString(R.string.your_turn));
            nowPlaying = TicTacToe.NowPlaying.PLAYER;
            ticTacToe.setSymbol('X', 'O');
        }
        return opponent;
    }

    @Override
    public void onClick(View p1) {
        if (nowPlaying == PLAYER) {
            switch (p1.getId()) {
                case R.id.TL:
                    position = 1;
                    break;
                case R.id.TM:
                    position = 2;
                    break;
                case R.id.TR:
                    position = 3;
                    break;
                case R.id.ML:
                    position = 4;
                    break;
                case R.id.MM:
                    position = 5;
                    break;
                case R.id.MR:
                    position = 6;
                    break;
                case R.id.BL:
                    position = 7;
                    break;
                case R.id.BM:
                    position = 8;
                    break;
                case R.id.BR:
                    position = 9;
                    break;
            }
            toggleText(position);
        }
    }

    private void onConfirmClick() {
        if (ticTacToe.makePlayerMove(position)) {
            toggleText(position);

            if (ticTacToe.isWinner(ticTacToe.getPlayer())) {
                showWinner();
            } else if (ticTacToe.isFull()) {
                Toast.makeText(getApplication(), getString(R.string.game_tie), Toast.LENGTH_SHORT).show();
                showTie();
            } else {
                nowPlaying = OPPONENT;
            }

            statusText.setText(getString(R.string.sending));
            opponent.setOpponentMove(position, new OnJobDoneListener<Void>() {
                @Override
                public void onComplete(Job<Void> job) {
                    statusText.setText(String.format(getString(R.string.turn), opponent.getName()));
                }
            });
        }
    }

    private void onResetStartClick() {
        if (reset.getText().equals(getString(R.string.reset))) {
            DialogInterface.OnClickListener gameResetListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            resetGame();
                            reset.setText(getString(R.string.start));
                            opponent.setOpponentStatus(TicTacToeController.RESET_GAME, new OnJobDoneListener<Void>() {
                                @Override
                                public void onComplete(Job<Void> job) {
                                    statusText.setText(R.string.you_resetted_game);
                                }
                            });
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            new AlertDialog.Builder(this).setMessage(getString(R.string.exit_confirmation))
                    .setPositiveButton(getString(R.string.yes), gameResetListener)
                    .setNegativeButton(getString(R.string.no), gameResetListener)
                    .show();
        } else {
            resetGame();

            if (opponent.getType() == Opponent.COMPUTER)
                chooseXO.show();

            opponent.setOpponentStatus(TicTacToeController.START_GAME, new OnJobDoneListener<Void>() {
                @Override
                public void onComplete(Job<Void> job) {
                    statusText.setText(R.string.you_started_game);
                }
            });

            reset.setText(getString(R.string.reset));
        }
    }

    private void makeOpponentMove(int move) {
        if (ticTacToe.makeOpponentMove(move)) {
            toggleText(move);
            if (ticTacToe.isWinner(ticTacToe.getOpponent())) {
                showWinner();
            } else if (ticTacToe.isFull()) {
                showTie();
            } else {
                nowPlaying = PLAYER;
            }
        }
    }

    private void resetGame() {
        finishGame();
        toggleText(0);
    }

    private void toggleText(int pos) {
        for (int i = 1; i < 10; i++) {
            if (i == pos && ticTacToe.isFree(pos))
                board[i].setText(String.valueOf(ticTacToe.getPlayer()));
            else
                board[i].setText(String.valueOf(ticTacToe.getSymbolAt(i)));
        }
    }

    private void finishGame() {
        ticTacToe.clear();
        position = 0;
        nowPlaying = NONE;
        reset.setText(getString(R.string.start));
    }

    @Override
    public void onBackPressed() {
        if (isPlaying()) {
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
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.exit_confirmation))
                    .setPositiveButton(getString(R.string.yes), gameExitListener)
                    .setNegativeButton(getString(R.string.no), gameExitListener)
                    .show();
        } else {
            finish();
        }
    }

    private void showWinner() {
        if (nowPlaying == OPPONENT) {
            Toast.makeText(this, getString(R.string.game_lost, opponent.getName()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplication(), getString(R.string.won_the_game), Toast.LENGTH_SHORT).show();
        }
        finishGame();
    }

    private void showTie() {
        Toast.makeText(this, getString(R.string.game_tie), Toast.LENGTH_SHORT).show();
        finishGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nowPlaying = NONE;
    }

    public static boolean isPlaying() {
        return nowPlaying != NONE;
    }
}