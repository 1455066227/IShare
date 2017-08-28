package cn.isaxon.ishare.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

import cn.isaxon.ishare.R;
import cn.isaxon.ishare.base.BaseActivity;
import cn.isaxon.ishare.utils.Const;
import cn.isaxon.ishare.utils.ToastUtils;

/**
 * <p>setting</p>
 * Created by toor on 2/20/2017.
 */

public final class SettingActivity extends BaseActivity
{
    private Toolbar mToolbar;
    private Switch mSwitvhShowThumbnails;
    private LinearLayout ll_about_activitysetting;

    @Override
    public void initView()
    {
        setContentView(R.layout.activity_setting);

        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        mSwitvhShowThumbnails = (Switch) findViewById(R.id.switch_show_thumbnails);
        ll_about_activitysetting = (LinearLayout) findViewById(R.id.ll_about_activitysetting);
    }

    @Override
    public void initData()
    {
        // toolbar
        mToolbar.setTitle(getString(R.string.str_setting));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        mSwitvhShowThumbnails.setChecked(MainActivity.isShowThumbnails);
    }

    @Override
    public void initListener()
    {
        ll_about_activitysetting.setOnClickListener(this);
    }

    @Override
    public void processClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ll_about_activitysetting:
                // TODO: ABOUT
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                saveSetting();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveSetting()
    {
        MainActivity.isShowThumbnails = mSwitvhShowThumbnails.isChecked();
        SharedPreferences sharedPreferences = getSharedPreferences(Const.Sp.SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Const.Sp.SP_KEY_ISTHUMBNAILS, MainActivity.isShowThumbnails);
        editor.apply();
        ToastUtils.toastShort(this, getString(R.string.str_savefinish));
    }

}
