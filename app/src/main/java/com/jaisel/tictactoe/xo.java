package com.jaisel.tictactoe;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;

public class xo extends Activity implements View.OnClickListener
{
	AlertDialog.Builder gameExit, chooseXO;
	ttt t;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xoboard);
		t = new ttt();
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
		gameExit = new AlertDialog.Builder(xo.this);
		gameExit.setMessage("Are you sure?\nThe current game will be lost.")
			.setPositiveButton("Yes", gameExitListener)
			.setNegativeButton("No", gameExitListener);
		DialogInterface.OnClickListener chooseXOListener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which)
				{
					case DialogInterface.BUTTON_POSITIVE:
						t.playerLetter='O';
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						t.computerLetter='X';
						break;
				}
			}
		};
		chooseXO = new AlertDialog.Builder(xo.this);
		chooseXO.setMessage("Select X or O")
			.setPositiveButton("O", chooseXOListener)
			.setNegativeButton("X", chooseXOListener);
		chooseXO.show();
	}
	@Override
	public void onBackPressed()
	{
			gameExit.show();
	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId())
		{
			case R.id.TL:
				break;
			case R.id.TM:
				break;
			case R.id.TR:
				break;
			case R.id.ML:
				break;
			case R.id.MM:
				break;
			case R.id.MR:
				break;
			case R.id.BL:
				break;
			case R.id.BM:
				break;
			case R.id.BR:
				break;
		}
	}
}
