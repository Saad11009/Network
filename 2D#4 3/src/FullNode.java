// FullNode.java

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

interface FullNodeInterface {
    public boolean listen(String ipAddress, int portNumber) throws IOException;
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress);
}

public class FullNode implements FullNodeInterface {
    private Map<String, String> networkMap = new HashMap<>();

    private void handleConnection(Socket clientSocket, String startingNodeName, String startingNodeAddress) {
        try (
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String requestType;
            while ((requestType = in.readLine()) != null) {
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                System.out.println("Request received: " + requestType);

                if (requestType.startsWith("START")) {
                    String fullNodeName = "";
                    out.write("START 1" + " " + fullNodeName);
                    out.newLine(); // Add newline character to indicate end of message
                    out.flush();
                    System.out.println("Start message sent");
                } else if (requestType.startsWith("ECHO")) {
                    // Respond with OHCE
                    out.write("OHCE");
                    out.newLine(); // Add newline character to indicate end of message
                    out.flush();
                    System.out.println("ECHO request processed.");
                } else if (requestType.startsWith("END")) {
                    String[] parts = requestType.split("\\s+", 2);
                    String reason = parts.length > 1 ? parts[1] : "Timed Out";
                    System.out.println("Received END message. Reason: " + reason);
                    // Close the connection
                    clientSocket.close();
                    return; // Exit the method and terminate the thread
                } else if (requestType.startsWith("PUT?")) {
                    try {
                        String[] requestParts = requestType.split("\\s+", 3);
                        if (requestParts.length != 3) {
                            System.out.println("Invalid PUT? request format");
                            continue;
                        }
                        int keyLines = Integer.parseInt(requestParts[1]);
                        int valueLines = Integer.parseInt(requestParts[2]);

                        StringBuilder keyBuilder = new StringBuilder();
                        StringBuilder valueBuilder = new StringBuilder();

                        // Read key lines
                        for (int i = 0; i < keyLines; i++) {
                            String keyLine = in.readLine();
                            keyBuilder.append(keyLine).append("\n");
                        }

                        // Read value lines
                        for (int i = 0; i < valueLines; i++) {
                            String valueLine = in.readLine();
                            valueBuilder.append(valueLine).append("\n");
                        }

                        String key = keyBuilder.toString().trim();
                        String value = valueBuilder.toString().trim();

                        // Store the key-value pair
                        networkMap.put(key, value);
                        System.out.println("store successful");

                        // Respond with SUCCESS
                        out.write("SUCCESS");
                        out.newLine(); // Add newline character to indicate end of message
                        out.flush();
                        System.out.println("Sent SUCCESS response for PUT? request with key: " + key);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (requestType.startsWith("GET?")) {
                    try {
                        String[] requestParts = requestType.split("\\s+", 2);
                        if (requestParts.length != 2) {
                            System.out.println("Invalid GET? request format");
                            continue;
                        }

                        int keyLines = Integer.parseInt(requestParts[1]);
                        StringBuilder keyBuilder = new StringBuilder();

                        // Read key lines
                        for (int i = 0; i < keyLines; i++) {
                            String keyLine = in.readLine();
                            keyBuilder.append(keyLine).append("\n");
                        }

                        String key = keyBuilder.toString().trim();

                        // Check if the key exists in the networkMap
                        if (networkMap.containsKey(key)) {
                            String value = networkMap.get(key);
                            // Respond with VALUE response
                            out.write("VALUE " + value.split("\n").length);
                            out.newLine(); // Add newline character to indicate end of message
                            out.write(value);
                            out.newLine(); // Add newline character to indicate end of value
                            out.flush();
                            System.out.println("Sent VALUE response for GET? request with key: " + key);
                        } else {
                            // Respond with NOPE
                            out.write("NOPE");
                            out.newLine(); // Add newline character to indicate end of message
                            out.flush();
                            System.out.println("Sent NOPE response for GET? request with key: " + key);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean listen(String ipAddress, int portNumber) {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("FullNode is now listening on " + ipAddress + ":" + portNumber);

            while (true) {
                // Accept incoming connections in a loop
                Socket clientSocket = serverSocket.accept();

                // Handle each incoming connection in a separate thread
                new Thread(() -> {
                    handleConnection(clientSocket);
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void handleConnection(Socket clientSocket) {
    }


    @Override
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) {

    }
}

