/*
 *	@autor Adela Jaworowska / adela.jaworowska@gmail.com
 */

package src;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import src.MessageText;

public class TextEncoder implements Encoder {

	public TextEncoder() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(EndpointConfig arg0) {
	}

	public String encode(MessageText messText) throws EncodeException {
		return messText.toString();
	}
}
