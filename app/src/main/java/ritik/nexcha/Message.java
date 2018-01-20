package ritik.nexcha;

/**
 * Created by SuperUser on 07-06-2017.
 */

public class Message {
    private String name;
    private String message;
    private int typing;
    private String color;
    private int sequence;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTyping() {
        return typing;
    }

    public void setTyping(int typing) {
        this.typing = typing;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
