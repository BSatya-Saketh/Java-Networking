import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class ClientHandler extends Frame implements Runnable, ActionListener {
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    
    private final Socket clientSocket;
    private final List<ClientHandler> allClients;
    private TextArea messageArea;
    private TextField inputField;
    private Button sendButton;
    private Thread receiverThread;
    private BufferedReader reader;
    private PrintWriter writer;
    
    /**
     * Creates a new client handler with UI for server-side chat.
     * 
     * @param clientSocket The socket connection to the client
     * @param allClients Reference to the list of all connected clients
     */
    public ClientHandler(Socket clientSocket, List<ClientHandler> allClients) {
        this.clientSocket = clientSocket;
        this.allClients = allClients;
        
        initializeConnection();
        initializeUI();
        
        // Start message receiver thread
        receiverThread = new Thread(this);
        receiverThread.start();
    }
    
    /**
     * Sets up network streams for communication.
     */
    private void initializeConnection() {
        try {
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error setting up client connection: " + e.getMessage());
            e.printStackTrace();
            closeConnection();
        }
    }
    
    /**
     * Initializes the user interface for the client handler.
     */
    private void initializeUI() {
        setTitle("Client: " + clientSocket.getPort());
        
        // Initialize UI components
        messageArea = new TextArea();
        messageArea.setEditable(false);
        inputField = new TextField("", 40);
        sendButton = new Button("Send");
        sendButton.addActionListener(this);
        
        // Add input components to a panel
        Panel inputPanel = new Panel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(inputField);
        inputPanel.add(sendButton);
        
        // Add components to frame
        setLayout(new BorderLayout());
        add(messageArea, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        
        // Configure window
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Add window closing event handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeConnection();
                dispose();
            }
        });
        
        setVisible(true);
    }
    
    /**
     * Sends a message to the connected client.
     * 
     * @param message The message to send
     */
    public void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }
    
    /**
     * Handles button click events to send messages.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            messageArea.append("Server: " + message + "\n");
            sendMessage(message);
            inputField.setText("");  // Clear input field
        }
        inputField.requestFocus();
    }
    
    /**
     * Thread for receiving and processing messages from clients.
     */
    @Override
    public void run() {
        try {
            String incomingMessage;
            while ((incomingMessage = reader.readLine()) != null) {
                final String message = incomingMessage;
                // Update UI on the Event Dispatch Thread
                EventQueue.invokeLater(() -> {
                    messageArea.append("Client " + clientSocket.getPort() + ": " + message + "\n");
                });
            }
        } catch (IOException e) {
            if (!clientSocket.isClosed()) {
                System.err.println("Error reading from client: " + e.getMessage());
            }
        } finally {
            closeConnection();
        }
    }
    
    /**
     * Closes all connections and removes this handler from the clients list.
     */
    private void closeConnection() {
        try {
            allClients.remove(this);
            System.out.println("Client disconnected: " + clientSocket.getPort());
            
            if (receiverThread != null) {
                receiverThread.interrupt();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class MCChatServer {
    private static final int PORT = 6000;
    
    public static void main(String[] args) {
        // Use thread-safe list implementation for concurrent access
        List<ClientHandler> clients = new CopyOnWriteArrayList<>();
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server started on port " + PORT);
            
            while (true) {
                System.out.println("Waiting for client connections...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getPort());
                
                // Create and add new client handler
                ClientHandler handler = new ClientHandler(clientSocket, clients);
                clients.add(handler);
                
                // Log all connected clients
                // System.out.println("Connected clients:");
                // for (ClientHandler client : clients) {
                //     System.out.println("- Client on port: " + client.clientSocket.getPort());
                // }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}