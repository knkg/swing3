
package websquare.hybrid.android.signature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
//import com.sec.penup.imagecache.ImageCache.onImageLoaderPostExecute;

public class SignatureEditor extends FrameLayout {

    final static String TAG = "SignatureEditor";

    public static final int SIGNATURE = 0;
    public static final int BROKEN_SIGNATURE = 1;
    public static final int BLURRED_SIGNATURE = 2;
    public static final int NUM_SIGNATURE_TYPES = 3;

    private static final int TOUCH_SLOP = 4;
    private boolean mIsDrawed;

    static final int PEN_COLOR = Color.BLACK;
    static final int BG_COLOR = Color.TRANSPARENT;

    private Bitmap mSignatureBmp;
    private Canvas mCanvas;
    private final ArrayList<Path> pointsToDraw = new ArrayList<Path>();
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;

    private Context mContext;

    private String mUserSigUrl = "";
    private int mW, mH;
    private float mX, mY;

    public SignatureEditor(Context context) {
        super(context);
        init(context);
    }

    public SignatureEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SignatureEditor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * SignatureEditor를 초기화한다.
     * 
     * @param context 컨텍스트
     */
    void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(PEN_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    /**
     * 사용자 Signature Url의 Setter.
     * 
     * @param userSigUrl 사용자 Signature Url
     */
    public void setUserSigUrl(String userSigUrl) {
        // PLog.e(TAG, "JH: setUserSigId - userSigId= " + userSigId);
        mUserSigUrl = userSigUrl;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mW = w;
        mH = h;

        // PLog.e(TAG, "onSizeChanged " + w + ", " + h);
        mSignatureBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mSignatureBmp);

        loadSignature();
  
    }

    /**
     * 화면에 Signature를 그린다.
     * 
     * @param canvas 캔버스
     */
    protected void drawImpl(Canvas canvas) {
        canvas.drawColor(BG_COLOR);

        // PLog.e(TAG, "drawImpl - " + mSignatureBmp);
        canvas.drawBitmap(mSignatureBmp, 0, 0, mBitmapPaint);

        synchronized (pointsToDraw) {
            for (Path p : pointsToDraw) {
                canvas.drawPath(p, mPaint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawImpl(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        final float x = me.getX();
        final float y = me.getY();

        final int action = me.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mPath = new Path();
            mPath.moveTo(x, y);
            synchronized (pointsToDraw) {
                pointsToDraw.add(mPath);
            }
            mX = x;
            mY = y;
            mIsDrawed = false;
            invalidate();
        } else if (action == MotionEvent.ACTION_MOVE) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_SLOP || dy >= TOUCH_SLOP) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
                mIsDrawed = true;
            }
            invalidate();
        } else if (action == MotionEvent.ACTION_UP) {
            mPath.lineTo(mX, mY);
            mCanvas.drawPath(mPath, mPaint);
            invalidate();
        }
        return true;
    }

    /**
     * 저장된 Signature를 가져온다.
     */
    /*public void loadSignature() {
        // PLog.e(TAG, "loadSingature");

        if (TextUtils.isEmpty(mUserSigUrl)) return;
        LoadingImageView artwork = new LoadingImageView(mContext);
        LayoutParams params = new LayoutParams(getLayoutParams().width, getLayoutParams().height);

        addView(artwork, params);
        //artwork.setVisibility(View.INVISIBLE);// FIXME

        // FIXME: it is better to make artwork.load() a static function
        // so that we need not create "artwork" variable
        artwork.load(mUserSigUrl, new onImageLoaderPostExecute() {
            @Override
            public void onPostExecute(boolean success, Object... params) {
                if (success) {
                    BitmapDrawable drawable = (BitmapDrawable) params[0];

                    mSignatureBmp = drawable.getBitmap();
                    invalidate();
                } else {
                    String signaturePath = getSignatureFilePath(mContext);
                    File fp = new File(signaturePath);
                    if (fp.exists()) {
                        mSignatureBmp = BitmapFactory.decodeFile(signaturePath);
                        invalidate();
                    } else {
                        Log.i(TAG, "signature not found " + signaturePath);
                    }
                }
            }
        });
    }*/
    
