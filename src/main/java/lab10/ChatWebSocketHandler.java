package lab10;

import java.util.NoSuchElementException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;


@WebSocket
public class ChatWebSocketHandler {

	private ChatFunctions chatFunctions = new ChatFunctions();
	
    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
    
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
    	chatFunctions.removeUser(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	String reason = message.substring( 0, message.indexOf('|') );
    	String contents = message.substring( message.indexOf('|') + 1 );
        switch( reason ){
        case "user":
        	try{
        		chatFunctions.sendUsersMessage(user, contents);
        	}
        	catch(NoSuchElementException ex){
        		chatFunctions.sendMessageToUser(user, "You must enter channel first, to send message");
        	}
        	break;
        case "name":
        	try{
        		chatFunctions.addUsername(user, contents);
//        		cookie
//        		List<HttpCookie> cookies = new ArrayList<HttpCookie>();
//        		cookies.add(new HttpCookie("username", contents));
//        		user.getUpgradeRequest().setCookies(cookies);
        		
        		chatFunctions.refreshForUser(user);
        	}
        	catch(IllegalArgumentException ex){
        		try{ user.getRemote().sendString(String.valueOf(new JSONObject().put("reason", "taken_username") ) );
        		}catch (Exception inEx){inEx.printStackTrace(); };
        	}
        	break;
        case "addChannel":
        	chatFunctions.addChannel();
        	chatFunctions.refresh();
        	break;
        case "channelEnter":
        	chatFunctions.addUserToChannel(user, contents);
        	break;
        case "channelExit":
        	try{
        		chatFunctions.removeUserFromChannel(user);
        	}catch(NoSuchElementException ex){
        		chatFunctions.sendMessageToUser(user, "you arent on any channel");
        	}
        	break;
        
        }
    }

}
