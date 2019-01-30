// Nicolas Stoian

import java.io.*;
import java.net.*;

public class GameClient {
	private static long startTime;

    public static void main(String[] args) throws IOException {
    	if (args.length == 2){
    		String hostName = args[0];
            int portNumber = Integer.parseInt(args[1]);
            try{
            	startTime = System.currentTimeMillis( );
            	System.out.println("[age = " + age( ) + "ms] " + "Game client thread running");
                Socket gameSocket = new Socket(hostName, portNumber);
                DataInputStream in = new DataInputStream(gameSocket.getInputStream());
            	DataOutputStream out = new DataOutputStream(gameSocket.getOutputStream());
            	out.writeUTF("Game");
            	out.writeUTF("Default");
            	String result = in.readUTF();
            	System.out.println("[age = " + age( ) + "ms] " + "Game client - " + result);
            	for (int i = 0; i < 13; i++){
            		new ContestantClient(hostName, portNumber).start();
            	}
            	new AnnouncerClient(hostName, portNumber).start();
            	new HostClient(hostName, portNumber).start();
            	System.out.println("[age = " + age( ) + "ms] " + "Game client thread terminates");
            	gameSocket.close();
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
    	else if (args.length == 8){
    		String hostName = args[0];
            int portNumber = Integer.parseInt(args[1]);
            try{
            	startTime = System.currentTimeMillis( );
            	System.out.println("[age = " + age( ) + "ms] " + "Game client thread running");
                Socket gameSocket = new Socket(hostName, portNumber);
                DataInputStream in = new DataInputStream(gameSocket.getInputStream());
            	DataOutputStream out = new DataOutputStream(gameSocket.getOutputStream());
            	out.writeUTF("Game");
            	out.writeUTF("Custom");
            	int numRounds = Integer.parseInt(args[2]);
    			int numQuestions = Integer.parseInt(args[3]);
    			int questionValues = Integer.parseInt(args[4]);
    			Double rightPercent = Double.parseDouble(args[5]);
    			int room_capacity = Integer.parseInt(args[6]);
    			int num_contestants = Integer.parseInt(args[7]);
    			out.writeInt(numRounds);
    			out.writeInt(numQuestions);
    			out.writeInt(questionValues);
    			out.writeDouble(rightPercent);
    			out.writeInt(room_capacity);
    			out.writeInt(num_contestants);
            	String result = in.readUTF();
            	System.out.println("[age = " + age( ) + "ms] " + "Game client - " + result);
            	for (int i = 0; i < num_contestants; i++){
            		new ContestantClient(hostName, portNumber).start();
            	}
            	new AnnouncerClient(hostName, portNumber).start();
            	new HostClient(hostName, portNumber).start();
            	System.out.println("[age = " + age( ) + "ms] " + "Game client thread terminates");
            	gameSocket.close();
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
    	else {
    		System.err.println("Usage: java GameClient <host name> <port number>  or,");
            System.err.println("Usage: java GameClient <host name> <port number> <int numRounds> <int numQuestions> <int questionValues> <double rightPercent> <int room_capacity> <int num_contestants>");
            System.exit(1);
    	}



























    	/*

        if (args.length != 2 || args.length != 8) {
            System.err.println("Usage: java GameClient <host name> <port number>  or,");
            System.err.println("Usage: java GameClient <host name> <port number> <int numRounds> <int numQuestions> <int questionValues> <double rightPercent> <int room_capacity> <int num_contestants>");
            System.exit(1);
        }
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try{
            Socket gameSocket = new Socket(hostName, portNumber);
            DataInputStream in = new DataInputStream(gameSocket.getInputStream());
        	DataOutputStream out = new DataOutputStream(gameSocket.getOutputStream());
        	out.writeUTF("Game");
        	String result = in.readUTF();
        	System.out.println(result);

        	for (int i = 0; i < 13; i++){
        		new ContestantClient(hostName, portNumber).start();
        	}
        	new AnnouncerClient(hostName, portNumber).start();
        	new HostClient(hostName, portNumber).start();

        	gameSocket.close();
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
        } */

    }

    // method to keep track of age
	 public static long age( ){
	 	return System.currentTimeMillis( ) - startTime;
	 }
}
