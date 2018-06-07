package com.jaisel.tictactoe.Utils;

import android.util.Log;
import android.util.Pair;

import com.jaisel.tictactoe.data.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

/**
 * Created by jaisel on 4/3/17.
 */
public class Server {;
    private static final String TAG = "Server";
    private URL url;
    private Vector<Pair<String, String>> params = new Vector<>();

    public Server() {
        try {
            url = new URL(Constants.SERVER);
        } catch (Exception ex) {
            Log.d(TAG, "Server " + ex.getMessage());
        }
    }

    public Server addParams(String key, Object value) {
        params.add(new Pair<>(key, String.valueOf(value)));
        return this;
    }

    public void clearParams() {
        params.clear();
    }

    private String getPostData() {
        String data = "";
        String join = "";
        try {
            for (Pair<String, String> s : params) {
                data += join + URLEncoder.encode(s.first, "UTF-8") + "=" + URLEncoder.encode(s.second, "UTF-8");
                if (params.size() > 1) join = "&";
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        return data;
    }

    public Vector<User> getUsers(String userid, String pattern) {
        Vector<User> usersList = new Vector<>();
        try {
            clearParams();
            String jsonData = addParams("userid", userid)
                    .addParams("name", pattern)
                    .addParams("action", "getusers")
                    .sendRequest();

            Log.d(TAG, "jsonData " + jsonData);
            JSONArray reader = new JSONArray(jsonData);
            for (int i = 0; i < reader.length(); i++) {
                JSONObject userObject = reader.getJSONObject(i);
                User user = new User();
                user.setId(userObject.getString("id"));
                user.setName(userObject.getString("name"));
                user.setStatus(Integer.parseInt(userObject.getString("status")));
                usersList.add(user);
            }
        } catch (Exception ex) {
            Log.d(TAG, "getUsers " + ex.getMessage());
        }
        return usersList;
    }

    public String sendRequest() {
        String postData = getPostData();
        BufferedReader reader;
        String data = "";
        try {
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(postData);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
                data += line;
            Log.d(TAG, "Data Received : " + data);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, "sendRequest " + ex.getMessage());
        }
        return data.trim();
    }
}
