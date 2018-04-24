/*
 *	@autor Adela Jaworowska / adela.jaworowska@gmail.com
 */
package src;

import javax.json.Json;
import javax.json.JsonObject;

public class MessageText extends Message {
	private String message;

	public MessageText(String mess, String sender) {
		setMessage(mess);
		super.setSender(sender);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		JsonObject textJson = Json.createObjectBuilder().add("type", "TEXT").add("sender", super.getSender())
				.add("message", getMessage()).build();
		return textJson.toString();
	}
}
