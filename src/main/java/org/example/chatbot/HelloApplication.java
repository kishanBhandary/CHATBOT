package org.example.chatbot;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;


public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Root Layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Chat Display Area
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setStyle("-fx-control-inner-background: rgb(255,255,255); -fx-text-fill: #000000; -fx-font-size: 14px;-fx-font-weight: bold;");

        // Input Field
        TextField inputField = new TextField();
        inputField.setPromptText("Type your message...");
        inputField.setStyle("-fx-background-color: rgb(255,255,255); -fx-text-fill: rgb(0,0,0); -fx-font-size: 20px; -fx-font-weight: bold;");

        // Send Button
        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #5b9ef5; -fx-text-fill: rgb(255,255,255); -fx-font-size: 20px; -fx-font-weight: bold;");

        // Input Area
        HBox inputArea = new HBox(10, inputField, sendButton);
        inputArea.setPadding(new Insets(10));
        inputArea.setStyle("-fx-background-color: rgb(255,255,255);");

        // Add Components to Layout
        root.setCenter(chatArea);
        root.setBottom(inputArea);

        // Set custom border styling for the entire layout
        root.setStyle("-fx-border-color: #ffffff; -fx-border-width: 4; -fx-border-style:  dotted; -fx-background-color: #ffffff;");

        // Scene Setup
        Scene scene = new Scene(root, 600, 400);
        scene.setFill(Color.WHITESMOKE);
        primaryStage.setTitle("ChullBull ChatBot");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial Greeting
        chatArea.appendText("ChatBot: Hello! How can I help you today?\n");

        // Button Action
        sendButton.setOnAction(e -> processInput(inputField, chatArea));

        // Enter Key Action
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                processInput(inputField, chatArea);
            }
        });
    }

    private void processInput(TextField inputField, TextArea chatArea) {
        String userInput = inputField.getText().trim().toLowerCase();
        if (userInput.isEmpty()) return;

        chatArea.appendText("You: " + userInput + "\n");
        inputField.clear();

        if ("exit".equals(userInput)) {
            chatArea.appendText("ChatBot: Goodbye! Have a great day!\n");
            System.exit(0);
        }

        switch (userInput.toLowerCase()) {
            case "hello":
            case "hi":
            case "hyy":
            case "Hyy":
            case "Hello":
                chatArea.appendText("ChatBot: Hello! How are you?\n");
                break;
            case "what is your name":
            case "name":
            case "Name pls":
            case "What is your Name":
                chatArea.appendText("ChatBot: My name is ChatBot everyone call me ChullBull!\n");
                break;
            case  "what is the time":
            case "Whats the time now":
            case "Can you tell me the current time?":
            case "What time is it":
            case "Time please ":
                chatArea.appendText("ChatBot: " + getCurrentTime() + "\n");
                break;
            case "set budget":
                chatArea.appendText("ChatBot: Please specify the budget (e.g., 'set budget 5000').\n");
                break;
            case "ask me a trivia question":
                chatArea.appendText("ChatBot: " + askTriviaQuestion() + "\n");
                break;
            case " what is the date" :
            case "what is the date":
            case "whatisthedate":
            case "What's today's date":
            case "What date is it":
            case "Can you tell me the date":
            case "Give me the current date and time":
                    chatArea.appendText("ChatBot:"+getCurrentDate());
                break;
            case "generate gmail":
            case "Generate Gmail":
            case "Pls Generate Gmail":
            case "pls generate gmail":
                chatArea.appendText("ChatBot: Please tell me your first name to generate a Gmail address.\n");
                break;
            case "tell me a joke":
            case "joke":
                chatArea.appendText("ChatBot: Fetching a joke...\n");
                chatArea.appendText("ChatBot: " + fetchJoke() + "\n");
                break;
            case "show calender":
            case "calender":
                chatArea.appendText("ChatBot:calender"+showCurrentMonthCalendar());
            default:
                if (userInput.startsWith("convert currency")) {
                    String[] parts = userInput.split(" ");
                    if (parts.length == 5) {
                        try {
                            double amount = Double.parseDouble(parts[2]);
                            String fromCurrency = parts[3].toUpperCase();
                            String toCurrency = parts[4].toUpperCase();
                            chatArea.appendText("ChatBot: " + convertCurrency(fromCurrency, toCurrency, amount) + "\n");
                        } catch (Exception e) {
                            chatArea.appendText("ChatBot: Invalid format. Use 'convert currency [amount] [fromCurrency] [toCurrency]'.\n");
                        }
                    } else {
                        chatArea.appendText("ChatBot: Invalid format. Use 'convert currency [amount] [fromCurrency] [toCurrency]'.\n");
                    }
                }


                if (userInput.startsWith("my name is")) {
                    String name = userInput.replace("my name is", "").trim();
                    chatArea.appendText("ChatBot: Great! Now generating your Gmail address for " + name + "...\n");
                    chatArea.appendText("ChatBot: " + generateRandomGmail(name) + "\n");
                }
                if (userInput.startsWith("set budget")) {
                    chatArea.appendText("ChatBot: " + setBudget(userInput) + "\n");
                } else if (userInput.startsWith("add expense")) {
                    chatArea.appendText("ChatBot: " + addExpense(userInput) + "\n");
                }
                else if (isArithmeticExpression(userInput)) {
                    chatArea.appendText("ChatBot: " + calculateExpression(userInput) + "\n");
                }
                else {
                    chatArea.appendText("ChatBot: I'm not sure how to respond to that.\n");
                }
        }
    }

    private String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return "The current time is " + LocalDateTime.now().format(formatter);
    }

    private String setBudget(String userInput) {
        try {
            String[] parts = userInput.split(" ");
            double budget = Double.parseDouble(parts[2]);
            return "Your budget has been set to " + budget;
        } catch (Exception e) {
            return "Invalid format. Use 'set budget [amount]'.";
        }
    }

    private String addExpense(String userInput) {
        try {
            String[] parts = userInput.split(" ", 3);
            double expense = Double.parseDouble(parts[2]);
            return "Expense of " + expense + " added successfully.";
        } catch (Exception e) {
            return "Invalid format. Use 'add expense [amount]'.";
        }
    }

    private String askTriviaQuestion() {
        String[] questions = {
                "What is the capital of France?",
                "Who wrote 'To Kill a Mockingbird'?",
                "What is the tallest mountain in the world?"
        };
        return questions[(int) (Math.random() * questions.length)];
    }
    public String getCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd--MM--yyy");
        return "Current date is: " + now.format(dateFormatter);
    }
    private boolean isArithmeticExpression(String input) {
        return input.matches(".*[\\d]+[\\+\\-\\*/][\\d]+.*");
    }

    private String calculateExpression(String expression) {
        try {
            String[] tokens = expression.split(" ");
            double num1 = Double.parseDouble(tokens[0]);
            String operator = tokens[1];
            double num2 = Double.parseDouble(tokens[2]);

            double result = 0;
            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 != 0) {
                        result = num1 / num2;
                    } else {
                        return "ChatBot: Cannot divide by zero!";
                    }
                    break;
                default:
                    return "ChatBot: Invalid operator.";
            }
            return "Result: " + result;
        } catch (Exception e) {
            return "ChatBot: Invalid mathematical expression. Please use the format 'number operator number'.";
        }
    }
    private String generateRandomGmail(String firstName) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder email = new StringBuilder(firstName.toLowerCase());
        Random random = new Random();
        email.append(random.nextInt(1000)); // Appending a random number to avoid duplication
        email.append("@gmail.com");
        return email.toString();
    }



    private String fetchJoke() {
        String apiUrl = "https://v2.jokeapi.dev/joke/Any?type=single";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getString("joke");
            } else {
                return "Unable to fetch a joke at the moment.";
            }
        } catch (Exception e) {
            return "Error: Unable to fetch a joke. Please try again.";
        }
    }
    private String convertCurrency(String fromCurrency, String toCurrency, double amount) {
        String apiKey = "d5aee23ad4641ac4e951385a";
        String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + fromCurrency + "/" + toCurrency;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                double conversionRate = jsonResponse.getDouble("conversion_rate");
                double convertedAmount = amount * conversionRate;

                return "Converted " + amount + " " + fromCurrency + " to " + convertedAmount + " " + toCurrency + ".";
            } else {
                return "Unable to fetch exchange rate. Please try again.";
            }
        } catch (Exception e) {
            return "Error: Unable to fetch exchange rate. Please check the currency codes.";
        }
    }


    // Generate a text-based calendar for the given month
    private String showCurrentMonthCalendar() {
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(today);
        return displayCalendar(yearMonth);
    }

    // Generate a text-based calendar for the given month
    private String displayCalendar(YearMonth yearMonth) {
        StringBuilder calendarBuilder = new StringBuilder();
        calendarBuilder.append("Calendar for ").append(yearMonth.getMonth()).append(" ").append(yearMonth.getYear()).append("\n");
        calendarBuilder.append("Sun   Mon   Tue   Wed   Thu   Fri   Sat\n");

        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday

        // Padding the first row with spaces to align the first day properly
        for (int i = 1; i < dayOfWeekValue; i++) {
            calendarBuilder.append("     "); // Adding space for the empty days
        }

        // Building the days of the month
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            // Adjusting formatting for proper alignment
            if (day < 10) {
                calendarBuilder.append("    ").append(day).append("    ");  // Single-digit dates have extra spaces
            } else {
                calendarBuilder.append("    ").append(day).append("    ");   // Double-digit dates
            }

            // Start a new line after Saturday (7th day of the week)
            if ((day + dayOfWeekValue - 1) % 7 == 0) {
                calendarBuilder.append("\n");
            }
        }

        // Handle the last row (if the last day of the month is not Saturday)
        calendarBuilder.append("\n");
        return calendarBuilder.toString();
    }


    public static void main(String[] args) {
        launch(args);
    }
}