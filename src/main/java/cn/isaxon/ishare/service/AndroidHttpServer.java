package cn.isaxon.ishare.service;

import android.content.Context;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import cn.isaxon.ishare.R;
import cn.isaxon.ishare.utils.LogUtils;
import cn.isaxon.ishare.utils.ResourceUtil;

public final class AndroidHttpServer extends Thread
{
    public static String ipaddress; // ip adress
    public static boolean isRunning = false;

    private BasicHttpProcessor httpProc = null;
    private HttpService httpService = null;
    private HttpRequestHandlerRegistry registry = null;

    //
    private int port;

    public AndroidHttpServer(Context context, int port, String sharePath)
    {
        this.port = port;

        // copy file to cache
        ResourceUtil.copyAssetsToCache(context, "icon.png");
        ResourceUtil.copyAssetsToCache(context, "floder.png");
        ResourceUtil.copyAssetsToCache(context, "file.png");

        this.ipaddress = context.getString(R.string.str_hotspotIp);

        httpProc = new BasicHttpProcessor();

        httpProc.addInterceptor(new ResponseDate());
        httpProc.addInterceptor(new ResponseServer());
        httpProc.addInterceptor(new ResponseContent());
        httpProc.addInterceptor(new ResponseConnControl());

        httpService = new HttpService(httpProc,
                new DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory());

        registry = new HttpRequestHandlerRegistry();
        registry.register("/assets*", new AssetsFileRequestHandler(context)); // 访问assets下的文件， 这些文件已经被copy到cache目录下了就是
        registry.register("/test*", new HomePageRequestHandler(context));
        registry.register("*", new ShareFileRequestHandler(context, sharePath, this.port)); // 注册访问文件地址
        registry.register("/apk*", new ShareAppRequestHandler(context, port)); // 注册访问共享apk地址
        registry.register("/UploadFile", new UploadFileRequestHandler(context, port)); // 注册文件上传的地址

        httpService.setHandlerResolver(registry);
    }

    @Override
    public void run()
    {
        super.run();
        try
        {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            while (AndroidHttpServer.isRunning)
            {
                Socket socket = serverSocket.accept();
                DefaultHttpServerConnection serverConnection = new DefaultHttpServerConnection();
                serverConnection.bind(socket, new BasicHttpParams());

                new ResposedClientThread(serverConnection, httpService).start();
            }
            LogUtils.log("httpserver", "finishStop");
            serverSocket.close();
            serverSocket = null;
        }
        catch (BindException e)
        {

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void startServer()
    {
        this.isRunning = true;
        super.start();
    }

    public synchronized void stopServer()
    {
        this.isRunning = false;
    }


    static class ResposedClientThread extends Thread
    {
        private DefaultHttpServerConnection serverConnection = null;
        private HttpService httpService = null;

        public ResposedClientThread(DefaultHttpServerConnection serverConnection,
                                    HttpService httpService)
        {
            this.serverConnection = serverConnection;
            this.httpService = httpService;
        }

        @Override
        public void run()
        {
            super.run();

            HttpContext httpContext = new BasicHttpContext();
            try
            {
                while (isRunning && !Thread.interrupted() && this.serverConnection.isOpen())
                    httpService.handleRequest(serverConnection, httpContext);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (HttpException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    this.serverConnection.shutdown();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}