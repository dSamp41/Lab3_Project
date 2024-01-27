package src.client;
public class ClientMain {
    public static void main(String[] args) {
        String clientConfigPath = "src/client/client.properties";

        Client client = new Client();
        client.readConfig(clientConfigPath);
        client.start();
    }
    
}
