/*
 *	@autor Adela Jaworowska / adela.jaworowska@gmail.com
 */
package src;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageFile extends Message {
	private byte[] messageFile;
	private String fileName;

	public MessageFile(String fileName, byte[] fileBytes, String sender) {
		setFileName(fileName);
		setMessageFile(fileBytes);
		super.setSender(sender);
	}

	@Override
	public String toString() {

		String fileJsonS = null;
		try {
			fileJsonS = new JSONObject().put("type", "FILE").put("fileName", getFileName())
					.put("sender", super.getSender()).put("message", getMessageFile()).toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return fileJsonS;
	}

	public byte[] getMessageFile() {
		return messageFile;
	}

	public void setMessageFile(byte[] messageFile) {
		this.messageFile = messageFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
