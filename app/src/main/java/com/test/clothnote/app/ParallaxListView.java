package com.test.clothnote.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by huanglizhuo on 14-5-25.
 */
public class ParallaxListView extends ListView {

    private static final int BACK_SCALE = 0;
    private boolean isHaveHead = false;// 头部是否有图片
    private float scaleY = 0;
    private boolean isBacking = false;// 是否处在回弹状态
    private int displayWidth;
    private Context mContext;
    private Bitmap bmp;
    private View headerView;
    private ImageView imageView;
    /** 用于记录拖拉图片移动的坐标位置 */
    private Matrix matrix = new Matrix();
    /** 用于记录图片要进行拖拉时候的坐标位置 */
    private Matrix currentMatrix = new Matrix();
    private Matrix defaultMatrix = new Matrix();
    private float imgHeight, imgWidth;
    /** 记录是拖拉照片模式还是放大缩小照片模式 0:拖拉模式，1：放大 */
    private int mode = 0;// 初始状态
    /** 拖拉照片模式 */
    private final int MODE_DRAG = 1;
    /** 用于记录开始时候的坐标位置 */
    private PointF startPoint = new PointF();
    //记录点击
    private PointF clickpoint = new PointF();
    private int mImageId;
    private AttributeSet attrs;
    private SlideView mFocusedItemView;
    public boolean isMove ;
    public ParallaxListView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public ParallaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.attrs = attrs;
        initView();
    }

    public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        this.attrs = attrs;
        initView();
    }

    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    public void addHeaderView(View v) {
        super.addHeaderView(v);
    }

    /**
     * 初始化图片
     */
    private void initView() {
		/* 取得屏幕分辨率大小 */

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);
        displayWidth = dm.widthPixels;
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.ImgListView);
        mImageId = a.getResourceId(R.styleable.ImgListView_headimage, 0);
        a.recycle();
        if (null == bmp && mImageId != 0) {
            bmp = BitmapFactory.decodeResource(getResources(), mImageId);
            initHead();
        }
    }

    public void setImageView(String picturePath){
        imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

    }

    private void initHead() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        headerView = inflater.inflate(R.layout.top_img, null);
        imageView = (ImageView) headerView.findViewById(R.id.imageView);

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, EditView.class);
                i.putExtra("isnew", true);
                mContext.startActivity(i);
            }
        });
        float scale = (float) displayWidth / (float) bmp.getWidth();// 1080/1800
        matrix.postScale(scale, scale, 0, 0);
        imageView.setImageMatrix(matrix);
        defaultMatrix.set(matrix);
        imgHeight = scale * bmp.getHeight();
        imgWidth = scale * bmp.getWidth();
        ListView.LayoutParams relativeLayout = new ListView.LayoutParams((int) imgWidth, (int) imgHeight);
        imageView.setLayoutParams(relativeLayout);
        this.addHeaderView(headerView);
        isHaveHead = true;
    }

    /**
     * 向下滑动让图片变大
     *
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (!isHaveHead) {// 无头部图片
            return super.onTouchEvent(event);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                clickpoint.set(event.getX(), event.getY());
                Log.e("test","down "+clickpoint.x+":"+clickpoint.y);
                isMove = true;
                int x = (int) event.getX();
                int y = (int) event.getY();
                int position = pointToPosition(x, y);
                if ((position != INVALID_POSITION) && position !=0){
                    NoteAdapter.MessageItem data = (NoteAdapter.MessageItem) getItemAtPosition(position);
                    mFocusedItemView = data.slideView;
                }

                if (isBacking) {
                    return super.onTouchEvent(event);
                }
                int[] location = new int[2];
                imageView.getLocationInWindow(location);
                if (location[1] >= 0) {
                    mode = MODE_DRAG;
                    // 记录ImageView当前的移动位置
                    currentMatrix.set(imageView.getImageMatrix());
                    startPoint.set(event.getX(), event.getY());
                }
                break;

            case MotionEvent.ACTION_MOVE:
                isMove = true;
                Log.e("test","Move "+isMove);
                // 拖拉图片
                if (mode == MODE_DRAG) {
                    float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
                    float dy = event.getY() - startPoint.y; // 得到y轴的移动距离
                    // 在没有移动之前的位置上进行移动
                    if (dy / 2 + imgHeight <= 1.5 * imgHeight) {
                        matrix.set(currentMatrix);
                        float scale = (dy / 2 + imgHeight) / (imgHeight);// 得到缩放倍数
                        if (dy > 0) {
                            scaleY = dy;
                            ListView.LayoutParams relativeLayout = new ListView.LayoutParams((int) (scale * imgWidth), (int) (scale * imgHeight));
                            imageView.setLayoutParams(relativeLayout);
                            matrix.postScale(scale, scale, imgWidth / 2, 0);
                            imageView.setImageMatrix(matrix);
                        }
                    }
                }

                break;
            // 手指离开屏幕
            case MotionEvent.ACTION_UP:
                if(Math.abs(event.getX() - clickpoint.x)<40&&Math.abs(event.getY() - clickpoint.y)<40){
                    isMove = false;
                }
//                Log.e("test","up "+event.getX()+":"+event.getY());
//                Log.e("test","UP "+isMove);
                // 当触点离开屏幕，图片还原
                mHandler.sendEmptyMessage(BACK_SCALE);
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
        }

        if (mFocusedItemView != null) {
            mFocusedItemView.onRequireTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }



//    public void shrinkListItem(int position) {
//        View item = getChildAt(position);
//
//        if (item != null) {
//            try {
//                ((SlideView) item).shrink();
//            } catch (ClassCastException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case BACK_SCALE:
                    float scale = (scaleY / 2 + imgHeight) / (imgHeight);// 得到缩放倍数
                    if (scaleY > 0) {
                        isBacking = true;
                        matrix.set(currentMatrix);
                        ListView.LayoutParams relativeLayout = new ListView.LayoutParams((int) (scale * imgWidth), (int) (scale * imgHeight));
                        imageView.setLayoutParams(relativeLayout);
                        matrix.postScale(scale, scale, imgWidth / 2, 0);
                        imageView.setImageMatrix(matrix);
                        scaleY = (float) (scaleY / 2 - 1);
                        mHandler.sendEmptyMessageDelayed(BACK_SCALE, 20);
                    } else {
                        scaleY = 0;
                        ListView.LayoutParams relativeLayout = new ListView.LayoutParams((int) imgWidth, (int) imgHeight);
                        imageView.setLayoutParams(relativeLayout);
                        matrix.set(defaultMatrix);
                        imageView.setImageMatrix(matrix);
                        isBacking = false;
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

}