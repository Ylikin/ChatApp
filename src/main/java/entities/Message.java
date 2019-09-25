package entities;

import lombok.Data;

@Data
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

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
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

