package networking;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Marcus on 30/03/2019.
 */

public class PeerManager {

    private static ConcurrentHashMap<String,PeerDevice> peerDevices = new ConcurrentHashMap<>();

    public static void registerOrUpdatePeerDevice(PeerDevice device)
    {
        peerDevices.put(device.getMacAddress(),device);
    }

    public static ConcurrentHashMap<String,PeerDevice> getPeersDevices()
    {
        return peerDevices;
    }



}
