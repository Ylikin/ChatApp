package entities;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Story {

    private static LinkedHashMap<Session, LinkedList<String>> storyMap = new LinkedHashMap<>();

    public void addStory(String el, Session session1) {
        storyMap.computeIfAbsent(session1, k -> new LinkedList<>()).add(el);

    }

    public void printStory(Session session1, Session session2, Message msg) {

        if (storyMap.size() > 0 && storyMap.containsKey(session1)) {
            try {
                for (String vr : storyMap.get(session1)) {
                    msg.setText(vr);
                    session2.getBasicRemote().sendObject(msg);
                }


            } catch (IOException ignored) {

            } catch (EncodeException e) {
                e.printStackTrace();
            }
            storyMap.remove(session1);

        }

    }
}
