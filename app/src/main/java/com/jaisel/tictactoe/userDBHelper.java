package com.jaisel.tictactoe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jaisel on 2/3/17.
 */
public class userDBHelper extends SQLiteOpenHelper {
    public final static String DB = "ttt";
    public final static String TABLE = "users";

    public userDBHelper(Context context) {
        super(context, DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, userId INTEGER);");
        } catch (Exception ex) {
            Log.d("DataBase", ex.getMessage());
            return;
        }
        Log.d("DataBase", "Successfully Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase p1, int p2, int p3) {
    }
}
