package com.example.face;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public abstract class AbsObjectAdapter<T> extends BaseAdapter {

	private Context mContext;
	private List<T> mData;
	private int mLayoutRes;

	public AbsObjectAdapter(Context context, List<T> data, int layoutRes) {
		mContext = context;
		mData = data;
		mLayoutRes = layoutRes;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public T getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, mLayoutRes, null);
		}

		setData(position, convertView, getItem(position));

		return convertView;
	}

	abstract protected void setData(int pos, View convertView, T itemData);

	public <K extends View> K getViewFromHolder(View convertView, int id) {
		return ViewHolder.getView(convertView, id);
	}
}

