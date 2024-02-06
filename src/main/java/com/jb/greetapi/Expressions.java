package com.jb.greetapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Expressions {
    private static final Gson gson = new Gson();

   public static void main(String[] args) {
       String apiUrl = "https://raw.githubusercontent.com/arcjsonapi/expressionDataService/main/test1";
       String apiResponse = fetchApiResponse(apiUrl);
       evaluateAllGroups(apiResponse);
   }

    public static String fetchApiResponse(String apiUrl) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching API response: " + e.getMessage());
        }
        return response.toString();
    }

    public static void evaluateAllGroups(String apiResponse) {
        try {
            JsonArray apiResponseJson = gson.fromJson(apiResponse, JsonArray.class);

            int maxGroupsInMemory = 10;
            for (int i = 0; i < Math.min(apiResponseJson.size(), maxGroupsInMemory); i++) {
                JsonObject group = apiResponseJson.get(i).getAsJsonObject();
                evaluateGroup(group);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void evaluateGroup(JsonObject group) {
        String groupName = group.get("groupName").getAsString();
        JsonArray expressions = group.getAsJsonArray("expressions");

        Map<String, String> expressionResults = new HashMap<>();
        Map<String, String> temp = new HashMap<>();

        for (int i = 0; i < expressions.size(); i++) {
            JsonObject expressionObj = expressions.get(i).getAsJsonObject();
            String expressionName = expressionObj.get("name").getAsString();
            String expressionType = expressionObj.get("expressionType").getAsString();
            String expression = expressionObj.get("expression").getAsString();
            JsonArray jasonArray = expressionObj.getAsJsonArray("dependencies");
            String depend = "";
            if (!jasonArray.isEmpty()) {
                depend = jasonArray.getAsString();
                    String variable = "${" + depend + "}";
                    expression = expression.replace(variable, temp.get(depend));
                String[] nums = expression.replaceAll("\\s", "").split("\\+|(?=-)");
                expression = String.valueOf(Arrays.stream(nums).mapToInt(Integer::parseInt).sum());
            }
            temp.put(expressionName, expression);
            // Evaluate the expression based on its type
            String result = evaluateExpression(expressionType, expression, expressionResults);

            // Store the result for later references
            expressionResults.put(expressionName, result);

            System.out.println("Group: " + groupName
                    +", expressionName: "+expressionName
                    +", expressionType: "+expressionType
                    +", expression: "+expression);
        }

        //expressionResults.forEach((name, result) -> System.out.println(name + ": " + result));
        System.out.println();
    }

    public static String evaluateExpression(String expressionType, String expression, Map<String, String> expressionResults) {
        // Replace ${expression name} with its result from the map
        // Replace ${expression name} with its result from the map
        return switch (expressionType) {
            case "DIRECT" -> expression;
            case "DOLLAR_EXPRESSION" -> {
                for (Map.Entry<String, String> entry : expressionResults.entrySet()) {
                    String variable = "${" + entry.getKey() + "}";
                    expression = expression.replace(variable, entry.getValue());
                }
                yield "(" + expression + ")$";
            }
            case "RS_EXPRESSION" -> {
                for (Map.Entry<String, String> entry : expressionResults.entrySet()) {
                    String variable = "${" + entry.getKey() + "}";
                    expression = expression.replace(variable, entry.getValue());
                }
                yield "(" + expression + ") RS";
            }
            default -> throw new IllegalArgumentException("Unsupported expression type: " + expressionType);
        };
    }

}
