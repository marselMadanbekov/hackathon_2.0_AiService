package hackathon.chatservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GPTService {
    public String sendOpenAIRequest(String requestBody) throws JSONException {
        String apiUrl = "https://api.openai.com/v1/completions";

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("model", "text-davinci-003");
        jsonObject1.put("prompt", requestBody);
        jsonObject1.put("max_tokens", 2000);

        // Создание заголовков запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Configs.OPENAI_API_KEY);
        String jsonString = jsonObject1.toString();
        // Создание объекта запроса
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonString, headers);

        // Создание объекта RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Отправка запроса
        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);

        // Обработка ответа
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();

            // Извлечение текста ответа
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray choicesArray = jsonObject.getAsJsonArray("choices");
            if (choicesArray.size() > 0) {
                JsonObject choiceObject = choicesArray.get(0).getAsJsonObject();
                String answerText = choiceObject.get("text").getAsString();

                return answerText;
            } else {
                return "Ответ не найден";
            }
        } else {
            return "Ошибка при отправке запроса";
        }
    }

//    public List<Category>
}
