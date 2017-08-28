package cn.isaxon.ishare.bean;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.Serializable;

public final class AppInfomation implements Serializable
{
    private String appPath = null;
    private Long appSize = null;
    private String appName = null;
    private Drawable appIcon = null;

    public AppInfomation(ApplicationInfo info, PackageManager pm)
    {
        appPath = info.sourceDir;

        File apkFile = new File(info.sourceDir);
        appSize = apkFile.length();

        appName = info.loadLabel(pm).toString();

        appIcon = info.loadIcon(pm);
    }

    public AppInfomation(String appPath, String appName)
    {
        this.appPath = appPath;
        this.appSize = Long.valueOf(new File(appPath).length());
        this.appName = appName;
    }

    @Override
    public String toString()
    {
        return "AppInfomation{" +
                "appPath='" + appPath + '\'' +
                ", appSize=" + appSize +
                ", appName='" + appName + '\'' +
                ", appIcon=" + appIcon +
                '}';
    }

    public String getAppPath()
    {
        return appPath;
    }

    public Long getAppSize()
    {
        return appSize;
    }

    public String getAppName()
    {
        return appName;
    }

    public Drawable getAppIcon()
    {
        return appIcon;
    }
}
