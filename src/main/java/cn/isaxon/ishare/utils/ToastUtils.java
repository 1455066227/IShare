package cn.isaxon.ishare.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * <p>Toast utils<p/>
 * Created by toor on 1/30/2017.
 */

public class ToastUtils
{
    /**
     * 弹短吐司
     * @param context context
     * @param text content
     */
    public static void toastShort(Context context, String text)
    {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹长吐司
     * @param context context
     * @param text content
     */
    public static void toastLong(Context context, Object text)
    {
        Toast.makeText(context, text.toString(), Toast.LENGTH_LONG).show();
    }
}
