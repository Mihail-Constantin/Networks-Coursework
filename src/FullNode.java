// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// YOUR_NAME_GOES_HERE
// YOUR_STUDENT_ID_NUMBER_GOES_HERE
// YOUR_EMAIL_GOES_HERE


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

// DO NOT EDIT starts
interface FullNodeInterface {
    public boolean listen(String ipAddress, int portNumber);
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress);
}
// DO NOT EDIT ends


public class FullNode implements FullNodeInterface {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private HashMap<Integer, List<String>> networkMap = new HashMap<Integer, List<String>>();

    private HashMap<String[],String[]> valueMap = new HashMap<>();

    private BufferedReader bufferedReader;

    private BufferedWriter writer;
    private Socket connectClientSocket;

    public boolean listen(String ipAddress, int portNumber) {

        try {
            serverSocket = new ServerSocket(Integer.parseInt(ipAddress), portNumber);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) {
        while (true) {

            String[] aux = null;
            try {
                aux = startingNodeAddress.split(":");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Node address is not correct");
                return;
            }
            if (aux.length == 2)
                try {
                    connectClientSocket = serverSocket.accept();
                    clientSocket = new Socket(aux[0], Integer.parseInt(aux[1]));
                    bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String message = bufferedReader.readLine();
                    String[] messageLines = message.split("\n");
                    String[] protocol = messageLines[0].split(" ");
                    switch (protocol[0]) {
                        case "GET?": {
                            serverGet(messageLines, connectClientSocket);
                            break;
                        }
                        case "STORE?": {
                            serverStore(messageLines,connectClientSocket, protocol);
                            break;
                        }
                        case "START?": {
                            serverStart(connectClientSocket, startingNodeName);
                            break;
                        }
                        case "NOTIFY?": {
                            serverNotify(messageLines, serverSocket.getInetAddress().toString(), serverSocket.getLocalPort());
                            break;
                        }
                        case "NEAREST:": {
                            serverNearest(connectClientSocket, protocol);
                            break;
                        }
                        default:
                        {
                            writer.write("END? Unknown Protocol" );
                            clientSocket.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            else
                System.out.println("Invalid connection");
            // Implement this!
        }
    }

    private void serverNearest(Socket connectClientSocket, String[] protocol) throws IOException{
        OutputStream output = connectClientSocket.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(output);
        byte[] hashID = protocol[1].getBytes();
        StringBuilder message = new StringBuilder("NODES 3" + "\n");
        HashMap<Integer,String> aux = new HashMap<>();
        for(Map.Entry<Integer,List<String>> entry : networkMap.entrySet())
        {
            try {
                for (String helper: entry.getValue()) {
                    aux.put(byteDistance(hashID,HashID.computeHashID(helper)),helper);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            int noOfNodes = 0;
            HashMap<Integer, List<String>> tempMap = networkMap;

            while(noOfNodes !=3)
            {
                Integer minKey = Collections.min(aux.keySet());
                List<String> values = tempMap.get(minKey);
                if (values.size() < 3) {
                    for (String helper : values) {
                        if(noOfNodes !=3)
                        {
                            message.append(helper).append("\n");
                            noOfNodes++;
                        }
                    }
                    tempMap.remove(minKey);
                }
                else
                    for(String helper : values)
                        if(noOfNodes !=3)
                        {
                            message.append(helper).append("\n");
                            noOfNodes++;
                        }
            }
        }
        writer.write(String.valueOf(message));
    }

    private void serverStart(Socket connectClientSocket, String startingNodeName) throws IOException {
        OutputStream output = connectClientSocket.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(output);
        writer.write("START? 1" + startingNodeName);
    }

    private void serverNotify(String[] messageLines, String s, int localPort) throws Exception {
        String currentNodeAddress = s+ ":" + localPort;
        String notifiedNodeAddress = messageLines[1] + ":" + messageLines[2];
        byte[] currentNodeHashID = HashID.computeHashID(currentNodeAddress);
        byte[] notifiedNodeHashID = HashID.computeHashID(notifiedNodeAddress);
        int distance = byteDistance(currentNodeHashID,notifiedNodeHashID);
        if(distance <=3 && distance >0)
        {
            List<String> valuesList = networkMap.get(distance);
            valuesList.add(notifiedNodeAddress);
            networkMap.remove(distance);
            networkMap.put(distance,valuesList);

        }

    }

    private void serverStore(String[] messageLines, Socket connectClientSocket, String[] protocol) throws IOException {
        OutputStream output = connectClientSocket.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(output);
        String keyLines = protocol[1];
        String valueLines = protocol[2];
        String[] key = new String[10];
        String[] value = new String[10];
        for(int i = 1; i < 1+ Integer.parseInt(keyLines); i++)
        {
            int aux = 0;
            key[aux] = messageLines[i];
            key[1+ Integer.parseInt(keyLines)] = "\n";
        }
        for(int i = Integer.parseInt(keyLines); i<Integer.parseInt(keyLines) + Integer.parseInt(valueLines);i++)
        {
            int aux = 0;
            value[aux] = messageLines[i];
            value[1+Integer.parseInt(valueLines)] = "\n";
        }
        if(valueMap.get(key) != null)
            writer.write("SUCCESS");
        else
            writer.write("FAILURE");

    }

    private void serverGet(String[] key, Socket connectClientSocket) throws IOException
    {
        OutputStream output = connectClientSocket.getOutputStream();
        if(valueMap.get(key) != null) {
            OutputStreamWriter writer = new OutputStreamWriter(output);
            String message = Arrays.toString(valueMap.get(key));
            writer.write("VALUE " + valueMap.get(key).length + "\n" + message);
        }


    }

    private int byteDistance(byte[] x, byte[] y)
    {
        if(x.length != y.length)
            return 0;
        int distance = 0;
        for(int i = 0; i< x.length; i++)
            if(x[i] != y[i])
                distance++;
        return distance;
    }
}
