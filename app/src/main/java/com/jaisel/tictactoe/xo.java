package com.jaisel.tictactoe;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.view.*;
import android.widget.*;


public class xo extends Activity implements Button.OnClickListener
{
	AlertDialog.Builder chooseXO;
	DialogInterface.OnClickListener gameFinished;
	ttt t;
	char player='P';
	boolean isPlaying=false;
	Button board[];
	Button reset,confirm;
	int pos=0;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xoboard);
		t = new ttt();
		board = new Button[10];
		board[1] = (Button)findViewById(R.id.TL);
		board[2] = (Button)findViewById(R.id.TM);
		board[3] = (Button)findViewById(R.id.TR);
		board[4] = (Button)findViewById(R.id.ML);
		board[5] = (Button)findViewById(R.id.MM);
		board[6] = (Button)findViewById(R.id.MR);
		board[7] = (Button)findViewById(R.id.BL);
		board[8] = (Button)findViewById(R.id.BM);
		board[9] = (Button)findViewById(R.id.BR);
		for (int i=1;i < 10;i++)
		{
			board[i].setOnClickListener(this);
		}
		reset = (Button)findViewById(R.id.reset);
		reset.setOnClickListener(this);
		confirm = (Button)findViewById(R.id.confirm);
		confirm.setOnClickListener(this);
		DialogInterface.OnClickListener chooseXOListener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which)
				{
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
				if (player == 'C')
				{
					Toast.makeText(xo.this.getApplicationContext(), "Computer goes first !", Toast.LENGTH_SHORT).show();
					playComputer();
				}
				else
					Toast.makeText(xo.this.getApplicationContext(), "You goes first !", Toast.LENGTH_SHORT).show();

			}
		};
		chooseXO = new AlertDialog.Builder(xo.this);
		chooseXO.setMessage("Select X or O")
			.setPositiveButton("O", chooseXOListener)
			.setNegativeButton("X", chooseXOListener)
			.setNeutralButton("Exit", chooseXOListener)
			.setCancelable(false)
			.show();
		gameFinished = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				recreate();
			}
		};
	}
	@Override
	public void onBackPressed()
	{
		DialogInterface.OnClickListener gameExitListener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which)
				{
					case DialogInterface.BUTTON_POSITIVE:
						finish();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						break;
				}
			}
		};
		AlertDialog.Builder gameExit = new AlertDialog.Builder(xo.this);
		gameExit.setMessage("Are you sure?\nThe current game will be lost.")
			.setPositiveButton("Yes", gameExitListener)
			.setNegativeButton("No", gameExitListener);
		if (isPlaying)
			gameExit.show();
		else
			finish();			
	}
	void playComputer()
	{
		int move=t.compMove();
		t.makeMove(t.board, move, t.computerLetter);
		if (move != 0) board[move].setText("" + t.computerLetter + "");
		if (t.isWinner(t.board, t.computerLetter))
		{
			new AlertDialog.Builder(xo.this)
				.setMessage("You Computer Has Beaten You!\nYou lose.")
				.setPositiveButton("Ok", gameFinished)
				.show();
		}
		else
		if (t.isFull())
		{
			new AlertDialog.Builder(xo.this)
				.setMessage("The Game Is A Tie")
				.setPositiveButton("Ok", gameFinished)
				.show();				
		}
		else
			player = 'P';
	}
	void toggleText(int _pos)
	{

		if (board[_pos].getText().charAt(0) == '-')
		{
			board[_pos].setText("" + t.playerLetter + "");
		}
		else
		{
			board[_pos].setText("-");
		}
		for (int i=1;i < 10;i++)
		{
			if (t.isFree(i) && i != _pos)
				board[i].setText("-");
		}
	}
	@Override
	public void onClick(View p1)
	{
		isPlaying = true;
		if (player == 'P')
		{
			switch (p1.getId())
			{
				case R.id.TL: pos = 1; if (t.isFree(pos)) toggleText(pos); break;
				case R.id.TM: pos = 2; if (t.isFree(pos)) toggleText(pos); break;
				case R.id.TR: pos = 3; if (t.isFree(pos)) toggleText(pos); break;
				case R.id.ML: pos = 4; if (t.isFree(pos)) toggleText(pos); break;
				case R.id.MM: pos = 5; if (t.isFree(pos)) toggleText(pos); break;
				case R.id.MR: pos = 6; if (t.isFree(pos)) toggleText(pos); break;
				case R.id.BL: pos = 7; if (t.isFree(pos)) toggleText(pos); break;
				case R.id.BM: pos = 8; if (t.isFree(pos)) toggleText(pos); break;
				case R.id.BR: pos = 9; if (t.isFree(pos)) toggleText(pos); break;
				case R.id.reset: this.recreate(); break;
				case R.id.confirm: 
					if (t.isFree(pos))
					{
						t.makeMove(t.board, pos, t.playerLetter);
						board[pos].setText("" + t.playerLetter + "");
						if (t.isWinner(t.board, t.playerLetter))
						{
							new AlertDialog.Builder(xo.this)
								.setMessage("You Won The Game !")
								.setPositiveButton("Ok", gameFinished)
								.show();
						}
						else
						if (t.isFull())
						{
							new AlertDialog.Builder(xo.this)
								.setMessage("The Game Is A Tie")
								.setPositiveButton("Ok", gameFinished)
								.show();
						}
						else
							player = 'C';
					}
					break;
			}
		}
		if (player == 'C')
		{
			playComputer();
		}
	}
}
