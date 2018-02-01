package com.andreiruse.sampleWebTwitterImpl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/**
 * POJO representing a message posted by a user. Includes the author of the message, the message content, and the creation time.
 * <p>
 * As the author field does not get serialized to JSON, it also includes a username field - this is extracted from the author's data.
 */
public class Message {
    @JsonIgnore
    private final User author; //Serializing this in JSON will result in a cycle between
    private final String username;
    private final String content;
    private final LocalDateTime createdAt;

    public Message(User author, String content) {
        this.author = author;
        this.username = author.getUsername();
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * The only condition we have for a message to be valid is that it should be up to 140 characters long.
     *
     * @return true if the message is valid, false otherwise
     */
    public boolean isValid() {
        return this.content.length() <= 140;
    }

    public User getAuthor() {
        return author;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
