package com.andreiruse.sampleWebTwitterImpl.controller;

import com.andreiruse.sampleWebTwitterImpl.DataStore;
import com.andreiruse.sampleWebTwitterImpl.domain.Message;
import com.andreiruse.sampleWebTwitterImpl.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * REST controller for the /message endpoints. It handles the creation of new messages.
 * <p>
 * This is backed by the same data store as the rest of the application, which is wired in by the SpringBoot framework.
 * <p>
 * Consistently with the rest of the codebase, the rest method responds with a typed {@link ResponseEntity}, that includes the response content, together with the HTTP status code.
 */

@RestController
public class MessageController {
    @Autowired
    private DataStore dataStore;

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    ResponseEntity postMessage(@RequestBody MessageInput messageInput) {
        Optional<User> userMatch = dataStore.getUser(messageInput.username);
        if (!userMatch.isPresent()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND); //HTTP 404
        }
        User user = userMatch.get();
        Message message = new Message(user, messageInput.messageBody);

        if (!message.isValid()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST); //HTTP 400
        }

        user.addMessage(message);
        return new ResponseEntity(HttpStatus.CREATED); //HTTP 201
    }

    static class MessageInput {
        public String username;
        public String messageBody;
    }
}

