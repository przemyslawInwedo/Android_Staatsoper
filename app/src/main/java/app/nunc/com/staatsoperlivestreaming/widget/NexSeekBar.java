package app.nunc.com.staatsoperlivestreaming.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;

/**
 * Created by bonnie.kyeon on 2017. 9. 1..
 */

public class NexSeekBar extends AppCompatSeekBar {

    private int[] mMarkerArray;

    public NexSeekBar(Context context) {
        super(context);
    }

    public NexSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NexSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMarkers(int[] markers) {
        mMarkerArray = markers;

        invalidate();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mMarkerArray != null && mMarkerArray.length != 0) {

            final int width = getMeasuredWidth() - (getPaddingLeft()*2);
            final int height = getMeasuredHeight();
            final float step = (float) width / (float)getMax();
            final int margin = (int)(height/4.0);

             // draw dots if we have ones
            Paint paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStrokeWidth(5);

            for (int position : mMarkerArray) {
                if(position < 0) {
                    position = getMax();
                }
                if (position >= 0 && position <= getMax()) {
                    canvas.drawLine(getPaddingLeft() + (step * position), margin, getPaddingLeft() + (step * position), height-margin, paint);
                }
            }
        }
    }
}
