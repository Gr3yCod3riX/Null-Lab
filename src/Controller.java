import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.bouncycastle.util.IPAddress;

import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.networking.chat.ClientChat;
import com.networking.chat.OnlineUsers;
import com.networking.chat.server.ServerMain;
import com.simulator.networking.Client;
import com.simulator.networking.NetworkConnection;
import com.simulator.networking.NewApp;
import com.simulator.networking.Server;
import com.simulator.protocols.DigitalniPotpis;
import com.simulator.protocols.VDigitalniPotpis;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller implements Initializable{
	
	@FXML
	Line ln1;

	@FXML
	ToggleButton btnTrans;
	@FXML
	Button start, btnSimulate, btnSendCert, btnServerStart, btnVerifyCert, btnGenCert, btnClientDisc, btnClientConn;

	@FXML
	AnchorPane aPane;
	@FXML
	Slider slNoise, slPacket;
	@FXML
	Label lbNoise, lbPacket, lbSent, lbErrors;
	@FXML
	Group gr;
	@FXML
	public TextArea taSender, taReceiver;
	@FXML
	public volatile TextField tfSender;
	@FXML
	PointLight pl = new PointLight();
	@FXML
	TabPane tabPane;
	@FXML
	Tab tabRConnection;
	@FXML
	TextField tfUName, tfIpAdd, tfChatPort, tfTransPort;
	@FXML
	ToggleButton tbSimulator;
	
	FileChooser fc = new FileChooser();
	
	@FXML
	ImageView imgServer, imgClient;
	@FXML
	Label lbTPort, lbUName, lbChatPort, lbRIP, lbClient, lbServer;
	@FXML
	Text txtServerName, txtClientName;
	@FXML
	VBox vbClientComm, vbServerComm;
	
	
	Lighting lighting = new Lighting();
	Light.Point light = new Light.Point();

	@FXML
	Circle circ = new Circle(250, 150, 100, Color.TRANSPARENT);

	PathTransition pt;
	static Thread t1;

	static Thread t2;

	Thread srvConnection;

	volatile static Thread cliConnection;

	volatile int packetState;
	public int noise = 0;
	int sendingBits = 0;

	String generatedNoise = "";
	String binMessage = "";
	String noiseValue = "";
	Rectangle r;
	boolean add = false;
	boolean rem = false;
	boolean anim = true;
	
	boolean addNotActive = true;
	static boolean running = false;
	String packetStatus = "";

	Conversion_Module cm = new Conversion_Module();
	DigitalniPotpis dp = new DigitalniPotpis();
	VDigitalniPotpis vdp = new VDigitalniPotpis();
	
	
	String remoteIP;
	String hostIP;
	String username;
	
	
	
	private int portChat;
	private int portTransfer;
	private static boolean simu_link = false;
	
	public com.networking.chat.server.Server server;
	static ServerMain sm;
	@FXML
	public Label lbAuth;
	
	
	Path dest;
	String destString;
	
	
	// SERVER -- Will be modified from login controller
	public static volatile boolean isServer = true;
	//~~--------------------------------------
	
	
	volatile static boolean runn = true;
	//~~~~----Visual Simulator----~~~~//
	public void Transmission() {
		Line line = new Line();
		line.setStartX(-30);
		line.setStartY(0.0f);
		line.setEndX(180);
		line.setEndY(0);
		
		t1 = new Thread(new Runnable() {

			@Override
			public void run() {

				while (runn) {

					if (btnTrans.isSelected()) {

						if (add || rem && addNotActive) {
							anim = true;
							try {
								wait();
								add = false;
								rem = false;

							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}

						synchronized (this) {

							try {

								pt = new PathTransition(Duration.seconds(5), line, r);
								pt.setCycleCount(Animation.INDEFINITE);
								pt.setRate(1.5);
								pt.play();

								Thread.sleep(1);

								add = false;
								rem = false;

							} catch (InterruptedException e) {
							}

						}

					} else {
						System.out.println("no packets");
						try {
							Thread.sleep(500);

						} catch (InterruptedException e) {
							e.printStackTrace();
							running = false;
						}

					}

				}

			}

		});

		t2 = new Thread(new Runnable() {

			@Override
			public void run() {

				circ.setStroke(Color.TRANSPARENT);
				circ.setEffect(lighting);
				circ.setStrokeWidth(6);

				Timeline timeline = new Timeline(
						new KeyFrame(Duration.seconds(0.25), e -> circ.setFill(Color.TRANSPARENT)),
						new KeyFrame(Duration.seconds(0.5), e -> circ.setFill(Color.GREEN)));

				timeline.setCycleCount(Animation.INDEFINITE);
				timeline.play();

			}

		});

	}

	@FXML
	public void add() throws InterruptedException {
		if (running) {
			synchronized (this) {
				r = new Rectangle();
				r.setWidth(7);
				r.setHeight(7);
				r.setEffect(lighting);
				r.setFill(javafx.scene.paint.Color.GREEN);

				gr.getChildren().add(r);
				// System.out.println("ADDED RECTANGLE!");
				packetState++;
				add = true;
			}
			notify();
		}
		
	}

	public void start() {

			
			if (btnTrans.isSelected()) {
				running = true;
				
				t1.interrupt();
				t2.interrupt();
				t1.start();
				t2.start();
				
			}
		
		
		
		
	}

	public void removePackets() {
		synchronized (this) {
			gr.getChildren().removeAll(gr.getChildren());
			gr.getChildren().clear();
		}
		slNoise.setMax(packetState);

		rem = true;
		packetState = 0;
		sendingBits = 0;
	}

	@FXML
	public void updateSlider() { // REQUIRES FIX
		
		if (running) {
			noise = (int) slNoise.getValue();
			lbSent.setText(Integer.toString(packetState));

			lbErrors.setText(Integer.toString(noise));
			slNoise.setMax(packetState);
			synchronized (this) {

				if (noise <= packetState) {
					generatedNoise = cm.noiser(noise, packetState);
					for (int i = 0; i < generatedNoise.length(); i++) {

						if (generatedNoise.charAt(i) == '1') {
							((Rectangle) (gr.getChildren().get(i))).setFill(javafx.scene.paint.Color.RED);
						} else if (generatedNoise.charAt(i) == '0') {
							((Rectangle) (gr.getChildren().get(i))).setFill(javafx.scene.paint.Color.GREEN);
						}

						noiseValue += generatedNoise.charAt(i); // Adding all bits to the screen
					}
				}

				else {

					for (int i = 0; i < generatedNoise.length(); i++) {
						((Rectangle) (gr.getChildren().get(i))).setFill(javafx.scene.paint.Color.RED);
						noiseValue += generatedNoise.charAt(i); // Adding all bits to the screen
					}
				}
				lbNoise.setText(noiseValue);
				noiseValue = "";
			}
			taReceiver.clear();
			receiveMessage(cm.messagePlusNoise(binMessage, generatedNoise));
			slNoise.setMax(packetState);
		}
		
	}

	@FXML
	public void updatePackets() {
		if (running) {
			int selected = (int) slPacket.getValue(); // Selected number of bits to send 

			lbPacket.setText(Integer.toString((int) slPacket.getValue()));

			int limit = selected - packetState; // Limit to add the remaining packets

			synchronized (this) {
				if (selected > packetState) {
					addNotActive = false;
					for (int i = 0; i < limit; i++) {
						try {
							add();
							Thread.sleep(5);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// System.out.println("SELECTED > PACKETSENT");
						addNotActive = true;
					}

				} else {
					packetState = 0;
					removePackets(); // Remove the particle cubes

					addNotActive = false; // ?
					for (int i = 0; i < selected; i++) {
						try {
							add();
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// System.out.println("SELECTED > PACKETSENT");
						addNotActive = true;

					}

				}

				lbSent.setText(Integer.toString(packetState));
				lbErrors.setText(Integer.toString(noise));
				updateSlider();
				notify(); // Resava problem kada se ociste paketi i ponovo
							// pokrecu.
				// OVAKO RADI I ZAPRAVO KADA SE POZOVE REMOVE ALL I
				// POKRENE PONOVO, RADI.
			}
		}

	}
	
	public void toggleSimulator(){
		if (simu_link == true) {
			simu_link = false;			
		}
		else{
			simu_link = true;
		}
	}
	
	public static void stopSimulator(){
		try {
			running = false;
			runn = false;
			t1.setDaemon(true);
			t2.setDaemon(true);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void stopTransfer(){
		try {
			runn = false;
			cliConnection.setDaemon(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//	CLIENT  CHAT//
	ClientChat cc;
	volatile boolean srvRunning = true;
	
	
	public void callClientConnect(){
		cc = new ClientChat(username, remoteIP, portChat);
		cc.getFxComponents(taSender, taReceiver, tfSender);
		cc.clientConnect(remoteIP, portChat, username);
		System.out.println("ChatClient initialized...");
	}
	public void callClientDisconnect(){
		cc.clientDisc();
	}
	public void callServerStart(){
	if (isServer) {
			btnServerStart.setVisible(true);
			btnVerifyCert.setVisible(true);

			chatServer(portChat);
			
			
			
			
			server = sm.getServer(); //When server starts, it gets the copy of his instance
			server.writeReceived(taReceiver, tfSender, true); // that instance has a method which accepts from GUI controller required components like textArea for inputing text which server receives
						
			downloadDir();
			
			
			srvConnection = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					ServerSocket serverSocket = null;
					Socket socket;
					InputStream in;
					OutputStream out;
					socket = null;
			        in = null;
			        out = null;
			        byte[] bytes;
					
					while(srvRunning){
					serverSocket = null;
			        try {
			            serverSocket = new ServerSocket(portTransfer);
			        } catch (IOException ex) {
			            System.out.println("Can't setup server on this port number. #1");
			        }

			        
			        try {
			            socket = serverSocket.accept();
			        } catch (IOException ex) {
			            System.out.println("Can't accept client connection. #2");
			        }

			        try {
			            in = socket.getInputStream();
			        } catch (IOException ex) {
			            System.out.println("Can't get socket input stream. #3");
			        }

			        try {
			            out = new FileOutputStream(destString);
			        } catch (FileNotFoundException ex) {
			            System.out.println("File not found. ");
			        }

			       bytes = new byte[32*1024];

			        int count;
			        
			        
			        
			        try {

			        		while ((count = in.read(bytes)) > 0) {
							    out.write(bytes, 0, count);
							}
			        		 out.close();
			        		 in.close();
						        socket.close();
						        serverSocket.close();
			        		System.out.println("Waiting...");

					        
					} catch (IOException e) {
						
						e.printStackTrace();
					}


					}
				}
			});
			srvConnection.setDaemon(true);
			srvConnection.start();
			
			System.out.println("Server has successfully opened a connection for the transfer!");
			
			
		}
	}
	public static void stopServer(){
		sm.stopServer();
	}
	
	
	//~~~~----CHAT----~~~~//
	
	public void chatServer(int port){
				portChat = port;
				sm = new ServerMain(port);
				
			}
	//-------------------------------
	public void sendMessage() throws UnknownHostException {	
		String MESSAGE = tfSender.getText().toString();
		
		if (simu_link) {
			//InetAddress ipToSend = InetAddress.getByName(remoteIP);
			if (isServer) {
				//server.sendToAll(MESSAGE);
				server.send(MESSAGE, true);
				//tfSender.clear();		//-----------------------------------
				System.out.println("SERVER SALJE klijentu....");
			}
			else{
				cc.sendTA(MESSAGE, taSender);
				
				System.out.println("KLIJENT SALJE serveru.....");
			}
		}
			if (running) {
				// generatedNoise = cm.noiser(noise, packetState);
				taSender.clear(); // Clear sender text area
				binMessage = cm.toBin(tfSender.getText().toString());

				
				taSender.appendText(binMessage); // append binary of the text from
													// textfield


				sendingBits = taSender.getLength(); // take length of the binary
													// message
													// inside of textarea

				lbPacket.setText(Integer.toString((int) slPacket.getValue()));

				int limit = sendingBits - packetState;

				synchronized (this) {

					if (sendingBits >= packetState) {
						addNotActive = false;
						for (int i = 0; i < limit; i++) {
							try {
								add();
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							// System.out.println("SELECTED > PACKETSENT");

						}
						addNotActive = true;
					} else {
						removePackets();
						// packetState = 0;

						for (int i = 0; i < sendingBits; i++) {

							try {
								add();
								Thread.sleep(50);

							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							System.out.println("SELECTED > PACKETSENT");
						}
						addNotActive = true;
					}

					notify(); // Resava problem kada se ociste paketi i ponovo
								// pokrecu.
								// OVAKO RADI I ZAPRAVO KADA SE POZOVE REMOVE ALL I
								// POKRENE PONOVO, RADI.

					lbSent.setText(Integer.toString(packetState));
					lbErrors.setText(Integer.toString(noise));
					//--------------------------------------------------
					
					
				}
			
		}
		
		tfSender.clear();
		
	}
	
	public void receiveMessage(String receive) {

		String output = cm.binToText(receive);

		taReceiver.appendText(output);

	}

	@FXML
	public void handleEnterPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			System.out.println("I pressed ENTER");
		}
	}

	@FXML
	public void handleKeyEvent(KeyEvent event) {
		System.out.println(".....................");

		pt.setCycleCount(Timeline.INDEFINITE);
		pt.setAutoReverse(true);
		pt.play();

	}
	
	
	
	//~~~~----NETWORKING----~~~~//
	public void simulate(){
		
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			Pane root = loader.load(getClass().getResource("/ui/g.fxml").openStream());

			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void fileToSign()throws Exception{
	
		
	}
	
	//Generate a Certificate
	public void sign()throws Exception{
		dp.inic();
	}
		
	public void certToSend() throws Exception{		
		
		// CREATE CLIENT SOCKET CONNECTION FOR TRANSFER + FILE TO SEND
		if (!isServer) {
			FileChooser fc = new FileChooser();
			//File f = DigitalniPotpis.selectedFile;
			File f = fc.showOpenDialog(null);
			
			System.out.println("Pre SLANJA CERTA");
			
			System.out.println("Uspeo da selektuje fajl");
			cliConnection = new Thread(new Runnable() {

				@Override
				public void run() {
					while(runn){
					
					try {
						
						Socket socket = null;
				        //String host = ip;

				        socket = new Socket(remoteIP, portTransfer);

				        // Get the size of the file
				        long length = f.length();
				        byte[] bytes = new byte[16 * 1024];
				        InputStream in = new FileInputStream(f);
				        OutputStream out = socket.getOutputStream();

				        int count;
				        
				        
				        while ((count = in.read(bytes)) > 0) {
				            out.write(bytes, 0, count);
				        }

				        out.close();
				        in.close();
				        socket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				
				}
				}
				
			});
			cliConnection.start();
		}
	}
	
	
	File destFile; 
	public void verifyCertificate() throws Exception{
		fc.setTitle("Load Public Key");
		destFile = fc.showOpenDialog(null);
		
		vdp.publicKeyLoad(destFile.toPath());
		System.out.println("PKey load DONE!");
		
		
		fc.setTitle("Load Signature");
		destFile = fc.showOpenDialog(null);
		vdp.signatureLoad(destFile.toPath());
		System.out.println("Sig load DONE!");
		
		fc.setTitle("Select a file to verify");
		destFile = fc.showOpenDialog(null);
		vdp.verification(destFile.toPath(), lbAuth);
		System.out.println("Verify DONE~~~");
	}
	
	public void saveConfig() throws Exception{
		if (!isServer) {
			
			
			portTransfer = Integer.parseInt(tfTransPort.getText().toString());
			remoteIP = tfIpAdd.getText().toString();
			portChat = Integer.parseInt(tfChatPort.getText().toString());
			username = tfUName.getText();
		}
		portTransfer = Integer.parseInt(tfTransPort.getText().toString());
		portChat = Integer.parseInt(tfChatPort.getText().toString());
		hostIP = tfIpAdd.getText().toString();
		username = tfUName.getText();
	}
	
	public void downloadDir(){
		 fc.setTitle("Download Destination");
			destFile = fc.showSaveDialog(null);
			dest = destFile.toPath();
	        destString = dest.toString();
	       
	}
	
	public void showConfig(){
		lbTPort.setText(Integer.toString(portTransfer));
		lbChatPort.setText(Integer.toString(portChat));
		if (isServer) {
			lbRIP.setText(hostIP);
		}
		else{
			lbRIP.setText(remoteIP);
		}

		
		lbUName.setText(username);
		
		System.out.println("SHOW CONFIG");
	}
	
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
		
		if (!isServer) {
			imgServer.setVisible(false);
			tfUName.appendText("Client123");
			txtServerName.setVisible(false);
			
			username = "Client123";
			vbServerComm.setVisible(false);
			
			
		
		}
		else{
			imgClient.setVisible(false);
			lbAuth.setVisible(false);
			txtClientName.setVisible(false);

			username = "Server123";
			tfUName.appendText("Server123");
			vbClientComm.setVisible(false);
			
		}
		
		remoteIP = "192.168.1.9";
		hostIP = "192.168.1.9";
		portChat = Integer.parseInt("9911");
		portTransfer = Integer.parseInt("6611");
		
		Transmission();
		
		
		tfIpAdd.appendText("192.168.1.9");
		tfChatPort.appendText("9911");
		tfTransPort.appendText("6611");

		
			
		lbNoise.setFont(Font.font(15));
		slNoise.setMin(0);
		slNoise.setBlockIncrement(1);
		slPacket.setMin(0);
		slPacket.setMax(64);

		
	}
	


}


