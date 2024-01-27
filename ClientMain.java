public class ClientMain {
    public static void main(String[] args) {
        String clientConfigPath = "client.properties";

        Client client = new Client();
        client.readConfig(clientConfigPath);
        client.start();
    }
    
}
