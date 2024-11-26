module org.example.chatbot {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.chatbot to javafx.fxml;
    exports org.example.chatbot;
}