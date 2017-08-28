package cn.isaxon.ishare.utils;

import android.widget.ImageView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.isaxon.ishare.R;


public final class FileUtil
{
	
	/**
	 * 获取路径的父目录
	 * @param path : 路径
	 * @param isWindowsSeperator : 路径分隔符是否为windows系
	 * @return : 路径的父目录
	 */
	public static String getParentPath(final String path, final boolean isWindowsSeperator)
	{
		String parentPath = "";
		if (isWindowsSeperator)
		{
			String[]splits = path.split("\\\\");
			for (int i = 0; i < splits.length - 1; i++)
			{
				parentPath = parentPath + splits[i] + "\\";
			}
		}
		else
		{
			String[]splits = path.split("/");
			for (int i = 0; i < splits.length - 1; i++)
			{
				parentPath = parentPath + splits[i] + "/";
			}
		}
		return parentPath;
	}
	
	
	/**
	 * <p>根据路径获取文件名，url也适用（只要把seperatorIsWindows设置为false就可以了）</p>
	 * @param filePath : 路径
	 * @param seperatorIsWindows : 路径分隔符是否为windows系
	 */
	public static String getFileNameByPath(final String filePath, final boolean seperatorIsWindows)
	{
		return seperatorIsWindows ? 
				filePath.split("\\\\")[ filePath.split("\\\\").length - 1]
				: filePath.split("/")[ filePath.split("/").length - 1];
	}
	
	/**
	 * 文件大小转换为单位
	 * @param fileSize : 文件大小
	 * @return 字符串表示的大小
	 */
	public static String formatFileSisze(final long fileSize)
	{
		DecimalFormat dFormat = new DecimalFormat("#.0");
		String str_fileSize;
		
		if (fileSize < 1024)
			str_fileSize = dFormat.format((double) fileSize) + "B";
		else if (fileSize < (1024 * 1024))
			str_fileSize = dFormat.format((double) fileSize / 1024) + "KB";
		else if (fileSize < (1024 * 1024 * 1024))
			str_fileSize = dFormat.format((double) fileSize / (1024 * 1024)) + "MB";
		else
			str_fileSize = dFormat.format((double) fileSize / (1024 * 1024 * 1024)) + "GB";
		
		return str_fileSize;
	}
	
	/**
	 * 获取不包括格式的名字(纯名字)
	 * @param name : 文件全部名称(如test.test)
	 * @return 纯名字
	 */
	public static String getPureName(final String name)
	{
		String pureName = "";
		for (int i = 0; i < name.split("\\.").length - 1; i++)
		{
			pureName += name.split("\\.")[i];
			if (i != name.split("\\.").length - 2)
				pureName += ".";
		}
		return pureName;
	}

	
	/**
	 * 根据文件名获取文件后缀, 如果无后缀就返回""
	 * @param name 全面
	 * @return 后缀
	 */
	public static String getSuffix(final String name)
	{
		if (name.split("\\.").length == 1) // 无后缀
			return "";
		return name.split("\\.")[name.split("\\.").length - 1].toLowerCase();
	}
	
	/**
	 * 根据文件后缀名判断是否为图片文件
	 * @param suffix 后缀
	 * @return is image
	 */
	public static boolean isImage(final String suffix)
	{
		return (suffix.equals("png") ||
				suffix.equals("jpg") ||
				suffix.equals("jpeg") ||
				suffix.equals("bmp") ||
				suffix.equals("jpe") ||
				suffix.equals("ico") ||
				suffix.equals("tif") ||
				suffix.equals("gif"));
	}
	
	public static boolean isAudio(final String suffix)
	{
		return (suffix.equals("mp3") ||
				suffix.equals("wmv") ||
				suffix.equals("ogg") ||
				suffix.equals("wav") ||
				suffix.equals("ape") ||
				suffix.equals("flac") ||
				suffix.equals("aac") ||
				suffix.equals("mmf") ||
				suffix.equals("amr") ||
				suffix.equals("m4r") ||
				suffix.equals("m4a"));
	}
	
	/**
	 * 根据文件后缀名判断是否为视频文件
	 * @param suffix 或追
	 * @return is video
	 */
	public static boolean isViedo(final String suffix)
	{
		return (suffix.equals("rmvb") ||
				suffix.equals("flv") ||
				suffix.equals("mp4") ||
				suffix.equals("avi") ||
				suffix.equals("mkv") ||
				suffix.equals("3gp") ||
				suffix.equals("mov") ||
				suffix.equals("mpg"));
	}
	
	/**
	 * 根据文件后缀名判断是否为压缩文件
	 * @param suffix suffix
	 * @return is compress
	 */
	public static boolean isCompress(final String suffix)
	{
		return (suffix.equals("zip") ||
				suffix.equals("rar") ||
				suffix.equals("gz") ||
				suffix.equals("7z") ||
				suffix.equals("cab"));
	}

