package eu.kpgtb.shop.serivce.impl;

import eu.kpgtb.shop.serivce.iface.IRconService;
import eu.kpgtb.shop.serivce.impl.rcon.RconClient;
import lombok.SneakyThrows;

public class RconServiceImpl implements IRconService {
    @SneakyThrows
    @Override
    public void sendMessage(String socket, String password, String message) {
        String[] elements = socket.split(":",2);

        RconClient client = new RconClient(elements[0], Integer.parseInt(elements[1]), password.getBytes());
        client.command(message);
        client.disconnect();
    }
}
