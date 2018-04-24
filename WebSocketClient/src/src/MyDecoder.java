/*
 *	@autor Adela Jaworowska / adela.jaworowska@gmail.com
 */
package src;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import src.Message;
import src.MessageFile;
import src.MessageText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyDecoder implements Decoder.Text<Message> {
	private byte[] JSONArrayToByte(JSONArray jsonArray) {
		byte[] byteArray = new byte[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); ++i) {
			try {
				byteArray[i] = (byte) jsonArray.getInt(i);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		return byteArray;
	}

	@Override
	public void init(EndpointConfig ec) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public Message decode(String string) throws DecodeException {
		try {
			JSONObject json = new JSONObject(string);
			String check = json.getString("type");
			String sender = json.getString("sender");

			if (check.equals("TEXT")) {
				String message = null;
				message = json.getString("message");

				return new MessageText(message, sender);
			} else if (check.equals("FILE")) {
				JSONArray message = (JSONArray) json.get("message");
				byte[] messageByte = JSONArrayToByte(message);
				String fileName = json.getString("fileName");

				return new MessageFile(fileName, messageByte, sender);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean willDecode(String string) {
		return false;
	}
}
