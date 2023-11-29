package com.example.homies;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.homies.model.Message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MessageTest {

    private static final String MOCK_USER_ID = "mockUserId";
    private static final String MOCK_MESSAGE_CONTENT = "test";
    private static final long MOCK_TIMESTAMP = 123456789;

    private Message message;

    @Before
    public void setUp() {
        message = new Message(MOCK_USER_ID, MOCK_MESSAGE_CONTENT, MOCK_TIMESTAMP);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(MOCK_USER_ID, message.getSenderUserId());
        assertEquals(MOCK_MESSAGE_CONTENT, message.getMessageContent());
        assertEquals(MOCK_TIMESTAMP, message.getTimestamp());
    }

    @Test
    public void testIsSentByCurrentUser() {
        assertTrue(message.isSentByCurrentUser(MOCK_USER_ID));
        assertFalse(message.isSentByCurrentUser("differentUserId"));
    }

    @Test
    public void testGetSenderDisplayNameSuccess() throws InterruptedException {
        // Mock Firestore success
        Message mockMessage = new Message(MOCK_USER_ID, MOCK_MESSAGE_CONTENT, MOCK_TIMESTAMP) {
            @Override
            public void getSenderDisplayName(DisplayNameCallback callback) {
                callback.onCallback("MockDisplayName");
            }
        };

        // Create a latch to wait for the asynchronous Firestore query
        CountDownLatch latch = new CountDownLatch(1);

        // Perform the asynchronous query
        mockMessage.getSenderDisplayName(displayName -> {
            assertEquals("MockDisplayName", displayName);
            latch.countDown();
        });

        // Wait for the callback to be invoked
        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void testGetSenderDisplayNameFailure() throws InterruptedException {
        // Mock Firestore failure
        Message mockMessage = new Message(MOCK_USER_ID, MOCK_MESSAGE_CONTENT, MOCK_TIMESTAMP) {
            @Override
            public void getSenderDisplayName(DisplayNameCallback callback) {
                callback.onCallback(null);
            }
        };

        // Create a latch to wait for the asynchronous Firestore query
        CountDownLatch latch = new CountDownLatch(1);

        // Perform the asynchronous query
        mockMessage.getSenderDisplayName(displayName -> {
            assertNull(displayName);
            latch.countDown();
        });

        // Wait for the callback to be invoked
        latch.await(5, TimeUnit.SECONDS);
    }
}