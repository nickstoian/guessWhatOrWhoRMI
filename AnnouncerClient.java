// Nicolas Stoian

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class AnnouncerClient extends Thread{

	private String hostName;
	private int portNumber;
	private long startTime;

	public AnnouncerClient (String hn, int pn){
		hostName = hn;
		portNumber = pn;
		startTime = System.currentTimeMillis( );
	}

	public void run( ){
		try{
			System.out.println("[age = " + age( ) + "ms] " + "Announcer client thread running");
            Socket socket = new Socket(hostName, portNumber);
            DataInputStream in = new DataInputStream(socket.getInputStream());
        	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        	out.writeUTF("Announcer");
        	out.writeUTF("startExam");
        	in.readUTF();
        	out.writeUTF("gradeExams");
        	in.readUTF();
        	out.writeUTF("startTheShow");
        	in.readUTF();
        	out.writeUTF("Done");
        	System.out.println("[age = " + age( ) + "ms] " + "Announcer client thread terminates");
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
}
