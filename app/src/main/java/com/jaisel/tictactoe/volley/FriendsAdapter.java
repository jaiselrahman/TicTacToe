package com.jaisel.tictactoe.volley;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jaisel.tictactoe.R;
import com.jaisel.tictactoe.Utils.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends ArrayAdapter<User> {
    private static final String TAG = FriendsAdapter.class.getSimpleName();
    private ArrayList<User> friends;
    private Context context;

    public FriendsAdapter(Context context) {
        super(context, R.layout.friends_list_item);
        this.context = context;
    }

    public void setFriends(ArrayList<User> friends) {
        if (friends == null) return;
        if (this.friends == null) {
            this.friends = new ArrayList<>();
        }
        this.friends.clear();
        this.friends.addAll(friends);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (friends == null) return 0;
        return friends.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.friends_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.friendName = convertView.findViewById(R.id.friend_name);
            viewHolder.friendPhoneNumber = convertView.findViewById(R.id.friend_phone);
            viewHolder.profilePic = convertView.findViewById(R.id.friend_pic);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.friendName.setText(friends.get(position).getName());
        viewHolder.friendPhoneNumber.setText(friends.get(position).getId());
        viewHolder.profilePic.setImageURI(friends.get(position).getProfilePic());

        return convertView;
    }

    private static class ViewHolder {
        private CircleImageView profilePic;
        private TextView friendName, friendPhoneNumber;
    }
}
