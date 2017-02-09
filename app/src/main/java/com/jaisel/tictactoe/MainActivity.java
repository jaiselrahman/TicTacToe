package com.jaisel.tictactoe;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

public class MainActivity extends Activity 
{
	private Menu menu;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	mainFragment mf = new mainFragment(this);
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
  		setContentView(R.layout.activity_main);
		if (savedInstanceState == null)
		{	
        	FragmentManager FM=getFragmentManager();
			FragmentTransaction FT= FM.beginTransaction();
			FT.replace(R.id.content_frame, mf);
			FT.commit();
		}
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
												R.layout.drawer_list_item, new String[] {"Account", "About", "Exit"}));
        mDrawerList.setOnItemClickListener(new  ListView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
			    switch (position)
				{
					case 0:
						Toast.makeText(getApplicationContext(),"Account",Toast.LENGTH_SHORT).show();
						break;
					case 1:
						FragmentManager FM=getFragmentManager();
						FragmentTransaction FT= FM.beginTransaction();
						FT.replace(R.id.content_frame, new Fragment()
							{
								public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
								{
									return inflater.inflate(R.layout.about, container, false);
								}
							});
						FT.commit();
						menu.setGroupVisible(R.id.menu_group, false);
						inAbout = true;
						break;
					case 2:
						System.exit(0);
				}
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(
		this,                  /* host Activity */
		mDrawerLayout,         /* DrawerLayout object */
		R.drawable.ic_launcher,  /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open,  /* "open drawer" description for accessibility */
		R.string.drawer_close  /* "close drawer" description for accessibility */
		)
		{
			public void onDrawerClosed(View view)
			{
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
            public void onDrawerOpened(View drawerView)
			{
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	@Override
    protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
		this.menu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
	private boolean inAbout=false;
	public boolean onOptionsItemSelected(MenuItem item)
	{
        switch (item.getItemId())
		{
			case R.id.about:
				FragmentManager FM=getFragmentManager();
				FragmentTransaction FT= FM.beginTransaction();
				FT.replace(R.id.content_frame, new Fragment()
				{
					public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
					{
						return inflater.inflate(R.layout.about, container, false);
					}
				});
				FT.commit();
				menu.setGroupVisible(R.id.menu_group, false);
				inAbout = true;
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
		if (inAbout)
		{
			FragmentManager FM=getFragmentManager();
			FragmentTransaction FT= FM.beginTransaction();
			FT.replace(R.id.content_frame, mf);
			FT.commit();
			menu.setGroupVisible(R.id.menu_group, true);
			inAbout = false;
		}
		else
			super.onBackPressed();
	}
	public static class mainFragment extends Fragment
	{
		Context context;
		public mainFragment(Context _c){
			context=_c;
		}
		public mainFragment(){}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View v = inflater.inflate(R.layout.main, container, false);
			Button Computer = (Button)v.findViewById(R.id.computer);
			Computer.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					xo.player2 = 'C';
					Intent i=new Intent(context, xo.class);
					startActivityForResult(i, 0);
				}
			});
			Button Player = (Button)v.findViewById(R.id.player);
			Player.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					xo.player2 = 'O';
					Intent i=new Intent(context, xo.class);
					startActivityForResult(i, 0);
				}
			});
			return v;
		}
	}
}
class ttt
{
	char board[]={'-', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	char computerLetter='O', playerLetter='X';
	char first='P',second='Q';
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
			second = 'Q';
			return 'P';
		}
		else 
		{
			first = 'Q';
			second = 'P';
			return 'Q';
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
