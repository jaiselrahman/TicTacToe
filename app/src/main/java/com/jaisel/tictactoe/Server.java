package com.jaisel.tictactoe;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by jaisel on 4/3/17.
 */
public class Server {
    URL url;
    HttpURLConnection conn;

    Server(String _url) {
        try {
            url = new URL(_url);

        } catch (Exception ex) {
            Log.d("start", ex.getMessage());
        }
    }

    public int getResponse(String url) {
        try {
            return new Request().execute(url).get();
        } catch (Exception ex) {
            Log.d("getResponse", ex.getMessage());
            return -1;
        }
    }

    public void gameStart() {
        try {
            String data = URLEncoder.encode("name", "UTF-8")
                    + "=" + URLEncoder.encode("P", "UTF-8");
            data += "&" + URLEncoder.encode("oppname", "UTF-8")
                    + "=" + URLEncoder.encode("Q", "UTF-8");
            data += "&" + URLEncoder.encode("action", "UTF-8") + "="
                    + URLEncoder.encode("gamestarted", "UTF-8");
            data += "&" + URLEncoder.encode("source", "UTF-8") + "="
                    + URLEncoder.encode("app", "UTF-8");
            new Request().execute(data);
        } catch (Exception ex) {
            Log.d("start", ex.getMessage());
        }
    }

    private int sendRequest(String data) {
        int result = -1;
        BufferedReader reader = null;
        try {
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
            result = Integer.parseInt(line.trim());
        } catch (Exception ex) {
            Log.d("sendrequest ", ex.getMessage());
            return -1;
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                Log.d("reader", ex.getMessage());
                return -1;
            }
        }
        return result;
    }

    private class Request extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... args) {
            int pos = 0;
            try {
                pos = sendRequest(args[0]);
            } catch (Exception ex) {
                Log.d("doInBackground ", ex.getMessage());
            }
            return pos;
        }

        protected int onPostExecute(int s) {
            return s;
        }
    }

    public int getmove() {
        try {
            String data = URLEncoder.encode("name", "UTF-8")
                    + "=" + URLEncoder.encode("P", "UTF-8");
            data += "&" + URLEncoder.encode("oppname", "UTF-8")
                    + "=" + URLEncoder.encode("Q", "UTF-8");
            data += "&" + URLEncoder.encode("action", "UTF-8") + "="
                    + URLEncoder.encode("get", "UTF-8");
            data += "&" + URLEncoder.encode("source", "UTF-8") + "="
                    + URLEncoder.encode("app", "UTF-8");
            return new Request().execute(data).get();
        } catch (Exception ex) {
            Log.d("getmove", ex.getMessage());
            return -1;
        }
    }

    public void setmove(int move) {
        try {
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
        } catch (Exception ex) {
            Log.d("setmove", ex.getMessage());
        }
    }

    public void gameFinish() {
        try {
            String data = URLEncoder.encode("name", "UTF-8")
                    + "=" + URLEncoder.encode("P", "UTF-8");
            data += "&" + URLEncoder.encode("oppname", "UTF-8")
                    + "=" + URLEncoder.encode("Q", "UTF-8");
            data += "&" + URLEncoder.encode("action", "UTF-8") + "="
                    + URLEncoder.encode("gamefinished", "UTF-8");
            data += "&" + URLEncoder.encode("source", "UTF-8") + "="
                    + URLEncoder.encode("app", "UTF-8");
            new Request().execute(data);
        } catch (Exception ex) {
            Log.d("finish", ex.getMessage());
        }
    }
}
