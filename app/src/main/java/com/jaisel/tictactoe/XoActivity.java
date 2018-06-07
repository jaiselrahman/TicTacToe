package com.jaisel.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jaisel.tictactoe.Utils.UserAccount;

public class XoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = XoActivity.class.getSimpleName();
    private static final String RESET_GAME = "RESET_GAME";
    private static final String START_GAME = "START_GAME";

    private TicTacToe mTicTacToe = new TicTacToe();
    private AlertDialog.Builder chooseXO;
    private int mPosition = 0;
    static boolean isPlaying = false;
    private Opponent mOpponent;
    private TicTacToe.PlayerType nowPlaying;
    private Button mBoard[] = new Button[10];
    private Button mReset;
    private TextView mStatusText;

    private DocumentReference myDocRef, opponentDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xoboard);

        TextView mStatusHeader = (TextView) findViewById(R.id.xo_status_header);
        mStatusText = (TextView) findViewById(R.id.xo_status_text);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            for (String key : b.keySet()) {
                Log.d(TAG, "key " + key + ": value " + b.get(key));
            }
            if (b.getBoolean("PLAY")
                    && b.getString("PLAYER_TYPE", "").equals("PLAYER")) {
                String userid = b.getString("PLAYER_ID","");
                if(TextUtils.isEmpty(userid)) {
                    Log.d(TAG, "PLAYER_UID Empty");
                    finish();
                }

                myDocRef = UserAccount.getInstance().getMyDocRef();
                opponentDocRef = UserAccount.getUserDocRef(userid);
                opponentDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if(e != null && documentSnapshot != null ) {
                            mOpponent.setMove(documentSnapshot.getLong("lastmove").intValue());
                            makeOpponentMove();
                            mStatusText.setText(getString(R.string.your_turn));

                            String status = documentSnapshot.getString("status");
                            if (status.equals(RESET_GAME)) {
                                mStatusText.setText(String.format(getString(R.string.resetted_game), mOpponent.getName()));
                                finishGame();
                                toggleText(0);
                                mReset.setText(getString(R.string.start));
                            } else if (status.equals(START_GAME)) {
                                String gameStatus = String.format(getString(R.string.started_game), mOpponent.getName());
                                isPlaying = true;
                                mReset.setText(getString(R.string.reset));
                                if (nowPlaying == TicTacToe.PlayerType.player)
                                    mStatusText.setText(gameStatus + "\n" + getString(R.string.your_turn));
                                else if (nowPlaying == TicTacToe.PlayerType.opponent)
                                    mStatusText.setText(gameStatus + "\n" + String.format(getString(R.string.turn), mOpponent.getName()));
                            }
                        }
                    }
                });

                mStatusHeader.setText(R.string.status);
                final String id = b.getString("PLAYER_ID");
                final String name = b.getString("PLAYER_NAME");
                final int turn = b.getInt("PLAYER_TURN");
                // TODO: Accepting play request
