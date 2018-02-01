package com.andreiruse.sampleWebTwitterImpl;

import com.andreiruse.sampleWebTwitterImpl.domain.User;

import java.util.Optional;
import java.util.Set;

/**
 * This interface defined the data store that backs the application.
 * This exposes user-related accessors, as the individual messages belong to the user class.
 * <p>
 * This data store can be backed by virtually anything. The simplest method (which is also implemented in {@link InMemoryDataStore}
 * is backed by memory, however we could also have a file-backed / SQL-Database-backed / etc.
 */
public interface DataStore {
    /**
     * Stores the user's details. The calling code has to validate the details, as this method should not have any custom logic.
     * @param user the user details
     */
    void storeUser(User user);

    /**
     * Looks up the user matching the given username.
     * @param username the username to search for
     * @return an optional, representing the user matching the username. The optional is empty if no corresponding user has been found
     */
    Optional<User> getUser(String username);

    /**
     * Gets all users in the database.
     *
     * @return the list of users
     */
    Set<User> getUsers();
}
