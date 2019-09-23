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
    private Session vr1 = null;

    private static LinkedList<Session> sessionList = new LinkedList<>();
    private static LinkedList<Session> sessionListAvailableAgents = new LinkedList<>();
    private static LinkedHashMap<Session, Session> chatList = new LinkedHashMap<>();


    @OnOpen//this thread
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
        chatList.remove(session);
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, Message msg) {
        if(msg.getText().equals("")){
            log.info(msg.getRole()+"|"+msg.getName()+" registered");
        }
        boolean leave = true;
        if (msg.getRole().equals("agent") && !sessionListAvailableAgents.contains(session) && !chatList.containsKey(session)) {
            sessionListAvailableAgents.add(session);
            Collections.shuffle(sessionListAvailableAgents);
        }
        if (msg.getText().equals("/exit")) {
            exit(session, msg);
        } else {
            if (msg.getRole().equals("agent") && chatList.containsKey(session)) {
                try {
                    chatList.get(session).getBasicRemote().sendObject(msg);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
            if (msg.getRole().equals("client") && msg.getText().equals("/leave") && chatList.containsKey(this.vr1)) {
                try {
                    msg.setText("Disconnected");
                    this.vr1.getBasicRemote().sendObject(msg);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
                log.info("Client :"+msg.getName()+" disconnected");
                chatList.remove(this.vr1);
                sessionListAvailableAgents.add(this.vr1);
                Collections.shuffle(sessionListAvailableAgents);
                leave = false;
            } else if (msg.getRole().equals("client") && msg.getText().equals("/leave") && !chatList.containsKey(this.vr1)) {
                try {
                    msg.setName("");
                    msg.setText("Agent not connected");
                    session.getBasicRemote().sendObject(msg);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
                leave = false;
            }
            if (msg.getRole().equals("client") && leave) {
                if (sessionList.contains(session) && !sessionListAvailableAgents.isEmpty() && !chatList.containsKey(this.vr1)) {
                    Collections.shuffle(sessionListAvailableAgents);
                    this.vr1 = sessionListAvailableAgents.get(0);
                    chatList.put(this.vr1, session);
                    String msg1 = msg.getText();
                    msg.setText("Connected");
                    log.info("Agent connected to client: "+msg.getName());
                    try {
                        this.vr1.getBasicRemote().sendObject(msg);
                    } catch (IOException | EncodeException e) {
                        e.printStackTrace();
                    }
                    story.printStory(session, this.vr1, msg);
                    msg.setText(msg1);
                    sessionListAvailableAgents.remove(0);
                }
                if (chatList.get(this.vr1) != session) {
                    if (!msg.getText().equals("Connected") && !msg.getText().equals("")) {

                        story.addStory(msg.getText(), session);
                        msg.setName("");
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
                            this.vr1.getBasicRemote().sendObject(msg);
                        }
                    } catch (IOException | EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }

//sessionList.forEach(s->{
//    if(s==this.session) return;
//    try {
//
//        System.out.println(msg);
//        s.getBasicRemote().sendObject(msg);
//    } catch (IOException | EncodeException e) {
//        e.printStackTrace();
//}
//});
        }
    }

    private void exit(Session session, Message msg) {
        if (msg.getRole().equals("client") && chatList.containsKey(this.vr1)) {
            msg.setText("Disconnected");
            try {
                this.vr1.getBasicRemote().sendObject(msg);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
            chatList.remove(vr1);
            sessionListAvailableAgents.add(vr1);
            Collections.shuffle(sessionListAvailableAgents);
            log.info("Client :"+msg.getName()+" /exit: ");
        } else if (msg.getRole().equals("agent") && chatList.containsKey(session)) {
            msg.setText("Disconnected");
            try {
                chatList.get(session).getBasicRemote().sendObject(msg);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
            chatList.remove(session);
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

}

