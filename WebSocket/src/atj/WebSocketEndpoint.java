package atj;
import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;



@ApplicationScoped
@ServerEndpoint("/websocketendpoint")
public class WebSocketEndpoint {
	
	@OnOpen
	public void onOpen(Session session) { 
		System.out.println("onOpen");
	}
	
	@OnClose
	public void onClose(Session session) { 
		System.out.println("onClose");
	}
	
	@OnError
	public void onError(Throwable error) { 
		System.out.println("onError");
	}
	
	
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("onMessage");
		try {
		for (Session oneSession : session.getOpenSessions()) {
			if (oneSession.isOpen()) {
				oneSession.getBasicRemote().sendText(message);
				}
				}
			}
		catch (IOException e) { e.printStackTrace(); }
	}
} // public class WebSocketEndpoint
