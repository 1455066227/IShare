package cn.isaxon.ishare.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.isaxon.ishare.R;
import cn.isaxon.ishare.bean.FileInformation;
import cn.isaxon.ishare.utils.ColorShades;
import cn.isaxon.ishare.utils.FileUtil;

public final class FileAdapter extends BaseAdapter
{
	private ListView listView = null;
	private List<FileInformation> files = null;
	private Context context = null;
	public FileAdapter(Context context, List<FileInformation> files, ListView listView)
	{
		this.context = context;
		this.files = files;
		this.listView = listView;
	}

	public void updateDataAdapter(List<FileInformation> files)
	{
		this.files.clear();
		this.files.addAll(files);
		notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		return files.size();
	}

	@Override
	public Object getItem(int position)
	{
		return files.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder mHolder;
		if (null == convertView)
		{
			convertView = View.inflate(context, R.layout.lvitem_file, null);
			
			mHolder = new ViewHolder();
			mHolder.iv_fileImage = convertView.findViewById(R.id.iv_fileImage);
			mHolder.tv_fileName = convertView.findViewById(R.id.tv_fileName);
			mHolder.tv_fileInfo = convertView.findViewById(R.id.tv_fileInfo);
			
			convertView.setTag(mHolder);
		}
		else
		{
			mHolder = (ViewHolder) convertView.getTag();
		}
		FileInformation fileInformation = files.get(position);
		
		ImageView iv_fileImage = mHolder.iv_fileImage;
		TextView tv_fileName = mHolder.tv_fileName;
		TextView tv_fileInfo = mHolder.tv_fileInfo;
		
		if (fileInformation.isDir())
		{
			iv_fileImage.setImageResource(R.drawable.file_floder);
			tv_fileInfo.setText(fileInformation.getLastModifyTime());
		}
		else
		{
			FileUtil.matchImageToIV(iv_fileImage, fileInformation.getSuffix());
			tv_fileInfo.setText(fileInformation.getLastModifyTime() + "   " 
				+ Formatter.formatFileSize(context, fileInformation.getSize()));
		}
		
		tv_fileName.setText(fileInformation.getName());
		updateBackground(position, convertView);
		return convertView;
	}
	
	class ViewHolder
	{
		ImageView iv_fileImage;
		TextView tv_fileName;
		TextView tv_fileInfo;
	}
	
	public void updateBackground(int position, View view)
	{
		ColorShades shades = new ColorShades();
		if (listView.isItemChecked(position))
		{
			// view.setBackgroundColor(Color.rgb(180, 180, 180));
			shades.setFromColor(Color.WHITE)
					.setToColor(ContextCompat.getColor(context, R.color.colorPrimary))
					.setShade(1);
			view.setBackgroundColor(shades.generate());
			
		}
		else
		{
			view.setBackgroundColor(Color.WHITE);
		}
	}
}

