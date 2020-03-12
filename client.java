import java.io.*;
import java.net.*;
class TCPClient {
	public static void main(String argv[]) throws Exception
	{
		String sentence;
		String modifiedSentence ="";
		Socket clientSocket = null;
	//while(!modifiedSentence.equals("END")){
		BufferedReader inFromUser 		= new BufferedReader(new InputStreamReader(System.in));
		clientSocket = new Socket("52.138.13.93", Integer.parseInt(argv[0]));
		DataOutputStream outToServer 	= new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer 	= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

	
		sentence = inFromUser.readLine()+'\n';
		
		String line;

        while((line=inFromUser.readLine()).length()!=0){
               if(sentence.startsWith("POST") && line.startsWith("Content-Length:")){
                        sentence+= (line+'\n');
                        }

        }
        
        System.out.println(sentence);
        
		outToServer.writeBytes(sentence + '\n');
		while((line = inFromServer.readLine())!=null)
			System.out.println(line);
	//}


			clientSocket.close();

	}
}
