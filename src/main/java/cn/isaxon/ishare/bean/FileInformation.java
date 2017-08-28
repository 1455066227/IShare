package cn.isaxon.ishare.bean;

import java.io.File;
import java.sql.Timestamp;
import java.io.Serializable;

/**
 * File info
 * Created by qq145 on 8/28/2017.
 */
public final class FileInformation implements Serializable
{
    private static final long serialVersionUID = 3743401889148367462L;
    private String name = null;
    private long size;
    private String path = null;
    private boolean isDir = false;
    private String lastModifyTime = null;
    private String suffix = null;

    public String toString()
    {
        return "FileInformation [name=" + this.name + ", size=" + this.size + ", isDir=" + this.isDir + ", lastModifyTime=" + this.lastModifyTime + "]";
    }

    public FileInformation(File file)
    {
        this.name = file.getName();
        this.size = file.length();
        this.isDir = file.isDirectory();
        this.path = file.getPath();
        this.lastModifyTime = (new Timestamp(file.lastModified())).toString();
        this.suffix = getSuffix(this.name);
    }

    public static String getSuffix(String name)
    {
        return name.split("\\.").length == 1 ? "" : name.split("\\.")[name.split("\\.").length - 1].toLowerCase();
    }

    public String getSuffix()
    {
        return this.suffix;
    }

    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
    }

    public String getPath()
    {
        return this.path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getSize()
    {
        return this.size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public boolean isDir()
    {
        return this.isDir;
    }

    public void setDir(boolean isDir)
    {
        this.isDir = isDir;
    }

    public String getLastModifyTime()
    {
        return this.lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime)
    {
        this.lastModifyTime = lastModifyTime;
    }
}
