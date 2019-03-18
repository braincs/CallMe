package dev.mars.callme.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by ma.xuanwei on 2017/3/23.
 */

public class WifiUtils {
    private final static String TAG = WifiUtils.class.getSimpleName();
    public static String getWifiIP(Context c) {
        WifiManager wifiManager = (WifiManager) c.getSystemService(WIFI_SERVICE);
        //获取当前连接的wifi的信息
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
//        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeInfo = null;
//        if (cm != null) {
//            activeInfo = cm.getActiveNetworkInfo();
//        }
//        int ipAddress = getCodecIpAddress(wifiManager, activeInfo);
//
//        Log.d("debug", "ipaddr = " + ipAddress);
        return intToIp(ipAddress);

//        String ipstr = "192.168.43.1";
//        try {
//            ipstr = IPUtils.getIp(c);
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//        return ipstr;
    }

    private static String intToIp(int i) {
        return ((i) & 0xFF) + "."

                + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    public static String getLocalIpAddress(Context c) {
        String ipAddr = "192.168.43.1";
        WifiManager wifiManager = (WifiManager) c.getSystemService(WIFI_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (networkInfo != null && networkInfo.isConnected() && wifiManager.isWifiEnabled()) {
            int ip = wifiInfo.getIpAddress();

            return intToIp(ip);
        }
        Method method = null;
        try {
            method = wifiManager.getClass().getDeclaredMethod("getWifiApState");
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (method != null)
            method.setAccessible(true);
        int actualState = -1;
        try {
            if (method != null)
                actualState = (Integer) method.invoke(wifiManager, (Object[]) null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (actualState == 13) {  //if wifiAP is enabled
//            String[] split = ipAddr.split("//.");
            Log.d("debug", "ipaddr = " + ipAddr);

            return ipAddr; //hardcoded WifiAP ip
        }

//        String ip = "192.168.43.1";
//        try {
//            ip = getLocalIp(c);
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
        return ipAddr;
    }

    public static int getCodecIpAddress(WifiManager wm, NetworkInfo wifi) {
        WifiInfo wi = wm.getConnectionInfo();
        if (wifi != null && wifi.isConnected() && wm.isWifiEnabled())
            return wi.getIpAddress(); //normal wifi
        Method method = null;
        try {
            method = wm.getClass().getDeclaredMethod("getWifiApState");
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (method != null)
            method.setAccessible(true);
        int actualState = -1;
        try {
            if (method != null)
                actualState = (Integer) method.invoke(wm, (Object[]) null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (actualState == 13) {  //if wifiAP is enabled
            String[] split = "192.168.43.1".split("//.");
            return convertIP2Int(split); //hardcoded WifiAP ip
        }
        return 0;
    }

    public static int convertIP2Int(String[] ipAddress) {
        return (int) (Math.pow(256, 3) * (Integer.parseInt(ipAddress[3]) & 0xFF) + Math.pow(256, 2) * (Integer.parseInt(ipAddress[2]) & 0xFF) + 256 * (Integer.parseInt(ipAddress[1]) & 0xFF) + (Integer.parseInt(ipAddress[0]) & 0xFF));
    }

    public static String getLocalIp(Context mContext) throws SocketException {
        String ip = "";
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                String dhcpInfos = intToIp(dhcpInfo.netmask);
                String[] split = intToIp(ipAddress).split("\\.");
                ip = split[0] + "." + split[1] + "." + split[2] + "." + split[3];//根据子网掩码获取广播的IP地址
            } else {
                String asd = getInfo();
                String[] split = asd.split(",");
                String ipStr = split[0];
                String NetMask = split[1];
                String[] split1 = ipStr.split("\\.");
                ip = split1[0] + "." + split1[1] + "." + split1[2] + "." + split1[3];//根据子网掩码获取广播的IP地址
            }
        }
        return ip;
    }

    public static String getBroadcastIp(Context mContext)  {
        String ip = "";
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                String dhcpInfos = intToIp(dhcpInfo.netmask);
                String[] split = intToIp(ipAddress).split("\\.");
                ip = split[0] + "." + split[1] + "." + split[2] + "." + (255 - Integer.parseInt(dhcpInfos.split("\\.")[3]));//根据子网掩码获取广播的IP地址
            } else {

                String asd = null;
                try {
                    asd = getInfo();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                String[] split = new String[0];
                if (asd != null) {
                    split = asd.split(",");
                }
                String ipStr = split[0];
                String NetMask = split[1];
                String[] split1 = ipStr.split("\\.");
                ip = split1[0] + "." + split1[1] + "." + split1[2] + "." + (255 - Integer.parseInt(NetMask.split("\\.")[3]));//根据子网掩码获取广播的IP地址
            }
        }
        return ip;
    }

    public static String getInfo() throws SocketException {
        String ipAddress = "";
        String maskAddress = "";

        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            List<InterfaceAddress> mList = intf.getInterfaceAddresses();
            for (InterfaceAddress l : mList) {
                InetAddress inetAddress = l.getAddress();
                if (!inetAddress.isLoopbackAddress()) {
                    String hostAddress = inetAddress.getHostAddress();
                    if (hostAddress.indexOf(":") > 0) {
                        continue;
                    } else {
                        ipAddress = hostAddress;
                        maskAddress = calcMaskByPrefixLength(l.getNetworkPrefixLength());
                    }
                }
            }
        }
        return ipAddress + "," + maskAddress;
    }


    private static String calcMaskByPrefixLength(int length) {
        int mask = -1 << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }


    public static InetAddress getIpAddress() {
        InetAddress inetAddress = null;
        InetAddress myAddr = null;

        try {
            for (Enumeration < NetworkInterface > networkInterface = NetworkInterface
                    .getNetworkInterfaces(); networkInterface.hasMoreElements();) {

                NetworkInterface singleInterface = networkInterface.nextElement();

                for (Enumeration < InetAddress > IpAddresses = singleInterface.getInetAddresses(); IpAddresses
                        .hasMoreElements();) {
                    inetAddress = IpAddresses.nextElement();

                    if (!inetAddress.isLoopbackAddress() && (singleInterface.getDisplayName()
                            .contains("wlan0") ||
                            singleInterface.getDisplayName().contains("eth0") ||
                            singleInterface.getDisplayName().contains("ap0"))) {

                        myAddr = inetAddress;
                    }
                }
            }

        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return myAddr;
    }


    public static InetAddress getBroadcast(InetAddress inetAddr) {

        NetworkInterface temp;
        InetAddress iAddr = null;
        try {
            temp = NetworkInterface.getByInetAddress(inetAddr);
            List < InterfaceAddress > addresses = temp.getInterfaceAddresses();

            for (InterfaceAddress inetAddress: addresses)

                iAddr = inetAddress.getBroadcast();
            Log.d(TAG, "iAddr=" + iAddr);
            return iAddr;

        } catch (SocketException e) {

            e.printStackTrace();
            Log.d(TAG, "getBroadcast" + e.getMessage());
        }
        return null;
    }

    public static String getBroadcastIP(){
        InetAddress ipAddress = getIpAddress();
        InetAddress broadcast = getBroadcast(ipAddress);
        String hostIP = "192.168.43.1";
        try {
            hostIP = broadcast.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "hostIp " + hostIP);
        return hostIP;
    }

}
