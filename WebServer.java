
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class WebServer {

    public static int PORT = 8080;
    public static int THREADS = 5;
    public static String ROOT = ".";

    public static void main(String[] args) {

        if (args.length >= 1) PORT = Integer.parseInt(args[0]);
        if (args.length >= 2) THREADS = Integer.parseInt(args[1]);
        if (args.length >= 3) ROOT = args[2];

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Server running on port " + PORT);

            while (true) {
                Socket client = server.accept();
                pool.execute(new ClientHandler(client));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// ================= CLIENT HANDLER =================

class ClientHandler implements Runnable {

    private Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {

        String requestLine = "";
        String fileName = "index.html";

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));

            requestLine = in.readLine();
            logAccess("Request: " + requestLine);

            if (requestLine != null && requestLine.startsWith("GET")) {
                String[] parts = requestLine.split(" ");
                String path = parts[1];

                if (path.equals("/")) {
                    fileName = "index.html";
                } else {
                    fileName = path.substring(1);
                }
            }

            if (fileName.contains("..")) {
                throw new Exception("Invalid path");
            }

            File file = new File(WebServer.ROOT, fileName);
            OutputStream out = client.getOutputStream();

            try {
                if (file.exists()) {

                    BufferedReader fileReader = new BufferedReader(new FileReader(file));
                    String line;

                    String header = "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: text/html; charset=UTF-8\r\n\r\n";
                    out.write(header.getBytes());

                    while ((line = fileReader.readLine()) != null) {
                        out.write(line.getBytes());
                    }

                    fileReader.close();

                } else {
                    String notFound = "HTTP/1.1 404 Not Found\r\n\r\n<h1>404 Not Found</h1>";
                    out.write(notFound.getBytes());
                }

            } catch (Exception e) {
                logError(e.toString());

                String error = "HTTP/1.1 500 Internal Server Error\r\n\r\n<h1>500 Internal Server Error</h1>";
                out.write(error.getBytes());
            }

            out.flush();
            client.close();

        } catch (Exception e) {
            logError(e.toString());
        }
    }

    private void logAccess(String msg) {
        try (FileWriter fw = new FileWriter("access.log", true)) {
            fw.write(msg + "\n");
        } catch (IOException ignored) {}
    }

    private void logError(String msg) {
        try (FileWriter fw = new FileWriter("error.log", true)) {
            fw.write(msg + "\n");
        } catch (IOException ignored) {}
    }
}
