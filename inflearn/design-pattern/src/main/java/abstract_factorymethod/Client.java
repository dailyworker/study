package abstract_factorymethod;

import factorymethod.Ship;
import factorymethod.WhiteShip;

public class Client extends DefaultShipFactory{
    private ShipPartsFactory shipPartsFactory;

    public Client(ShipPartsFactory shipPartsFactory) {
        this.shipPartsFactory = shipPartsFactory;
    }

    @Override
    Ship createShip() {
        WhiteShip ship = new WhiteShip();
        ship.setAnchor(shipPartsFactory.createAnchor());
        ship.setWheel(shipPartsFactory.createWheel());
        return ship;
    }
}
