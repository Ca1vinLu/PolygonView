package com.meitu.lyz.polygonview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.meitu.lyz.polygonview.R;

import java.util.Map;

/**
 * 多边形View,可根据数据集绘制任意多边形
 * 可动态绑定数据集刷新View
 *
 * @author LYZ 2018.04.26
 */
public class PolygonView extends View {

    private Context mContext;

    //边数
    private int mEdgeNum;

    //小标题
    private String[] mKeys;
    //数据集
    private float[] mValues;
    //最大值的下标
    private int mMaxValueIndex;


    //View实际的宽高
    private int mWidth, mHeight;

    //边界及分隔线宽度
    private int mEdgeWidth;
    //数据集遮罩层边界线宽度
    private int mCoverEdgeWidth;

    //多边形的半径
    private int mRadius;
    //中间四个圈的半径
    private int[] mBackgroundRadius;
    //绘制虚线的PathEffect
    private PathEffect mPathEffect;

    //中间多边形的比例
    private float mPolygonRate;
    //默认比例
    private static final float DEFAULT_POLYGON_RATE = 0.92f;

    //小标题和数据的文字大小，颜色
    private int mKeyTextSize;
    private int mValueTextSize;
    private int mKeyTextColor;
    private int mValueTextColor;

    //数据最大的下小标题和数据的文字大小，颜色
    private int mMaxKeyTextSize;
    private int mMaxValueTextSize;
    private int mMaxKeyTextColor;
    private int mMaxValueTextColor;

    //用于获取文字高度的FontMetrics
    private Paint.FontMetrics mValueFontMetrics;
    private Paint.FontMetrics mMaxKeyFontMetrics;
    private Paint.FontMetrics mMaxValueFontMetrics;

    //文字和多边形的间距
    private int mTextGraphMargin;

    //边界及分割线的颜色
    private int mEdgeColor;
    //数据集遮罩层边界的颜色
    private int mCoverEdgeColor;
    //数据集遮罩层的渐变起始颜色
    private int mCoverStartColor;
    //数据集遮罩层的渐变结束颜色
    private int mCoverEndColor;

    //数据集遮罩层的透明度
    private int mCoverAlpha;
    //默认透明度
    private static final int DEFAULT_COVER_ALPHA = 205;

    //多边形中心点
    private PointF mCenterPoint;

    //外圈的点集
    private PointF[] mOutsideEdgePoints;

    //数据集遮罩层的点集
    private PointF[] mValueEdgePoints;
    //文字中心点集
    private PointF[] mTextPoints;

    //数据集绘制路径
    private Path mValueEdgePath;

    //各个部分的Paint
    private Paint mEdgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCoverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCoverEdgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PolygonView(Context context) {
        this(context, null);
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttr(attrs);
        initPaint();
    }


