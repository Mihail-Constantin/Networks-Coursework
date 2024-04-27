// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// YOUR_NAME_GOES_HERE
// YOUR_STUDENT_ID_NUMBER_GOES_HERE
// YOUR_EMAIL_GOES_HERE


import java.io.*;
import java.net.Socket;

// DO NOT EDIT starts
interface TemporaryNodeInterface {
    public boolean start(String startingNodeName, String startingNodeAddress);
    public boolean store(String key, String value);
    public String get(String key);
}
// DO NOT EDIT ends


public class TemporaryNode implements TemporaryNodeInterface {


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

        if(port > 65535 || port < 0)
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

                currentConnectionIP = ipAddress;
                currentConnectionPort = port;

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
                currentConnectionPort = port;
                currentConnectionIP = ipAddress;

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
        try {
            int keyLines, valueLines;
            String[] aux = key.split(" ");
            keyLines = aux.length;
            aux = value.split(" ");
            valueLines = aux.length;
            OutputStreamWriter outputStream = new OutputStreamWriter(clientSocket.getOutputStream());
            String protocol = "STORE?" + " " + keyLines + " " + valueLines;
            outputStream.write(protocol + "\n" + key + "\n" + value);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Implement this!
	// Return true if the store worked
	// Return false if the store failed
	return true;
    }

    public String get(String key) {
        int keyLines;
        String[] aux = key.split(" ");
        keyLines = aux.length;
        String message;
        try {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStreamWriter outputStream = new OutputStreamWriter(clientSocket.getOutputStream());
            outputStream.write("GET? " + keyLines + "\n" + key);
            if((message = inputStream.readLine()) != null)
            {
                return message;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Cannot connect to Server";
        }
        // Implement this!
	// Return the string if the get worked
	// Return null if it didn't
	return "GET? Did not return any String";
    }

}
