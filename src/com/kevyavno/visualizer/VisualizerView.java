package com.kevyavno.visualizer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class VisualizerView extends View {
	private Boolean isWave = false;
	private byte[] mBytes;
	private byte[] mFFTBytes;
	private Rect mRect = new Rect();


	private Paint mFadePaint = new Paint();

	public VisualizerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs);
		init();
	}

	public VisualizerView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public VisualizerView(Context context)
	{
		this(context, null, 0);
	}
	public void toggleWave() {
		isWave = !isWave;
	}
	private void init() {
		mBytes = null;
		mFFTBytes = null;

		mFadePaint.setColor(Color.argb(122, 255, 255, 255)); // Adjust alpha to change how quickly the image fades
		mFadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
		setColour(Color.argb(200, 56, 138, 252));
	}


	public void updateVisualizer(byte[] bytes) {
		mBytes = bytes;
		invalidate();
	}

	public void updateVisualizerFFT(byte[] bytes) {
		mFFTBytes = bytes;
		invalidate();
	}


	Bitmap mCanvasBitmap;
	Canvas mCanvas;

	private float[] mPoints;
	protected float[] mFFTPoints;
	private int mDivisions = 8;
	Paint paint = new Paint();

	public void setColour(int col) {
		paint.setColor(col);
		Log.v("test","color being changed");
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isWave) {
			if (mBytes == null) {
				return;
			}

			if (mPoints == null || mPoints.length < mBytes.length * 4) {
				mPoints = new float[mBytes.length * 4];
			}

			mRect.set(0, 0, getWidth(), getHeight());
			if(mCanvasBitmap == null)
			{
				mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
			}
			if(mCanvas == null)
			{
				mCanvas = new Canvas(mCanvasBitmap);
			}

			for (int i = 0; i < mBytes.length - 1; i++) {
				mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
				mPoints[i * 4 + 1] = mRect.height() / 2
				+ ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
				mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
				mPoints[i * 4 + 3] = mRect.height() / 2
				+ ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
			}

			paint.setStrokeWidth(1.5f);
			paint.setAntiAlias(true);
			
			mCanvas.drawLines(mPoints, paint);
			mCanvas.drawPaint(mFadePaint);
			canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
		} else {
			if (!(mFFTBytes == null)) {
				if (mFFTPoints == null || mFFTPoints.length < mFFTBytes.length * 4) {
					mFFTPoints = new float[mFFTBytes.length * 4];
				}

				// Create canvas once we're ready to draw
				mRect.set(0, 0, getWidth(), getHeight());

				if(mCanvasBitmap == null)
				{
					mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
				}
				if(mCanvas == null)
				{
					mCanvas = new Canvas(mCanvasBitmap);
				}

				for (int i = 0; i < mFFTBytes.length / mDivisions; i++) {
					mFFTPoints[i * 4] = i * 4 * mDivisions;
					mFFTPoints[i * 4 + 2] = i * 4 * mDivisions;
					byte rfk = mFFTBytes[mDivisions * i];
					byte ifk = mFFTBytes[mDivisions * i + 1];
					float magnitude = (rfk * rfk + ifk * ifk);
					int dbValue = (int) (50 * Math.log10(magnitude));


					mFFTPoints[i * 4 + 1] = mRect.height();
					mFFTPoints[i * 4 + 3] = mRect.height() - (dbValue * 2 - 10);
				}

				paint.setStrokeWidth(25f);
				paint.setAntiAlias(true);
				canvas.drawLines(mFFTPoints, paint);

				// Fade out old contents
				mCanvas.drawPaint(mFadePaint);

				canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
			}
		}
	}
}

