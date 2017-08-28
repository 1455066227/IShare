package cn.isaxon.ishare.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.isaxon.ishare.R;
import cn.isaxon.ishare.adapter.ApplistAdapter;
import cn.isaxon.ishare.adapter.FileAdapter;
import cn.isaxon.ishare.base.BaseActivity;
import cn.isaxon.ishare.bean.AppInfomation;
import cn.isaxon.ishare.bean.FileInformation;
import cn.isaxon.ishare.service.AndroidHttpServer;
import cn.isaxon.ishare.service.HttpServerService;
import cn.isaxon.ishare.utils.ConnectUtil;
import cn.isaxon.ishare.utils.Const;
import cn.isaxon.ishare.utils.FileUtil;
import cn.isaxon.ishare.utils.QRCode;
import cn.isaxon.ishare.utils.ToastUtils;


/**
 * <p>Main avcivity</p>
 */
public final class MainActivity extends BaseActivity
{
    // view
    private Toolbar toolbar = null;
    private TextView tv_ipaddress = null;
    private EditText et_port = null;
    private TextView tv_shareApk = null;
    private ImageButton ibtn_shareApk = null;
    private TextView tv_shareFile = null;
    private ImageButton ibtn_shareFile = null;
    private Button btn_start = null;
    private LinearLayout ll_qrCode = null;
    private ImageView iv_QRCode = null;
    private TextView tv_httpStartSuccessTip = null;

    // data
    private int port;
    private Intent httpService = null;
    private static Map<String, AppInfomation> selectAppMap = new HashMap<>();
    public static boolean isShowThumbnails = false; // show thumbnils
    private static final int REQUEST_EXTERAL_STORAGE_CODE = 1;

    public static Map<String, AppInfomation> getShareAppMap()
    {
        return selectAppMap;
    }

    @Override
    public void initView()
    {
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        tv_ipaddress = (TextView) findViewById(R.id.tv_ipaddress);
        et_port = (EditText) findViewById(R.id.et_port);
        tv_shareApk = (TextView) findViewById(R.id.tv_shareApk);
        ibtn_shareApk = (ImageButton) findViewById(R.id.ibtn_shareApk);
        tv_shareFile = (TextView) findViewById(R.id.tv_shareFile);
        ibtn_shareFile = (ImageButton) findViewById(R.id.ibtn_shareFile);
        btn_start = (Button) findViewById(R.id.btn_start);
        ll_qrCode = (LinearLayout) findViewById(R.id.ll_qrcode);
        iv_QRCode = (ImageView) findViewById(R.id.iv_QRCode);
        tv_httpStartSuccessTip = (TextView) findViewById(R.id.tv_httpStartSucessTip);
    }

    @Override
    public void initData()
    {
        httpService = new Intent(this, HttpServerService.class);

        // toolbar
        setSupportActionBar(toolbar);

        // init port
        SharedPreferences sharedPreferences = getSharedPreferences(Const.Sp.SP_NAME, Context.MODE_PRIVATE);
        port = sharedPreferences.getInt("httpServerPort", 8080);
        et_port.setText(String.valueOf(port));

        //
        isShowThumbnails = sharedPreferences.getBoolean("isShowThumbnails", false);
    }

