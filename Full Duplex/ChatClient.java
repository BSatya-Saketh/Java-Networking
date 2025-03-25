import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

/**
 * A simple chat client using Java AWT for the user interface.
 * Connects to a server on localhost port 6000.
 */
public class ChatClient extends Frame implements Runnable, ActionListener {
    private static final int PORT = 6000;
    private static final String SERVER_HOST = "localhost";
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;

    private TextArea messageArea;
    private TextField inputField;
    private Button sendButton;
    private Socket socket;
    private Thread receiverThread;
    private BufferedReader reader;
    private PrintWriter writer;

    /**
     * Creates and initializes the chat client UI and network connections.
     */
    public ChatClient() {
        initializeUI();
        connectToServer();
    }

    /**
     * Sets up the user interface components.
     */
    private void initializeUI() {
        setTitle("Chat Client");
        
        // Initialize UI components
        messageArea = new TextArea();
        messageArea.setEditable(false);
        inputField = new TextField("", 40);
        sendButton = new Button("Send");
        sendButton.addActionListener(this);
        
        // Add input components to a panel with FlowLayout
        Panel inputPanel = new Panel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(inputField);
        inputPanel.add(sendButton);
        
        // Add components to frame with BorderLayout
        setLayout(new BorderLayout());
        add(messageArea, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        
        // Configure window
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Add window closing event handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });
        
        setVisible(true);
    }

    /**
     * Establishes a connection to the chat server.
     */
    private void connectToServer() {
        try {
            socket = new Socket(SERVER_HOST, PORT);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Start the message receiver thread
            receiverThread = new Thread(this);
            receiverThread.start();
            
            messageArea.append("Connected to server at " + SERVER_HOST + ":" + PORT + "\n");
        } catch (IOException e) {
            messageArea.append("Error connecting to server: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    /**
     * Handles the button click event to send messages.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            messageArea.append("Me: " + message + "\n");
            writer.println(message);
            inputField.setText("");  // Clear the input field
        }
        // Set focus back to input field
        inputField.requestFocus();
    }

    /**
     * Thread for receiving messages from the server.
     */
    @Override
    public void run() {
        try {
            String incomingMessage;
            while ((incomingMessage = reader.readLine()) != null) {
                final String message = incomingMessage;
                // Update UI on the Event Dispatch Thread
                EventQueue.invokeLater(() -> {
                    messageArea.append("Server: " + message + "\n");
                });
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                messageArea.append("Connection error: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    /**
     * Cleans up resources when disconnecting.
     */
    private void disconnect() {
        try {
            if (receiverThread != null) {
                receiverThread.interrupt();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new ChatClient();
        });
    }
}