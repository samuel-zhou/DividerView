package cn.zht.dividerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 *  虚线或图片repeat构成的分隔线视图
 */
public class DividerView extends View {

    public static final String TAG = "DividerView";

    public static final int MODE_DASH = 0x1;
    public static final int MODE_DASH_IMG = 0x2;

    private static final int DEFAULT_DASH_LINE_HEIGHT = 1;
    private static final int DEFAULT_DASH_LINE_GAP = 5;
    private static final int DEFAULT_DASH_LINE_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_DASH_LINE_LENGTH = 5;

    private int mWidth;
    private int mHeight;
    private Paint dashLinePaint;

    private int mode;
    private int dashLineColor = DEFAULT_DASH_LINE_COLOR;
    private int dashLineGap = DEFAULT_DASH_LINE_GAP;
    private int dashLineHeight = DEFAULT_DASH_LINE_HEIGHT;
    private int dashLineLength = DEFAULT_DASH_LINE_LENGTH;
    private Drawable dashDrawable;
    private int dashDrawableWidth;
    private int dashDrawableHeight;
    private int mNumX;
    private int mRemainX;

    public DividerView(Context context,AttributeSet attrs){
        super(context,attrs);
        init(context,attrs);
    }

    public DividerView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DividerView);
        mode = a.getInt(R.styleable.DividerView_dv_mode,MODE_DASH);
        dashLineGap = a.getDimensionPixelSize(R.styleable.DividerView_dv_dash_gap, dp2Px(DEFAULT_DASH_LINE_GAP, context));
        if(mode == MODE_DASH) {
            dashLineHeight = a.getDimensionPixelSize(R.styleable.DividerView_dv_dash_line_height, dp2Px(DEFAULT_DASH_LINE_HEIGHT, context));
            dashLineLength = a.getDimensionPixelSize(R.styleable.DividerView_dv_dash_line_length, dp2Px(DEFAULT_DASH_LINE_LENGTH, context));
            dashLineColor = a.getColor(R.styleable.DividerView_dv_dash_line_color, DEFAULT_DASH_LINE_COLOR);
        }else {
            dashDrawable = a.getDrawable(R.styleable.DividerView_dv_dash_img);
            dashDrawableWidth = a.getDimensionPixelSize(R.styleable.DividerView_dv_dash_img_width,Math.max(1,dashDrawable.getIntrinsicWidth()));
            dashDrawableHeight = a.getDimensionPixelSize(R.styleable.DividerView_dv_dash_img_height,Math.max(1,dashDrawable.getIntrinsicHeight()));
        }
        a.recycle();

        dashLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashLinePaint.setDither(true);
        dashLinePaint.setColor(dashLineColor);
        dashLinePaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mode == MODE_DASH?dashLineHeight:dashDrawableHeight,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int remainX = mRemainX;
        int left = 0;
        int top = 0;
        for(int i=0;i<=mNumX;i++){
            if(mode == MODE_DASH_IMG){

                left += i>0?dashDrawableWidth+dashLineGap:0;
                if(remainX > 0 && i > 0){
                    remainX--;
                    left += 1;
                }
                int right = left+dashDrawableWidth;
                int bottom = dashDrawableHeight;
                dashDrawable.setBounds(left,top,right,bottom);
                dashDrawable.draw(canvas);
            }else {
                left += i>0?dashLineLength+dashLineGap:0;
                if(remainX > 0 && i > 0){
                    remainX--;
                    left += 1;
                }
                int right = left+dashLineLength;
                int bottom = dashLineHeight;
                canvas.drawRect(left,top,right,bottom,dashLinePaint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        calculate();
    }

    private void calculate(){
        int sWidth = mode == MODE_DASH?dashLineLength:dashDrawableWidth;
        int remainX = (mWidth-sWidth)%(sWidth+dashLineGap);
        mNumX = (mWidth-sWidth)/(sWidth+dashLineGap);
        dashLineGap += remainX/mNumX;
        mRemainX = mWidth-(sWidth+dashLineGap)*mNumX-sWidth;

        Log.e(TAG,"RemainX = "+mRemainX);
        Log.e(TAG,"dashLineGap = "+dashLineGap);
        Log.e(TAG,"mNumX="+mNumX);
    }

    private int dp2Px(float dp,Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    private int px2Dp(float px,Context context) {
        return (int) (px / context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
