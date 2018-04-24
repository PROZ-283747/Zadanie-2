/*
 *	@autor Adela Jaworowska / adela.jaworowska@gmail.com
 */
package src;

import javax.websocket.EncodeException;

public class FileEncoder {

	public String encode(MessageFile messFile) throws EncodeException {
		return messFile.toString();
	}
}
