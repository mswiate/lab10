package lab10;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.websocket.api.Session;

public class ChatData {
	
	private final Map<Session, String> usernames = new ConcurrentHashMap<>();
    private final Map<String, String> userToChannel = new ConcurrentHashMap<>();
    private final List<String> channels = new CopyOnWriteArrayList<String>();
    private AtomicInteger nextChannelNumber;
    private Chatbot chatbot;
    
    public ChatData(){
    	channels.add("chatbot");
    	chatbot = new Chatbot();
    	nextChannelNumber = new AtomicInteger(0);
    }
    
   
   
    
    public void addChannel(){
    	String channel = "channel " + nextChannelNumber.incrementAndGet();
    	channels.add(channel);
    }
    

    public void addUserToChannel(String username, String channel){
        userToChannel.put(username, channel);
    }
  
    public void addUsername(Session user, String username){//!!!!
    	if(!usernames.containsValue(username))
    		usernames.put(user, username);
    	else
    		throw new IllegalArgumentException();
    }
    
    public void removeUserFromChannel(String username){
    	if(userToChannel.containsKey(username))
    		userToChannel.remove(username);
    	else
    		throw new NoSuchElementException();
    }
    
	public void removeUser(Session user) {
		String username = usernames.get(user);
		if(isUserToChannel(username))
			removeUserFromChannel(username);
		
		usernames.remove(user);
	}

	public Map<Session, String> getUsernames() {
		return usernames;
	}
	
	public Set<Session> getsUsersSessions(){
		return usernames.keySet();
	}

	public Map<String, String> getUserToChannel() {
		return userToChannel;
	}

	

	public String getUsersChannel(String username){
		if(userToChannel.containsKey(username))
			return userToChannel.get(username);
		else
			throw new NoSuchElementException();
	}
	
	public List<String> getChannels() {
		return channels;
	}

	public boolean isUserToChannel(String username){
		if(userToChannel.containsKey(username))
			return true;
		else 
			return false;
	}
	
	public String getUsername(Session user){
		if(usernames.containsKey(user))
			return usernames.get(user);
		return "";
	}
	
	public String getChatbotAnswer(String question){
		String answer = chatbot.getAnswer(question);
		return answer;
	}
}
