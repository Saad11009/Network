import java.io.IOException;

public class Main {public static void main(String[] args) throws IOException {
    FullNode node = new FullNode();
    //node.setHashIDRoNode("younes.bekhti@city.ac.uk");
    node.listen("127.0.0.1:2000", 2000); // Start listening on localhost:4567
    node.handleIncomingConnections("younes.bekhti@city.ac.uk", "127.0.0.1:2000");
}
}


