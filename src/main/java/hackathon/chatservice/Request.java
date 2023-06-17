package hackathon.chatservice;

import lombok.Data;

import java.util.List;

@Data
public class Request {
    private String answer;
    private List<Category> instructions;
}
