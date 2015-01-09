package com.example.face;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends FragmentActivity implements OnClickListener {
	
	private EditText etContent;
	private ImageView imgFace;
	private GridView gvMore;
	private LinearLayout rlFacePane;
	private ViewPager mViewPager;
	private IconPageIndicator mPagerIndicator;
	private List<List<Face>> mData = new ArrayList<List<Face>>();
	private final static int RESULT_LOAD_COLLEAGUE = 1;
	private final static int RESULT_LOAD_IMAGE = 2;
	private ImageView ivUpImg;
	private String path;
	private float imgW;
	private float imgH;
	
	private int screenWidth; //屏幕宽度
	private FragmentStatePagerAdapter mAdapter;
	private String[] mSmileyTexts;
	private SmileyParser parser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSmileyTexts = this.getResources().getStringArray(R.array.default_smiley_texts);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		etContent = (EditText) findViewById(R.id.etContent);
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		
		rlFacePane = (LinearLayout) findViewById(R.id.RlFacePaneEdit);
		gvMore = (GridView) findViewById(R.id.gvMoreEdit);
		mViewPager = (ViewPager) findViewById(R.id.viewpagerEdit);
		
		mPagerIndicator = (IconPageIndicator) findViewById(R.id.pager_indicatorEdit);
		imgFace = (ImageView) findViewById(R.id.photo_jianpan);
		imgFace.setOnClickListener(this);
		initGridView();
		afterViews();
		// 焦点事件
		etContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) { //

				if (etContent.hasFocus()) {
					Log.i("edit", "scrollview有焦点击事件");
					if (rlFacePane.isShown()) {
						imgFace.setBackgroundResource(R.drawable.sc_am_ok_default);
						rlFacePane.setVisibility(View.GONE);
						OpenKeyBoard();
						etContent.setFocusable(true);
						etContent.setFocusableInTouchMode(true);
						etContent.requestFocus();
						etContent.setPadding(30, 10, 20, 30);
					} else {
						OpenKeyBoard();
						etContent.setFocusable(true);
						etContent.setFocusableInTouchMode(true);
						etContent.requestFocus();
						etContent.setPadding(30, 10, 20, 30);
					}
				} else {
					Log.i("edit", "scrollview没有焦点击事件");
					CloseKeyBoard();
					/*
					 * linEdit.setFocusable(true);
					 * linEdit.setFocusableInTouchMode(true);
					 * linEdit.requestFocus();
					 */

				}
				etContent.setPadding(15, 10, 10, 15);

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.photo_jianpan:
			if (rlFacePane.isShown()) {
				imgFace.setBackgroundResource(R.drawable.sc_am_ok_default);
				rlFacePane.setVisibility(View.GONE);
				OpenKeyBoard();
				etContent.setFocusable(true);
				etContent.setFocusableInTouchMode(true);
				etContent.requestFocus();
				etContent.setPadding(20, 10, 20, 30);
			} else {
				imgFace.setBackgroundResource(R.drawable.photo_jianpan);
				CloseKeyBoard();
				etContent.setFocusable(true);
				etContent.setFocusableInTouchMode(true);
				etContent.requestFocus();
				rlFacePane.setVisibility(View.VISIBLE);
				// gvMore.setVisibility(View.GONE);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etContent.getWindowToken(), 0);
				// CloseKeyBoard();
			}
			break;

		default:
			break;
		}

	}

	// 开启键盘
	public void OpenKeyBoard() {
		etContent.setFocusable(true);
		etContent.requestFocus();
		etContent.setFocusableInTouchMode(true);
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		Log.i("statete", "et焦点软件盘状态" + imm.isActive());
		imm.showSoftInput(etContent, 0);

	}

	// 关闭键盘
	public void CloseKeyBoard() {
		etContent.clearFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etContent.getWindowToken(), 0);
	}

	private void initGridView() {

		// 生成动态数组，并且转入数据
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.pick_pic);// 添加图像资源的ID
		map.put("ItemText", "图片");// 按序号做ItemText

		lstImageItem.add(map);

		// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
		SimpleAdapter saImageItems = new SimpleAdapter(this, // 没什么解释
				lstImageItem,// 数据来源
				R.layout.night_item,// night_item的XML实现

				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemText" },

				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemText });
		// 添加并且显示
		gvMore.setAdapter(saImageItems);
		// 添加消息处理
		gvMore.setOnItemClickListener(new ItemClickListener());
	}

	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			gvMore.setVisibility(View.GONE);
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, RESULT_LOAD_IMAGE);
		}

	}

	private void afterViews() {
		initData();
		createAdapter();
		mViewPager.setAdapter(mAdapter);
		mPagerIndicator.setViewPager(mViewPager);

	}

	private void initData() {

		int pageCount = 20;
		int index = 0;

		List<Face> pageData = null;
		for (int i = 0; i < 106; i++) {
			if (index % pageCount == 0) {
				if (pageData != null) {
					pageData.add(new Face("/face_back", R.drawable.f));
				}
				pageData = new ArrayList<Face>();
				mData.add(pageData);
			}

			pageData.add(new Face(mSmileyTexts[i],
					SmileyParser.DEFAULT_SMILEY_RES_IDS[i]));

			index++;
		}

		pageData.add(new Face("/face_back", R.drawable.f));
	}

	private void createAdapter() {
		mAdapter = new FaceAdapter(getSupportFragmentManager());
	}

	class FaceAdapter extends FragmentStatePagerAdapter implements
			IconPagerAdapter {

		public FaceAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			Fragment fragment = new FaceGridFragment();
			Bundle args = new Bundle();
			fragment.setArguments(args);
			args.putSerializable(FaceGridFragment.KEY_ARG_FACE_DATA,
					(Serializable) mData.get(pos));
			return fragment;
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getIconResId(int index) {
			return R.drawable.selector_dot;
		}

	}

	@SuppressLint("ValidFragment")
	public class FaceGridFragment extends Fragment {

		public static final String KEY_ARG_FACE_DATA = "face_data";

		private GridView mFaceGrid;
		private AbsObjectAdapter<Face> mAdapter;
		private List<Face> mData = new ArrayList<Face>();

		SharedPreferences preferences;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_face_grid, container,
					false);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			mData = (List<Face>) getArguments().getSerializable(
					KEY_ARG_FACE_DATA);
			mFaceGrid = (GridView) view.findViewById(R.id.grid_view);
			afterViews();
		}

		private void afterViews() {
			createAdapter();
			mFaceGrid.setAdapter(mAdapter);
			setListener();
		}

		private void createAdapter() {
			mAdapter = new AbsObjectAdapter<Face>(getActivity(), mData,
					R.layout.item_face_grid) {

				@Override
				protected void setData(int pos, View convertView, Face itemData) {
					ImageView faceBtn = getViewFromHolder(convertView,
							R.id.face_btn);
					faceBtn.setImageResource(itemData.getPath());
				}

			};
		}

		private float getDensity() {
			/* DisplayMetrics主要用于查看手机相关的实际高度、宽度、密度、以及字体缩放等信息 */
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			/* 手机的密 度 */
			return metrics.densityDpi;
		}

		private void setListener() {
			mFaceGrid
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int pos, long arg3) {
							String name = mData.get(pos).getName();
							if (!name.equals("/face_back")) {

								Drawable aa = MainActivity.this.getResources()
										.getDrawable(mData.get(pos).getPath());
								aa.setBounds(0, 0,
										(int) (30 * (getDensity() / 220)),
										(int) (30 * (getDensity() / 220)));
								ImageSpan imageSpan = new ImageSpan(aa);

								SpannableString spannableString = new SpannableString(
										name);
								spannableString.setSpan(imageSpan, 0,
										name.length(),
										Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

								etContent.append(spannableString);

							} else {

								etContent.onKeyDown(KeyEvent.KEYCODE_DEL,
										keyEventDown);
							}
						}

					});
		}

		final KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_DEL);

		private Drawable getFaceImage(String assetPath) {
			Drawable dr = null;
			try {
				dr = Drawable.createFromStream(
						getActivity().getAssets().open(assetPath), assetPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return dr;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(this.getClass().getSimpleName(), "onActivityReslt--->"
				+ requestCode);
		if (resultCode != RESULT_OK)

			return;

		if (TextUtils.isEmpty(path))
			return;
		Bitmap bitmap = BitmapFactory.decodeFile(path);

		imgW = bitmap.getWidth();
		imgH = bitmap.getHeight();

		float height = screenWidth * imgH / imgW;

		ivUpImg.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
				screenWidth, (int) height));
		ivUpImg.setImageBitmap(bitmap);
	}

}
