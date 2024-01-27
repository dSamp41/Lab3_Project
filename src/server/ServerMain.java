package src.server;
public class ServerMain {
    public static void main(String[] args) {
        String serverConfigPath = "src/server/server.properties";
        Server server = new Server();
        server.readConfig(serverConfigPath);
        server.start();
    }
}
