public class TempMain {
    public static void main(String[] args) {
        TemporaryNode tempNode = new TemporaryNode();
        String startingNodeName = "localhost";
        String startingNodeAddresss = "127.0.0.1:2000";
       if (tempNode.start(startingNodeName, startingNodeAddresss)) {
            //tempNode.nearest("0f003b106b2ce5e1f95df39fffa34c2341f2141383ca46709269b13b1e6b4832");
            //tempNode.end("Timed Out");
            System.out.println("success >:)");
        } else {
            System.out.println("fail :(");
        }
    }
}