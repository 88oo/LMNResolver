import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkInterfaceInfo {
    
    public static List<String> getInterfaceIPs(String interfaceName) {
        List<String> ipAddresses = new ArrayList<>();
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
            if (networkInterface == null) {
                System.out.println("Interface not found.");
                System.out.println("Usage: java NetworkInterfaceInfo <interface_name>");
                return ipAddresses;
            }

            System.out.println("Interface: " + networkInterface.getName());
            System.out.println("Display Name: " + networkInterface.getDisplayName());
            System.out.println("Hardware Address: " + getMACAddress(networkInterface));
          
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (!inetAddress.isLoopbackAddress()) {
                    NetworkInterface subNetInterface = NetworkInterface.getByInetAddress(inetAddress);
                    InterfaceAddress subNet = subNetInterface.getInterfaceAddresses().stream()
                            .filter(ia -> ia.getAddress().equals(inetAddress))
                            .findFirst().orElse(null);
                    if (subNet != null) {
                        String subnetMask = getSubnetMask(subNet.getNetworkPrefixLength());
                        ipAddresses.addAll(iterateSubnet(inetAddress, subnetMask));
                    }
                }
            }
            
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipAddresses;
    }

    private static List<String> iterateSubnet(InetAddress inetAddress, String subnetMask) {
        List<String> subnetIPs = new ArrayList<>();
        try {
            byte[] ipBytes = inetAddress.getAddress();
            int ipAddressInt = byteArrayToInt(ipBytes);

            // Calculate the network address
            int subnetMaskInt = byteArrayToInt(parseDottedDecimal(subnetMask));
            int networkAddressInt = ipAddressInt & subnetMaskInt;

            // Iterate over all IP addresses in the network
            for (int i = 1; i <= ((int) Math.pow(2, 32 - countBits(subnetMaskInt))) - 2; i++) {
                byte[] ipAddressBytes = intToByteArray(networkAddressInt + i);
                subnetIPs.add(InetAddress.getByAddress(ipAddressBytes).getHostAddress());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return subnetIPs;
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

    private static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) | (b & 0xFF);
        }
        return value;
    }

    private static byte[] intToByteArray(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value >> 24);
        bytes[1] = (byte) (value >> 16);
        bytes[2] = (byte) (value >> 8);
        bytes[3] = (byte) value;
        return bytes;
    }

    private static byte[] parseDottedDecimal(String dottedDecimal) {
        String[] octets = dottedDecimal.split("\\.");
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) Integer.parseInt(octets[i]);
        }
        return result;
    }

    private static int countBits(int mask) {
        int count = 0;
        while (mask != 0) {
            count += mask & 1;
            mask >>>= 1;
        }
        return count;
    }
    
}
