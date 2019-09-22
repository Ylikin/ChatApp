package entities;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {
    public Message(String name, String role, String text) {
        this.name = name;
        this.role = role;
        this.text = text;
    }

    private String name;
    private String role;
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getRole() {
        return role;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }


}

