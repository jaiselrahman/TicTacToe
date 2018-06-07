package com.jaisel.tictactoe.volley;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jaisel.tictactoe.R;
import com.jaisel.tictactoe.UserItem;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends ArrayAdapter<UserItem> {
    private static final String TAG = FriendsAdapter.class.getSimpleName();
    private static int c = 0;
    private ArrayList<UserItem> friends;
    private Context context;

    public FriendsAdapter(Context context, ArrayList<UserItem> friends) {
        super(context, R.layout.friends_list_item, friends);
        this.context = context;
        this.friends = friends;
        Log.i(TAG, "FriendsAdapter: " + friends.size());
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.friends_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.friendName = convertView.findViewById(R.id.friend_name);
            viewHolder.friendPhoneNumber = convertView.findViewById(R.id.friend_phone);
            viewHolder.profilePic = convertView.findViewById(R.id.friend_pic);
            convertView.setTag(viewHolder);
            c++;
            Log.d(TAG, "getView: Recycled " + c);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.friendName.setText(friends.get(position).getName());
        viewHolder.friendPhoneNumber.setText(friends.get(position).getPhoneNumber());
        viewHolder.profilePic.setImageURI(friends.get(position).getProfilePic());

        return convertView;
    }

    private static class ViewHolder {
        private CircleImageView profilePic;
        private TextView friendName, friendPhoneNumber;
    }
}
