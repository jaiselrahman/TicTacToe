package com.jaisel.tictactoe;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Button Player = (Button)findViewById(R.id.player);
		Player.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v)
			{
				Intent i=new Intent(getApplicationContext(),xo.class);
				startActivityForResult(i,0);
			}
		});	
    }
}
class ttt
{
	char board[]={'-', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	char computerLetter, playerLetter;
	boolean isWinner(char tboard[], char l)
	{
		return ((tboard[1] == l && tboard[2] == l && tboard[3] == l) ||
				(tboard[1] == l && tboard[2] == l && tboard[3] == l) ||
				(tboard[1] == l && tboard[2] == l && tboard[3] == l) ||
				(tboard[1] == l && tboard[2] == l && tboard[3] == l) ||
				(tboard[1] == l && tboard[2] == l && tboard[3] == l) ||
				(tboard[1] == l && tboard[2] == l && tboard[3] == l) ||
				(tboard[1] == l && tboard[2] == l && tboard[3] == l) ||
				(tboard[1] == l && tboard[2] == l && tboard[3] == l) ||
				(tboard[1] == l && tboard[2] == l && tboard[3] == l));
	}
	boolean isFree(int pos)
	{
		return board[pos] == ' ';
	}
	boolean isFull()
	{
		for(int i=1;i<10;i++)
		{
			if(board[i]!=' ')
				return false;
		}
		return true;
	}
	char[] getBoardCopy()
	{
		char t[]=new char[board.length];
		for(int i=0;i<board.length;i++)
			t[i]=board[i];
		return t;
	}
	void makeMove(char _board[], int pos, char l)
	{
		_board[pos]=l;
	}
	int randomMove(int move[])
	{
		int possibleMove[]=new int[move.length];
		int n=0;
		for(int i=0; i<move.length; i++)
		{
			if(isFree(move[i]))
			{
				possibleMove[n]=move[i];
				n++;
			}
		}
		if(n==0)
			return 0;
		return possibleMove[((int)Math.random()*10) % n];
	}
	int compMove()
	{
		int pos=0;
		char[] t;	
		for(int i=0; i< 10; i++)
			if(isFree(i))
			{
				t=getBoardCopy();
				makeMove(t, i, computerLetter);
				if(isWinner(t, computerLetter))
					return i;
			}
		for(int i=0; i< 10; i++)
			if(isFree(i))
			{
				t=getBoardCopy();
				makeMove(t, i, playerLetter);
				if(isWinner(t, playerLetter))
					return i;
			}
		return pos;
	}
}


