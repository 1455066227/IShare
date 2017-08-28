package cn.isaxon.ishare.service;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URLDecoder;

import cn.isaxon.ishare.utils.FileUtil;


/**
 * 请求assets目录文件的类（文件已经移到了cache目录下）
 */
public final class AssetsFileRequestHandler implements HttpRequestHandler
{
    private Context context = null;

    public AssetsFileRequestHandler(Context context)
    {
        this.context = context;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response,
                       HttpContext httpContext) throws HttpException, IOException
    {
        String contentType = "text/html";
        String uriPath = URLDecoder.decode(request.getRequestLine().getUri(), "utf-8"); // 如果有中文就把url解码
        uriPath = uriPath.replace("assets", "");
        String filePath = context.getCacheDir().getPath();
        if (!uriPath.equalsIgnoreCase("/"))
        {
            filePath += uriPath;
        }
        HttpEntity entity = null;
        final File file = new File(filePath);
        if (file.exists() && file.isFile())
        {
            if (FileUtil.isImage(FileUtil.getSuffix(file.getName())))
            {
                contentType = URLConnection.guessContentTypeFromName(file
                        .getAbsolutePath());
                entity = new FileEntity(file, contentType);

                response.addHeader("Content-Type", contentType);
            }
        }

        response.setEntity(entity);
    }
}
