package com.jaisel.tictactoe;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;

public class xo extends Activity implements Button.OnClickListener
{
	ttt t=new ttt();
	char player='P';
	static char player2='C';
	String secondPlayerType, firstPlayerName, secondPlayerName;
	Server S=new Server("http://0.0.0.0:8080/app.php");
	Player P;
	boolean isPlaying=false;
	Button board[] ,reset, confirm;
	int pos=0;
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.xoboard);
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
				isPlaying = true;
				S.start();
				if (player2 == 'O')
				{
					P = new Opponent();
					secondPlayerType = "Opponent";
				}
				else
				{
					P = new Computer(t);
					secondPlayerType = "Computer";
				}
				if (player == 'Q')
				{
					Toast.makeText(xo.this.getApplicationContext(), secondPlayerType + " goes first !", Toast.LENGTH_SHORT).show();
					playSecondPlayer();
				}
				else if (player == 'P')
					Toast.makeText(xo.this.getApplicationContext(), "You goes first !", Toast.LENGTH_SHORT).show();
			}
		};
		AlertDialog.Builder chooseXO = new AlertDialog.Builder(xo.this);
		chooseXO.setMessage("Select X or O")
			.setPositiveButton("O", chooseXOListener)
			.setNegativeButton("X", chooseXOListener)
			.setNeutralButton("Exit", chooseXOListener)
			.setCancelable(false)
			.show();
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
	void playSecondPlayer()
	{
		int move=-1;
		while (move == -1)
		{
			move = P.getmove();
		}
		t.makeMove(t.board, move, t.computerLetter);
		if (move != 0) board[move].setText("" + t.computerLetter + "");
		if (t.isWinner(t.board, t.computerLetter))
		{
			Toast.makeText(xo.this.getApplicationContext(), secondPlayerType + " has beaten you\nYou loose!", Toast.LENGTH_SHORT).show();		
			isPlaying = false;
			S.finish();
		}
		else
		if (t.isFull())
		{
			Toast.makeText(xo.this.getApplicationContext(), "The Game is a tie !", Toast.LENGTH_SHORT).show();			
			isPlaying = false;
			S.finish();
		}
		else
			player = 'P';
	}
	void toggleText(int _pos)
	{
		if (t.isFree(_pos))
		{
			if (board[_pos].getText().charAt(0) == '-')
			{
				board[_pos].setText("" + t.playerLetter + "");
			}	
			else 
			{
				board[_pos].setText("-");
				pos = 0;
			}
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
		if (p1.getId() == R.id.reset) this.recreate();
		if (isPlaying && player == 'P')
		{
			switch (p1.getId())
			{
				case R.id.TL: pos = 1; toggleText(pos); break;
				case R.id.TM: pos = 2; toggleText(pos); break;
				case R.id.TR: pos = 3; toggleText(pos); break;
				case R.id.ML: pos = 4; toggleText(pos); break;
				case R.id.MM: pos = 5; toggleText(pos); break;
				case R.id.MR: pos = 6; toggleText(pos); break;
				case R.id.BL: pos = 7; toggleText(pos); break;
				case R.id.BM: pos = 8; toggleText(pos); break;
				case R.id.BR: pos = 9; toggleText(pos); break;
				case R.id.confirm:
					if (t.isFree(pos))
					{	
						t.makeMove(t.board, pos, t.playerLetter);
						P.setmove(pos);
						board[pos].setText("" + t.playerLetter + "");
						if (t.isWinner(t.board, t.playerLetter))
						{
							Toast.makeText(xo.this.getApplicationContext(), "You won the game !", Toast.LENGTH_SHORT).show();
							isPlaying = false;
							S.finish();
						}
						else
						if (t.isFull())
						{
							Toast.makeText(xo.this.getApplicationContext(), "The Game is a tie !", Toast.LENGTH_SHORT).show();			
							isPlaying = false;
							S.finish();
						}
						else
							player = 'Q';
					}
					break;
			}
		}
		if (isPlaying && player == 'Q')
		{
			playSecondPlayer();
		}
	}
	abstract class Player
	{
		abstract int getmove();
		abstract void setmove(int m);
	}
	class Computer extends Player
	{
		private ttt t;
		Computer(ttt _t)
		{
			t = _t;
		}
		int getmove()
		{
			return t.compMove();
		}
		void setmove(int m)
		{
		}
	}
	class Opponent extends Player
	{
		int move;
		int getmove()
		{
			return move = S.getmove();
		}
		void setmove(int m)
		{
			S.setmove(m);
		}
	}
	class Server
	{
		URL url;
		HttpURLConnection conn;
		Server(String _url)
		{
			try
			{
				url = new URL(_url);

			}
			catch (Exception ex)
			{
				Log.d("start", ex.getMessage());
			}
		}
		public void start()
		{
			try
			{
				String data = URLEncoder.encode("name", "UTF-8") 
					+ "=" + URLEncoder.encode("P", "UTF-8"); 
				data += "&" + URLEncoder.encode("oppname", "UTF-8")
					+ "=" + URLEncoder.encode("Q", "UTF-8"); 
				data += "&" + URLEncoder.encode("action", "UTF-8") + "="
					+ URLEncoder.encode("gamestarted", "UTF-8");	
				data += "&" + URLEncoder.encode("source", "UTF-8") + "="
					+ URLEncoder.encode("app", "UTF-8");
				new Request().execute(data);
			}
			catch (Exception ex)
			{
				Log.d("start", ex.getMessage());
			}
		}
		private int  sendRequest(String data)
		{
			int _move = 0;
			BufferedReader reader=null;
			try
			{
				conn = (HttpURLConnection) url.openConnection(); 
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.connect();
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
				wr.write(data); 
				wr.flush();
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line = line = reader.readLine();
				_move = Integer.parseInt(line.trim());
			}
			catch (Exception ex)
			{
				Log.d("sendrequest ", ex.toString());
				return -1;
			}
			finally
			{
				try
				{
					reader.close();
				}
				catch (Exception ex)
				{
					Log.d("sendrequest2 ", ex.toString());
					return 0;
				}
			}
			return _move;
		}
		private class Request extends AsyncTask<String,Void,Integer>
		{ 
			protected Integer doInBackground(String... args)
			{ 
				int pos=0;
				try
				{
					pos = sendRequest(args[0]);
				}
				catch (Exception ex)
				{
					Log.d("doInBackground ",ex.toString());
				}
				return pos;
			}
			protected int onPostExecute(int s)
			{ 
				return  s;
			}
		}
		public int getmove()
		{	
			try
			{
				String data = URLEncoder.encode("name", "UTF-8") 
					+ "=" + URLEncoder.encode("P", "UTF-8");
				data += "&" + URLEncoder.encode("oppname", "UTF-8") 
					+ "=" + URLEncoder.encode("Q", "UTF-8"); 
				data += "&" + URLEncoder.encode("action", "UTF-8") + "="
					+ URLEncoder.encode("get", "UTF-8"); 
				data += "&" + URLEncoder.encode("source", "UTF-8") + "="
					+ URLEncoder.encode("app", "UTF-8");
				return new Request().execute(data).get();
			}
			catch (Exception ex)
			{
				Log.d("getmove", ex.getMessage());
				return -1;
			}
		}
		void setmove(int move)
		{
			try
			{
				String data = URLEncoder.encode("name", "UTF-8") 
					+ "=" + URLEncoder.encode("P", "UTF-8"); 
				data += "&" + URLEncoder.encode("oppname", "UTF-8") 
					+ "=" + URLEncoder.encode("Q", "UTF-8"); 
				data += "&" + URLEncoder.encode("move", "UTF-8") + "="
					+ URLEncoder.encode(String.valueOf(move), "UTF-8"); 
				data += "&" + URLEncoder.encode("action", "UTF-8") + "="
					+ URLEncoder.encode("set", "UTF-8");	
				data += "&" + URLEncoder.encode("source", "UTF-8") + "="
					+ URLEncoder.encode("app", "UTF-8");	
				new Request().execute(data);
			}
			catch (Exception ex)
			{
				Log.d("setmove", ex.getMessage());
			}
		}
		public void finish()
		{
			try
			{
				String data = URLEncoder.encode("name", "UTF-8") 
					+ "=" + URLEncoder.encode("P", "UTF-8"); 
				data += "&" +  URLEncoder.encode("oppname", "UTF-8") 
					+ "=" + URLEncoder.encode("Q", "UTF-8"); 
				data += "&" + URLEncoder.encode("action", "UTF-8") + "="
					+ URLEncoder.encode("gamefinished", "UTF-8");	
				data += "&" + URLEncoder.encode("source", "UTF-8") + "="
					+ URLEncoder.encode("app", "UTF-8");
				new Request().execute(data);
			}
			catch (Exception ex)
			{
				Log.d("finish", ex.getMessage());
			}
		}
	}
}