    @Override
    public void initListener()
    {
        btn_start.setOnClickListener(this);
        ibtn_shareApk.setOnClickListener(this);
        ibtn_shareFile.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.http, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_setting_http:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_start: // start / stop
                if (AndroidHttpServer.isRunning) // httpserver is starting
                {
                    btn_start.setText(R.string.str_start);
                    // hide qucode view
                    ll_qrCode.setVisibility(View.GONE);
                    stopService(httpService);
                }
                else
                {
                    if (!ConnectUtil.isApEnabled(this)) // hostwap is not enable
                    {
                        Toast.makeText(this, R.string.str_hotspot_NotStart, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else if (et_port.getText().toString().isEmpty()) // port textview is empty
                    {
                        Toast.makeText(this, R.string.str_noInputPort, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else if (tv_shareFile.getText().toString().equals(getText(R.string.str_shareFile)))
                    {
                        Toast.makeText(this, R.string.str_chooseShareFile, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else
                    {
                        port = Integer.valueOf(et_port.getText().toString());
                        // start service
                        btn_start.setText(R.string.str_stop);
                        httpService.putExtra("sharePath", tv_shareFile.getText().toString());
                        httpService.putExtra("port", port);
                        startService(httpService);

                        // create qrcode
                        iv_QRCode.setImageBitmap(QRCode.createQRCodeWithLogo(
                                "http://" + tv_ipaddress.getText() + ":" + et_port.getText(),
                                500,
                                BitmapFactory.decodeResource(this.getResources(), R.drawable.about)));
                        tv_httpStartSuccessTip.setText(
                                getString(R.string.str_accessLink) + "\"http://"
                                        + tv_ipaddress.getText() + ":" + et_port.getText() + "\"\r\n"
                                        + getString(R.string.str_qrCodeTip));
                        ll_qrCode.setVisibility(View.VISIBLE);

                        Toast.makeText(this, R.string.str_startSucess, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            //
            case R.id.ibtn_shareApk: // share app dialog
                AlertDialog.Builder builder_ = new AlertDialog.Builder(this);

                View view = View.inflate(this, R.layout.alertdialog_applist, null);
                final TextView tv_count_select_applist = view.findViewById(R.id.tv_count_select_applist);
                RecyclerView rv_applist = view.findViewById(R.id.rv_applist);
                Button btn_submit_applist = view.findViewById(R.id.btn_submit_applist);
                Button btn_cancel_applist = view.findViewById(R.id.btn_cancel_applist);


                final AlertDialog dialog = builder_.setView(view).create();
                btn_submit_applist.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                        tv_shareApk.setText("已选择" + selectAppMap.size() + "个应用");
                    }
                });
                btn_cancel_applist.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                    }
                    {}
                });
                List<AppInfomation> appInfomations = new ArrayList<>();
                PackageManager pm = getPackageManager();
                for (ApplicationInfo app : pm.getInstalledApplications(0))
                {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                        appInfomations.add(new AppInfomation(app, pm));
                }
                selectAppMap.clear();
                rv_applist.setLayoutManager(new GridLayoutManager(this, 3));
                ApplistAdapter applistAdapter = new ApplistAdapter(appInfomations, this);

                // set callback for CheckBox
                applistAdapter.setRvCheckBoxChangeListener(new ApplistAdapter.RvCheckBoxChangeListener()
                {
                    @Override
                    public void onCheckedChanged(AppInfomation infomation, boolean isChecked, int position)
                    {
                        if (isChecked)
                        {
                            selectAppMap.put(infomation.getAppName(), infomation);
                        }
                        else
                        {
                            selectAppMap.remove(infomation.getAppName());
                        }
                        tv_count_select_applist.setText(getString(R.string.selected) + ":" + selectAppMap.size());
                    }
                });
                rv_applist.setAdapter(applistAdapter);
                //  set item padding for recycleview
                rv_applist.addItemDecoration(new RecyclerView.ItemDecoration()
                {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
                    {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.top = 15;
                        if (parent.getChildLayoutPosition(view) % 3 != 0)
                        {
                            outRect.left = 18;
                        }
                    }
                });

                dialog.show();
                break;

            case R.id.ibtn_shareFile: // choose share direcory
                // request READ_EXTERNAL_STORAGE permission
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_EXTERAL_STORAGE_CODE);
                    break;
                }

                // create select directory dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final AlertDialog alertDialog = builder.create();
                View alertView = View.inflate(MainActivity.this, R.layout.alertdialog_selectdirectory, null);
                final TextView tv_currentDir =  alertView.findViewById(R.id.tv_currentDir_select);
                final ListView lv_selectDir = alertView.findViewById(R.id.lv_selectDir);
                Button btn_submit = alertView.findViewById(R.id.btn_submit_selectDir);
                Button btn_cancel =  alertView.findViewById(R.id.btn_cancel_selectDir);
                tv_currentDir.setText(Environment.getExternalStorageDirectory().getPath());

                // select directory in sdcard
                List<FileInformation> fileInformations = new ArrayList<>();
                List<File> files = FileUtil.sortFiles(Environment.getExternalStorageDirectory());
                for (File file : files)
                    if (file.isDirectory())
                        fileInformations.add(new FileInformation(file));
                lv_selectDir.setAdapter(new FileAdapter(MainActivity.this,
                        fileInformations, lv_selectDir));

                // add listener
                lv_selectDir.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        FileInformation fileInformation =
                                (FileInformation) lv_selectDir.getItemAtPosition(position);
                        tv_currentDir.setText(fileInformation.getPath());

                        //
                        List<FileInformation> fileInformations = new ArrayList<>();
                        List<File> clickFiles = FileUtil.sortFiles(new File(fileInformation.getPath()));
                        for (File file : clickFiles)
                            if (file.isDirectory())
                                fileInformations.add(new FileInformation(file));
                        lv_selectDir.setAdapter(new FileAdapter(MainActivity.this,
                                fileInformations, lv_selectDir));
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                    }
                });
                btn_submit.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        SharedPreferences sharedPreferences =
                                MainActivity.this.getSharedPreferences("settings", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("sharePath", tv_currentDir.getText().toString());
                        editor.apply();

                        // update view tv_shareFile
                        tv_shareFile.setText(tv_currentDir.getText());

                        alertDialog.dismiss();
                    }
                });
                alertDialog.setView(alertView);
                alertDialog.show();
                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_EXTERAL_STORAGE_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ibtn_shareFile.performClick();
                else
                {
                    ToastUtils.toastShort(this, "您拒绝了权限的申请!");
                    this.finish();
                }
                break;
        }
    }

    @Override
    public boolean isFinish()
    {
        stopService(httpService);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                if (AndroidHttpServer.isRunning)
                {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.str_tip)
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(R.string.str_exitHttpServerTip)
                            .setNegativeButton(R.string.str_submit, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    finish();
                                }
                            })
                            .setPositiveButton(R.string.str_cancel, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();

                }
                else
                {
                    finish();
                }
                break;

        }
        return false;
    }
}
