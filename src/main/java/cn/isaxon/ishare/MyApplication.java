package cn.isaxon.ishare;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.tencent.bugly.crashreport.CrashReport;

import cn.isaxon.ishare.utils.LogUtils;

/**
 * Created by toor on 2017/1/15.
 * 初始化工作
 */

public final class MyApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

		/* TENCENT Bugly SDK */
//        CrashReport.initCrashReport(getApplicationContext(), "d3e3f42b89", false);

        // Fresco
        Fresco.initialize(this);

        // Log init
        LogUtils.setIsLog(false);
    }
}
