package coders;

import com.google.gson.Gson;
import entities.Message;
import org.junit.Test;

import javax.websocket.EncodeException;

import static org.junit.Assert.*;


public class MessageEncoderTest {
    private static Gson gson = new Gson();
    @Test
    public void encode() throws EncodeException {
        Message msg=new Message("qwerty","agent","qq");
        MessageEncoder obj1=new MessageEncoder();
        assertEquals(gson.toJson(msg),obj1.encode(msg));
    }
}