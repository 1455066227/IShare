package cn.isaxon.ishare.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * <p>资源文件 工具类</p>
 */
final public class ResourceUtil
{
    /**
     * 获取raw资源的字符内容
     * @param context :context
     * @param id : raw文件资源id
     * @return ： raw资源的字符内容
     */
    public static String openHTMLString(Context context, int id)
    {
        InputStream is = context.getResources().openRawResource(id);

        return convertStreamToString(is);
    }

    /**
     * 获取InputStream流里面的字符串
     * @param is ： 流
     * @return ： 字符串
     */
    public static String convertStreamToString(InputStream is)
    {
        if (is != null)
        {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try
            {
                Reader reader = new BufferedReader(new InputStreamReader(is,
                        "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1)
                {
                    writer.write(buffer, 0, n);
                }
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            return writer.toString();
        }
        else
        {
            return "";
        }
    }

    /**
     * Copy Assets File To Cache
     * @param context : context
     * @param fileName : assets file name
     */
    public static void copyAssetsToCache(Context context, String fileName)
    {
        AssetManager manager = context.getAssets();
        try
        {
            InputStream is = manager.open(fileName);
            File short_cut_icon = new File(context.getCacheDir().getPath() + "/" + fileName);
            if (!short_cut_icon.exists()) // cache目录下不存在此文件, 就复制， 否则不复制
            {
                short_cut_icon.createNewFile();
                FileOutputStream fos = new FileOutputStream(short_cut_icon);

                byte []buff = new byte[1024 * 4];
                int len;
                while (-1 != (len = is.read(buff)))
                {
                    fos.write(buff, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
