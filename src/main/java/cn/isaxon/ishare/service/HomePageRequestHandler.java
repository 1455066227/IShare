package cn.isaxon.ishare.service;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import cn.isaxon.ishare.R;
import cn.isaxon.ishare.utils.ResourceUtil;


public final class HomePageRequestHandler implements HttpRequestHandler
{
    private Context context = null;

    public HomePageRequestHandler(Context context)
    {
        this.context = context;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response,
                       HttpContext httpContext) throws HttpException, IOException
    {
        String contentType = "text/html";
        HttpEntity entity = new EntityTemplate(new ContentProducer()
        {
            public void writeTo(final OutputStream outstream)
                    throws IOException
            {
                OutputStreamWriter writer = new OutputStreamWriter(outstream,
                        "UTF-8");
                String resp = ResourceUtil.openHTMLString(context, R.raw.home);

                writer.write(resp);
                writer.flush();
            }
        });

        ((EntityTemplate) entity).setContentType(contentType);

        response.setEntity(entity);
    }
}