    public void loadSignature(){
    
    	if (TextUtils.isEmpty(mUserSigUrl)) return;
    	RequestQueue mQueue = Volley.newRequestQueue(mContext);// thread pool(4)
        
    	ImageLoader mImageLoader;
    	mImageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
    	    private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
    	    public void putBitmap(String url, Bitmap bitmap) {
    	        mCache.put(url, bitmap);
    	    }
    	    public Bitmap getBitmap(String url) {
    	        return mCache.get(url);
    	    }
    	});
    
    	// start load
    	Listener<Bitmap> listener = new Listener<Bitmap>() {
    	@Override
    	public void onResponse(Bitmap result) {
	    		mSignatureBmp = result;
	            invalidate();
    		}
    	};
    	 
    	ErrorListener errorListener = new ErrorListener() {
    	@Override
    	public void onErrorResponse(VolleyError error) {
	    		String signaturePath = getSignatureFilePath(mContext);
	            File fp = new File(signaturePath);
	            if (fp.exists()) {
	                mSignatureBmp = BitmapFactory.decodeFile(signaturePath);
	                invalidate();
	            } else {
	                Log.i(TAG, "signature not found " + signaturePath);
	            }
    	    }
    	};
    	 
    	ImageRequest imageRequest = new ImageRequest(mUserSigUrl, listener, 0, 0, Config.ARGB_8888, errorListener);
    	imageRequest.setTag(TAG);
    	mQueue.add(imageRequest);
    }

    /**
     * Signature를 저장한다.
     * 
     * @param ctx 컨텍스트
     * @return path 정보를 리턴
     */
    public String[] saveSignature(Context ctx) {
        Log.d(TAG, "BHKO, save signature");
        final String signaturePath = getSignatureFilePath(mContext);

        final File f = new File(signaturePath);
        final String dir = f.getParent();

        final String[] path = new String[SignatureEditor.NUM_SIGNATURE_TYPES];
        path[SignatureEditor.SIGNATURE] = signaturePath;
        path[SignatureEditor.BROKEN_SIGNATURE] = dir + "/broken.png";
        path[SignatureEditor.BLURRED_SIGNATURE] = dir + "/blurred.png";

        Bitmap signature = null, brokenSg = null, blurredSg = null;
        Bitmap outpBmp = null;
        final int w = (int) (getWidth() * 1.1), h = (int) (1.1 * getHeight());

        try {
            outpBmp = Bitmap.createBitmap(w, h, mSignatureBmp.getConfig());
            Canvas outpCanvas = new Canvas(outpBmp);

            outpCanvas.drawColor(BG_COLOR);
            outpCanvas.drawBitmap(mSignatureBmp, 0, 0, mBitmapPaint);

            for (Path p : pointsToDraw) {
                outpCanvas.drawPath(p, mPaint);
            }

            signature = Bitmap.createScaledBitmap(outpBmp, w, h, true);

            // save full sized signature
            signature.compress(CompressFormat.PNG, 100, new FileOutputStream(signaturePath));

            File signatureNoteFp = new File(signaturePath);
            if (!signatureNoteFp.exists()) // if not saved correctly
            {
                Log.e(TAG, "failed to save signature SpenNoteDoc: " + signaturePath);
                return null;
            }

            // save broken signature
            brokenSg = SignatureEditor.breakSignature(signature);
            brokenSg.compress(CompressFormat.PNG, 100, new FileOutputStream(
                    path[SignatureEditor.BROKEN_SIGNATURE]));

            // save blurred signature
            blurredSg = SignatureEditor.blur(signature, ctx, 8);
            blurredSg.compress(CompressFormat.PNG, 100, new FileOutputStream(
                    path[SignatureEditor.BLURRED_SIGNATURE]));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recycle(brokenSg);
            recycle(blurredSg);
        }
        return path;
    }

    /**
     * 비트맵을 리사이클한다.
     * 
     * @param b 비트맵
     */
    static void recycle(Bitmap b) {
        if (null != b) {
            b.recycle();
        }
    }

    /**
     * Signature를 지운다.
     */
    public void erase() {
        // PLog.e(TAG, "erase");
        mSignatureBmp = Bitmap.createBitmap(mW, mH, Bitmap.Config.ARGB_8888);
        pointsToDraw.clear();
        invalidate();
    }

    // public static String getSignatureFilePath() {
    // File sdCard = Environment.getExternalStorageDirectory();
    // File signFp = new File(sdCard.getAbsolutePath() + "/DCIM/signature.png");
    // return signFp.getAbsolutePath();
    // }

    /**
     * 저장된 Signature의 파일 경로를 불러온다.
     * 
     * @param ctx 컨텍스트
     * @return 경로 문자열
     */
    public static String getSignatureFilePath(Context ctx) {
        String filename = "/signature.png";
         return "/sdcard/DCIM" + filename;
        //return ctx.getFilesDir().getAbsolutePath() + filename;
    }

    /**
     * 저장된 Signature를 비트맵으로 가져온다.
     * 
     * @param path 경로
     * @return 비트맵
     */
    public static Bitmap getSignatureBitmap(String path) {
        // PLog.e(TAG, "getSignature");
        return BitmapFactory.decodeFile(path);
    }

    /**
     * 해당하는 비트맵을 리사이즈한다.
     * 
     * @param src 소스
     * @param w 길이
     * @param h 높이
     * @return 비트맵
     */
    public static Bitmap resizeSignature(Bitmap src, int w, int h) {
        Bitmap tmpSign = Bitmap.createScaledBitmap(src, w, h, true);
        src.recycle();
        return tmpSign;
    }

    /**
     * Artwork와 Signature를 합성한다.
     * 
     * @param art Artwork
     * @param sign Signature
     * @return 비트맵
     */
    public static Bitmap compositeSignatureInFullArtwork(Bitmap art, Bitmap sign) {
        int sw = art.getWidth();
        int sh = art.getHeight();

        Bitmap finalBitmap = Bitmap.createBitmap(sw, sh, art.getConfig());

        int newSignW = sw / 5;
        float ratio = (float) newSignW / (float) sign.getWidth();
        int newSignH = (int) (sign.getHeight() * ratio);

        Bitmap rescaledSign = Bitmap.createScaledBitmap(sign, newSignW,
                newSignH, true);

        Canvas canvas = new Canvas(finalBitmap);
        Matrix sigMatrix = new Matrix();
        sigMatrix.postTranslate(sw - newSignW, sh - newSignH);
        canvas.drawBitmap(rescaledSign, sigMatrix, null);
        return finalBitmap;
    }

    /**
     * Artwork와 Signature를 합성한다.
     * 
     * @param art Artwork
     * @param sign Signature
     * @return 비트맵
     */
    public static Bitmap compositeSignature(Bitmap art, Bitmap sign) {

        Bitmap finalBitmap = Bitmap.createBitmap(art.getWidth(),
                art.getHeight(), art.getConfig());

        final int sw = art.getWidth(), sh = art.getHeight();
        final int newSignW = sw / 5;

        final float ratio = (float) newSignW / (float) sign.getWidth();

        final int newSignH = (int) (sign.getHeight() * ratio);
        final Bitmap rescaledSign = Bitmap.createScaledBitmap(sign, newSignW,
                newSignH, true);

        Canvas canvas = new Canvas(finalBitmap);
        Matrix sigMatrix = new Matrix();
        sigMatrix.postTranslate(sw - newSignW, sh - newSignH);

        canvas.drawBitmap(art, new Matrix(), null);
        canvas.drawBitmap(rescaledSign, sigMatrix, null);
        return finalBitmap;
    }

    /**
     * Signature를 break한다.
     * 
     * @param bmp 비트맵
     * @return 비트맵
     */
    public static Bitmap breakSignature(Bitmap bmp) {
        final int bmp_width = bmp.getWidth(), bmp_height = bmp.getHeight();
        final int w = bmp_width, line_height = 3;

        int[] whitePixels = new int[w * line_height];

        Arrays.fill(whitePixels, 0x00);

        Bitmap ret = bmp.copy(bmp.getConfig(), true);

        for (int y = 0; y < bmp_height; y += line_height * 2) {
            final int h0 = Math.min(line_height, bmp_height - y);
            ret.setPixels(whitePixels, 0, bmp_width, 0, y, bmp_width, h0);
        }
        return ret;
    }

    /**
     * 비트맵에 Blur효과를 준다.
     * 
     * @param bmp 비트맵
     * @param c 컨텍스트
     * @param radius Blur Radius
     * @return 비트맵
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Bitmap bmp, Context c, int radius) {

        final RenderScript rs = RenderScript.create(c);
        final Allocation input = Allocation.createFromBitmap(rs, bmp,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs,
                    Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
        }

        final int w = bmp.getWidth(), h = bmp.getHeight();
        Bitmap ret = Bitmap.createBitmap(w, h, bmp.getConfig());
        output.copyTo(ret);
        return ret;
    }

    /**
     * 화면에 그려졌는지 여부를 보여준다.
     * 
     * @return 그려졌는지 여부
     */
    public boolean isDrawed() {
        return mIsDrawed;
    }
}
