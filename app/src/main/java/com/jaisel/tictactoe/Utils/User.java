package com.jaisel.tictactoe.Utils;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by jaisel on 11/6/17.
 */

@Entity
public class User {

    private String name;
    @PrimaryKey
    @NonNull
    private String id;
    @Ignore
    private Uri profilePic;

    public User(@NonNull String uid, String name) {
        this.id = uid;
        this.name = name;
    }

    public User(String uid, String name, Uri profilePic) {
        this.id = uid;
        this.name = name;
        this.profilePic = profilePic;
    }

    public User() {
        this(null, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Uri profilePic) {
        this.profilePic = profilePic;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof User) {
            User otherUser = (User) obj;
            return this.id.equals(otherUser.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
