package dev.mars.callme.utils;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Shuai
 * 17/03/2019.
 */

public class HotspotUtil {
    public final static String APName = "XiaomiLi8";
    public final static String APPassword = "1111155555";

    /**
     * 创建热点
     *
     * @return
     */
    public static boolean CreatHotspot(WifiManager wifiManager) {
        boolean request;
        //开启热点
        if (wifiManager.isWifiEnabled()) {
            //如果wifi处于打开状态，则关闭wifi,
            wifiManager.setWifiEnabled(false);
        }
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = APName;
        config.preSharedKey = APPassword;
        config.hiddenSSID = false;//是否隐藏网络
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);//开放系统认证
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        //通过反射调用设置热点
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean enable = (Boolean) method.invoke(wifiManager, config, true);
            if (enable) {
                request = true;
            } else {
                request = false;
                LogUtils.E("创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.E(e.toString() + "创建失败");
            request = false;
        }
        return request;
    }


    /**
     * 关闭热点,并开启wifi
     */
    public static void closeWifiHotspot(WifiManager wifiManager) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
        method.setAccessible(true);
        WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
        Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
        method2.invoke(wifiManager, config, false);
        //开启wifi
        wifiManager.setWifiEnabled(true);
    }

}
