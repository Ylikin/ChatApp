package coders;


import com.google.gson.Gson;
import entities.Message;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class MessageEncoderTest {
    private static Gson gson = new Gson();
    @Test
    public void encode()  {
        Message msg=new Message("qwerty","agent","qq");
        MessageEncoder obj1=new MessageEncoder();
        assertEquals(gson.toJson(msg),obj1.encode(msg));
    }
}