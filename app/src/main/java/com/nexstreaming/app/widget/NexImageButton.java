package com.nexstreaming.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.R.layout;
import app.nunc.com.staatsoperlivestreaming.R.id;
import app.nunc.com.staatsoperlivestreaming.R.string;
import app.nunc.com.staatsoperlivestreaming.R.array;

/**
 * Created by bonnie.kyeon on 2015-04-22.
 */
public class NexImageButton extends ImageButton {
	private String mText = "";
	private int mTextColor = Color.WHITE;
	private int mTextSize = 30;
	private int mStrokeColor = Color.BLACK;
	private float mStrokeWidth = 0;
	private int mUnfocusedBackgroundColor = Color.TRANSPARENT;
	private int mFocusedBackgroundColor = Color.LTGRAY;

	public NexImageButton(Context context) {
		super(context);
		initNexImageButton();
	}

	public NexImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		loadAttributeValue(context, attrs);
		initNexImageButton();
	}

	private void loadAttributeValue(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NexImageButton);

		mUnfocusedBackgroundColor = typedArray.getColor(R.styleable.NexImageButton_unfocused_backgroundColor, mUnfocusedBackgroundColor);
		mFocusedBackgroundColor = typedArray.getColor(R.styleable.NexImageButton_focused_backgroundColor, mFocusedBackgroundColor);
		mText = typedArray.getString(R.styleable.NexImageButton_text);
		mTextSize = typedArray.getDimensionPixelSize(R.styleable.NexImageButton_textSize, mTextSize);
		mTextColor = typedArray.getColor(R.styleable.NexImageButton_textColor, mTextColor);
		mStrokeWidth = typedArray.getFloat(R.styleable.NexImageButton_textStrokeWidth, mStrokeWidth);
		mStrokeColor = typedArray.getColor(R.styleable.NexImageButton_textStrokeColor, mStrokeColor);
	}

	private void initNexImageButton() {
		setBackgroundColor(mUnfocusedBackgroundColor);
		setText(mText);
	}

	public void setText(String text) {
		mText = text;
		invalidate();
	}

	public void setTextColor(int color) {
		mTextColor = color;
		invalidate();
	}

	private void changeBackgroundColor(boolean isFocused) {
		int color = mUnfocusedBackgroundColor;

		if( isFocused ) {
			color = mFocusedBackgroundColor;
		}

		setBackgroundColor(color);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		changeBackgroundColor(gainFocus);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				changeBackgroundColor(true);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_OUTSIDE:
				changeBackgroundColor(false);
				break;
		}

		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if( mText != null && !mText.equals("") ) {
			int width = this.getWidth();
			int height = this.getHeight();

			Rect rt = new Rect();
			Paint paint = new Paint();
			paint.setTextSize(mTextSize);
			paint.getTextBounds(mText, 0, mText.length(), rt);

			int textWidth = rt.width() + rt.left;
			int textHeight = rt.height();

			int x = (width - textWidth) / 2;
			int y = textHeight + ((height - textHeight) / 2);

			if( mStrokeWidth > 0 ) {
				paint.setStyle(Paint.Style.STROKE);
				paint.setColor(mStrokeColor);
				paint.setStrokeWidth(mStrokeWidth);
				canvas.drawText(mText, x, y, paint);
			}

			paint.reset();
			paint.setColor(mTextColor);
			paint.setTextSize(mTextSize);
			canvas.drawText(mText, x, y, paint);
		}
	}
}
