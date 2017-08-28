package cn.isaxon.ishare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.isaxon.ishare.R;
import cn.isaxon.ishare.bean.AppInfomation;


public final class ApplistAdapter extends RecyclerView.Adapter<ApplistAdapter.ViewHolder>
{
    private List<AppInfomation> appInfomations = null;
    private Context context = null;
    private RvCheckBoxChangeListener mOnCheckedChangeListener = null;

    /**
     * 记录checkbox已经checked的item
     */
    private SparseBooleanArray checkedMap = new SparseBooleanArray();

    public ApplistAdapter(List<AppInfomation> appInfomations, Context context)
    {
        this.appInfomations = appInfomations;
        this.context = context;
    }

    public void setRvCheckBoxChangeListener(RvCheckBoxChangeListener onCheckedChangeListener)
    {
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = View.inflate(parent.getContext(), R.layout.rvitem_appinfo, null);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount()
    {
        return appInfomations.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        AppInfomation information = appInfomations.get(position);
        holder.setData(information);

        if (mOnCheckedChangeListener != null)
        {
            holder.cb_appinfo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    boolean isChecked = holder.cb_appinfo.isChecked();
                    AppInfomation infomation = appInfomations.get(holder.getAdapterPosition());
                    mOnCheckedChangeListener.onCheckedChanged(infomation, isChecked, holder.getAdapterPosition());
                    checkedMap.put(holder.getAdapterPosition(), isChecked);
                }
            });
        }

        holder.cb_appinfo.setChecked(checkedMap.get(position));
    }

    final class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView iv_appIcon = null;
        private TextView tv_appName = null;
        private TextView tv_appSize = null;
        private CheckBox cb_appinfo = null;

        ViewHolder(View itemView)
        {
            super(itemView);
            cb_appinfo = itemView.findViewById(R.id.cb_appinfo);
            iv_appIcon = itemView.findViewById(R.id.iv_appIcon);
            tv_appName = itemView.findViewById(R.id.tv_appName);
            tv_appSize = itemView.findViewById(R.id.tv_appSize);
        }

        public void setData(AppInfomation infomation)
        {
            iv_appIcon.setImageDrawable(infomation.getAppIcon());
            tv_appName.setText(infomation.getAppName());
            tv_appSize.setText(Formatter.formatFileSize(context, infomation.getAppSize()));
        }
    }

    public interface RvCheckBoxChangeListener
    {
        void onCheckedChanged(AppInfomation infomation, boolean isChecked, int position);
    }
}
