public class ServerMain {
    public static void main(String[] args) {
        String serverConfigPath = "server.properties";
        Server server = new Server();
        server.readConfig(serverConfigPath);
        server.start();
    }
}