	/**
	 * 给文件匹配图标
	 * @param iv : 要设置图标的ImageView控件
	 * @param suffix : 匹配文件的后缀
	 */
	public static void matchImageToIV(final ImageView iv, final String suffix)
	{
		if (suffix.equals("txt"))
			iv.setImageResource(R.drawable.file_text);
		else if (suffix.equals("apk"))
			iv.setImageResource(R.drawable.file_apk);
		else if (suffix.equals("sh"))
			iv.setImageResource(R.drawable.file_sh);
		else if (suffix.equals("bat"))
			iv.setImageResource(R.drawable.file_bat);
		else if (suffix.equals("db"))
			iv.setImageResource(R.drawable.file_db);
		else if (suffix.equals("exe"))
			iv.setImageResource(R.drawable.file_exe);
		else if (suffix.equals("html"))
			iv.setImageResource(R.drawable.file_html);
		else if (suffix.equals("pdf"))
			iv.setImageResource(R.drawable.file_pdf);
		else if (suffix.equals("chm"))
			iv.setImageResource(R.drawable.file_chm);
		else if (suffix.equals("doc") || suffix.equals("docx"))
			iv.setImageResource(R.drawable.file_word);
		else if (suffix.equals("ppt") || suffix.equals("pptx"))
			iv.setImageResource(R.drawable.file_ppt);
		else if (suffix.equals("xls") || suffix.equals("xlsx"))
			iv.setImageResource(R.drawable.file_excel);
		else if (suffix.equals("db"))
			iv.setImageResource(R.drawable.file_db);
		else if (suffix.equals("jar"))
			iv.setImageResource(R.drawable.file_jar);
		else if (FileUtil.isImage(suffix))
			iv.setImageResource(R.drawable.file_image);
		else if (FileUtil.isAudio(suffix))
			iv.setImageResource(R.drawable.file_audio);
		else if (FileUtil.isViedo(suffix))
			iv.setImageResource(R.drawable.file_video);
		else if (FileUtil.isCompress(suffix))
			iv.setImageResource(R.drawable.file_compress);
		else
			iv.setImageResource(R.drawable.file_unknow);
	}
	
	/**
	 * 删除文件夹, 如果isLiveCurrentDir == true, 就只保留文件夹; 否则, 文件夹及文件夹内所有都删除
	 * @param delFile file
	 * @param isLiveCurrentDir isLiveCurrentDir
	 */
	public static void deleteDirectory(final File delFile, final boolean isLiveCurrentDir)
	{
		if (delFile.isDirectory())
		{
			File[]files = delFile.listFiles();
			for (File file : files)
			{
				if (file.isDirectory())
					deleteDirectory(file, false);
				else
					file.delete();
			}
			if (!isLiveCurrentDir) // 最后删除文件夹
				delFile.delete();
		}
		else
		{
			System.out.println(delFile.getPath() + "不是文件夹");
		}
	}
	
	/**
	 * 获取文件 / 文件夹的大小
	 * @param file file
	 * @return length
	 */
	public static long getFileSize(final File file)
	{
		if (!file.isDirectory())
		{
			return file.length();
		}
		long totalSize = 0;
		File[]files = file.listFiles();
		for (File f : files)
		{
			totalSize += getFileSize(f);
		}
		return totalSize;
	}
	
	/**
	 * 将文件夹内文件按规则排序(跳过隐藏文件)
	 * 文件夹放上面(按开头字母排序), 相同格式的文件放一起排序, 无格式文件放在最后
	 * @param file : 文件
	 * @return : 返回排序后的文件列表
	 */
	public static List<File> sortFiles(final File file)
	{
		File[]files = file.listFiles();
		if (null == files)
		{
			return null;
		}
		
		List<File> dirList = new ArrayList<>(); // 存放文件夹
		List<File> fileList = new ArrayList<>(); // 存放文件
		Map<String, List<File>> diffFilesuffixMap = new HashMap<>();
							// 相同后缀的放在子list中, list组成diffFilesuffixMap, 
							// diffFilesuffixMap的key为list对应的后缀
		List<String> suffixs = new ArrayList<>(); // 保存所有后缀(相同的只保存一次), 以便于排序
		for (File file2 : files)
		{
			if (file2.isHidden()) // 跳过隐藏文件
				continue;
			if (file2.isDirectory())
				dirList.add(file2);
			else
			{
				// 将所有不同后缀加入子list
				String fileSuffix = FileUtil.getSuffix(file2.getName());

				List<File> list_ = diffFilesuffixMap.get(fileSuffix); // 找到对应后缀的list
				if (list_ == null) // 说明后缀不存在map中, 需要new一个新List来存新的后缀
				{
					suffixs.add(fileSuffix);
					List<File> list = new ArrayList<>();
					list.add(file2);
					diffFilesuffixMap.put(fileSuffix, list);
				} 
				else // 说明后缀已经存在map中, 只要将file录入对应list即可
					list_.add(file2);
			}
		}
		
		Collections.sort(suffixs); // 后缀名排序(会把空后缀的放第一个)
		Integer spaceSufficIndex = null;
		// 遍历Map, 将非空后缀的文件先排序再加入fileList
		for (int i = 0; i < diffFilesuffixMap.size(); i++)
		{
			if (suffixs.get(i).isEmpty()) // 空后缀的直接跳过
			{
				spaceSufficIndex = i;
				continue;
			}
			List<File> list = diffFilesuffixMap.get(suffixs.get(i));
			Collections.sort(list, new FileComparator()); // 相同后缀的文件排序
			fileList.addAll(list);
		}
		// 如果有空后缀的, 那么放到最后
		if (spaceSufficIndex != null)
		{
			List<File> list = diffFilesuffixMap.get(suffixs.get(spaceSufficIndex));
			Collections.sort(list, new FileComparator()); // 相同后缀的文件排序
			fileList.addAll(list);
		}

		Collections.sort(dirList, new FileComparator()); // 文件夹排序
		
		dirList.addAll(fileList); // 加入文件列表
		return dirList;
	}

	/**
	 * 比较文件名
	 * @author bird
	 */
	static final class FileComparator implements Comparator<File>
	{

		@Override
		public int compare(File o1, File o2)
		{
			// 不区分大小写, 就把所有字母转换成小写再比较
			return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
		}
	}
}
