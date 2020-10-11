package hulva.luva.wxx.platform.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IPHelp {
	
	private static InetAddress address;
	private static String hostname;
	
	static{
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface network = (NetworkInterface) netInterfaces.nextElement();
				if(network.isVirtual()) { continue; }
				if(!network.isUp()) { continue; }
				if(network.isLoopback()) { continue; }
				Enumeration<InetAddress> addresss = network.getInetAddresses();
				while(addresss.hasMoreElements()){
					InetAddress address = addresss.nextElement();
					if(address.isAnyLocalAddress()) { continue; }
					if(address.isLinkLocalAddress()) { continue; }
					if(address.isLoopbackAddress()) { continue; }
					if(!address.isSiteLocalAddress()) { continue; }
					if(address.getAddress().length != 4) { continue; }
					if(IPHelp.address != null && network.getDisplayName().indexOf("Virtual") != -1) { continue; }
					IPHelp.address = address;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * 获取本机IP地址
	 * @return 本机IP地址
	 */
	public static String getIP() {
		return IPHelp.address.getHostAddress().toLowerCase();
	}
	
	public static InetAddress getAddress(){
		return IPHelp.address;
	}

	/**
	 * 获取本机机器名
	 * @return 本机机器名
	 */
	public static String getHostname() {
		return hostname;
	}
	
	public static String getIPPorts(int... port){
		List<String> buffer =  new ArrayList<>();
		for (int i : port) {
			buffer.add(getIP() + ":" + i);
		}
		return String.join(",", buffer);
	}
	
	public static void main(String[] args) throws UnknownHostException {
		System.out.println(getIP());
		System.out.println(getHostname());
	}
}