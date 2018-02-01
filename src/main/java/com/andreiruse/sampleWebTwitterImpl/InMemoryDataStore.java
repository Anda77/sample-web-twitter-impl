package com.andreiruse.sampleWebTwitterImpl;

import com.andreiruse.sampleWebTwitterImpl.domain.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A memory-backed implementation of the {@link DataStore} interface.
 * Stores a list of users in memory, with all associated data.
 */
public class InMemoryDataStore implements DataStore {
    /**
     * The list of users
     */
    private final Set<User> users = new HashSet<>();

    @Override
    public void storeUser(User user) {
        users.add(user);
    }

    @Override
    public Optional<User> getUser(String username) {
        return users.stream()
                .filter(x -> x.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public Set<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "InMemoryDataStore{" +
                "users=" + users +
                '}';
    }
}
