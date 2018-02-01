package com.andreiruse.sampleWebTwitterImpl.controller;

import com.andreiruse.sampleWebTwitterImpl.DataStore;
import com.andreiruse.sampleWebTwitterImpl.domain.Message;
import com.andreiruse.sampleWebTwitterImpl.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for the /users/ endpoints. It handles operations around the user object:
 * * new user creation
 * * listing of own user's messages
 * * listing of followed users
 * * listing of followed users' messages
 * * following of other users
 * <p>
 * This is backed by the same data store as the rest of the application, which is wired in by the SpringBoot framework.
 * <p>
 * All methods respond with a typed {@link ResponseEntity}, that includes the response content, together with the HTTP status code.
 */
@RestController
public class UsersController {
    /**
     * The data store which is wired in
     */
    @Autowired
    private DataStore dataStore;

    /*
     * This field is used to sort the messages in reverse chronological order, in a consistent way, across different endpoints.
     */
    private static final Comparator<Message> MESSAGE_COMPARATOR = Comparator.comparing(Message::getCreatedAt).reversed();

    @RequestMapping(value = "/users/{username}/wall", method = RequestMethod.GET)
    ResponseEntity<List<Message>> getWall(@PathVariable(value = "username") String username) {
        Optional<User> userMatch = dataStore.getUser(username);
        if (userMatch.isPresent()) {
            User user = userMatch.get();

            List<Message> wall = user.getWall();
            wall.sort(MESSAGE_COMPARATOR);
            return new ResponseEntity<>(wall, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/users/{username}/follow/{who}", method = RequestMethod.POST)
    ResponseEntity<Object> follow(@PathVariable String username, @PathVariable String who) {
        Optional<User> currentUser = dataStore.getUser(username);
        Optional<User> followedUser = dataStore.getUser(who);
        if (!currentUser.isPresent() || !followedUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        currentUser.get().follow(followedUser.get());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/users/{username}/followers/", method = RequestMethod.GET)
    ResponseEntity<List<User>> followers(@PathVariable String username) {
        Optional<User> userMatch = dataStore.getUser(username);
        if (!userMatch.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User currentUser = userMatch.get();
        List<User> followers = dataStore.getUsers().stream()
                .filter(user -> user.getFollowing().contains(currentUser))
                .collect(Collectors.toList());
        return new ResponseEntity<>(followers, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{username}/timeline", method = RequestMethod.GET)
    ResponseEntity<List<Message>> getTimeline(@PathVariable(value = "username") String username) {
        Optional<User> userMatch = dataStore.getUser(username);
        if (userMatch.isPresent()) {
            User user = userMatch.get();
            List<Message> messagesByFollowedUsers = user.getFollowing().stream()
                    .flatMap(messages -> messages.getWall().stream())
                    .sorted(MESSAGE_COMPARATOR)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(messagesByFollowedUsers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/users/{username}", method = RequestMethod.POST)
    ResponseEntity<Object> newUser(@PathVariable(value = "username") String username) {
        Optional<User> userMatch = dataStore.getUser(username);
        if (userMatch.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = new User(username);
        dataStore.storeUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
