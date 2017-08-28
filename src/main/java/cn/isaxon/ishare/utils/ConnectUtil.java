package cn.isaxon.ishare.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class ConnectUtil
{
    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;

    /**
     * 检测网络是否连接
     *
     * @param context : context
     * @return 网络是否连接
     */
    public static boolean isNetConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null)
        {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null)
            {
                for (NetworkInfo ni : infos)
                {
                    if (ni.isConnected())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static void setWifiApEnable(Context context, String ssid)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (!isApEnabled(context)) // 热点未打开
        {
            if (wifiManager.isWifiEnabled())
            {
                wifiManager.setWifiEnabled(false);
            }
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            try
            {
                //配置热点的名称(可以在名字后面加点随机数什么的)
                apConfig.SSID = ssid;

                apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                //通过反射调用设置热点
                Method method = wifiManager.getClass().getMethod(
                        "setWifiApEnabled", apConfig.getClass(), Boolean.TYPE);
                method.invoke(wifiManager, apConfig, true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取wifi状态
     *
     * @param context : context
     * @return : wifi状态
     */
    public static int getWifiState(Context context)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getWifiState();
    }

    /**
     * 判断wifi是否打开
     *
     * @param context : context
     * @return : wifi是否打开
     */
    public static boolean isWifiEnable(Context context)
    {
        int state = getWifiState(context);
        return state == WifiManager.WIFI_STATE_ENABLED || state == WifiManager.WIFI_STATE_ENABLING;
    }

    /**
     * 获取wifi热点状态
     * 用反射机制调用(invoke)获取wifi热点(wifiAp)状态的函数(getWifiApState)
     *
     * @param context : context
     * @return wifi热点状态
     */
    public static int getWifiApState(Context context)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try
        {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            return (int) method.invoke(wifiManager);
        } catch (Exception e)
        {
            return WIFI_AP_STATE_FAILED;
        }
    }

    /**
     * 判断wifi热点是否可用
     *
     * @param mContext : context
     * @return wifi热点是否可用
     */
    public static boolean isApEnabled(Context mContext)
    {
        int state = getWifiApState(mContext);
        return WIFI_AP_STATE_ENABLING == state || WIFI_AP_STATE_ENABLED == state;
    }


    /**
     * 获取链接到当前热点的设备IP：
     *
     * @return : 返回连接上热点的ip
     */
    public static List<String> getConnectedHotIP()
    {
        List<String> connectedIP = new ArrayList<>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4)
                {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return connectedIP;
    }
}
