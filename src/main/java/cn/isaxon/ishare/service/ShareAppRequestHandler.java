package cn.isaxon.ishare.service;

import android.content.Context;
import android.text.format.Formatter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;

import cn.isaxon.ishare.R;
import cn.isaxon.ishare.activity.MainActivity;
import cn.isaxon.ishare.bean.AppInfomation;
import cn.isaxon.ishare.utils.FileUtil;
import cn.isaxon.ishare.utils.LogUtils;
import cn.isaxon.ishare.utils.ResourceUtil;


/**
 * <p>共享apk的响应类,
 * 响应网址为  "http://[ip]:[port]/apk*"  </p>
 * Created by toor on 1/30/2017.
 */

public final class ShareAppRequestHandler implements HttpRequestHandler
{
    private Context context = null;
    private int port;
    private Map<String, AppInfomation> shareAppMap;

    public ShareAppRequestHandler(Context context, int port)
    {
        this.context = context;
        this.port = port;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext httpContext) throws HttpException, IOException
    {
        shareAppMap = MainActivity.getShareAppMap();
        String contentType = "text/html;charset=utf-8";

        // 如果访问的是http://127.0.0.1:8080/apk/test.apk， 那么uriPath == "/test.txt"
        final String uriPath = URLDecoder.decode(request.getRequestLine().getUri(), "utf-8"); // 如果有中文就把url解码
        LogUtils.log("uri", uriPath);
        HttpEntity entity;

        String appName = FileUtil.getFileNameByPath(uriPath, false); // 根据网址获取链接中的app应用的名字
        if (uriPath.equals("/apk")) // 显示所有共享的文件
        {
            entity = new EntityTemplate(new ContentProducer()
            {
                public void writeTo(OutputStream paramAnonymousOutputStream)
                        throws IOException
                {
                    OutputStreamWriter writer = new OutputStreamWriter(paramAnonymousOutputStream, "UTF-8");
                    String htmlTemplate = ResourceUtil.openHTMLString(context, R.raw.file);
                    String fielHtmlList = "";

                    // 遍历出所有的共享app
                    Iterator iterator = shareAppMap.entrySet().iterator();
                    while (iterator.hasNext())
                    {
                        AppInfomation appInfo = (AppInfomation)((Map.Entry)iterator.next()).getValue();;
                        String appName = appInfo.getAppName();
                        String appSize = Formatter.formatFileSize(context, appInfo.getAppSize());
                        String fileHtmlItem = ("<tr><td>\n\t\t\t<div>\n" +
                                "\t\t\t\t<a href='%LINK_HREF%'>\n" +
                                "\t\t\t\t\t<div style='float:left'>\n" +
                                "\t\t\t\t\t\t<img style='width:32px;height:32px' src='http://" + AndroidHttpServer.ipaddress + ":" + ShareAppRequestHandler.this.port + "/assets/icon.png'/>\n" +
                                "\t\t\t\t\t</div>\n\t\t\t\t\t<div style='float:left'>\n" +
                                "\t\t\t\t\t\t<div>\n\t\t\t\t\t\t\t%FILE_NAME%\n" +
                                "\t\t\t\t\t\t</div>\n" +
                                "\t\t\t\t\t\t<div id='datails'>\n" +
                                "\t\t\t\t\t\t\t%FILE_SIZE%\n" +
                                "\t\t\t\t\t\t</div>\n" +
                                "\t\t\t\t\t</div>\n" +
                                "\t\t\t\t</a>\n" +
                                "\t\t\t</div>\n" +
                                "\t\t</td></tr>\n")
                                .replace("%LINK_HREF%", "http://" + AndroidHttpServer.ipaddress + ":" + port + "/apk/" + appName + "/")
                                .replace("%FILE_NAME%", appName)
                                .replace("%FILE_SIZE%", appSize);
                        fielHtmlList += fileHtmlItem;
                    }
                    String fileShareLink = "&nbsp&nbsp&nbsp&nbsp&nbsp<a href='http://"
                            + AndroidHttpServer.ipaddress + ":" + port + "'>查看共享文件</a>";
                    writer.write(htmlTemplate
                            // 整个html的图标
                            .replace("%SHORTCUT_ICON%", "http://" + AndroidHttpServer.ipaddress + ":" + port + "/assets/icon.png")
                            // 所有共享的app列表
                            .replace("%SHAR_EFILE%", fielHtmlList)
                            // 响应下载本app(iShare)
                            .replace("%MY_APP_LINK%", "http://" + AndroidHttpServer.ipaddress + ":" + port + "/apk/saxon_ishare_apk")
                            // 设置列表的标题
                            .replace("%SHARE_TITLE%", "共享APP列表" + fileShareLink));
                    writer.flush();
                }
                {}
            });
        }
        else
        {
            // 判断app应用是否存在
            AppInfomation appInfomation = null;
            for (Map.Entry<String, AppInfomation> entry : shareAppMap.entrySet())
            {
                AppInfomation infomation = entry.getValue();
                if (appName.equals(infomation.getAppName()))
                {
                    appInfomation = infomation;
                    break;
                }
            }
            // 访问下载本应用
            if (uriPath.equals("/apk/saxon_ishare_apk"))
            {
                appInfomation = new AppInfomation(context.getPackageResourcePath(), "iShare");
            }

            if (appInfomation != null)
            {
                final File file = new File(appInfomation.getAppPath());
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
                        "attachment; filename=" + new String((appInfomation.getAppName() +".apk").getBytes("UTF-8"), "iso-8859-1"));
                // 设置强制下载
                ((EntityTemplate)entity).setContentType("application/x-msdownload");
            }
            else
            {
                response.addHeader("Content-Type", contentType);
                entity = new StringEntity("<h1 style='color:red; text-align:center'>没有共享的应用</h1>", "UTF-8");
            }
        }

        response.setEntity(entity);
    }
}
