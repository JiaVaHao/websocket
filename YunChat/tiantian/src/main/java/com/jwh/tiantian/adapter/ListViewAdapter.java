package com.jwh.tiantian.adapter;

import java.util.List;

import com.activity.R;
import com.jwh.tiantian.bean.AppInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


//自定义适配器类，提供给listView的自定义view
public class ListViewAdapter extends BaseAdapter {
	
	private List<AppInfo> mlistAppInfo = null;
	
	LayoutInflater infater = null;
    
	public ListViewAdapter(Context context,  List<AppInfo> apps) {
		infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mlistAppInfo = apps ;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		System.out.println("size" + mlistAppInfo.size());
		return mlistAppInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mlistAppInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertview, ViewGroup arg2) {
		System.out.println("getView at " + position);
		View view = null;
		ViewHolder holder = null;
		if (convertview == null || convertview.getTag() == null) {
			view = infater.inflate(R.layout.lv_item, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} 
		else{
			view = convertview ;
			holder = (ViewHolder) convertview.getTag() ;
		}
		AppInfo appInfo = (AppInfo) getItem(position);
		holder.appIcon.setImageDrawable(appInfo.getAppIcon());
		holder.tvAppLabel.setText(appInfo.getAppLabel());
		holder.tvPkgName.setText(appInfo.getPkgName());
		return view;
	}

	class ViewHolder {
		ImageView appIcon;
		TextView tvAppLabel;
		TextView tvPkgName;

		public ViewHolder(View view) {
			this.appIcon = (ImageView) view.findViewById(R.id.lv_icon);
			this.tvAppLabel = (TextView) view.findViewById(R.id.lv_item_appname);
			this.tvPkgName = (TextView) view.findViewById(R.id.lv_item_packageame);
		}
	}
}