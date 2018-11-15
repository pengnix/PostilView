package com.ruaho.note.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ruaho.note.activity.R;
import com.ruaho.note.bean.Picture;
import com.ruaho.note.bean.PostilRecordList;
import com.ruaho.note.bean.PostilWord;
import com.ruaho.note.bean.PostilWordsList;
import com.ruaho.note.util.BitmapUtils;
import com.ruaho.note.util.DimenUtils;
import com.ruaho.note.util.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ruaho.note.view.PostilView.Mode.DRAW;

public class PostilView extends View{

    private Paint mPaint;
    private Path mPath;
    private float mLastX;
    private float mLastY;
    private float mTagOriginX;
    private float mTagOriginY;
    //用于直线两点确定图形
    private float mTopLeftX;
    private float mTopLeftY;
    private float mBottomRightX;
    private float mBottomRightY;
    private Bitmap mBufferBitmap;
    private Bitmap mTagBitmap;
    private List<Bitmap> mHistoryBitmap;
    private int mTagBitmapHeight;
    private int mTagBitmapWidth;
    private Canvas mBufferCanvas;
    private PostilRecordList picRecord;
    private PostilWordsList mPostilWordsList;
    private PostilWord mCurrentWord;
    private float offsetY;
    private float offsetX;
    private static float CLICK_PRECISION= 3.0f;
    private boolean needDrawLine = false;
    public final static float SCALE_BASE = 1.5f;
    private float currentNewScale = 1.5f;
    private static final int MAX_CACHE_STEP = 20;
    private Matrix positionMatrix = null;

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

    private int currentTuYaIndex;

    public enum Mode {
        DRAW,
        ERASER,
        NOT_EDIT,
        MOVE_TAG,
        MANAGE_TUYA
    }

    public enum DRAWMode {
        CURVE,
        LINE,
        RECT,
        OVAL
    }

    private Mode mMode = Mode.NOT_EDIT;
    //曲线-直线-矩形-椭圆
    private DRAWMode mDrawMode = DRAWMode.CURVE;


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
        void openTag(PostilWord tag);
        void scrollTo(int x,int y);
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
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawSize = 10;//DimenUtils.dp2pxInt(3);
        Log.i("progresschange","size  = " + mDrawSize);
        mEraserSize = DimenUtils.dp2pxInt(30);
        mPaint.setStrokeWidth(mDrawSize);
        mPaint.setColor(0XFFFF0000);
        mXferModeDraw = new PorterDuffXfermode(PorterDuff.Mode.SRC);
        mXferModeClear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mPaint.setXfermode(mXferModeDraw);

        mTagBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pizhu_tag);
        mTagBitmap = BitmapUtils.bitMapScale(mTagBitmap,0.7f);
        mTagBitmapHeight = mTagBitmap.getHeight();
        mTagBitmapWidth = mTagBitmap.getWidth();

        Log.i("getResult!","mTagBitmap" + mTagBitmapHeight + ":" + mTagBitmapWidth);
    }

    void initData(){
        positionMatrix = new Matrix();
        mHistoryBitmap = new ArrayList<Bitmap>();
        offsetX = 0;
        offsetY = 0;
        currentNewScale = 1.5f;
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
            if (mMode == DRAW) {
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
        if(mMode == DRAW){
            mPaint.setStrokeWidth(mDrawSize);
        }
    }

    public void setPenColor(int color) {
        mPaint.setColor(color);
        mPaint.setAlpha(getPenAlpha());
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
        if(mMode == DRAW){
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

        if(mCurrentWord != null && mCurrentWord.isCanMove()){
            Log.i("activityR","can move");
//            positionMatrix.reset();
//            float scale = currentNewScale/mCurrentWord.getScale();
//            float oX = mCurrentWord.getOffsetX() * scale - offsetX;
//            float oY = mCurrentWord.getOffsetY() * scale- offsetY;
//            positionMatrix.setTranslate(oX,oY);
//            positionMatrix.preScale(scale, scale);
//            canvas.drawBitmap(mTagBitmap,positionMatrix,null);
            float left = mCurrentWord.getxPos() - mTagBitmapWidth/2;
            float top = mCurrentWord.getyPos() - mTagBitmapHeight/2 - offsetY +mCurrentWord.getOffsetY();
            canvas.drawBitmap(mTagBitmap , left, top, null);
        }

        if(mPostilWordsList != null && mPostilWordsList.getList() != null){
            List<PostilWord> list =  mPostilWordsList.getList();
            for(PostilWord tag:list){
                String address = tag.getAddress();
                Integer index = url2Index.get(address);
                if(index == null){
                    continue;
                }
                Bitmap bmp = mHistoryBitmap.get(index);
                positionMatrix.reset();
                float scale = currentNewScale/tag.getScale();
                float oX = tag.getOffsetX() * scale - offsetX;
                float oY = tag.getOffsetY() * scale- offsetY;
                positionMatrix.setTranslate(oX,oY);
                positionMatrix.preScale(scale, scale);
                canvas.drawBitmap(bmp,positionMatrix,null);
            }
        }

        if(mMode == Mode.MANAGE_TUYA){
            if(picRecord != null && picRecord.getPicList() != null){
                if(currentTuYaIndex < picRecord.getPicList().size()){
                    Picture pic = picRecord.getPicList().get(currentTuYaIndex);
                    String address = pic.getAddress();
                    Integer index = url2Index.get(address);
                    if(index == null){
                        return;
                    }
                    Bitmap bmp = mHistoryBitmap.get(index);
                    positionMatrix.reset();
                    float scale = currentNewScale/pic.getScale();
                    float oX = pic.getOffsetX() * scale - offsetX;
                    float oY = pic.getOffsetY() * scale- offsetY;
                    positionMatrix.setTranslate(oX,oY);
                    positionMatrix.preScale(scale, scale);
                    canvas.drawBitmap(bmp,positionMatrix,null);
                }
            }
        } else {
            if(picRecord != null && picRecord.getPicList() != null){
                List<Picture> picList = picRecord.getPicList();
                for(Picture pic:picList){
                    String address = pic.getAddress();
                    Integer index = url2Index.get(address);
                    if(index == null){
                        continue;
                    }
                    Bitmap bmp = mHistoryBitmap.get(index);
                    positionMatrix.reset();
                    float scale = currentNewScale/pic.getScale();
                    float oX = pic.getOffsetX() * scale - offsetX;
                    float oY = pic.getOffsetY() * scale- offsetY;
                    positionMatrix.setTranslate(oX,oY);
                    positionMatrix.preScale(scale, scale);
                    canvas.drawBitmap(bmp,positionMatrix,null);
                }
            }
        }

        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }
        if(mMode == DRAW && mDrawMode == DRAWMode.LINE && needDrawLine){
            canvas.drawLine(mTopLeftX,mTopLeftY,mBottomRightX,mBottomRightY,mPaint);
        }
        if(mMode == DRAW && mDrawMode == DRAWMode.RECT && needDrawLine){
            drawPreviewRect(canvas);
        }
        if(mMode == DRAW && mDrawMode == DRAWMode.OVAL && needDrawLine){
            drawPreviewOval(canvas);
        }
    }

    void drawPreviewRect(Canvas canvas){
        float left = Math.min(mTopLeftX,mBottomRightX);
        float right = Math.max(mTopLeftX,mBottomRightX);
        float top = Math.min(mTopLeftY,mBottomRightY);
        float bottom = Math.max(mTopLeftY,mBottomRightY);
        canvas.drawRect(left,top,right,bottom,mPaint);
    }

    void drawPreviewOval(Canvas canvas){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            float left = Math.min(mTopLeftX,mBottomRightX);
            float right = Math.max(mTopLeftX,mBottomRightX);
            float top = Math.min(mTopLeftY,mBottomRightY);
            float bottom = Math.max(mTopLeftY,mBottomRightY);
            canvas.drawOval(left,top,right,bottom,mPaint);
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

        if(mMode == Mode.MANAGE_TUYA){
            return super.onTouchEvent(event);
        }

        if(mMode == Mode.NOT_EDIT){
            boolean isTouchTag = containTagBitmap((int)x,(int)y);
            Log.i("checkClick","isTouchTag = " + isTouchTag);
            if(!isTouchTag){
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
                    Log.i("containTagBitmap","333333" + canMove());
                    if(canMove()){
                        mCurrentWord.updatePos((int)x,(int)y);
                    }
                    break;
                }
                mLastX = x;
                mLastY = y;
                if(mDrawMode == DRAWMode.LINE || mDrawMode == DRAWMode.RECT || mDrawMode == DRAWMode.OVAL){
                    mTopLeftX = x;
                    mTopLeftY = y;
                    needDrawLine = true;
                }
                if (mPath == null) {
                    mPath = new Path();
                }
                if(mDrawMode == DRAWMode.CURVE){
                    mPath.moveTo(x,y);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(mMode == Mode.MOVE_TAG){
                    Log.i("containTagBitmap","444444" + canMove());
                    if(canMove()){
                        mCurrentWord.updatePos((int)x,(int)y);
                    }
                    invalidate();
                    break;
                }
                //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
                if (mBufferBitmap == null) {
                    initBuffer();
                }
                if(mDrawMode == DRAWMode.CURVE){
                    mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                } else if(mDrawMode == DRAWMode.LINE || mDrawMode == DRAWMode.RECT || mDrawMode == DRAWMode.OVAL){
                    Log.i("mDrawMode","Line");
                    mBottomRightX = x;
                    mBottomRightY = y;
                }
                if (mMode == Mode.ERASER && !mCanEraser) {
                    break;
                }
                if(mDrawMode == DRAWMode.CURVE){
                    mBufferCanvas.drawPath(mPath,mPaint);
                }
                invalidate();
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if(mMode == Mode.MOVE_TAG){
                    mMode = Mode.NOT_EDIT;
                    Log.i("checkClick","isClickTag = " + isClickTag(x,y));
                    if(isClickTag(x,y)){
                        openTag();
                    }
                    break;
                }
                if(mDrawMode == DRAWMode.LINE){
                    mBottomRightX = x;
                    mBottomRightY = y;
                    mPath.moveTo(mTopLeftX,mTopLeftY);
                    mPath.lineTo(x,y);
                    mBufferCanvas.drawPath(mPath,mPaint);
                    needDrawLine = false;
                    invalidate();
                } else if(mDrawMode == DRAWMode.RECT){
                    mBottomRightX = x;
                    mBottomRightY = y;
                    mPath.moveTo(mTopLeftX,mTopLeftY);
                    mPath.lineTo(mBottomRightX,mTopLeftY);
                    mPath.lineTo(mBottomRightX,mBottomRightY);
                    mPath.lineTo(mTopLeftX,mBottomRightY);
                    mPath.close();
                    mBufferCanvas.drawPath(mPath,mPaint);
                    needDrawLine = false;
                    invalidate();
                } else if(mDrawMode == DRAWMode.OVAL){
                    mBottomRightX = x;
                    mBottomRightY = y;
                    mPath.moveTo(mTopLeftX,mTopLeftY);
                    mPath.addOval(mTopLeftX,mTopLeftY,mBottomRightX,mBottomRightY,Path.Direction.CW);
                    mBufferCanvas.drawPath(mPath,mPaint);
                    needDrawLine = false;
                    invalidate();
                }
                if (mMode == DRAW || mCanEraser) {
                    saveDrawingPath();
                }
                mPath.reset();
                break;
        }
        return true;
    }

    public boolean containTagBitmap(int x,int y){
        if(mCurrentWord != null){
            int top = mCurrentWord.getyPos() - mTagBitmapHeight/2 - (int)offsetY +mCurrentWord.getOffsetY();
            int bottom= mCurrentWord.getyPos() + mTagBitmapHeight/2 - (int)offsetY +mCurrentWord.getOffsetY();
            int left= mCurrentWord.getxPos() - mTagBitmapWidth/2;
            int right= mCurrentWord.getxPos() + mTagBitmapWidth/2;
            Log.i("containTagBitmap","2222222");
            if(x > left && x < right && y > top && y < bottom){
                Log.i("containTagBitmap","111111");
                return true;
            }
        }

        if(mPostilWordsList != null && mPostilWordsList.getList() != null){
            List<PostilWord> list =  mPostilWordsList.getList();
            Log.i("checkClick","x="+x+"y="+y);
            for(PostilWord tag:list){
                float scale = currentNewScale/tag.getScale();
                float centerX = (tag.getOffsetX() + tag.getxPos()) * scale - offsetX;
                float centerY = (tag.getOffsetY() + tag.getyPos()) * scale - offsetY;
                float top =  centerY - mTagBitmapWidth/2 ;
                float bottom= centerY + mTagBitmapWidth/2;
                float left = centerX - mTagBitmapWidth/2;
                float right= centerX + mTagBitmapWidth/2;
//                Log.i("checkClick","offsetX="+offsetX+"offsetY=" +offsetY
//                        +"getOffsetX="+tag.getOffsetX()+"getOffsetY="+tag.getOffsetY()
//                        +"getxPos="+ tag.getxPos()+"getyPos=" + tag.getyPos()+"BitmapWidth="+mTagBitmapWidth);
//                Log.i("checkClick","left="+left+"right="+right+
//                        "top="+top+"bottom="+bottom+"scale="+scale);
                if(x > left && x < right && y > top && y < bottom){
                    mCurrentWord = tag;
                    Log.i("activityR","containTagBitmap="+ mCurrentWord.toString());
                    return true;
                }
            }
        }
        return false;
    }

    boolean canMove(){
        if(mCurrentWord == null){
            Log.i("containTagBitmap","55555");
            return false;
        }
        return mCurrentWord.isCanMove();
    }

    public boolean isClickTag(float x,float y){
        if((Math.abs(x-mTagOriginX) < CLICK_PRECISION) && (Math.abs(y-mTagOriginY) < CLICK_PRECISION)){
            return true;
        }
        return false;
    }

    public void openTag(){
        if(mCurrentWord != null && mCallback != null){
            Log.i("activityR","openTag= "+ mCurrentWord.toString());
            mCallback.openTag(mCurrentWord);
        }
    }

    public void setPostilTags(PostilWordsList list){
        mPostilWordsList = list;
        Log.i("saveImage","setHistoryPictureRecord");
        if(mPostilWordsList == null){
            return;
        }
        List<PostilWord> picList = mPostilWordsList.getList();
        if(picList == null){
            return;
        }
        for(PostilWord pic:picList){
            Bitmap bmp = FileUtils.loadImage(pic.getAddress());
            mHistoryBitmap.add(bmp);
            int index = mHistoryBitmap.size() - 1;
            url2Index.put(pic.getAddress(),index);
        }
        invalidate();
    }

    public void addPostilTag(PostilWord tag){
        mCurrentWord = tag;
        invalidate();
    }

    public void savePostilTag(PostilWord tag){
        mPostilWordsList.getList().add(tag);
        invalidate();
    }

    public void updatePostilTag(PostilWord tag){
        mCurrentWord.updateAll(tag.getOffsetX(),tag.getOffsetY(),tag.getxPos(),tag.getyPos(),tag.getScale(),tag.getContent());
        invalidate();
    }

    public void updatePositionInfo(int dx,int dy, float newScale){
        Log.i("updatePositionInfo","dx:dy:newScale=" + dx +":" + dy+":"+ newScale);
        offsetX = dx;
        offsetY = dy;
        currentNewScale = newScale;
        invalidate();
    }

    public float getCurrentNewScale(){
        return currentNewScale;
    }

    public float getOffsetY(){
        return offsetY;
    }

    public float getOffsetX(){
        return offsetX;
    }

    public void setHistoryPictureRecord(PostilRecordList record){
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

    public PostilWord getCurrentPostilTag(){
        return mCurrentWord;
    }

    public void clearCurrentPostilTag(){
        mCurrentWord = null;
        invalidate();
    }

    public DRAWMode getDrawMode() {
        return mDrawMode;
    }

    public void setDrawMode(DRAWMode drawMode) {
        this.mDrawMode = drawMode;
    }
    public void clearAllBitmap(){
        url2Index.clear();
        mPostilWordsList = new PostilWordsList();
        picRecord = new PostilRecordList();
        mCurrentWord = null;
        clearBitmap(mBufferBitmap);
        clearBitmap(mTagBitmap);
        for(Bitmap bitmap:mHistoryBitmap){
            clearBitmap(bitmap);
        }
        System.gc();
    }

    public void clearBitmap(Bitmap bitmap){
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
    }

    public int getCurrentTuYaIndex() {
        return currentTuYaIndex;
    }

    public void setCurrentTuYaIndex(int currentTuYaIndex) {
        this.currentTuYaIndex = currentTuYaIndex;
    }

    public void nextTuya(){
        if(picRecord != null && picRecord.getPicList() != null){
            if((currentTuYaIndex + 1) < picRecord.getPicList().size()){
                currentTuYaIndex++;
                if(mCallback != null){
                    Picture pic = picRecord.getPicList().get(currentTuYaIndex);
                    mCallback.scrollTo(0,pic.getOffsetY());
                    invalidate();
                }
            }
        }
    }

    public void previewTuya(){
        if(picRecord != null && picRecord.getPicList() != null){
            if(currentTuYaIndex -1 >= 0){
                currentTuYaIndex--;
                if(mCallback != null){
                    Picture pic = picRecord.getPicList().get(currentTuYaIndex);
                    mCallback.scrollTo(0,pic.getOffsetY());
                    invalidate();
                }
            }
        }
    }

    public void deleteCurrentTuya(){
        if(picRecord != null && picRecord.getPicList() != null){
            if(currentTuYaIndex < picRecord.getPicList().size() && currentTuYaIndex >=0){
                picRecord.getPicList().remove(currentTuYaIndex);
            }
        }
    }

}
