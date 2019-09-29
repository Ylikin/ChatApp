package endpoints;

import coders.MessageDecoder;
import coders.MessageEncoder;
import entities.Message;
import entities.Story;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;


@ServerEndpoint(value = "/chat", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class ChatEndpoint {
    private static final Logger log = LogManager.getLogger(ChatEndpoint.class);
    private Story story = new Story();
    private Session agentSession = null;

    private static LinkedList<Session> sessionList = new LinkedList<>();//as this collections static, to my mind here should be used concurrent collectionsversion  
    private static LinkedList<Session> sessionListAvailableAgents = new LinkedList<>();
    private static LinkedHashMap<Session, Session> chatMap = new LinkedHashMap<>();


    @OnOpen
    public void onOpen(Session session) {
        log.info("Session opened :"+session);
        sessionList.add(session);

    }

    @OnClose
    public void onClose(Session session) {
        sessionList.remove(session);

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("Error :"+throwable);
        sessionList.remove(session);
        sessionListAvailableAgents.remove(session);
        chatMap.remove(session);
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, Message msg) {
        if(msg.getText().equals("")){
            log.info(msg.getRole()+"|"+msg.getName()+" registered");
        }
        //you can create field for role and name and don`t check them every time
        if (msg.getRole().equals("agent") && !sessionListAvailableAgents.contains(session) && !chatMap.containsKey(session)) {
            sessionListAvailableAgents.add(session);
            Collections.shuffle(sessionListAvailableAgents);
        }
        if (msg.getText().equals("/exit")) {
            exit(session, msg);
        } else {
            if (msg.getRole().equals("agent") && chatMap.containsKey(session)) {
                try {
                    chatMap.get(session).getBasicRemote().sendObject(msg);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
            if (msg.getRole().equals("client") && leave(session,msg)) {
                logIn(session,msg);
            }
        }
    }

    private void exit(Session session, Message msg) {

        if (msg.getRole().equals("client") && chatMap.containsKey(this.agentSession)) {
            msg.setText("Disconnected");
            try {
                this.agentSession.getBasicRemote().sendObject(msg);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
            chatMap.remove(agentSession);
            sessionListAvailableAgents.add(agentSession);
            Collections.shuffle(sessionListAvailableAgents);
            log.info("Client :"+msg.getName()+" /exit: ");
        } else if (msg.getRole().equals("agent") && chatMap.containsKey(session)) {
            msg.setText("Disconnected");
            try {
                chatMap.get(session).getBasicRemote().sendObject(msg);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
            chatMap.remove(session);
            log.info("Agent :"+msg.getName()+" /exit : ");
        }
        else
        {
            if(msg.getRole().equals("client")) {
                log.info("Client :" + msg.getName() + " /exit: ");
            }
            else if(msg.getRole().equals("agent"))
            {
                log.info("Agent :"+msg.getName()+" /exit : ");
            }
        }
        onClose(session);
    }
    private void logIn(Session session,Message msg){
        Collections.shuffle(sessionListAvailableAgents);
        if (sessionList.contains(session) && !sessionListAvailableAgents.isEmpty() && !chatMap.containsKey(this.agentSession)) {
            this.agentSession = sessionListAvailableAgents.get(0);
            chatMap.put(this.agentSession, session);
            String msg1 = msg.getText();
            msg.setText("Connected");
            log.info("Agent connected to client: "+msg.getName());
            try {
                this.agentSession.getBasicRemote().sendObject(msg);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
            story.printStory(session, this.agentSession, msg);
            msg.setText(msg1);
            sessionListAvailableAgents.remove(0);
        }
        if (chatMap.get(this.agentSession) != session) {
            if (!msg.getText().equals("Connected") && !msg.getText().equals("")) {

                story.addStory(msg.getText(), session);
                msg.setName("");
                msg.setRole("");
                msg.setText("Agent not connected");
                try {
                    session.getBasicRemote().sendObject(msg);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                if (!msg.getText().equals("")) {
                    this.agentSession.getBasicRemote().sendObject(msg);
                }
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean leave(Session session,Message msg){
        boolean leave = true;
        if (msg.getRole().equals("client") && msg.getText().equals("/leave") && chatMap.containsKey(this.agentSession)) {
            try {
                msg.setText("Disconnected");
                this.agentSession.getBasicRemote().sendObject(msg);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
            log.info("Client :"+msg.getName()+" disconnected");
            chatMap.remove(this.agentSession);
            sessionListAvailableAgents.add(this.agentSession);
            Collections.shuffle(sessionListAvailableAgents);
            leave = false;
        } else if (msg.getRole().equals("client") && msg.getText().equals("/leave") && !chatMap.containsKey(this.agentSession)) {
            try {
                msg.setName("");
                msg.setText("Agent not connected");
                session.getBasicRemote().sendObject(msg);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
            leave = false;
        }
        return leave;
    }
}

