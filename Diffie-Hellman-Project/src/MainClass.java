
public class MainClass {
	
	//Parameters: As Server: int portNumber. As Client: String servername, int portNumber
	//Function: Entry point to program
	public static void main(String[] args){
		//Server
		if (args.length == 1){
			TCPServer tcpS = new TCPServer();
			tcpS.runServer(new Integer(args[0]).intValue());
		}
		//Client
		else{
			TCPClient tcp = new TCPClient();
			tcp.setup(args[0], new Integer(args[1]).intValue());
			tcp.generateAllAndSend();
			tcp.runClient(args[0], new Integer(args[1]).intValue());
		}
	}
}
