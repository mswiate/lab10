package lab10;

import java.util.NoSuchElementException;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

public class ChatFunctions {
		private ChatData chatData = new ChatData();
		private HTMLMaker htmlMaker = new HTMLMaker(); 
	
		public void refresh(){
			chatData.getsUsersSessions().stream().filter(Session::isOpen).forEach(session -> {
	            try {
	                session.getRemote().sendString(String.valueOf(new JSONObject()
	                		.put("reason", "refresh")
	                		.put("channellist", chatData.getChannels())
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
	               		.put("channellist", chatData.getChannels())
	        			));
	        } 
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
			
		}
	    
	    public void sendUsersMessage(Session user, String contents) {
        	String username = chatData.getUsername(user);
        	String channel = chatData.getUsersChannel(username);
        	broadcastMessage(username, contents, channel);
        	if(channel.equals("chatbot"))
        		askChatbot(contents);

		}
	    
	    public void askChatbot(String question){
	    	String answer = chatData.getChatbotAnswer(question);
	    	broadcastMessage("chatbot", answer, "chatbot");
	    }
	    
	    public void broadcastMessage(String sender, String message, String channel) {
	    	chatData.getsUsersSessions().stream().filter(Session::isOpen)
	   	   .filter(session -> {
	   		   try{
	   			   return chatData.getUsersChannel( chatData.getUsername(session) ).equals(channel);
	   		   }catch(NoSuchElementException ex){return false;}
	   	   })
	   	   .forEach(session -> {
	   		   try {
	   			   session.getRemote().sendString(String.valueOf(new JSONObject()
	   					   .put("reason", "message")
	   					   .put("userMessage", htmlMaker.createHtmlMessageFromSender(sender, message))
	   					   .put("channellist", chatData.getChannels())
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
	               		.put("channellist", chatData.getChannels())
	        			));
	        } 
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
	    
	    public void addUserToChannel(Session user, String channel){
	    	String username = chatData.getUsername(user);
	    	
	    	if(chatData.isUserToChannel(username))
	    		removeUserFromChannel(user);
	    
	    	chatData.addUserToChannel(username, channel);
	        broadcastMessage(channel, username + " joined", channel);
	        
	    }
	    	
	    public void removeUserFromChannel(Session user){
	    	String username = chatData.getUsername(user);
	    	
	    	String channel = chatData.getUsersChannel(username);
	    	chatData.removeUserFromChannel(username);
	    	broadcastMessage(channel, username + " left", channel);
	    	
	    	
	    }
	    
	    public void addChannel(){
	    	chatData.addChannel();
	    }
	    
	    public void removeUser(Session user){
	    	chatData.removeUser(user);
	    }

		public void addUsername(Session user, String username) {
			chatData.addUsername(user, username);
			
		}

		
}
