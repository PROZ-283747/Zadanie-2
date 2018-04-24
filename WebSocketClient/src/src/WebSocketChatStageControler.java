/*
 *	@autor Adela Jaworowska / adela.jaworowska@gmail.com
 */
package src;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Optional;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DecodeException;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

public class WebSocketChatStageControler {
	@FXML
	TextField userTextField;
	@FXML
	TextArea chatTextArea;
	@FXML
	TextField messageTextField;
	@FXML
	Button btnSet;
	@FXML
	Button btnSend;
	@FXML
	Button btnUpload;
	@FXML
	Button btnAttach;
	@FXML
	TextArea fileField;
	private String user = "";
	private WebSocketClient webSocketClient;
	private File selectedFile;

	@FXML
	private void initialize() {
		webSocketClient = new WebSocketClient();
		user = userTextField.getText();
	}

	@FXML
	private void btnSet_Click() {
		if (userTextField.getText().isEmpty()) {
			return;
		}
		user = userTextField.getText();
		userTextField.setEditable(false);
		btnSet.setDisable(true);
	}

	@FXML
	private void btnAttach_Click() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"),
				new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
				new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"), new ExtensionFilter("All Files", "*.*"));
		Window mainStage = null;
		selectedFile = fileChooser.showOpenDialog(mainStage);
		if (selectedFile != null) {
			fileField.setText(selectedFile.getAbsolutePath());
			btnUpload.setDisable(false);
		}
	}

	@FXML
	private void btnUpload_Click() {
		if (fileField.getText().isEmpty() || user.equals("")) {
			return;
		}
		webSocketClient.sendMessage(selectedFile);
		fileField.clear();
	}

	@FXML
	private void btnSend_Click() {
		if (userTextField.getText().isEmpty() || user.equals("") || messageTextField.getText().isEmpty()) {
			return;
		}
		webSocketClient.sendMessage(messageTextField.getText());
		messageTextField.clear();
	}

	private boolean confirmationDialog() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("You have been sent a file.");
		alert.setContentText("Do you want to save it?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			return true;
		} else {
			return false;
		}
	}

	private void saveFile(byte[] decodedFile, String fileName) {
		Stage primaryStage = new Stage();
		FileChooser fileChooser = new FileChooser();

		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"),
				new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
				new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"), new ExtensionFilter("All Files", "*.*"));
		fileChooser.setInitialFileName(fileName);

		File file = fileChooser.showSaveDialog(primaryStage);

		if (file != null) {
			try {
				Files.write(file.toPath(), decodedFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void closeSession(CloseReason closeReason) {
		try {
			webSocketClient.session.close(closeReason);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ClientEndpoint
	public class WebSocketClient {
		private Session session;

		public WebSocketClient() {
			connectToWebSocket();
		}

		private void connectToWebSocket() {
			WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
			try {
				URI uri = URI.create("ws://localhost:8080/WebSocket/websocketendpoint");
				webSocketContainer.connectToServer(this, uri);
			} catch (DeploymentException | IOException e) {
				e.printStackTrace();
			}
		}

		@OnOpen
		public void onOpen(Session session) {
			System.out.println("Connection is opened.");
			this.session = session;
		}

		@OnClose
		public void onClose(CloseReason closeReason) {
			System.out.println("Connection is closed: " + closeReason.getReasonPhrase());
		}

		@OnError
		public void onError(Throwable throwable) {
			System.out.println("Error occured");
			throwable.printStackTrace();
		}

		@OnMessage
		public void onMessage(String message, Session session) {
			MyDecoder myDecoder = new MyDecoder();
			try {
				if (myDecoder.decode(message) instanceof MessageText) {
					System.out.println("Message was received");
					try {
						chatTextArea.setText(chatTextArea.getText() + (myDecoder.decode(message)).getSender() + ": "
								+ ((MessageText) myDecoder.decode(message)).getMessage() + "\n");
					} catch (DecodeException e1) {
						e1.printStackTrace();
					}
				}
			} catch (DecodeException e1) {
				e1.printStackTrace();
			}
			try {
				if (myDecoder.decode(message) instanceof MessageFile) {
					System.out.println("File was received");
					chatTextArea.setText(chatTextArea.getText() + (myDecoder.decode(message)).getSender()
							+ " sent file: " + ((MessageFile) myDecoder.decode(message)).getFileName() + "\n");
					byte[] decodedFile = ((MessageFile) myDecoder.decode(message)).getMessageFile();

					if (!(myDecoder.decode(message)).getSender().equals(user)) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								if (confirmationDialog()) {
									try {
										saveFile(decodedFile, ((MessageFile) myDecoder.decode(message)).getFileName());
									} catch (DecodeException e) {
										e.printStackTrace();
									}
								}
							}
						});
					}
				}
			} catch (DecodeException e) {
				e.printStackTrace();
			}
		}

		public void sendMessage(String message) {
			MessageText messageText = new MessageText(message, userTextField.getText().toString());
			System.out.println("Message was sent: " + messageText.getMessage());
			try {
				TextEncoder tEncoder = new TextEncoder();
				session.getBasicRemote().sendText(tEncoder.encode(messageText));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (EncodeException e) {
				e.printStackTrace();
			}
		}

		// for sending files
		public void sendMessage(File file) {
			byte[] fileBytes = null;
			try {
				fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			MessageFile messageFile = new MessageFile(file.getName(), fileBytes, userTextField.getText().toString());
			FileEncoder fEncoder = new FileEncoder();
			try {
				session.getBasicRemote().sendText(fEncoder.encode(messageFile));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (EncodeException e) {
				e.printStackTrace();
			}
		}

	} // public class WebSocketClient
} // public class WebSocketChatStageControler
