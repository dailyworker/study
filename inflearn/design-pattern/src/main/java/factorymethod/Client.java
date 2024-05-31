package factorymethod;

public class Client {
    public static void main(String[] args) {
        Client client = new Client();
//        client.print(new WhiteShipFactory(), "WhiteShip", "workingssu@gmail.com");
//        client.print(new BlackShipFactory(), "BlackShip", "workingssu@gmail.com");
    }

    private void print(ShipFactory shipFactory, String name, String email) {
        System.out.println(shipFactory.orderShip(name, email));
    }
}
