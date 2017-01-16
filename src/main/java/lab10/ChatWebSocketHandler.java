package lab10;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class ChatWebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
    
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
    	String username = Chat.getUsernames().get(user);
        Chat.removeUserFromChannel(username);
    	Chat.removeUser(user);
        
        
    }
    
    /*
     * user|wiadomosc - wiadomosc od usera na danym kanale
     * name|wiadomosc - nazwa uzytkownika
     * addChannel|"nazwa kana³u" - dodanie kana³u
     * channelEnter|"kana³ 1"- dodanie siebie na kana³
     * channelExit| - wyjœcie z kana³u
     */
    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	String reason = message.substring( 0, message.indexOf('|') );
    	String contents = message.substring( message.indexOf('|') + 1 );
        switch( reason ){
        case "user":
        	String username = Chat.getUsernames().get(user);
        	if(!Chat.getUserToChannel().containsKey(username))
        		Chat.sendMessageToUser(user, "Musisz najpierw zapisaæ siê do kana³u!");
        	else{
        		String channel = Chat.getUserToChannel().get(username);
        		Chat.broadcastMessage(username, contents, channel);
        	
        		if(channel.equals("chatbot"))
        			Chat.askChatbot(contents);
        	}
        	break;
        case "name":
        	if(Chat.addUsername(user, contents))
        		Chat.refreshForUser(user);
        	break;
        case "addChannel":
        	Chat.addChannel();
        	Chat.refresh();
        	break;
        case "channelEnter":
        	Chat.addUserToChannel(Chat.getUsernames().get(user), contents);
        	//Chat.refresh();
        	break;
        case "channelExit":
        	if(!Chat.getUserToChannel().containsKey( Chat.getUsernames().get(user) )){
        		Chat.sendMessageToUser(user, "Nie jestes na zadnym kanale !");
        	}
        	else{
        		Chat.removeUserFromChannel(Chat.getUsernames().get(user));
        	}
        	//Chat.refresh();
        	break;
        
        }
    }

}
