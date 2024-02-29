import java.net.*;
import java.util.Enumeration;

public class NetworkInfo {
    public static void main(String[] args) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                System.out.println("Interface: " + networkInterface.getName());
                System.out.println("Display Name: " + networkInterface.getDisplayName());
                System.out.println("Hardware Address: " + getMACAddress(networkInterface));

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        System.out.println("  IP Address: " + inetAddress.getHostAddress());
                        NetworkInterface subNetInterface = NetworkInterface.getByInetAddress(inetAddress);
                        InterfaceAddress subNet = subNetInterface.getInterfaceAddresses().stream()
                                .filter(ia -> ia.getAddress().equals(inetAddress))
                                .findFirst().orElse(null);
                        if (subNet != null) {
                            System.out.println("  Subnet Mask: " + getSubnetMask(subNet.getNetworkPrefixLength()));
                        }
                    }
                }
                System.out.println();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private static String getMACAddress(NetworkInterface networkInterface) throws SocketException {
        byte[] mac = networkInterface.getHardwareAddress();
        if (mac == null) return "Unknown";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        return sb.toString();
    }

    private static String getSubnetMask(int prefixLength) {
        int maskLength = 32 - prefixLength;
        int mask = 0xffffffff << maskLength;
        byte[] bytes = new byte[]{
                (byte) (mask >>> 24),
                (byte) (mask >> 16 & 0xff),
                (byte) (mask >> 8 & 0xff),
                (byte) (mask & 0xff)
        };
        try {
            InetAddress inetAddress = InetAddress.getByAddress(bytes);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}
