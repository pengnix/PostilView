package com.ruaho.note.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ruaho.note.activity.R;
import com.ruaho.note.bean.Picture;
import com.ruaho.note.bean.PostilRecord;
import com.ruaho.note.bean.PostilTag;
import com.ruaho.note.util.DimenUtils;
import com.ruaho.note.util.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostilView extends View{

    private Paint mPaint;
    private Path mPath;
    private float mLastX;
    private float mLastY;
    private float mTagOriginX;
    private float mTagOriginY;
    private Bitmap mBufferBitmap;
    private Bitmap mTagBitmap;
    private List<Bitmap> mHistoryBitmap;
    private int mTagBitmapHeight;
    private int mTagBitmapWidth;
    private PostilTag currentTag;
    private Canvas mBufferCanvas;
    List<PostilTag> postilTagList;
    private PostilRecord picRecord;
    private float offsetY;
    private static float CLICK_PRECISION= 3.0f;

    private static final int MAX_CACHE_STEP = 20;

    private List<DrawingInfo> mDrawingList;
    private List<DrawingInfo> mRemovedList;

    private Xfermode mXferModeClear;
    private Xfermode mXferModeDraw;
    private int mDrawSize;
    private int mEraserSize;
    private int mPenAlpha = 255;
    private Map<String,Integer> url2Index;

    private boolean mCanEraser;

    private Callback mCallback;

    public enum Mode {
        DRAW,
        ERASER,
        NOT_EDIT,
        MOVE_TAG
    }

    private Mode mMode = Mode.NOT_EDIT;


    public PostilView(Context context) {
        super(context);
        initData();
        init();
    }

    public PostilView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData();
        init();
    }

    public PostilView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        init();
    }

    public interface Callback {
        void onUndoRedoStatusChanged();
        void openTag(PostilTag tag);
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    private void init() {
        url2Index = new HashMap<String,Integer>();
        setDrawingCacheEnabled(true);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawSize = DimenUtils.dp2pxInt(3);
        mEraserSize = DimenUtils.dp2pxInt(30);
        mPaint.setStrokeWidth(mDrawSize);
        mPaint.setColor(0XFF000000);
        mXferModeDraw = new PorterDuffXfermode(PorterDuff.Mode.SRC);
        mXferModeClear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mPaint.setXfermode(mXferModeDraw);

        mTagBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tag);
        mTagBitmapHeight = mTagBitmap.getHeight();
        mTagBitmapWidth = mTagBitmap.getWidth();

        Log.i("getResult!","mTagBitmap" + mTagBitmapHeight + ":" + mTagBitmapWidth);
    }

    void initData(){
        mHistoryBitmap = new ArrayList<Bitmap>();
        offsetY = 0;
        postilTagList = new ArrayList<PostilTag>();
        currentTag = null;
    }

    private void initBuffer(){
        mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
    }

    private abstract static class DrawingInfo {
        Paint paint;
        abstract void draw(Canvas canvas);
    }

    private static class PathDrawingInfo extends DrawingInfo{

        Path path;

        @Override
        void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
    }

    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        if (mode != mMode) {
            mMode = mode;
            if (mMode == Mode.DRAW) {
                mPaint.setXfermode(mXferModeDraw);
                mPaint.setStrokeWidth(mDrawSize);
            } else {
                mPaint.setXfermode(mXferModeClear);
                mPaint.setStrokeWidth(mEraserSize);
            }
        }
    }

    public void setEraserSize(int size) {
        mEraserSize = size;
    }

    public void setPenRawSize(int size) {
        mDrawSize = size;
        if(mMode == Mode.DRAW){
            mPaint.setStrokeWidth(mDrawSize);
        }
    }

    public void setPenColor(int color) {
        mPaint.setColor(color);
    }

    private void reDraw(){
        if (mDrawingList != null) {
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (DrawingInfo drawingInfo : mDrawingList) {
                drawingInfo.draw(mBufferCanvas);
            }
            invalidate();
        }
    }

    public int getPenColor(){
        return mPaint.getColor();
    }

    public int getPenSize(){
        return mDrawSize;
    }

    public int getEraserSize(){
        return mEraserSize;
    }

    public void setPenAlpha(int alpha){
        mPenAlpha = alpha;
        if(mMode == Mode.DRAW){
            mPaint.setAlpha(alpha);
        }
    }

    public int getPenAlpha(){
        return mPenAlpha;
    }

    public boolean canRedo() {
        return mRemovedList != null && mRemovedList.size() > 0;
    }

    public boolean canUndo(){
        return mDrawingList != null && mDrawingList.size() > 0;
    }

    public void redo() {
        int size = mRemovedList == null ? 0 : mRemovedList.size();
        if (size > 0) {
            DrawingInfo info = mRemovedList.remove(size - 1);
            mDrawingList.add(info);
            mCanEraser = true;
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public void undo() {
        int size = mDrawingList == null ? 0 : mDrawingList.size();
        if (size > 0) {
            DrawingInfo info = mDrawingList.remove(size - 1);
            if (mRemovedList == null) {
                mRemovedList = new ArrayList<>(MAX_CACHE_STEP);
            }
            if (size == 1) {
                mCanEraser = false;
            }
            mRemovedList.add(info);
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public void clear() {
        if (mBufferBitmap != null) {
            if (mDrawingList != null) {
                mDrawingList.clear();
            }
            if (mRemovedList != null) {
                mRemovedList.clear();
            }
            mCanEraser = false;
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            invalidate();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public Bitmap buildBitmap() {
        Bitmap bm = getDrawingCache();
        Bitmap result = Bitmap.createBitmap(bm);
        destroyDrawingCache();
        return result;
    }

    private void saveDrawingPath(){
        if (mDrawingList == null) {
            mDrawingList = new ArrayList<>(MAX_CACHE_STEP);
        } else if (mDrawingList.size() == MAX_CACHE_STEP) {
            mDrawingList.remove(0);
        }
        Path cachePath = new Path(mPath);
        Paint cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();
        info.path = cachePath;
        info.paint = cachePaint;
        mDrawingList.add(info);
        mCanEraser = true;
        if (mCallback != null) {
            mCallback.onUndoRedoStatusChanged();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }
//        for(PostilTag tag:postilTagList){
//            Log.i("getResult!","draw tag" + tag.xPos + ":" + tag.yPos);
//            canvas.drawBitmap(mTagBitmap , tag.xPos - mTagBitmapWidth/2, tag.yPos - mTagBitmapHeight/2, null);
//        }
        if(currentTag != null){
            Log.i("getResult!","draw tag" + currentTag.getxPos() + ":" + currentTag.getyPos());
            canvas.drawBitmap(mTagBitmap , currentTag.getxPos() - mTagBitmapWidth/2, currentTag.getyPos() - mTagBitmapHeight/2 - offsetY, null);

        }
        if(picRecord != null && picRecord.getPicList() != null){
            List<Picture> picList = picRecord.getPicList();
            for(Picture pic:picList){
                String address = pic.getAddress();
                Integer index = url2Index.get(address);
                if(index == null){
                    continue;
                }
                Bitmap bmp = mHistoryBitmap.get(index);
                canvas.drawBitmap(bmp, 0, pic.getOffsetY()-offsetY, null);
            }
        }
    }

    @SuppressWarnings("all")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()){
            return false;
        }
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final float x = event.getX();
        final float y = event.getY();

        if(mMode == Mode.NOT_EDIT){
            boolean isTouchTag = containTagBitmap((int)x,(int)y);
            Log.i("getResult!","isTouchTag = " + isTouchTag);
            if(!isTouchTag){
//                offsetY += (y-mLastY);
//                Log.i("offset","offsetY = " + offsetY + "y = " + y + "mLastY = " +mLastY);
//                mLastX = x;
//                mLastY = y;
//                Log.i("scrollY","ScrollY = " + mCallback.getScrollY());
//                mCallback.getScrollY();
                return super.onTouchEvent(event);
            } else {
                mTagOriginX = x;
                mTagOriginY = y;
                mMode = Mode.MOVE_TAG;
            }
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if(mMode == Mode.MOVE_TAG){
                    currentTag.updatePos((int)x,(int)y);
                    break;
                }
                mLastX = x;
                mLastY = y;
                if (mPath == null) {
                    mPath = new Path();
                }
                mPath.moveTo(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                if(mMode == Mode.MOVE_TAG){
                    currentTag.updatePos((int)x,(int)y);
                    invalidate();
                    break;
                }
                //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
                mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                if (mBufferBitmap == null) {
                    initBuffer();
                }
                if (mMode == Mode.ERASER && !mCanEraser) {
                    break;
                }
                mBufferCanvas.drawPath(mPath,mPaint);

                invalidate();
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if(mMode == Mode.MOVE_TAG){
                    mMode = Mode.NOT_EDIT;
                    if(isClickTag(x,y)){
                        openTag();
                    }
                    break;
                }
                if (mMode == Mode.DRAW || mCanEraser) {
                    saveDrawingPath();
                }
                mPath.reset();
                break;
        }
        return true;
    }

    public boolean containTagBitmap(int x,int y){
        if(currentTag != null){
            int top = currentTag.getyPos() - mTagBitmapHeight/2;
            int bottom= currentTag.getyPos() + mTagBitmapHeight/2;
            int left= currentTag.getxPos() - mTagBitmapWidth/2;
            int right= currentTag.getxPos() + mTagBitmapWidth/2;
            Log.i("getResult!","top = " + top + "bottom = " + bottom + "left = " + left + "right = " + right);
            Log.i("getResult!","x = " + x + "y = " + y);
            if(x > left && x < right && y > top && y < bottom){
                return true;
            }
        }
        return false;
    }

    public boolean isClickTag(float x,float y){
        if((Math.abs(x-mTagOriginX) < CLICK_PRECISION) && (Math.abs(y-mTagOriginY) < CLICK_PRECISION)){
            return true;
        }
        return false;
    }

    public void openTag(){
        if(currentTag != null && mCallback != null){
            mCallback.openTag(currentTag);
        }
    }

    public void updatePostilTag(List<PostilTag> list){
        postilTagList = list;
        invalidate();
    }

    public void addPostilTag(PostilTag tag){
        currentTag = tag;
        invalidate();
    }

    public void updateOffsetY(int dy){
        offsetY = dy;
        Log.i("offsetY","dy = " + dy);
        invalidate();
    }

    public float getOffsetY(){
        return offsetY;
    }

    public void setHistoryPictureRecord(PostilRecord record){
        Log.i("saveImage","setHistoryPictureRecord");
        picRecord = record;
        if(picRecord == null){
            return;
        }
        List<Picture> picList = picRecord.getPicList();
        if(picList == null){
            return;
        }
        for(Picture pic:picList){
            Bitmap bmp = FileUtils.loadImage(pic.getAddress());
            mHistoryBitmap.add(bmp);
            int index = mHistoryBitmap.size() - 1;
            url2Index.put(pic.getAddress(),index);
        }
        invalidate();
    }
}
