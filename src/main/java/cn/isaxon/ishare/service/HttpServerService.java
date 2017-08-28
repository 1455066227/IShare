package cn.isaxon.ishare.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.isaxon.ishare.utils.LogUtils;


public final class HttpServerService extends Service
{
    private AndroidHttpServer webServer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent != null)
        {
            Integer port = intent.getIntExtra("port", 8080);
            LogUtils.log("port", port);
            webServer = new AndroidHttpServer(this, port,
                    intent.getStringExtra("sharePath"));
            webServer.startServer();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        webServer.stopServer();
        webServer = null;
    }
}
