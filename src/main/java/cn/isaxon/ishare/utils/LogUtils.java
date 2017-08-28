package cn.isaxon.ishare.utils;

import android.util.Log;

final public class LogUtils
{
	private static boolean isLog = true;
	public static void log(String tag, Object msg)
	{
		if (isLog)
			Log.i(tag, msg == null ? "null": msg.toString());
	}

	public static void setIsLog(boolean isLog)
	{
		LogUtils.isLog = isLog;
	}
}
