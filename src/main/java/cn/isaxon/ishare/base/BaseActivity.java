package cn.isaxon.ishare.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import cn.isaxon.ishare.utils.AppUtil;

public abstract class BaseActivity extends AppCompatActivity implements OnClickListener
{
	public abstract void initView();
	public abstract void initData();
	public abstract void initListener();
	public abstract void processClick(View v); // click listener
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{		
		super.onCreate(savedInstanceState);
		
		AppUtil.activityStack.add(this);

		initView();
		initData();
		initListener();
	}

	@Override
	public void onClick(View v)
	{
		processClick(v);
	}

	@Override
	public void finish()
	{
		if (isFinish())
		{
			AppUtil.activityStack.remove(this);
			super.finish();
		}
	}

	/**
	 * default is finish
	 * @return isfinish
	 */
	protected boolean isFinish()
	{
		return true;
	}
}
