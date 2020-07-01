package com.jwh.tiantian.activity.alarm;


import java.util.Calendar;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;


public class DigitalClock extends android.widget.DigitalClock {

	Calendar mCalendar;
	private final static String m12 = "h:mm aa";// h:mm:ss aa
	private final static String m24 = "k:mm";// k:mm:ss
	private FormatChangeObserver mFormatChangeObserver;
	private Runnable mTicker;
	private Handler mHandler;
	private boolean mTickerStopped = false;
	String mFormat;

	public DigitalClock(Context context) {
		super(context);
		initClock(context);
	}

	public DigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		initClock(context);
	}

	private void initClock(Context context) {
		Resources r = context.getResources();
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}
		mFormatChangeObserver = new FormatChangeObserver();
		getContext().getContentResolver().registerContentObserver(
				Settings.System.CONTENT_URI, true, mFormatChangeObserver);
		setFormat();
	}

	//当此view附加到窗体上时调用该方法。在这时，view有了一个用于显示的Surface，将开始绘制。注意，此方法要保证在调用onDraw(Canvas) 之前调用，但可能在调用 onDraw(Canvas) 之前的任何时刻，包括调用 onMeasure(int, int) 之前或之后。
	protected void onAttachedToWindow() {
		mTickerStopped = false;
		super.onAttachedToWindow();
		mHandler = new Handler();
		mTicker = new Runnable() {
			public void run() {
				if (mTickerStopped)
					return;
				mCalendar.setTimeInMillis(System.currentTimeMillis());
				setText(DateFormat.format(mFormat, mCalendar));
				invalidate();
				long now = SystemClock.uptimeMillis();
				long next = now + (1000 - now % 1000);
				mHandler.postAtTime(mTicker, next);
			}
		};
		mTicker.run();
	}

	//将视图从窗体上分离的时候调用该方法。这时视图已经不具有可绘制部分
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mTickerStopped = true;
	}

	//设置日期格式为24小时
	private void setFormat() {
		mFormat = m24;
	}

	//ContentObserver——内容观察者，目的是观察(捕捉)特定Uri引起的数据库的变化，继而做一些相应的处理，它类似于
	//数据库技术中的触发器(Trigger)，当ContentObserver所观察的Uri发生变化时，便会触发它。触发器分为表触发器、行触发器，
	//相应地ContentObserver也分为“表“ContentObserver、“行”ContentObserver，当然这是与它所监听的Uri MIME Type有关的。

	private class FormatChangeObserver extends ContentObserver {
		public FormatChangeObserver() {
			super(new Handler());
		}

		public void onChange(boolean selfChange) {
			setFormat();
		}
	}
}