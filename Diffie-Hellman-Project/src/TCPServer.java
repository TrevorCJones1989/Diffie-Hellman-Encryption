import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.StringTokenizer;

public class TCPServer {
	ServerSocket serverSocket;
	int p,g,A,b,B, s;
	PrintWriter outToClient;
	Socket clientSocket;
	
	//Parameters: int port number for server to listen on
	//Function: Start the server which echos encrypted message, capitalizes it, re-encrypts and responds.
	public void runServer(int port) {
		String clientSentence;
		String capitalizedSentence;
		try{
			serverSocket = new ServerSocket(port);
			clientSocket = serverSocket.accept();
			System.out.println("Client Connected");
			
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToClient = new PrintWriter(clientSocket.getOutputStream());
			
			while (true){	
				clientSentence = inFromClient.readLine();
				if (clientSentence.startsWith("pgA", 0)){
					//format pgA # # #
					StringTokenizer st = new StringTokenizer(clientSentence);
					st.nextToken(); //pgA
					p = new Integer(st.nextToken());
					g = new Integer(st.nextToken());
					A = new Integer(st.nextToken());
					Thread.sleep(1000);
					generateBandSend();
				}
				else{
					String decrypted = decrypt(clientSentence,s);
					capitalizedSentence = "ServerEchos "+decrypted;
					outToClient.println(encrypt(capitalizedSentence,s));
					outToClient.flush();
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	

	
	//Paramters: String to encrypt and key to encrypt it with
	//Function: Encrypts the message by reversing, then doing a cipher on the ascii value of the character.
	public String encrypt(String unencryptedString, int key){
		String reversedString = new StringBuffer(unencryptedString).reverse().toString();
		StringBuilder buildEncrypted = new StringBuilder();
		
		for (int i=0; i<reversedString.length(); i++){
			int ascii = (int)reversedString.charAt(i);
			ascii += key;
			buildEncrypted.append(Character.toString((char) ascii));
		}
		return buildEncrypted.toString();
	}
	
	//Paramters: String to decrypt and key to decrypt it with
	//Function: decrypts the message by doing a cipher on the ascii value of the character then reversing.
	public String decrypt(String encryptedString, int key){
		System.out.println("Encrypted Message From Client:"+encryptedString);
		StringBuilder buildEncrypted = new StringBuilder();
		
		for (int i=0; i<encryptedString.length(); i++){
			int ascii = (int)encryptedString.charAt(i);
			ascii -= key;
			buildEncrypted.append(Character.toString((char) ascii));
		}
		
		String decryptedString = new StringBuffer(buildEncrypted.toString()).reverse().toString();
		System.out.println("Decrypted Message From Client:"+decryptedString);
		return decryptedString;
	}
	
	//Random Value Generators
	public void generateSecretInt(){
		Random ran = new Random();
		b = ran.nextInt(5) +1;
	}
	
	public void generateBandSend(){
		generateSecretInt();
		B = (g^b) % p;
		try {
			outToClient.println("B "+B);
			outToClient.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		generateS();
	}
	
	public void generateS(){
		s = (A^b) % p;
		if (s==0){
			s+= 2;
		}
	}
}