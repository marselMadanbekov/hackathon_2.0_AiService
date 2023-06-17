package hackathon.chatservice;

import lombok.Data;

import java.util.List;

@Data
public class CategoryRequest {
    private String question;
    private List<Category> categories;
}
