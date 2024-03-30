// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// YOUR_NAME_GOES_HERE
// YOUR_STUDENT_ID_NUMBER_GOES_HERE
// YOUR_EMAIL_GOES_HERE


import java.io.*;
import java.net.Socket;
import java.util.HashMap;

// DO NOT EDIT starts
interface TemporaryNodeInterface {
    public boolean start(String startingNodeName, String startingNodeAddress);
    public boolean store(String key, String value);
    public String get(String key);
}
// DO NOT EDIT ends


public class TemporaryNode implements TemporaryNodeInterface,Runnable {


    private String ipAddress;
    private int port;

    private Socket clientSocket;

    private String currentConnectionIP;

    private int currentConnectionPort;

    public boolean start(String startingNodeName, String startingNodeAddress) {

        Writer outputStream;
        String[] aux;

        //Tests to make sure the address is fine
        try{
             aux = startingNodeAddress.split(":");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        if(aux.length == 2)
        {
            ipAddress = aux[0];
            try{
                port = Integer.parseInt(aux[1]);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }
        else
            return false;

        if(port > 65535)
            return false;

        //If there was no previous connection established we go in this side of the if
        if(clientSocket == null)
        {
            try  {

                clientSocket = new Socket(ipAddress, port);
                outputStream = new OutputStreamWriter(clientSocket.getOutputStream());
                String messageToSend = "START? 1  " + startingNodeName;
                outputStream.write(messageToSend);
                System.out.println("Send connection request to: " + startingNodeAddress);

                currentConnectionIP = aux[0];
                currentConnectionPort = Integer.parseInt(aux[1]);

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else
        {
            try{
                outputStream = new OutputStreamWriter(clientSocket.getOutputStream());
                String messageToSend = "NOTIFY?" + "\n" + startingNodeName +  "\n" + startingNodeAddress;
                outputStream.write(messageToSend);
                System.out.println("Current node NOTIFIED of new node");

                clientSocket = new Socket(ipAddress,port);
                outputStream = new OutputStreamWriter(clientSocket.getOutputStream());
                String messageToSend2 = "START? 1  " + startingNodeName;
                outputStream.write(messageToSend2);
                System.out.println("Send connection request to: " + startingNodeAddress);

            }
            catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }

        }
        // Implement this!
	// Return true if the 2D#4 network can be contacted
	// Return false if the 2D#4 network can't be contacted



	return true;
    }

    public boolean store(String key, String value) {
        //Sends HashID of the name of the node to the fullnode
        /*Socket clientSocket = null;
        try {
            clientSocket = new Socket(ipAddress,port);
            OutputStream outputStream = clientSocket.getOutputStream();
            String protocol = "STORE 1";
            String hashID =  key + value;
            outputStream.write(protocol.getBytes());
            outputStream.write(HashID.computeHashID(hashID));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
*/
        // Implement this!
	// Return true if the store worked
	// Return false if the store failed
	return true;
    }

    public String get(String key) {
        /*Socket clientSocket = null;
        try {
            clientSocket = new Socket(ipAddress,port);
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(key.getBytes());
            byte[] dataToRead = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
*/
        // Implement this!
	// Return the string if the get worked
	// Return null if it didn't
	return "Not implemented";
    }

    @Override
    public void run() {

    }
}
