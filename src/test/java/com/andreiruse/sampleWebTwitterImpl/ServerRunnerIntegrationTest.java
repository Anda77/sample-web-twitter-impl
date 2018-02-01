package com.andreiruse.sampleWebTwitterImpl;

import okhttp3.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This integration test fires up Spring Boot in order to test the HTTP functionality, parameters handling, etc.
 * The assumption is that the server will run on port 8080 (SpringBoot default)
 */
public class ServerRunnerIntegrationTest {
    private ExecutorService executorService;

    /**
     * This will start the server by calling the main method on {@link ServerRunner}.
     * It will also block the main thread, until the server /status endpoint is responding, and the server is up. This is to prevent tests from failing for connections reasons.
     */
    @Before
    public void setUp() {
        executorService = Executors.newFixedThreadPool(1);
        Runnable serverRunnerThread = () -> ServerRunner.main(new String[0]);
        executorService.submit(serverRunnerThread);

        while (httpGetStatusCode("http://localhost:8080/status") != HttpStatus.OK.value()) {
            System.out.println("Still waiting for the server to be up and running...");
        }
        System.out.println("Server started and available");
    }

    @After
    public void tearDown() {
        System.out.println("Shutting down server");
        executorService.shutdownNow();
    }

    @Test
    public void testSimulateUsage() throws IOException {
        OkHttpClient client = new OkHttpClient();
        //Get wall of a missing user, check 404 is returned
        Request getWallUser0Request = new Request.Builder()
                .url("http://localhost:8080/users/testUser0/wall")
                .build();
        Response getWallUser0Response = client.newCall(getWallUser0Request).execute();
        assertEquals(HttpStatus.NOT_FOUND.value(), getWallUser0Response.code());

        //Create a new user, testUser1
        RequestBody newUser1RequestBody = RequestBody.create(MediaType.parse("application/json"), "");
        Request newUser1Request = new Request.Builder()
                .url("http://localhost:8080/users/testUser1")
                .post(newUser1RequestBody)
                .build();
        Response newUser1Response = client.newCall(newUser1Request).execute();
        assertEquals(HttpStatus.CREATED.value(), newUser1Response.code());

        //Get wall, check empty
        Request getWallUser1Request = new Request.Builder()
                .url("http://localhost:8080/users/testUser1/wall")
                .build();
        Response getWallUser1Response = client.newCall(getWallUser1Request).execute();
        assertEquals(HttpStatus.OK.value(), getWallUser1Response.code());
        assertEquals("[]", getWallUser1Response.body().string());

        //Get timeline, check empty
        Request getTimelineUser1Request = new Request.Builder()
                .url("http://localhost:8080/users/testUser1/timeline")
                .build();
        Response getTimelineUser1Response = client.newCall(getTimelineUser1Request).execute();
        assertEquals(HttpStatus.OK.value(), getTimelineUser1Response.code());
        assertEquals("[]", getTimelineUser1Response.body().string());

        //Post message as 'testUser1'
        RequestBody postMessageUser1RequestBody = RequestBody.create(MediaType.parse("application/json"), "{ \"username\" : \"testUser1\", \"messageBody\" : \"Test message\"}");
        Request postMessageUser1Request = new Request.Builder()
                .url("http://localhost:8080/message")
                .post(postMessageUser1RequestBody)
                .build();
        Response postMessageUser1Response = client.newCall(postMessageUser1Request).execute();
        assertEquals(HttpStatus.CREATED.value(), postMessageUser1Response.code());

        //Get wall of testUser1, check message exists
        Request getWallUser1Request2 = new Request.Builder()
                .url("http://localhost:8080/users/testUser1/wall")
                .build();
        Response getWallUser1Response2 = client.newCall(getWallUser1Request2).execute();
        assertEquals(HttpStatus.OK.value(), getWallUser1Response2.code());
        assertTrue(getWallUser1Response2.body().string().startsWith("[{\"username\":\"testUser1\",\"content\":\"Test message\",\"createdAt\":{")); //As the time component will always change

        //Create a second user, testUser2
        RequestBody newUser2RequestBody = RequestBody.create(MediaType.parse("application/json"), "");
        Request newUser2Request = new Request.Builder()
                .url("http://localhost:8080/users/testUser2")
                .post(newUser2RequestBody)
                .build();
        Response newUser2Response = client.newCall(newUser2Request).execute();
        assertEquals(HttpStatus.CREATED.value(), newUser2Response.code());

        //Post something as the 2nd user
        RequestBody postMessageUser2RequestBody = RequestBody.create(MediaType.parse("application/json"), "{ \"username\" : \"testUser2\", \"messageBody\" : \"Test message 2\"}");
        Request postMessageUser2Request = new Request.Builder()
                .url("http://localhost:8080/message")
                .post(postMessageUser2RequestBody)
                .build();
        Response postMessageUser2Response = client.newCall(postMessageUser2Request).execute();
        assertEquals(HttpStatus.CREATED.value(), postMessageUser2Response.code());

        //Get wall as user 2, check not empty
        Request getWallUser2Request = new Request.Builder()
                .url("http://localhost:8080/users/testUser1/wall")
                .build();
        Response getWallUser2Response = client.newCall(getWallUser2Request).execute();
        assertEquals(HttpStatus.OK.value(), getWallUser2Response.code());
        assertTrue(getWallUser2Response.body().string().startsWith("[{\"username\":\"testUser1\",\"content\":\"Test message\",\"createdAt\":{"));

        //First user follows second user
        RequestBody user1FollowsUser2RequestBody = RequestBody.create(MediaType.parse("application/json"), "");
        Request user1FollowsUser2Request = new Request.Builder()
                .url("http://localhost:8080/users/testUser1/follow/testUser2")
                .post(user1FollowsUser2RequestBody)
                .build();
        Response user1FollowsUser2Response = client.newCall(user1FollowsUser2Request).execute();
        assertEquals(HttpStatus.CREATED.value(), user1FollowsUser2Response.code());

        //First user checks timeline
        Request getTimelineUser1Request2 = new Request.Builder()
                .url("http://localhost:8080/users/testUser1/timeline")
                .build();
        Response getTimelineUser1Response2 = client.newCall(getTimelineUser1Request2).execute();
        assertEquals(HttpStatus.OK.value(), getTimelineUser1Response2.code());
        assertTrue(getTimelineUser1Response2.body().string().startsWith("[{\"username\":\"testUser2\",\"content\":\"Test message 2\",\"createdAt\":{"));
    }


    private int httpGetStatusCode(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response;
        //Handle connection timeouts by returning a fake -1 code
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return -1;
        }
        return response.code();
    }

}