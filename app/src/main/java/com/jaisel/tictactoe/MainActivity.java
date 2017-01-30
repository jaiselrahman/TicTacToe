package com.jaisel.tictactoe;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity 
{
	private Menu menu;
	FragmentManager FM ;
	FragmentTransaction FT;
	MainFragment mf= new MainFragment();
	AboutFragment af = new AboutFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		FM = getFragmentManager();
		FT = FM.beginTransaction();
		FT.replace(android.R.id.content,mf);
		FT.commit();
	//	mf.setRetainInstance(true);
	//	af.setRetainInstance(true);
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
		this.menu=menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
	private boolean inmenu=false;
	public boolean onOptionsItemSelected(MenuItem item)
	{
        switch (item.getItemId())
		{
			case R.id.about:
				menu.setGroupVisible(R.id.menu_group,false);
				FT = FM.beginTransaction();
				FT.replace(android.R.id.content,af);
				FT.commit();
				inmenu=true;
				return true;
			case R.id.exit:
				System.exit(0);
				return true;
			default:
				return super.onOptionsItemSelected(item);
        } 
	}
	@Override
	public void onBackPressed()
	{
		if (inmenu)
		{
			FT = FM.beginTransaction();
			FT.replace(android.R.id.content,mf);
			FT.commit();
			menu.setGroupVisible(R.id.menu_group,true);
			inmenu = false;
		}
		else
			super.onBackPressed();
	}
}

class AboutFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.about,container,false);
	}
}
class MainFragment extends Fragment
{	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v= inflater.inflate(R.layout.main,container,false);
		Button Computer = (Button)v.findViewById(R.id.computer);
		Computer.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					Intent i=new Intent(getActivity(), xo.class);
					startActivityForResult(i, 0);
				}
			});
		return v;
	}
}

class ttt
{
	char board[]={'-', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	char computerLetter='O', playerLetter='X';
	char first='P',second='C';
	boolean isWinner(char tboard[], char l)
	{
		return ((tboard[1] == l && tboard[2] == l && tboard[3] == l) ||
			(tboard[4] == l && tboard[5] == l && tboard[6] == l) ||
			(tboard[7] == l && tboard[8] == l && tboard[9] == l) ||
			(tboard[1] == l && tboard[4] == l && tboard[7] == l) ||
			(tboard[2] == l && tboard[5] == l && tboard[8] == l) ||
			(tboard[3] == l && tboard[6] == l && tboard[9] == l) ||
			(tboard[1] == l && tboard[5] == l && tboard[9] == l) ||
			(tboard[3] == l && tboard[5] == l && tboard[7] == l));
	}
	boolean isFree(int pos)
	{
		return board[pos] == ' ';
	}
	boolean isFull()
	{
		for (int i=1;i < 10;i++)
		{
			if (board[i] == ' ')
				return false;
		}
		return true;
	}
	char[] getBoardCopy()
	{
		char t[]=new char[board.length];
		for (int i=0;i < board.length;i++)
			t[i] = board[i];
		return t;
	}
	void makeMove(char _board[], int pos, char l)
	{
		_board[pos] = l;
	}
	int randomMove(int move[])
	{
		int possibleMove[]=new int[move.length];
		int n=0;
		for (int i=0; i < move.length; i++)
		{
			if (isFree(move[i]))
			{
				possibleMove[n] = move[i];
				n++;
			}
		}
		if (n == 0)
			return 0;
		return possibleMove[(int)(Math.random() * 10) % n];
	}
	char chooseFirst()
	{
		if ((int)(Math.random() * 10) % 2 == 0)
		{
			first = 'P';
			second = 'C';
			return 'P';
		}
		else 
		{
			first = 'C';
			second = 'P';
			return 'C';
		}
	}
	int compMove()
	{
		char[] t;
		int c1[],c2[];
		for (int i=1; i < 10; i++)
			if (isFree(i))
			{
				t = getBoardCopy();
				makeMove(t, i, computerLetter);
				if (isWinner(t, computerLetter))
					return i;
			}
		for (int i=1; i < 10; i++)
			if (isFree(i))
			{
				t = getBoardCopy();
				makeMove(t, i, playerLetter);
				if (isWinner(t, playerLetter))
					return i;
			}
		if (first == 'P')
		{
			c1 = new int[] {2, 4, 6,8};
			c2 = new int[] {1, 3, 7, 9};
		}
		else
		{
			c1 = new int[] {1, 3, 7, 9};
			c2 = new int[] {2, 4, 6, 9};
		}
		int pos=randomMove(c1);
		if (pos != 0)
			return pos;
		if (isFree(5))
			return 5;
		return randomMove(c2);
	}
}
