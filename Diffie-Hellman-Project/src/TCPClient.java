import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.util.Random;
import java.util.StringTokenizer;

public class TCPClient {
	//Characters chosen based on example: http://en.wikipedia.org/wiki/Diffie%E2%80%93Hellman_key_exchange
	int p, g, a, B, s, A;
	PrintWriter outToServer;
	Socket clientSocket;
	BufferedReader inFromUser;
	BufferedReader inFromServer;
	
	//Parameters: Server's name and port
	//Function: Initial setup to allow communcation between server and client
	public void setup(String hostname, int port){
		try{
			clientSocket = new Socket(hostname, port);
			outToServer = new PrintWriter(clientSocket.getOutputStream());
			inFromUser = new BufferedReader( new InputStreamReader(System.in));
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Parameters: Server's name and port
	//Function: Allow user to send messages to server and receive replies.
	public void runClient(String hostname, int port) {
		String sentence = "";
		String echoedSentence;
		
		try{
			System.out.println("Type done to quit");
			while(true){
				sentence = inFromUser.readLine();
				if (sentence.equals("done")){
					break;
				}
				outToServer.println(encrypt(sentence, s));
				outToServer.flush();
				echoedSentence = inFromServer.readLine();
				decrypt(echoedSentence,s);
			}
			clientSocket.close();
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
		System.out.println("Encrypted Message From Server:"+encryptedString);
		StringBuilder buildEncrypted = new StringBuilder();
		for (int i=0; i<encryptedString.length(); i++){
			int ascii = (int)encryptedString.charAt(i);
			ascii -= key;
			buildEncrypted.append(Character.toString((char) ascii));
		}
		String decryptedString = new StringBuffer(buildEncrypted.toString()).reverse().toString();
		System.out.println("Decrypted Message From Server:"+decryptedString);
		return decryptedString;
	}
	
	
	//Random Value Generators
	public void generateP(){
		Random ran = new SecureRandom();
		p = BigInteger.probablePrime(13, ran).intValue();
	}
	public void generateG(){
		Random ran = new Random();
		g = ran.nextInt(8) +1;
	}
	
	public void generateSecretInt(){
		Random ran = new Random();
		a = ran.nextInt(5) +1;
	}
	
	public void generateAandSend(){
		A = (g^a) % p;
		try {
			outToServer.println("pgA "+ p+" "+g+" "+A);
			outToServer.flush();
			StringTokenizer st = new StringTokenizer(inFromServer.readLine());
			st.nextToken(); //B
			B = new Integer(st.nextToken());
			generateS();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void generateS(){
		s = (B^a) % p;
		if (s==0){
			s+= 2;
		}
	}
	
	public void generateAllAndSend(){
		generateP();
		generateG();
		generateSecretInt();
		generateAandSend();
	}
}