# sample-web-twitter-impl

This repository contains a sample social networking application, that exposes its functionality through a HTTP/REST API.

It allows:
* Creating new users
* Following other users
* Posting a message (up to 140 characters)
* Listing own messages ("the wall")
* Listing messages posted by followed users ("the timeline")

It does not handle:
* authentication
* persistent storage

*Requirements*

The application has been implemented as a Maven project, and has few dependencies. It compiles using Java 9.
It relies on the SpringBoot framework for the HTTP interaction, parameter mapping, and response management. It also relies on Spring to do the auto-wiring between the controllers, and to handle the data store.
It also includes JUnit 4 and OkHttp, for testing purposes.

It has been developed on a Windows machine, but there is no OS-specific dependency - should run fine on Linux / MacOS. It will require an Internet connection to download the Maven dependencies, but it does not require a network connection to run.

*How to run*

To run the server, go to the `ServerRunner` class and run the main() method.
To see a typical usage scenario, we have included a comprehensive integration test, which fires up the server, performs various requests, checks the responses, then shuts down the server. To run this integration test, check `ServerRunnerIntegrationTest`.

