package lab10;
import org.eclipse.jetty.websocket.api.*;
import org.json.*;


import java.text.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static j2html.TagCreator.*;
import static spark.Spark.*;

public class Chat {

    
    private static Map<Session, String> usernames = new ConcurrentHashMap<>();
    private static Map<String, String> userToChannel = new ConcurrentHashMap<>();
    private static List<String> channels = new CopyOnWriteArrayList<String>();
    private static AtomicInteger nextChannelNumber = new AtomicInteger(0);
    
    public static void main(String[] args) {
        staticFiles.location("/public"); //index.html is served at localhost:4567 (default port)
        staticFiles.expireTime(600);
        channels.add("chatbot");
        webSocket("/chat", ChatWebSocketHandler.class);
        init();
    }

    public static void refresh(){
    	usernames.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                		.put("userMessage", "")
                		.put("channellist", channels)
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
 	   });
    }
    public static void broadcastMessage(String sender, String message, String channel) {
   	   usernames.keySet().stream().filter(Session::isOpen)
   	   .filter(session -> {
   		   try{
   			   return userToChannel.get( usernames.get(session) ).equals(channel);
   		   }catch(NullPointerException ex){return false;}
   	   })
   	   .forEach(session -> {
   		   try {
   			   session.getRemote().sendString(String.valueOf(new JSONObject()
   					   .put("userMessage", createHtmlMessageFromSender(sender, message))
   					   .put("channellist", channels)
   					   ));
   		   } catch (Exception e) {
   			   e.printStackTrace();
   		   }
       });
       
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }
    
    public static void addChannel(){
    	String channel = "channel " + nextChannelNumber.incrementAndGet();
    	channels.add(channel);
    }
    

    public static void addUserToChannel(String username, String channel){
        if(userToChannel.containsKey(username)){
            removeUserFromChannel(username);
            
        }
        userToChannel.put(username, channel);
        broadcastMessage(channel, (username + " joined " + channel), channel);
    }

    public static boolean addUsername(Session user, String username){//!!!!
    	try{
    		if(usernames.containsValue(username)){
    			user.getRemote().sendString(String.valueOf(new JSONObject()
                   					.put("userMessage", "TAKEN_USERNAME") ) );
    			return false;
    		}else{
    			usernames.put(user, username);
    			return true;
    		}
    	} catch(Exception ex){return false;}
    		
    }
    
    public static void removeUserFromChannel(String username){
    	String channelLeft = userToChannel.get(username);
    	userToChannel.remove(username);
    	broadcastMessage(channelLeft, (username + " left the " + channelLeft), channelLeft);
    }
    
	public static void removeUser(Session user) {
		String username = usernames.get(user);
		removeUserFromChannel(username);
		usernames.remove(user);
	}

	public static Map<Session, String> getUsernames() {
		return usernames;
	}

	public static Map<String, String> getUserToChannel() {
		return userToChannel;
	}

	public static void refreshForUser(Session user) {
        try {
        	user.getRemote().sendString(String.valueOf(new JSONObject()
               		.put("userMessage", "")
               		.put("channellist", channels)
        			));
        } 
        catch (Exception e) {
        	e.printStackTrace();
        }
		
	}

	public static void sendMessageToUser(Session user, String message) {
		try {
        	user.getRemote().sendString(String.valueOf(new JSONObject()
               		.put("userMessage", createHtmlMessageFromSender("Server", message))
               		.put("channellist", channels)
        			));
        } 
        catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void askChatbot(String question){
		String answer = Chatbot.getAnswer(question);
		broadcastMessage("chatbot", answer, "chatbot");
	}
    
}