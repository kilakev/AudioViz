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
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class VisualizerView extends View {
  private static final String TAG = "VisualizerView";

  private byte[] mBytes;
  private byte[] mFFTBytes;
  private Rect mRect = new Rect();
  private Visualizer mVisualizer;

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

  private void init() {
    mBytes = null;
    mFFTBytes = null;

    mFadePaint.setColor(Color.argb(122, 255, 255, 255)); // Adjust alpha to change how quickly the image fades
    mFadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));

  }

  /**
   * Links the visualizer to a player
   * @param player - MediaPlayer instance to link to
   */
  public void link(MediaPlayer player)
  {
    if(player == null)
    {
      throw new NullPointerException("Cannot link to null MediaPlayer");
    }

    // Create the Visualizer object and attach it to our media player.
    mVisualizer = new Visualizer(player.getAudioSessionId());
    Equalizer mEqualizer = new Equalizer(0, player.getAudioSessionId());
    mEqualizer.setEnabled(true);
    mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

    // Pass through Visualizer data to VisualizerView
    Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener()
    {
      @Override
      public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
          int samplingRate)
      {
        updateVisualizer(bytes);
      }

      @Override
      public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
          int samplingRate)
      {
        updateVisualizerFFT(bytes);
      }
    };

    mVisualizer.setDataCaptureListener(captureListener,
        Visualizer.getMaxCaptureRate() / 2, false, true);

    // Enabled Visualizer and disable when we're done with the stream
    mVisualizer.setEnabled(true);
    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
    {
      @Override
      public void onCompletion(MediaPlayer mediaPlayer)
      {
        mVisualizer.setEnabled(false);
      }
    });
  }
  public void release()
  {
    mVisualizer.release();
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
  
  @Override
  protected void onDraw(Canvas canvas) {
	  super.onDraw(canvas);
	/*  if (mBytes == null) {
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
      
      paint.setStrokeWidth(1f);
      paint.setAntiAlias(true);
      paint.setColor(Color.argb(200, 56, 138, 252));
      mCanvas.drawLines(mPoints, paint);
      mCanvas.drawPaint(mFadePaint);
      canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
	 */
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
		  paint.setColor(Color.argb(200, 56, 138, 252));
		  canvas.drawLines(mFFTPoints, paint);

		  // Fade out old contents
		  mCanvas.drawPaint(mFadePaint);

		  canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
	  }
  }
}

