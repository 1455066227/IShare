package cn.isaxon.ishare.service;

import android.content.Context;
import android.os.Environment;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;

import cn.isaxon.ishare.utils.LogUtils;


/**
 * <p>响应文件上传
 * 响应网址为 "http://[ip]:[port]/UploadFile/"</p>
 * Created by toor on 2/20/2017.
 */

public final class UploadFileRequestHandler implements HttpRequestHandler
{
    private Context context = null;
    private int port;

    public UploadFileRequestHandler(Context context, int port)
    {
        this.context = context;
        this.port = port;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse response, HttpContext httpContext) throws HttpException, IOException
    {
        String method = httpRequest.getRequestLine().getMethod();
        String uri = httpRequest.getRequestLine().getUri();
        LogUtils.log("method", method);
        LogUtils.log("upload uri", uri);

        LogUtils.log("Headers", ""); // empty line before each request
        LogUtils.log("Headers", httpRequest.getRequestLine());
        LogUtils.log("Headers", "-------- HEADERS --------");
        for(Header header: httpRequest.getAllHeaders())
        {
            LogUtils.log("Headers", header.getName() + " : " + header.getValue());
        }
        LogUtils.log("Headers", "------------------------");

        HttpEntity entity = null;
        if (httpRequest instanceof HttpEntityEnclosingRequest)
        {
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/s.jpg");
            entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
            entity.writeTo(fos);

            LogUtils.log("entityString", EntityUtils.toString(entity));
            /*InputStream is = entity.getContent();
            int len = 0;
            byte []buff = new byte[1024];
            while (-1 != (len = is.read(buff)))
            {
                fos.write(buff, 0, len);
                fos.flush();
            }
            fos.close();*/
        }
    }
}
