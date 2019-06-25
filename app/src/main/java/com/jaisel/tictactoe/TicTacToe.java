package com.jaisel.tictactoe;

/**
 * Created by jaisel on 2/3/17.
 */
public class TicTacToe {
    public enum NowPlaying {PLAYER, OPPONENT, NONE}

    private char board[] = {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-'};
    private char playerSymbol = 'X', opponentSymbol = 'O';
    private NowPlaying playerType;

    public void setSymbol(char playerSymbol, char opponentSymbol) {
        this.playerSymbol = playerSymbol;
        this.opponentSymbol = opponentSymbol;
    }

    public void clear() {
        board = new char[]{'-', '-', '-', '-', '-', '-', '-', '-', '-', '-'};
    }

    public char getPlayer() {
        return playerSymbol;
    }

    public char getOpponent() {
        return opponentSymbol;
    }

    public char getSymbolAt(int i) {
        return board[i];
    }

    public boolean isWinner(char c) {
        return isWinner(board, c);
    }

    public boolean isFree(int pos) {
        return board[pos] == '-';
    }

    public boolean isFull() {
        for (int i = 1; i < 10; i++) {
            if (board[i] == '-')
                return false;
        }
        return true;
    }

    private void makeMove(char _board[], int pos, char c) {
        _board[pos] = c;
    }

    public boolean makePlayerMove(int pos) {
        if (pos != 0 && isFree(pos)) {
            board[pos] = playerSymbol;
            return true;
        }
        return false;
    }

    public boolean makeOpponentMove(int pos) {
        if (isFree(pos)) {
            board[pos] = opponentSymbol;
            return true;
        }
        return false;
    }

    public NowPlaying chooseFirst() {
        if ((int) (Math.random() * 10) % 2 == 0) {
            playerType = NowPlaying.PLAYER;
            return playerType;
        } else {
            playerType = NowPlaying.OPPONENT;
            return playerType;
        }
    }

    public int compMove() {
        char[] tempBoard;
        int c1[], c2[];
        for (int i = 1; i < 10; i++)
            if (isFree(i)) {
                tempBoard = getBoardCopy();
                makeMove(tempBoard, i, opponentSymbol);
                if (isWinner(tempBoard, opponentSymbol))
                    return i;
            }
        for (int i = 1; i < 10; i++)
            if (isFree(i)) {
                tempBoard = getBoardCopy();
                makeMove(tempBoard, i, playerSymbol);
                if (isWinner(tempBoard, playerSymbol))
                    return i;
            }
        if (playerType == NowPlaying.PLAYER) {
            c1 = new int[]{2, 4, 6, 8};
            c2 = new int[]{1, 3, 7, 9};
        } else {
            c1 = new int[]{1, 3, 7, 9};
            c2 = new int[]{2, 4, 6, 9};
        }
        int pos = randomMove(c1);
        if (pos != 0)
            return pos;
        if (isFree(5))
            return 5;
        return randomMove(c2);
    }

    private boolean isWinner(char tboard[], char c) {
        return ((tboard[1] == c && tboard[2] == c && tboard[3] == c) ||
                (tboard[4] == c && tboard[5] == c && tboard[6] == c) ||
                (tboard[7] == c && tboard[8] == c && tboard[9] == c) ||
                (tboard[1] == c && tboard[4] == c && tboard[7] == c) ||
                (tboard[2] == c && tboard[5] == c && tboard[8] == c) ||
                (tboard[3] == c && tboard[6] == c && tboard[9] == c) ||
                (tboard[1] == c && tboard[5] == c && tboard[9] == c) ||
                (tboard[3] == c && tboard[5] == c && tboard[7] == c));
    }

    private char[] getBoardCopy() {
        char t[] = new char[board.length];
        System.arraycopy(board, 0, t, 0, board.length);
        return t;
    }

    private int randomMove(int move[]) {
        int possibleMove[] = new int[move.length];
        int n = 0;
        for (int aMove : move) {
            if (isFree(aMove)) {
                possibleMove[n] = aMove;
                n++;
            }
        }
        if (n == 0)
            return 0;
        return possibleMove[(int) (Math.random() * 10) % n];
    }
}