    /**
     * 初始化自定义属性
     */
    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.PolygonView);

        mKeyTextColor = typedArray.getColor(R.styleable.PolygonView_key_text_color,
                ContextCompat.getColor(mContext, R.color.polygon_view_key_text_color));
        mMaxKeyTextColor = typedArray.getColor(R.styleable.PolygonView_max_key_text_color,
                ContextCompat.getColor(mContext, R.color.polygon_view_max_key_text_color));
        mValueTextColor = typedArray.getColor(R.styleable.PolygonView_value_text_color,
                ContextCompat.getColor(mContext, R.color.polygon_view_value_text_color));
        mMaxValueTextColor = typedArray.getColor(R.styleable.PolygonView_max_value_text_color,
                ContextCompat.getColor(mContext, R.color.polygon_view_max_value_text_color));
        mEdgeColor = typedArray.getColor(R.styleable.PolygonView_edge_color,
                ContextCompat.getColor(mContext, R.color.polygon_view_edge_color));
        mCoverEdgeColor = typedArray.getColor(R.styleable.PolygonView_cover_edge_color,
                ContextCompat.getColor(mContext, R.color.polygon_view_cover_edge_color));
        mCoverStartColor = typedArray.getColor(R.styleable.PolygonView_cover_start_color,
                ContextCompat.getColor(mContext, R.color.polygon_view_cover_start_color));
        mCoverEndColor = typedArray.getColor(R.styleable.PolygonView_cover_end_color,
                ContextCompat.getColor(mContext, R.color.polygon_view_cover_end_color));

        mCoverAlpha = typedArray.getInteger(R.styleable.PolygonView_cover_alpha, DEFAULT_COVER_ALPHA);
        mPolygonRate = typedArray.getFloat(R.styleable.PolygonView_polygon_rate, DEFAULT_POLYGON_RATE);

        mKeyTextSize = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_key_text_size,
                mContext.getResources().getDimensionPixelSize(R.dimen.polygon_view_key_text_size));
        mValueTextSize = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_value_text_size,
                mContext.getResources().getDimensionPixelSize(R.dimen.polygon_view_value_text_size));
        mMaxKeyTextSize = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_max_key_text_size,
                mContext.getResources().getDimensionPixelSize(R.dimen.polygon_view_max_key_text_size));
        mMaxValueTextSize = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_max_value_text_size,
                mContext.getResources().getDimensionPixelSize(R.dimen.polygon_view_max_value_text_size));

        mEdgeWidth = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_edge_width,
                mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_edge_width));
        mCoverEdgeWidth = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_cover_edge_width,
                mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_cover_edge_width));
        mTextGraphMargin = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_text_graph_margin,
                mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_text_graph_margin));


        typedArray.recycle();

    }

    /**
     * 初始化paint
     */
    private void initPaint() {
        mCoverEdgePaint.setColor(mCoverEdgeColor);
        mEdgePaint.setColor(mEdgeColor);

        mCoverPaint.setAlpha(mCoverAlpha);

        mEdgePaint.setStyle(Paint.Style.STROKE);
        mCoverEdgePaint.setStyle(Paint.Style.STROKE);

        mEdgePaint.setStrokeWidth(mEdgeWidth);
        mCoverEdgePaint.setStrokeWidth(mCoverEdgeWidth);

        mEdgePaint.setStrokeJoin(Paint.Join.ROUND);
        mCoverEdgePaint.setStrokeJoin(Paint.Join.ROUND);

        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        //获取FontMetrics
        mTextPaint.setTextSize(mValueTextSize);
        mValueFontMetrics = mTextPaint.getFontMetrics();
        mTextPaint.setTextSize(mMaxKeyTextSize);
        mMaxKeyFontMetrics = mTextPaint.getFontMetrics();
        mTextPaint.setTextSize(mMaxValueTextSize);
        mMaxValueFontMetrics = mTextPaint.getFontMetrics();
    }

    /**
     * 绑定数据集
     *
     * @param data String,Float键值对  size>=3
     * @return 是否绑定成功
     */
    public boolean bindData(Map<String, Float> data) {
        if (data != null && data.size() >= 3) {
            mEdgeNum = data.size();
            mKeys = new String[mEdgeNum];
            data.keySet().toArray(mKeys);
            mValues = new float[mEdgeNum];

            //获取数据集并计算最大值的下标
            mMaxValueIndex = 0;
            for (int i = 0; i < mKeys.length; i++) {
                mValues[i] = data.get(mKeys[i]);
                if (mValues[i] > mValues[mMaxValueIndex]) {
                    mMaxValueIndex = i;
                }
            }

            //刷新View
            invalidate();
            return true;
        }
        return false;
    }


    /**
     * 计算背景的点及绘制路径
     */
    private void initBackgroundData() {
        initBackgroundPoints();
    }

    /**
     * 计算多边形的点及绘制路径
     */
    private void initPolygonData() {
        initPolygonEdgePoints();
        initPolygonPath();
    }


    /**
     * 计算背景的点
     */
    private void initBackgroundPoints() {
        mOutsideEdgePoints = new PointF[mEdgeNum];
        mTextPoints = new PointF[mEdgeNum];

        //计算中心角的弧度
        double degree = 2 * Math.PI / mEdgeNum;

        //根据权重计算比例
        float textRate = 1 + (mTextGraphMargin + mMaxValueFontMetrics.bottom - mMaxValueFontMetrics.top) / mRadius;

        for (int i = 0; i < mEdgeNum; i++) {
            mOutsideEdgePoints[i] = new PointF((float) (mCenterPoint.x + mRadius * Math.sin(degree * i)), (float) (mCenterPoint.y - mRadius * Math.cos(degree * i)));
            mTextPoints[i] = new PointF(mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * textRate,
                    mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * textRate);
        }
    }


    /**
     * 计算多边形的点
     */
    private void initPolygonEdgePoints() {
        mValueEdgePoints = new PointF[mEdgeNum];


        for (int i = 0; i < mEdgeNum; i++) {
            mValueEdgePoints[i] = new PointF(mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * mPolygonRate,
                    mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * mPolygonRate);
        }
    }


    /**
     * 计算多边形的绘制路径
     */
    private void initPolygonPath() {
        mValueEdgePath = new Path();
        mValueEdgePath.moveTo(mValueEdgePoints[0].x, mValueEdgePoints[0].y);
        for (int i = 1; i < mEdgeNum; i++) {
            mValueEdgePath.lineTo(mValueEdgePoints[i].x, mValueEdgePoints[i].y);
        }

        mValueEdgePath.close();
    }


    /**
     * 使宽高比固定为1:1
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode != MeasureSpec.UNSPECIFIED) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        }
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 计算半径并初始化各个点集
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRadius = Math.min(w, h) / 2;
        mCenterPoint = new PointF(w / 2, h / 2);


        //计算半径，实际半径为减去文字高度和文图间距的值
        mRadius = (int) (mRadius - (mMaxValueFontMetrics.bottom - mMaxValueFontMetrics.top) * 1.5 -
                (mMaxKeyFontMetrics.bottom - mMaxKeyFontMetrics.top) * 1.5 - mTextGraphMargin);

        initBackgroundRadius();
        initPathEffect();
        initCoverPaintShader();


        initBackgroundData();
        initPolygonData();
    }

    /**
     * 计算内部四个圆的半径
     */
    private void initBackgroundRadius() {
        mBackgroundRadius = new int[4];
        mBackgroundRadius[0] = mRadius / 5;
        for (int i = 1; i < 4; i++) {
            mBackgroundRadius[i] = mBackgroundRadius[0] * (i + 1);
        }
    }


    /**
     * 初始化CoverPaint的Shader
     */
    private void initCoverPaintShader() {
        //计算LinearGradient的两个端点的位置
        int offset = (int) (Math.sin(0.25 * Math.PI) * mRadius);
        LinearGradient linearGradient = new LinearGradient(mCenterPoint.x + offset, mCenterPoint.y - offset,
                mCenterPoint.x - offset, mCenterPoint.y + offset,
                mCoverStartColor, mCoverEndColor, Shader.TileMode.CLAMP);
        mCoverPaint.setShader(linearGradient);
    }

    /**
     * 初始化PathEffect
     */
    private void initPathEffect() {
        //计算内部圆虚线的长度
        float interval = (float) (mBackgroundRadius[0] * 2 * Math.PI / 40f);

        float[] intervals = {interval, interval};
        mPathEffect = new DashPathEffect(intervals, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mEdgeNum == 0)
            return;

        //自底向上依次绘制
        drawBackgroundCircle(canvas);
        drawDivideLines(canvas);
        drawCover(canvas);
        drawCoverEdge(canvas);
        drawText(canvas);
    }


    /**
     * 绘制小标题及数值文字
     */
    private void drawText(Canvas canvas) {

        //绘制除去最大值的小标题及数值

        mTextPaint.setTextSize(mKeyTextSize);
        mTextPaint.setColor(mKeyTextColor);

        for (int i = 0; i < mEdgeNum; i++) {
            if (i == mMaxValueIndex) {
                continue;
            }
            canvas.drawText(mKeys[i], mTextPoints[i].x, mTextPoints[i].y, mTextPaint);
        }

        mTextPaint.setTextSize(mValueTextSize);
        mTextPaint.setColor(mValueTextColor);
        float yOffset = mValueFontMetrics.bottom - mValueFontMetrics.top;
        for (int i = 0; i < mEdgeNum; i++) {
            if (i == mMaxValueIndex) {
                continue;
            }
            String value = String.valueOf(mValues[i]);
            canvas.drawText(value, mTextPoints[i].x, mTextPoints[i].y + yOffset, mTextPaint);
        }


        //绘制最大值的小标题及数值
        mTextPaint.setTextSize(mMaxKeyTextSize);
        mTextPaint.setColor(mMaxKeyTextColor);


        canvas.drawText(mKeys[mMaxValueIndex], mTextPoints[mMaxValueIndex].x, mTextPoints[mMaxValueIndex].y, mTextPaint);

        mTextPaint.setTextSize(mMaxValueTextSize);
        mTextPaint.setColor(mMaxValueTextColor);
        yOffset = mMaxValueFontMetrics.bottom - mMaxValueFontMetrics.top;

        String value = String.valueOf(mValues[mMaxValueIndex]);
        canvas.drawText(value, mTextPoints[mMaxValueIndex].x, mTextPoints[mMaxValueIndex].y + yOffset, mTextPaint);

    }


    /**
     * 绘制背景圆
     */
    private void drawBackgroundCircle(Canvas canvas) {
        mEdgePaint.setPathEffect(null);
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mRadius, mEdgePaint);
        mEdgePaint.setPathEffect(mPathEffect);

        for (int mBackgroundRadiu : mBackgroundRadius) {
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mBackgroundRadiu, mEdgePaint);
        }
    }

    /**
     * 绘制内部分割线
     */
    private void drawDivideLines(Canvas canvas) {
        mEdgePaint.setPathEffect(null);
        for (int i = 0; i < mEdgeNum; i++) {
            canvas.drawLine(mCenterPoint.x, mCenterPoint.y, mOutsideEdgePoints[i].x, mOutsideEdgePoints[i].y, mEdgePaint);
        }
    }


    /**
     * 绘制数据集遮罩层
     */
    private void drawCover(Canvas canvas) {
        canvas.drawPath(mValueEdgePath, mCoverPaint);
    }

    /**
     * 绘制数据集遮罩层边界
     */
    private void drawCoverEdge(Canvas canvas) {
        canvas.drawPath(mValueEdgePath, mCoverEdgePaint);
    }


    public void setEdgeWidth(int edgeWidth) {
        mEdgeWidth = edgeWidth;
        mEdgePaint.setStrokeWidth(mEdgeWidth);
        invalidate();
    }

    public void setCoverEdgeWidth(int coverEdgeWidth) {
        mCoverEdgeWidth = coverEdgeWidth;
        mCoverEdgePaint.setStrokeWidth(mCoverEdgeWidth);
        invalidate();
    }

    public void setPolygonRate(float polygonRate) {
        mPolygonRate = polygonRate;
        initPolygonData();
        invalidate();
    }

    public void setKeyTextSize(int keyTextSize) {
        mKeyTextSize = keyTextSize;
        invalidate();
    }

    public void setValueTextSize(int valueTextSize) {
        mValueTextSize = valueTextSize;
        mTextPaint.setTextSize(mValueTextSize);
        mValueFontMetrics = mTextPaint.getFontMetrics();
        onSizeChanged(mWidth, mHeight, mWidth, mHeight);
        invalidate();
    }

    public void setKeyTextColor(int keyTextColor) {
        mKeyTextColor = keyTextColor;
        invalidate();
    }

    public void setValueTextColor(int valueTextColor) {
        mValueTextColor = valueTextColor;
        invalidate();
    }

    public void setMaxKeyTextSize(int maxKeyTextSize) {
        mMaxKeyTextSize = maxKeyTextSize;
        mTextPaint.setTextSize(mMaxKeyTextSize);
        mMaxKeyFontMetrics = mTextPaint.getFontMetrics();
        onSizeChanged(mWidth, mHeight, mWidth, mHeight);
        invalidate();
    }

    public void setMaxValueTextSize(int maxValueTextSize) {
        mMaxValueTextSize = maxValueTextSize;
        mTextPaint.setTextSize(mMaxValueTextSize);
        mMaxValueFontMetrics = mTextPaint.getFontMetrics();
        onSizeChanged(mWidth, mHeight, mWidth, mHeight);
        invalidate();
    }

    public void setMaxKeyTextColor(int maxKeyTextColor) {
        mMaxKeyTextColor = maxKeyTextColor;
        invalidate();
    }

    public void setMaxValueTextColor(int maxValueTextColor) {
        mMaxValueTextColor = maxValueTextColor;
        invalidate();
    }

    public void setTextGraphMargin(int textGraphMargin) {
        mTextGraphMargin = textGraphMargin;
        onSizeChanged(mWidth, mHeight, mWidth, mHeight);
        invalidate();
    }

    public void setEdgeColor(int edgeColor) {
        mEdgeColor = edgeColor;
        mEdgePaint.setColor(mEdgeColor);
        invalidate();
    }

    public void setCoverEdgeColor(int coverEdgeColor) {
        mCoverEdgeColor = coverEdgeColor;
        mCoverEdgePaint.setColor(mCoverEdgeColor);
        invalidate();
    }

    public void setCoverStartColor(int coverStartColor) {
        mCoverStartColor = coverStartColor;
        initCoverPaintShader();
        invalidate();
    }

    public void setCoverEndColor(int coverEndColor) {
        mCoverEndColor = coverEndColor;
        initCoverPaintShader();
        invalidate();
    }

    public void setCoverAlpha(int coverAlpha) {
        mCoverAlpha = coverAlpha;
        mCoverPaint.setAlpha(mCoverAlpha);
        invalidate();
    }
}
