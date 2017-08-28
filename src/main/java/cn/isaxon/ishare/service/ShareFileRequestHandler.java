package cn.isaxon.ishare.service;

import android.content.Context;
import android.text.format.Formatter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.List;

import cn.isaxon.ishare.R;
import cn.isaxon.ishare.activity.MainActivity;
import cn.isaxon.ishare.utils.FileUtil;
import cn.isaxon.ishare.utils.LogUtils;
import cn.isaxon.ishare.utils.ResourceUtil;


/**
 * <p>共享文件的响应类
 * 响应网址为 "http://[ip]:[port]/"  </p>
 */
public final class ShareFileRequestHandler implements HttpRequestHandler
{
    private final String sharePath;
    private final Context context;
    private final int port;

    ShareFileRequestHandler(Context context, final String sharePath, int port)
    {
        this.context = context;
        this.sharePath = sharePath;
        this.port = port;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext httpContext) throws HttpException, IOException
    {
        String contentType = "text/html;charset=utf-8";
        // 如果访问的是127.0.0.1:8080， 那么uriPath == "/"
        // 如果访问的是127.0.0.1:8080/test.txt， 那么uriPath == "/test.txt"
        final String uriPath = URLDecoder.decode(request.getRequestLine().getUri(), "utf-8"); // 如果有中文就把url解码
        LogUtils.log("uri", uriPath);

        String filePath;
        if (uriPath.equalsIgnoreCase("/")) // 根目录
        {
            filePath = sharePath;
        }
        else
        {
            filePath = sharePath + uriPath;
        }

        HttpEntity entity;
        final File file = new File(filePath);
        if (file.isDirectory())
        {
            entity = new EntityTemplate(new ContentProducer()
            {
                @Override
                public void writeTo(OutputStream outstream) throws IOException
                {
                    OutputStreamWriter writer = new OutputStreamWriter(
                            outstream, "UTF-8");
                    String htmlTemplate = ResourceUtil.openHTMLString(context, R.raw.file);

                    String fielHtmlList = "";
                    List<File> files = FileUtil.sortFiles(file);
                    assert files != null;
                    for (File f : files)
                    {
                        String fileHtmlItem = "<tr><td>\n" +
                            "\t\t\t<div>\n" +
                            "\t\t\t\t<a href='%LINK_HREF%'>\n" +
                            "\t\t\t\t\t<div style='float:left'>\n" +
                            "\t\t\t\t\t\t<img src='%FILE_TYPE_IMAGE%' style='width:32px; height:32px'/>\n" +
                            "\t\t\t\t\t</div>\n" +
                            "\t\t\t\t\t<div style='float:left'>\n" +
                            "\t\t\t\t\t\t<div>\n" +
                            "\t\t\t\t\t\t\t%FILE_NAME%\n" +
                            "\t\t\t\t\t\t</div>\n" +
                            "\t\t\t\t\t\t<div id='datails'>\n" +
                            "\t\t\t\t\t\t\t%LAST_MODIFY_TIME%&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp%FILE_SIZE%\n" +
                            "\t\t\t\t\t\t</div>\n" +
                            "\t\t\t\t\t</div>\n" +
                            "\t\t\t\t</a>\n" +
                            "\t\t\t</div>\n" +
                            "\t\t</td></tr>\n";
                        // 链接
                        fileHtmlItem = fileHtmlItem.replace("%LINK_HREF%", "http://" + AndroidHttpServer.ipaddress + ":" +
                                port + uriPath + f.getName()+ "/");

                        // 文件类型（文件或文件夹， 图片就显示缩略图）
                        {
                            // 图片文件就把图片的缩略图显示为图片(如果设置了显示缩略图的话)
                            if (MainActivity.isShowThumbnails
                                    && (!f.isDirectory())
                                    && FileUtil.isImage(FileUtil.getSuffix(f.getName())))
                            {
                                fileHtmlItem = fileHtmlItem.replace("%FILE_TYPE_IMAGE%", "http://" + AndroidHttpServer.ipaddress + ":" +
                                    port + uriPath + f.getName()+ "/");
                            }
                            else
                            {
                                fileHtmlItem = fileHtmlItem.replace("%FILE_TYPE_IMAGE%", "http://" + AndroidHttpServer.ipaddress + ":" +
                                        port + (f.isDirectory() ? "/assets/floder.png" : "/assets/file.png"));
                            }
                        }

                        // 文件名称
                        fileHtmlItem = fileHtmlItem.replace("%FILE_NAME%", f.getName());

                        // 文件最后修改时间
                        fileHtmlItem = fileHtmlItem.replace("%LAST_MODIFY_TIME%", new Timestamp(f.lastModified()).toString());

                        // 文件大小
                        fileHtmlItem = fileHtmlItem.replace("%FILE_SIZE%",
                                (f.isDirectory() ? "": Formatter.formatFileSize(context, f.length())));

                        // %SHAR_EFILE%的内容
                        fielHtmlList += fileHtmlItem;
                    }
                    String appShareLink = "&nbsp&nbsp&nbsp&nbsp&nbsp<a href='http://"
                            + AndroidHttpServer.ipaddress + ":" + port + "/apk'>查看共享app</a>";
                    htmlTemplate = htmlTemplate
                            // 整个html的图标
                            .replace("%SHORTCUT_ICON%", "http://" + AndroidHttpServer.ipaddress + ":"+ port + "/assets/icon.png")
                            // 所有共享的文件列表// 响应下载本app(iShare)
                            .replace("%MY_APP_LINK%", "http://" + AndroidHttpServer.ipaddress + ":" + port + "/apk/saxon_ishare_apk")
                            .replace("%SHAR_EFILE%", fielHtmlList)
                            // 设置列表的标题
                            .replace("%SHARE_TITLE%", "共享文件列表" + appShareLink);
                    writer.write(htmlTemplate);
                    writer.flush();
                }
            });

            ((EntityTemplate) entity).setContentType(contentType);
        }
        else if (file.exists())
        {
            // 图片直接返回给客户端显示
            if (FileUtil.isImage(FileUtil.getSuffix(file.getName())))
            {
                contentType = URLConnection.guessContentTypeFromName(file
                        .getAbsolutePath());
                entity = new FileEntity(file, contentType);

                response.addHeader("Content-Type", contentType);
            }
            else // 其他文件直接下载
            {
                // 传入参数file.length(), 以设置content-length
                entity = new MyEntity(new ContentProducer()
                {
                    {}
                    @Override
                    public void writeTo(OutputStream outstream) throws IOException
                    {
                        FileInputStream fis = new FileInputStream(file);

                        int len;
                        byte []buff = new byte[4096];
                        while (-1 != (len = fis.read(buff)))
                        {
                            outstream.write(buff, 0, len);
                            outstream.flush();
                        }
                        fis.close();
                    }
                }, file.length());
                // 设置文件保存的名字
                response.addHeader("Content-Disposition",
                        "attachment; filename=" + new String(file.getName().getBytes("UTF-8"), "iso-8859-1"));
                // 设置强制下载
                ((EntityTemplate)entity).setContentType("application/x-msdownload");
            }
        }
        else
        {
            response.addHeader("Content-Type", contentType);
            entity = new StringEntity("<h1 style='color:red; text-align:center'>文件不存在</h1>", "UTF-8");
        }

        response.setEntity(entity);
    }
}
