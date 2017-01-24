package com.jaisel.tictactoe;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

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
				startActivityForResult(i,RESULT_OK);
				//finish();
			}
		});
		
	
    }
}
