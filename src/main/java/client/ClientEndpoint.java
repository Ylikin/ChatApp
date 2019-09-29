package client;


import coders.MessageDecoder;
import coders.MessageEncoder;
import entities.Message;


import javax.websocket.*;


@javax.websocket.ClientEndpoint(decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class ClientEndpoint {

    @OnOpen
    public void onOpen(Session session) {


    }

    @OnMessage
    public void onMessage(Session session, Message msg) {
        System.out.println(msg.getRole() + "| " + msg.getName() + ": " + msg.getText());

    }
//you can log exceptions here
    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }
}
