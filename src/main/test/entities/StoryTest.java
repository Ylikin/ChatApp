package entities;

import client.ClientEndpoint;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class StoryTest {
    private WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    private static final String SERVER = "ws://localhost:8080/webChatApp_war/chat";
    private static Story story;

    @Before
    public  void setUp() {
        story=new Story();

    }

    @Test
    public void check_add_and_print_Story() throws IOException, DeploymentException {
        Session session1 = container.connectToServer(mock(ClientEndpoint.class), URI.create(SERVER));
        Session session2 = container.connectToServer(mock(ClientEndpoint.class), URI.create(SERVER));
        Message msg=mock(Message.class);
        String el="qwe";
        story.addStory(el,session1);
        story.printStory(session1,session2,msg);
        assertNotNull(msg);
    }
}