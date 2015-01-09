package com.example.face;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class SmileyParser {
	private Context mContext;
	private String[] mSmileyTexts;
	private String[] mRess;
	private Pattern mSmileyToResPattern;
	private Pattern mResToSmileyPattern;
	private HashMap<String, Integer> mSmileyToRes;
	private HashMap<String, String> mResToSmiley;
	public static final int[] DEFAULT_SMILEY_RES_IDS = { R.drawable.f000,
		R.drawable.f001, R.drawable.f002, R.drawable.f003, R.drawable.f004,
		R.drawable.f005, R.drawable.f006, R.drawable.f007, R.drawable.f008,
		R.drawable.f009, R.drawable.f010, R.drawable.f011, R.drawable.f012,
		R.drawable.f013, R.drawable.f014, R.drawable.f015, R.drawable.f016,
		R.drawable.f017, R.drawable.f018, R.drawable.f019, R.drawable.f020,
		R.drawable.f021, R.drawable.f022, R.drawable.f023, R.drawable.f024,
		R.drawable.f025, R.drawable.f026, R.drawable.f027, R.drawable.f028,
		R.drawable.f029, R.drawable.f030, R.drawable.f031, R.drawable.f032,
		R.drawable.f033, R.drawable.f034, R.drawable.f035, R.drawable.f036,
		R.drawable.f037, R.drawable.f038, R.drawable.f039, R.drawable.f040,
		R.drawable.f041, R.drawable.f042, R.drawable.f043, R.drawable.f044,
		R.drawable.f045, R.drawable.f046, R.drawable.f047, R.drawable.f048,
		R.drawable.f049, R.drawable.f050, R.drawable.f051, R.drawable.f052,
		R.drawable.f053, R.drawable.f054, R.drawable.f055, R.drawable.f056,
		R.drawable.f057, R.drawable.f058, R.drawable.f059, R.drawable.f060,
		R.drawable.f061, R.drawable.f062, R.drawable.f063, R.drawable.f064,
		R.drawable.f065, R.drawable.f066, R.drawable.f067, R.drawable.f068,
		R.drawable.f069, R.drawable.f070, R.drawable.f071, R.drawable.f072,
		R.drawable.f073, R.drawable.f074, R.drawable.f075, R.drawable.f076,
		R.drawable.f077, R.drawable.f078, R.drawable.f079, R.drawable.f080,
		R.drawable.f081, R.drawable.f082, R.drawable.f083, R.drawable.f084,
		R.drawable.f085, R.drawable.f086, R.drawable.f087, R.drawable.f088,
		R.drawable.f089, R.drawable.f090, R.drawable.f091, R.drawable.f092,
		R.drawable.f093, R.drawable.f094, R.drawable.f095, R.drawable.f096,
		R.drawable.f097, R.drawable.f098, R.drawable.f099, R.drawable.f100,
		R.drawable.f101, R.drawable.f102, R.drawable.f103, R.drawable.f104,
		R.drawable.f105, R.drawable.f106, R.drawable.f107, R.drawable.f108,
		R.drawable.f109, R.drawable.f110, R.drawable.f111, R.drawable.f112,
		R.drawable.f113, R.drawable.f114, R.drawable.f115, R.drawable.f116,
		R.drawable.f117, R.drawable.f118, R.drawable.f119, R.drawable.f120,
		R.drawable.f121, R.drawable.f122, R.drawable.f123, R.drawable.f124,
		R.drawable.f125, R.drawable.f126, R.drawable.f127, R.drawable.f128,
		R.drawable.f129, R.drawable.f130, R.drawable.f131, R.drawable.f132,
		R.drawable.f133, R.drawable.f134, R.drawable.f135, R.drawable.f136,
		R.drawable.f137, R.drawable.f138, R.drawable.f139, R.drawable.f140


	};

	public SmileyParser(Context context) {
		mContext = context;
		mSmileyTexts = mContext.getResources().getStringArray(
				DEFAULT_SMILEY_TEXTS);
		mRess = new String[mSmileyTexts.length];
		mSmileyToRes = buildSmileyToRes();
		mResToSmiley = buildResToSmiley();
		mSmileyToResPattern = buildSmileyToResPattern();
		mResToSmileyPattern = buildResToSmileyPattern();
	}

	public static final int DEFAULT_SMILEY_TEXTS = R.array.default_smiley_texts;

	private HashMap<String, Integer> buildSmileyToRes() {
		if (DEFAULT_SMILEY_RES_IDS.length != mSmileyTexts.length) {
			// Log.w("SmileyParser", "Smiley resource ID/text mismatch");
			Log.d("SmileyPa", "res ids--->" + DEFAULT_SMILEY_RES_IDS.length
					+ "---" + "text--" + mSmileyTexts.length);

			throw new IllegalStateException("Smiley resource ID/text mismatch");
		}

		HashMap<String, Integer> smileyToRes = new HashMap<String, Integer>(
				mSmileyTexts.length);
		for (int i = 0; i < mSmileyTexts.length; i++) {

			smileyToRes.put(mSmileyTexts[i], DEFAULT_SMILEY_RES_IDS[i]);
		}

		return smileyToRes;
	}

	private HashMap<String, String> buildResToSmiley() {
		if (DEFAULT_SMILEY_RES_IDS.length != mSmileyTexts.length) {
			// Log.w("SmileyParser", "Smiley resource ID/text mismatch");
			throw new IllegalStateException("Smiley resource ID/text mismatch");
		}

		HashMap<String, String> resToSmiley = new HashMap<String, String>(
				mSmileyTexts.length);
		for (int i = 0; i < mSmileyTexts.length; i++) {
			String fileName = mContext.getResources().getResourceName(
					DEFAULT_SMILEY_RES_IDS[i]);
			// Log.d("smilePar", fileName.substring(fileName.indexOf("/") + 1));
			mRess[i] = fileName.substring(fileName.indexOf("/") + 1);
			resToSmiley.put(fileName.substring(fileName.indexOf("/") + 1),
					mSmileyTexts[i]);
		}

		return resToSmiley;
	}

	// 构建正则表达式
	private Pattern buildSmileyToResPattern() {
		StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);
		patternString.append('(');
		for (String s : mSmileyTexts) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		// Log.d("Smile", patternString.toString());
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");
		// Log.d("Smile", patternString.toString());
		return Pattern.compile(patternString.toString());
	}

	// 构建正则表达式
	private Pattern buildResToSmileyPattern() {
		StringBuilder patternString = new StringBuilder(mRess.length * 3);
		patternString.append('(');
		for (String s : mRess) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		// Log.d("Smiley", patternString.toString());
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");
		// Log.d("Smiley", patternString.toString());
		return Pattern.compile(patternString.toString());
	}

	private float getDensity() {
		/* DisplayMetrics主要用于查看手机相关的实际高度、宽度、密度、以及字体缩放等信息 */
		DisplayMetrics metrics = new DisplayMetrics();

		WindowManager mWm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);

		mWm.getDefaultDisplay().getMetrics(metrics);
		/* 手机的密 度 */
		return metrics.densityDpi;
	}

	// 根据文本替换成图片
	public CharSequence replaceSmiley(String text) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		//		MyHtmlToSpannedConverter htmlToSpannedConvet = new MyHtmlToSpannedConverter(
		//				text, null, null, builder);
		//		htmlToSpannedConvet.convert();
		builder.append(text);
		Matcher matcher = mSmileyToResPattern.matcher(builder);
		while (matcher.find()) {
			int resId = mSmileyToRes.get(matcher.group());

			// Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(),
			// resId);

			Drawable aa = mContext.getResources().getDrawable(resId);
			aa.setBounds(0, 0, (int) (30 * (getDensity() / 220)),
					(int) (30 * (getDensity() / 220)));
			ImageSpan imageSpan = new ImageSpan(aa);

			builder.setSpan(imageSpan, matcher.start(), matcher.end(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			// builder.setSpan(, start, end, flags)

		}

		int end = builder.length();

		URLSpan[] urls = builder.getSpans(0, end, URLSpan.class);

		Log.d("Sssss", "urls length-->" + urls.length);

		for (URLSpan url : urls) {
			URLSpan myURLSpan = new URLSpan(url.getURL());
			builder.setSpan(myURLSpan, builder.getSpanStart(url),
					builder.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			builder.setSpan(new ForegroundColorSpan(Color.RED),
					builder.getSpanStart(url), builder.getSpanEnd(url),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);// 设置前景色为红色
		}
		return builder;
	}

	// 根据文本替换成图片
	public String replaceRes(String text) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcher = mResToSmileyPattern.matcher(text);
		int offset = 0;
		while (matcher.find()) {
			Log.d("Smiley", "group--->" + matcher.group());
			String smiley = mResToSmiley.get(matcher.group());

			Log.d("Smiley", smiley + "--" + smiley.length());

			builder.replace(matcher.start() + offset, matcher.end() + offset,
					smiley);

			offset = smiley.length() - matcher.group().length() + offset;
		}
		return builder.toString();
	}
}

