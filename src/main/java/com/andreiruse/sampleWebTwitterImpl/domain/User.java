package com.andreiruse.sampleWebTwitterImpl.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * POJO representing a user. Stores the username, the list of messages and the other users that the current user is following.
 * <p>
 * When comparing two users, only the username is taken into consideration.
 */
public class User {
    private final String username;
    private final List<Message> wall;
    private final List<User> following; //Other users that the current user is following

    public User(String username) {
        this.username = username;
        this.wall = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<Message> getWall() {
        return wall;
    }

    public void addMessage(Message message) {
        this.wall.add(message);
    }

    public List<User> getFollowing() {
        return following;
    }

    public void follow(User user) {
        this.following.add(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {

        return Objects.hash(username);
    }
}