//                        if(success){
//                            mOpponent = new Player(id, name);
//                            if(turn == 2){
//                                Toast.makeText(getApplication(), String.format(getString(R.string.goes_first), mOpponent.getName()), Toast.LENGTH_SHORT).show();
//                                mStatusText.setText(String.format(getString(R.string.turn),name));
//                                nowPlaying = TicTacToe.PlayerType.opponent;
//                                mTicTacToe.setSymbol('O', 'X');
//                            } else {
//                                Toast.makeText(getApplication(), R.string.you_goes_first, Toast.LENGTH_SHORT).show();
//                                mStatusText.setText(getString(R.string.your_turn));
//                                nowPlaying = TicTacToe.PlayerType.player;
//                                mTicTacToe.setSymbol('X', 'O');
//                            }
//                            isPlaying = true;
//                        } else {
//                            Toast.makeText(getApplication(),"Starting Game Failed", Toast.LENGTH_SHORT).show();
//                        }
//
            } else {
                mStatusHeader.setText("");
                DialogInterface.OnClickListener chooseXOListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mTicTacToe.setSymbol('O', 'X');
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                mTicTacToe.setSymbol('X', 'O');
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                finish();
                                return;
                        }
                        if (mTicTacToe.chooseFirst() == TicTacToe.PlayerType.opponent) {
                            Toast.makeText(XoActivity.this.getApplicationContext(), getString(R.string.opp_goes_first), Toast.LENGTH_SHORT).show();
                            nowPlaying = TicTacToe.PlayerType.opponent;
                            makeOpponentMove();
                        } else {
                            Toast.makeText(XoActivity.this.getApplicationContext(), getString(R.string.you_goes_first), Toast.LENGTH_SHORT).show();
                            nowPlaying = TicTacToe.PlayerType.player;
                        }
                        isPlaying = true;
                    }
                };

                chooseXO = new AlertDialog.Builder(XoActivity.this);
                chooseXO.setMessage(getString(R.string.select_x_o))
                        .setPositiveButton(getString(R.string.o), chooseXOListener)
                        .setNegativeButton(getString(R.string.x), chooseXOListener)
                        .setNeutralButton(getString(R.string.exit), chooseXOListener)
                        .setCancelable(false)
                        .show();
                mOpponent = new Computer(mTicTacToe);
            }
        }

        mBoard[1] = (Button) findViewById(R.id.TL);
        mBoard[2] = (Button) findViewById(R.id.TM);
        mBoard[3] = (Button) findViewById(R.id.TR);
        mBoard[4] = (Button) findViewById(R.id.ML);
        mBoard[5] = (Button) findViewById(R.id.MM);
        mBoard[6] = (Button) findViewById(R.id.MR);
        mBoard[7] = (Button) findViewById(R.id.BL);
        mBoard[8] = (Button) findViewById(R.id.BM);
        mBoard[9] = (Button) findViewById(R.id.BR);

        for (int i = 1; i < 10; i++) {
            mBoard[i].setOnClickListener(this);
            mBoard[i].setText(String.valueOf(mTicTacToe.getSymbolAt(i)));
        }

        mReset = (Button) findViewById(R.id.reset);
        mReset.setOnClickListener(this);
        Button mConfirm = (Button) findViewById(R.id.confirm);
        mConfirm.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private class MoveReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals(INTENT_RECEIVE_MOVE)) {
//                Bundle b = intent.getExtras();
//                Log.d(TAG, "MoveReceiver");
//                for(String key : b.keySet()){
//                    Log.d(TAG, "key " + key + ": value " + b.get(key));
//                }
//                if (b.getBoolean("MOVE_DATA")) {
//                    String userid = b.getString("USERID");
//                    if (userid.equals(mOpponent.getId())) {
//                        int move = b.getInt("MOVE");
//                        mOpponent.setMove(move);
//                        makeOpponentMove();
//                        mStatusText.setText(getString(R.string.your_turn));
//                    }
//                } else if (b.getBoolean("RESET_GAME")){
//                    mStatusText.setText(String.format(getString(R.string.resetted_game), mOpponent.getName()));
//                    finishGame();
//                    toggleText(0);
//                    mReset.setText(getString(R.string.start));
//                } else if (b.getBoolean("START_GAME")){
//                    String status = String.format(getString(R.string.started_game), mOpponent.getName());
//                    isPlaying = true;
//                    mReset.setText(getString(R.string.reset));
//                    if(nowPlaying == TicTacToe.PlayerType.player)
//                        mStatusText.setText(status +"\n" + getString(R.string.your_turn));
//                    else if (nowPlaying == TicTacToe.PlayerType.opponent)
//                        mStatusText.setText(status + "\n" + String.format(getString(R.string.turn), mOpponent.getName()));
//                }
//            }
//        }
//    }

    @Override
    public void onClick(View p1) {
        if (p1.getId() == R.id.reset) {
            if (mReset.getText().equals(getString(R.string.reset))) {
                if (isPlaying) {
                    DialogInterface.OnClickListener gameResetListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    finishGame();
                                    toggleText(0);
                                    mReset.setText(getString(R.string.start));
                                    if(mOpponent.getType() == Opponent.PLAYER){
                                        myDocRef.update("status", RESET_GAME).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mStatusText.setText(R.string.you_resetted_game);
                                            }
                                        });
                                    }
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
                    mReset.setText(getString(R.string.start));
                    finishGame();
                    toggleText(0);
                }
            } else {
                if(mOpponent.getType() == Opponent.COMPUTER)
                    chooseXO.show();
                else if(mOpponent.getType() == Opponent.PLAYER){
                    myDocRef.update("status",START_GAME).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mStatusText.setText(R.string.you_started_game);
                        }
                    });
                }
                mReset.setText(getString(R.string.reset));
                isPlaying = true;
            }
        }

        if (isPlaying && nowPlaying == TicTacToe.PlayerType.player) {
            switch (p1.getId()) {
                case R.id.TL:
                    mPosition = 1;
                    break;
                case R.id.TM:
                    mPosition = 2;
                    break;
                case R.id.TR:
                    mPosition = 3;
                    break;
                case R.id.ML:
                    mPosition = 4;
                    break;
                case R.id.MM:
                    mPosition = 5;
                    break;
                case R.id.MR:
                    mPosition = 6;
                    break;
                case R.id.BL:
                    mPosition = 7;
                    break;
                case R.id.BM:
                    mPosition = 8;
                    break;
                case R.id.BR:
                    mPosition = 9;
                    break;
                case R.id.confirm:
                    if (mTicTacToe.makePlayerMove(mPosition)) {
                        toggleText(mPosition);
                        final int pos = mPosition;
                        if(mOpponent.getType() == Opponent.PLAYER) {
                            mStatusText.setText(getString(R.string.sending));
                            myDocRef.update("lastmove", pos).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mStatusText.setText(String.format(getString(R.string.turn), mOpponent.getName()));
                                }
                            });
                        }
                        if (mTicTacToe.isWinner(mTicTacToe.getPlayer())) {
                            Toast.makeText(getApplication(), getString(R.string.won_the_game), Toast.LENGTH_SHORT).show();
                            nowPlaying = TicTacToe.PlayerType.player;
                            finishGame();
                            return;
                        } else if (mTicTacToe.isFull()) {
                            Toast.makeText(getApplication(), getString(R.string.game_tie), Toast.LENGTH_SHORT).show();
                            finishGame();
                            return;
                        } else
                            nowPlaying = TicTacToe.PlayerType.opponent;
                    }
                    break;
            }
            toggleText(mPosition);
        }
        if (isPlaying && nowPlaying == TicTacToe.PlayerType.opponent && mOpponent.getType() == Opponent.COMPUTER) {
            makeOpponentMove();
        }
    }

    private void makeOpponentMove() {
        int move = mOpponent.getMove();
        if(mTicTacToe.makeOpponentMove(move)) {
            toggleText(move);
            if (mTicTacToe.isWinner(mTicTacToe.getOpponent())) {
                Toast.makeText(this, String.format(getString(R.string.game_lost), mOpponent.getName()), Toast.LENGTH_SHORT).show();
                nowPlaying = TicTacToe.PlayerType.opponent;
                finishGame();
            } else if (mTicTacToe.isFull()) {
                Toast.makeText(this, getString(R.string.game_tie), Toast.LENGTH_SHORT).show();
                finishGame();
            } else
                nowPlaying = TicTacToe.PlayerType.player;
        }
    }

    private void toggleText(int pos) {
        for (int i = 1; i < 10; i++) {
            if (i == pos && mTicTacToe.isFree(pos))
                mBoard[i].setText(String.valueOf(mTicTacToe.getPlayer()));
            else
                mBoard[i].setText(String.valueOf(mTicTacToe.getSymbolAt(i)));
        }
    }

    private void finishGame() {
        mTicTacToe.clear();
        mPosition = 0;
        isPlaying = false;
    }

    @Override
    public void onBackPressed() {
        if (isPlaying) {
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

    private abstract class Opponent {
        final static int PLAYER = 1;
        final static int COMPUTER = 0;

        abstract String getName();

        abstract int getType();

        abstract String getId();

        abstract int getMove();

        abstract void setMove(int move);
    }

    private class Computer extends Opponent {

        private TicTacToe t;

        Computer(TicTacToe _t) {
            t = _t;
        }

        @Override
        String getName() {
            return "Computer";
        }

        @Override
        int getType(){
            return COMPUTER;
        }

        @Override
        String getId(){
            return "computer";
        }

        @Override
        int getMove() {
            return t.compMove();
        }

        @Override
        void setMove(int move){ }

    }

    private class Player extends Opponent {
        private String userid;
        private String name;
        private int move;

        Player(String userid, String name) {
            this.userid = userid;
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getId() {
            return userid;
        }

        @Override
        int getType(){
            return PLAYER;
        }

        @Override
        int getMove() {
            return move;
        }

        @Override
        void setMove(int move){
            this.move = move;
        }

    }
}