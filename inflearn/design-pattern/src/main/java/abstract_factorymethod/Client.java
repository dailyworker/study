package abstract_factorymethod;

import factorymethod.Ship;
import factorymethod.WhiteShip;

public class Client extends DefaultShipFactory{
    @Override
    Ship createShip() {
        WhiteShip ship = new WhiteShip();
        ship.setAnchor(new WhiteAnchor());
        ship.setWheel(new WhiteWheel());
        return ship;
    }
}
