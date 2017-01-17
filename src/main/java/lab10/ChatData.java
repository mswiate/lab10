package lab10;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.websocket.api.Session;

public class ChatData {
	
	
	private static Map<Session, String> usernames = new ConcurrentHashMap<>();
    private static Map<String, String> userToChannel = new ConcurrentHashMap<>();
    private static List<String> channels = new CopyOnWriteArrayList<String>();
    private static AtomicInteger nextChannelNumber = new AtomicInteger(0);
    private static Chatbot chatbot = new Chatbot();
    
    
    
   
   
    
    public static void addChannel(){
    	String channel = "channel " + nextChannelNumber.incrementAndGet();
    	channels.add(channel);
    }
    

    public static void addUserToChannel(String username, String channel){
        userToChannel.put(username, channel);
    }
  
    public static void addUsername(Session user, String username){//!!!!
    	if(!usernames.containsValue(username))
    		usernames.put(user, username);
    	else
    		throw new IllegalArgumentException();
    }
    
    public static void removeUserFromChannel(String username){
    	if(userToChannel.containsKey(username))
    		userToChannel.remove(username);
    	else
    		throw new NoSuchElementException();
    }
    
	public static void removeUser(Session user) {
		String username = usernames.get(user);
		if(isUserToChannel(username))
			removeUserFromChannel(username);
		
		usernames.remove(user);
	}

	public static Map<Session, String> getUsernames() {
		return usernames;
	}

	public static Map<String, String> getUserToChannel() {
		return userToChannel;
	}

	

	public static String getUsersChannel(String username){
		if(userToChannel.containsKey(username))
			return userToChannel.get(username);
		else
			throw new NoSuchElementException();
	}
	
	public static List<String> getChannels() {
		return channels;
	}

	public static boolean isUserToChannel(String username){
		if(userToChannel.containsKey(username))
			return true;
		else 
			return false;
	}
	
	public static String getUsername(Session user){
		if(usernames.containsKey(user))
			return usernames.get(user);
		return "";
	}
	
	public static String getChatbotAnswer(String question){
		String answer = chatbot.getAnswer(question);
		return answer;
	}
}
