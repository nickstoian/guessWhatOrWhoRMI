// Nicolas Stoian

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ContestantClient extends Thread{

	private static int num_threads = 1;

	private String name;
	private long startTime;
	private String hostName;
	private int portNumber;

	public ContestantClient (String hn, int pn){
		hostName = hn;
		portNumber = pn;
		name = "Contestant " + numFormat( num_threads++ );
		startTime = System.currentTimeMillis( );
	}

	public void run( ){
		try{
			System.out.println("[age = " + age( ) + "ms] " + name + " client thread running");
            Socket socket = new Socket(hostName, portNumber);
            DataInputStream in = new DataInputStream(socket.getInputStream());
        	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        	out.writeUTF(name);
        	out.writeUTF("formGroup");
        	in.readUTF();
        	out.writeUTF("takeSeat");
        	in.readUTF();
        	out.writeUTF("takeExam");
        	in.readUTF();
        	out.writeUTF("getTestResults");
        	String result = in.readUTF();
        	if(result.equals("loser")){
        		System.out.println("[age = " + age( ) + "ms] " + name + " client thread terminates");
        		socket.close();
        		return;
        	}
        	out.writeUTF("startTheGame");
        	in.readUTF();
        	out.writeUTF("playTheGame");
        	in.readUTF();
        	out.writeUTF("playFinal");
        	in.readUTF();


        	out.writeUTF("Done");
        	System.out.println("[age = " + age( ) + "ms] " + name + " client thread terminates");
        	socket.close();
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
        catch (Exception e) {
        	System.out.println(e);
            System.exit(1);
        }
    }

	// method to keep track of age
	 	public long age( ){
	 		return System.currentTimeMillis( ) - startTime;
	 	}

	// method to format numbers correctly for use in String name
	public String numFormat(int num){
		Integer i = new Integer(num);
		String str = i.toString( );
		if( num < 10 ){
			str = " " + i;
		}
		return str;
	}
}
