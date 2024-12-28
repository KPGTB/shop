package eu.kpgtb.shop.serivce.impl.rcon;

import java.io.IOException;

public class MalformedPacketException extends IOException {

    public MalformedPacketException(String message) {
        super(message);
    }

}