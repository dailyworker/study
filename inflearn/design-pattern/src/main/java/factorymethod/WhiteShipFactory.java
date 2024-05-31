package factorymethod;

import abstract_factorymethod.ShipPartsFactory;

public class WhiteShipFactory implements ShipFactory {
    private final ShipPartsFactory shipPartsFactory;

    public WhiteShipFactory(ShipPartsFactory shipPartsFactory) {
        this.shipPartsFactory = shipPartsFactory;
    }

    @Override
    public Ship createShip() {
        Ship whiteShip = new WhiteShip();
        whiteShip.setAnchor(shipPartsFactory.createAnchor());
        whiteShip.setWheel(shipPartsFactory.createWheel());
        return whiteShip;
    }
}
