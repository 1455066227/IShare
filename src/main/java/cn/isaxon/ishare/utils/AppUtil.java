package cn.isaxon.ishare.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.isaxon.ishare.base.BaseActivity;
import cn.isaxon.ishare.service.HttpServerService;


/**
 * 单例模式, 各个共用的方法和数据
 */

public final class AppUtil
{
    // 进入主界面后所有打开的Activity, 取出的时候要以栈的方式取
    public static List<BaseActivity> activityStack = new ArrayList<>();

    /**
     * 获取当前处于那个activity
     * @return 当前活动的activity
     */
    public static BaseActivity getCurrentActivity()
    {
        return AppUtil.activityStack.get(AppUtil.activityStack.size() - 1);
    }

    /**
     * 进程和服务统统杀死, 并且将所有存活的activity杀死
     * 关闭所有通知
     */
    public static void stopApp()
    {
        // 停止服务
        getCurrentActivity().stopService(new Intent(getCurrentActivity(), HttpServerService.class));

        if (activityStack.get(0) != null)
        {
            // 销毁通知
            NotificationManager manager =
                    (NotificationManager) activityStack.get(0).getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancelAll();
        }

        // 销毁所有存活的activity
        // 销毁所有activity时, 应该按照栈的方式销毁(先保存的activity后销毁)
        BaseActivity activity;
        for (int i = activityStack.size() - 1; i >= 0; i--)
        {
            // 这些activity的finish函数都被重写了, 即在结束activity前都会把其从activityStact中移除
            activity = activityStack.get(i);
            if (activity != null)
                activity.finish();
        }
    }
}
