package hackathon.chatservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GPTService {
    public String sendOpenAIRequest(String requestBody) throws JSONException {
        System.out.println(requestBody);
        String apiUrl = "https://api.openai.com/v1/completions";

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("model", "text-davinci-003");
        jsonObject1.put("prompt", requestBody);
        jsonObject1.put("max_tokens", 500);
        jsonObject1.put("top_p", 0.1);


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

    public Request whichCategories(CategoryRequest categoryRequest) throws JSONException, JsonProcessingException, JsonProcessingException {
        StringBuilder requestToGPT = new StringBuilder();
        requestToGPT.append("Oпредели к каким из нижеследующих категорий относится вопрос(можно выбрать несколько) если совпадения незначительные ничего не возвращай:\n" +
                "Вопрос:" +
                categoryRequest.getQuestion() + "\n" +
                "Кататегории:\n");
        for (Category category : categoryRequest.getInstructions()) {
            requestToGPT.append("id: " + category.getId() + ", name: " + category.getTitle() + "\n");
        }
        requestToGPT.append("Твой ответ должен начинаться с символа / дальше должен быть список id");

        System.out.println("Запрос :" + requestToGPT.toString());

        String response = sendOpenAIRequest(requestToGPT.toString());


        System.out.println("Ответ : " + response);
        List<Long> numbers = new ArrayList<>();

        String[] numberStrings;
        Request request = new Request();
        try {
            String res = response.split("/")[1];
            numberStrings = res.split(",");
            for (String numberString : numberStrings) {
                try {
                    Long number = Long.parseLong(numberString.trim());
                    numbers.add(number);
                } catch (NumberFormatException e) {
                    // Пропустить неправильные форматы чисел
                }
            }
            List<Category> resCat = new ArrayList<>();
            for (Long id : numbers) {
                Category cat = findCategoryById(categoryRequest.getInstructions(), id);
                if (cat != null) {
                    resCat.add(cat);
                }
            }
            request.setInstructions(resCat);
        } catch (IndexOutOfBoundsException e) {
            request.setInstructions(null);
        }

        String answer = sendOpenAIRequest(categoryRequest.getQuestion());
        request.setAnswer(answer);
        return request;
    }

    private Category findCategoryById(List<Category> categories, Long id) {
        for (Category cat : categories) {
            if (Objects.equals(cat.getId(), id)) return cat;
        }
        return null;
    }

    private String deleteLastWord(String input) {

        String[] words = input.split(" ");

        if (words.length > 0) {
            // Удаление последнего слова
            String[] updatedWords = new String[words.length - 1];
            System.arraycopy(words, 0, updatedWords, 0, words.length - 1);

            String output = String.join(" ", updatedWords);
            System.out.println(output);
            return output;
        } else {
            return input;
        }
    }
}
