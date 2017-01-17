package lab10;

import java.util.NoSuchElementException;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

public class ChatFunctions {
		
		private HTMLMaker htmlMaker = new HTMLMaker(); 
	
		public void refresh(){
	    	ChatData.getUsernames().keySet().stream().filter(Session::isOpen).forEach(session -> {
	            try {
	                session.getRemote().sendString(String.valueOf(new JSONObject()
	                		.put("reason", "refresh")
	                		.put("channellist", ChatData.getChannels())
	                ));
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	 	   });
	    }
	    
	    public  void refreshForUser(Session user) {
	        try {
	        	user.getRemote().sendString(String.valueOf(new JSONObject()
	        			.put("reason", "refresh")
	               		.put("channellist", ChatData.getChannels())
	        			));
	        } 
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
			
		}
	    
	    public void sendUsersMessage(Session user, String contents) {
        	String username = ChatData.getUsername(user);
        	String channel = ChatData.getUsersChannel(username);
        	broadcastMessage(username, contents, channel);
        	if(channel.equals("chatbot"))
        		askChatbot(contents);

		}
	    
	    public void askChatbot(String question){
	    	String answer = ChatData.getChatbotAnswer(question);
	    	broadcastMessage("chatbot", answer, "chatbot");
	    }
	    
	    public void broadcastMessage(String sender, String message, String channel) {
	   	   ChatData.getUsernames().keySet().stream().filter(Session::isOpen)
	   	   .filter(session -> {
	   		   try{
	   			   return ChatData.getUsersChannel( ChatData.getUsername(session) ).equals(channel);
	   		   }catch(NoSuchElementException ex){return false;}
	   	   })
	   	   .forEach(session -> {
	   		   try {
	   			   session.getRemote().sendString(String.valueOf(new JSONObject()
	   					   .put("reason", "message")
	   					   .put("userMessage", htmlMaker.createHtmlMessageFromSender(sender, message))
	   					   .put("channellist", ChatData.getChannels())
	   					   ));
	   		   } catch (Exception e) {
	   			   e.printStackTrace();
	   		   }
	       });
	       
	    }
	    
	    public void sendMessageToUser(Session user, String message) {
			try {
	        	user.getRemote().sendString(String.valueOf(new JSONObject()
	        			.put("reason", "message")
	               		.put("userMessage", htmlMaker.createHtmlMessageFromSender("Server", message))
	               		.put("channellist", ChatData.getChannels())
	        			));
	        } 
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
	    
	    public void addUserToChannel(Session user, String channel){
	    	String username = ChatData.getUsername(user);
	    	
	    	if(ChatData.isUserToChannel(username))
	    		removeUserFromChannel(user);
	    
	    	ChatData.addUserToChannel(username, channel);
	        broadcastMessage(channel, username + " joined", channel);
	        
	    }
	    	
	    public void removeUserFromChannel(Session user){
	    	String username = ChatData.getUsername(user);
	    	
	    	String channel = ChatData.getUsersChannel(username);
	    	ChatData.removeUserFromChannel(username);
	    	broadcastMessage(channel, username + " left", channel);
	    	
	    	
	    }
	    
	    public void removeUser(Session user){
	    	ChatData.removeUser(user);
	    }

		
}
