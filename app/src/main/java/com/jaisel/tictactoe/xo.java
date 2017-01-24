package com.jaisel.tictactoe;

import android.app.*;
import android.content.*;
import android.os.*;

public class xo extends Activity
{
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xoboard);
	}
	@Override
	public void onBackPressed() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
					case DialogInterface.BUTTON_POSITIVE:
						finish();
						
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						//No button clicked
						break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(xo.this);
		builder.setMessage("Are you sure?\nThe current game will be lost.")
			.setPositiveButton("Yes", dialogClickListener)
			.setNegativeButton("No", dialogClickListener)
			.show();
	}
}
