import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class HostnameFetcher{
	
	// private static Object inetAddressImpl;
	// private static Method getHostByAddr;

	// static {
	// 	try {
	// 		Field impl = InetAddress.class.getDeclaredField("impl");
	// 		impl.setAccessible(true);
	// 		inetAddressImpl = impl.get(null);
	// 		getHostByAddr = inetAddressImpl.getClass().getDeclaredMethod("getHostByAddr", byte[].class);
	// 		getHostByAddr.setAccessible(true);
	// 	}
	// 	catch (Exception e) {
	// 		System.out.println("error occured");
	// 	}
	// }

	// public static final String ID = "fetcher.hostname";

	public HostnameFetcher() {}

	// public String getId() {
	// 	return ID;
	// }

	// @SuppressWarnings("PrimitiveArrayArgumentToVariableArgMethod")
	// private String resolveWithRegularDNS(InetAddress ip) {
	// 	try {
	// 		// faster way to do lookup - getCanonicalHostName() actually does both reverse and forward lookups inside
	// 		return (String) getHostByAddr.invoke(inetAddressImpl, ip.getAddress());
	// 	}
	// 	catch (Exception e) {
	// 		if (e instanceof InvocationTargetException && e.getCause() instanceof UnknownHostException)
	// 			return null;

	// 		// return the returned hostname only if it is not the same as the IP address (this is how the above method works)
	// 		String hostname = ip.getCanonicalHostName();
	// 		return ip.getHostAddress().equals(hostname) ? null : hostname;
	// 	}
	// }

	private String resolveWithMulticastDNS(InetAddress ip) {
		try {
			MDNSResolver resolver = new MDNSResolver(1000);
			String name = resolver.resolve(ip);
			resolver.close();
			return name;
		}
		catch (SocketTimeoutException | SocketException e) {
			return null;
		}
		catch (Exception e) {
			return null;
		}
	}

	private String resolveWithNetBIOS(InetAddress ip) {
		try {
			NetBIOSResolver resolver = new NetBIOSResolver(1000);
			String[] names = resolver.resolve(ip);
			resolver.close();
			return names == null ? null : names[0];
		}
		catch (SocketTimeoutException | SocketException e) {
			return null;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String resolveHostname(String ipAddress) {
		try {
			InetAddress ip = InetAddress.getByName(ipAddress);
			
			String name = resolveWithMulticastDNS(ip);
			if (name == null) name = resolveWithNetBIOS(ip);
			return name;
		} catch (UnknownHostException e) {
			return null;
		}
	}
}
