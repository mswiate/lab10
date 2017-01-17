package lab10;

import static spark.Spark.init;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

public class Chat {	
    public static void main(String[] args) {
        staticFiles.location("/public"); //index.html is served at localhost:4567 (default port)
        staticFiles.expireTime(600);        
        
        ChatData.getChannels().add("chatbot");
        
        webSocket("/chat", ChatWebSocketHandler.class);
        init();
    }

}