package hackathon.chatservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {
    private final GPTService gptService;

    @Autowired
    public ChatController(GPTService gptService) {
        this.gptService = gptService;
    }

    @PostMapping("/openAi")
    public ResponseEntity<Object> getAnswer(@RequestBody Message request) {
        try {
            System.out.println(request.message);
            String response = gptService.sendOpenAIRequest(request.message);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/whichCategory")
    public ResponseEntity<Object> getWhichCategory(@RequestBody CategoryRequest request) {
        try {
            System.out.println("bye" + request.getQuestion());

            Request resp = gptService.whichCategories(request);
            System.out.println("hello");
            return new ResponseEntity<>(resp,HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
}
