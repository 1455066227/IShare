package cn.isaxon.ishare.service;

import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;

/**
 * <p>实现文件下载的实体类，
 * 因为不实现的花就没法告诉browser文件的长度</p>
 * <p>文件下载时重写getContentLength函数方可重新设置content-length</p>
 * Created by toor on 1/30/2017.
 */

public final class MyEntity extends EntityTemplate implements ContentProducer
{
    private long fileLength;

    MyEntity(ContentProducer contentproducer, long fileLength)
    {
        super(contentproducer);

        this.fileLength = fileLength;
    }

    @Override
    public long getContentLength()
    {
        return fileLength;
    }
}
