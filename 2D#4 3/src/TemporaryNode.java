import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

// DO NOT EDIT starts
interface TemporaryNodeInterface {
    public boolean start(String startingNodeName, String startingNodeAddress);
    public boolean store(String key, String value);
    public String get(String key);
}
// DO NOT EDIT ends

public class TemporaryNode implements TemporaryNodeInterface {

    public boolean start(String nodeName, String nodeAddress) {
        try (Socket socket = new Socket()) {
            String[] addressParts = nodeAddress.split(":");
            String ipAddress = addressParts[0];
            int portNumber = Integer.parseInt(addressParts[1]);

            // Connect to server
            socket.connect(new InetSocketAddress(ipAddress, portNumber));
            System.out.println("Connected to server");

            // Send start message
            try (BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                String startMessage = "START 1 " + nodeName + "\n";
                outputStream.write(startMessage);
                System.out.println(startMessage);
                outputStream.flush();

                // Keep the socket open until all communication is complete
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean store(String key, String value) {
        try (Socket socket = new Socket();
             OutputStream outStrm = socket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String putRequest = "PUT? 1 " + key.length() + " " + value.length() + "\n" + key + "\n" + value + "\n";
            outStrm.write(putRequest.getBytes());

            String putResponse = reader.readLine();
            if (putResponse != null && putResponse.equals("SUCCESS")) {
                System.out.println("Successfully stored pair");
                return true;
            } else if (putResponse != null && putResponse.equals("FAILED")) {
                System.out.println("Failed to store");
                return false;
            } else {
                System.out.println("Error Invalid PUT");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String get(String key) {
        try (Socket socket = new Socket();
             OutputStream outStrm = socket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String getRequest = "GET? " + key.length() + "\n" + key + "\n";
            outStrm.write(getRequest.getBytes());

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line).append("\n");
                if (line.isEmpty()) {
                    break;
                }
            }

            String getResponse = responseBuilder.toString().trim();
            if (getResponse.startsWith("VALUE")) {
                String[] parts = getResponse.split(" ");
                int valueLength = Integer.parseInt(parts[1]);
                StringBuilder valueBuilder = new StringBuilder();
                for (int i = 0; i < valueLength; i++) {
                    valueBuilder.append(reader.readLine()).append("\n");
                }
                return valueBuilder.toString().trim();
            } else if (getResponse.equals("NOPE")) {
                System.out.println("Key not found in the network.");
                return null;
            } else {
                System.out.println("Error in GET response: " + getResponse);
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

