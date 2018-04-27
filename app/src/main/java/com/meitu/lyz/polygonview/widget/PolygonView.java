package com.meitu.lyz.polygonview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.meitu.lyz.polygonview.R;

import java.util.LinkedHashMap;

/**
 * 多边形View,可根据数据集绘制任意多边形
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
    private LinkedHashMap<String, Float> mData;

    //最大最小数值 默认[0,10]
    private float mMaxValue, mMinValue;
    private static final int DEFAULT_MAX_VALUE = 10;
    private static final int DEFAULT_MIN_VALUE = 0;

    //内中外的比例 默认3:3:4
    private int mInsideWeight;
    private int mMiddleWight;
    private int mOutsideWeight;
    private static final int DEFAULT_INSIDE_WEIGHT = 3;
    private static final int DEFAULT_MIDDLE_WEIGHT = 3;
    private static final int DEFAULT_OUTSIDE_WEIGHT = 4;

    //View实际的宽高
    private int mWidth, mHeight;

    //边界及分隔线宽度
    private int mEdgeWidth;
    //数据集遮罩层边界线宽度
    private int mCoverEdgeWidth;
    //边界阴影的半径
    private int mEdgeShadowRadius;

    //多边形的半径
    private int mRadius;

    //小标题和数据的文字大小，颜色
    private int mKeyTextSize;
    private int mValueTextSize;
    private int mKeyTextColor;
    private int mValueTextColor;

    //用于获取文字高度的FontMetrics
    private Paint.FontMetrics mKeyFontMetrics, mValueFontMetrics;
    //文字和多边形的间距
    private int mTextGraphMargin;

    //边界及分割线的颜色
    private int mEdgeColor;
    //边界阴影的颜色
    private int mEdgeShadowColor;
    //内圈的颜色
    private int mInsideColor;
    //中圈的颜色
    private int mMiddleColor;
    //外圈的颜色
    private int mOutsideColor;
    //数据集遮罩层的颜色
    private int mCoverColor;
    //数据集遮罩层边界的颜色
    private int mCoverEdgeColor;

    //多边形中心点
    private PointF mCenterPoint;

    //内中外圈的点集
    private PointF[] mOutsideEdgePoints;
    private PointF[] mMiddleEdgePoints;
    private PointF[] mInsideEdgePoints;

    //数据集的点集
    private PointF[] mValueEdgePoints;
    //文字中心点集
    private PointF[] mTextPoints;


    //内中外圈绘制路径
    private Path mOutsidePath;
    private Path mMiddlePath;
    private Path mInsidePath;

    //边界及分割线绘制路径
    private Path mEdgePath;
    //数据集绘制路径
    private Path mValueEdgePath;
    //边界阴影绘制路径
    private Path mEdgeShadowPath;

    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mEdgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mEdgeShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
        //关闭硬件加速，以绘制阴影
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.PolygonView);
        mKeyTextColor = typedArray.getColor(R.styleable.PolygonView_key_text_color, ContextCompat.getColor(mContext, R.color.polygon_view_key_text_color));
        mValueTextColor = typedArray.getColor(R.styleable.PolygonView_value_text_color, ContextCompat.getColor(mContext, R.color.polygon_view_value_text_color));
        mEdgeColor = typedArray.getColor(R.styleable.PolygonView_edge_color, ContextCompat.getColor(mContext, R.color.polygon_view_edge_color));
        mEdgeShadowColor = typedArray.getColor(R.styleable.PolygonView_edge_shadow_color, ContextCompat.getColor(mContext, R.color.polygon_view_edge_shadow_color));
        mInsideColor = typedArray.getColor(R.styleable.PolygonView_inside_color, ContextCompat.getColor(mContext, R.color.polygon_view_inside_color));
        mMiddleColor = typedArray.getColor(R.styleable.PolygonView_middle_color, ContextCompat.getColor(mContext, R.color.polygon_view_middle_color));
        mOutsideColor = typedArray.getColor(R.styleable.PolygonView_outside_color, ContextCompat.getColor(mContext, R.color.polygon_view_outside_color));
        mCoverColor = typedArray.getColor(R.styleable.PolygonView_cover_color, ContextCompat.getColor(mContext, R.color.polygon_view_cover_color));
        mCoverEdgeColor = typedArray.getColor(R.styleable.PolygonView_cover_edge_color, ContextCompat.getColor(mContext, R.color.polygon_view_cover_edge_color));


        mKeyTextSize = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_key_text_size, mContext.getResources().getDimensionPixelSize(R.dimen.polygon_view_key_text_size));
        mValueTextSize = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_value_text_size, mContext.getResources().getDimensionPixelSize(R.dimen.polygon_view_value_text_size));

        mEdgeWidth = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_edge_width, mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_edge_width));
        mCoverEdgeWidth = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_cover_edge_width, mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_cover_edge_width));
        mTextGraphMargin = typedArray.getDimensionPixelOffset(R.styleable.PolygonView_text_graph_margin, mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_text_graph_margin));

        mInsideWeight = typedArray.getInteger(R.styleable.PolygonView_inside_weight, DEFAULT_INSIDE_WEIGHT);
        mMiddleWight = typedArray.getInteger(R.styleable.PolygonView_middle_weight, DEFAULT_MIDDLE_WEIGHT);
        mOutsideWeight = typedArray.getInteger(R.styleable.PolygonView_outside_weight, DEFAULT_OUTSIDE_WEIGHT);

        mMaxValue = typedArray.getFloat(R.styleable.PolygonView_max_value, DEFAULT_MAX_VALUE);
        mMinValue = typedArray.getFloat(R.styleable.PolygonView_min_value, DEFAULT_MIN_VALUE);

        typedArray.recycle();

    }

    /**
     * 初始化paint
     */
    private void initPaint() {
        mCoverPaint.setColor(mCoverColor);
        mCoverEdgePaint.setColor(mCoverEdgeColor);
        mEdgePaint.setColor(mEdgeColor);
        mEdgeShadowPaint.setColor(mEdgeShadowColor);

        mEdgePaint.setStyle(Paint.Style.STROKE);
        mCoverEdgePaint.setStyle(Paint.Style.STROKE);
        mEdgeShadowPaint.setStyle(Paint.Style.FILL);

        mEdgePaint.setStrokeWidth(mEdgeWidth);
        mEdgeShadowPaint.setStrokeWidth(mEdgeWidth);
        mCoverEdgePaint.setStrokeWidth(mCoverEdgeWidth);

        mEdgePaint.setStrokeJoin(Paint.Join.ROUND);
        mCoverEdgePaint.setStrokeJoin(Paint.Join.ROUND);

        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        //获取FontMetrics
        mTextPaint.setTextSize(mKeyTextSize);
        mKeyFontMetrics = mTextPaint.getFontMetrics();
        mTextPaint.setTextSize(mValueTextSize);
        mValueFontMetrics = mTextPaint.getFontMetrics();
    }

    /**
     * 绑定数据集
     *
     * @param data String,Float键值对  size>=3
     * @return 是否绑定成功
     */
    public boolean bindData(LinkedHashMap<String, Float> data) {
        if (data != null && data.size() >= 3) {
            mEdgeNum = data.size();
            mKeys = new String[mEdgeNum];
            data.keySet().toArray(mKeys);
            mData = data;

            if (mCenterPoint != null)
                initValueData();
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
        initBackgroundPath();
    }

    /**
     * 计算数据的点及绘制路径
     */
    private void initValueData() {
        initValueEdgePoints();
        initValuePath();
    }


    /**
     * 计算背景的点
     */
    private void initBackgroundPoints() {
        mOutsideEdgePoints = new PointF[mEdgeNum];
        mMiddleEdgePoints = new PointF[mEdgeNum];
        mInsideEdgePoints = new PointF[mEdgeNum];
        mTextPoints = new PointF[mEdgeNum];

        //计算中心角的弧度
        double degree = 2 * Math.PI / mEdgeNum;

        //根据权重计算比例
        int totalWeight = mInsideWeight + mMiddleWight + mOutsideWeight;
        float insideRate = mInsideWeight * 1f / totalWeight;
        float middleRate = (mMiddleWight + mInsideWeight) * 1f / totalWeight;
        float textRate = 1 + (mTextGraphMargin + mValueFontMetrics.bottom - mValueFontMetrics.top) / mRadius;

        for (int i = 0; i < mEdgeNum; i++) {
            mOutsideEdgePoints[i] = new PointF((float) (mCenterPoint.x + mRadius * Math.sin(degree * i)), (float) (mCenterPoint.y - mRadius * Math.cos(degree * i)));
            mMiddleEdgePoints[i] = new PointF(mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * middleRate,
                    mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * middleRate);
            mInsideEdgePoints[i] = new PointF(mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * insideRate,
                    mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * insideRate);
            mTextPoints[i] = new PointF(mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * textRate,
                    mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * textRate);
        }
    }


    /**
     * 计算背景的绘制路径
     */
    private void initBackgroundPath() {
        mInsidePath = new Path();
        mOutsidePath = new Path();
        mEdgePath = new Path();
        mEdgeShadowPath = new Path();

        mInsidePath.moveTo(mInsideEdgePoints[0].x, mInsideEdgePoints[0].y);
        for (int i = 1; i < mEdgeNum; i++) {
            mInsidePath.lineTo(mInsideEdgePoints[i].x, mInsideEdgePoints[i].y);
        }
        mInsidePath.lineTo(mInsideEdgePoints[0].x, mInsideEdgePoints[0].y);

        mMiddlePath = new Path(mInsidePath);
        mMiddlePath.moveTo(mMiddleEdgePoints[0].x, mMiddleEdgePoints[0].y);
        mOutsidePath.moveTo(mMiddleEdgePoints[0].x, mMiddleEdgePoints[0].y);
        for (int i = 1; i < mEdgeNum; i++) {
            mMiddlePath.lineTo(mMiddleEdgePoints[i].x, mMiddleEdgePoints[i].y);
            mOutsidePath.lineTo(mMiddleEdgePoints[i].x, mMiddleEdgePoints[i].y);
        }
        mMiddlePath.lineTo(mMiddleEdgePoints[0].x, mMiddleEdgePoints[0].y);
        mOutsidePath.lineTo(mMiddleEdgePoints[0].x, mMiddleEdgePoints[0].y);

        mEdgePath.moveTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);
        mOutsidePath.moveTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);
        mEdgeShadowPath.moveTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);
        for (int i = 1; i < mEdgeNum; i++) {
            mOutsidePath.lineTo(mOutsideEdgePoints[i].x, mOutsideEdgePoints[i].y);
            mEdgePath.lineTo(mOutsideEdgePoints[i].x, mOutsideEdgePoints[i].y);
            mEdgeShadowPath.lineTo(mOutsideEdgePoints[i].x, mOutsideEdgePoints[i].y);
        }
        mOutsidePath.lineTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);
        mEdgeShadowPath.lineTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);

        mEdgePath.lineTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);
        for (int i = 0; i < mEdgeNum; i++) {
            mEdgePath.moveTo(mCenterPoint.x, mCenterPoint.y);
            mEdgePath.lineTo(mOutsideEdgePoints[i].x, mOutsideEdgePoints[i].y);
        }


        //设置Path的FillType为EVEN_ODD
        mMiddlePath.setFillType(Path.FillType.EVEN_ODD);
        mOutsidePath.setFillType(Path.FillType.EVEN_ODD);

    }

    /**
     * 计算数据的点
     */
    private void initValueEdgePoints() {
        mValueEdgePoints = new PointF[mEdgeNum];

        //计算偏移值，防止边线超出多边形边界
        float offset = mCoverEdgeWidth / 2f / mRadius;

        for (int i = 0; i < mEdgeNum; i++) {
            float proportion = getValueProportion(mData.get(mKeys[i])) - offset;
            proportion = Math.max(0, proportion);
            mValueEdgePoints[i] = new PointF(mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * proportion,
                    mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * proportion);
        }
    }


    /**
     * 计算数据的绘制路径
     */
    private void initValuePath() {
        mValueEdgePath = new Path();
        mValueEdgePath.moveTo(mValueEdgePoints[0].x, mValueEdgePoints[0].y);
        for (int i = 1; i < mEdgeNum; i++) {
            mValueEdgePath.lineTo(mValueEdgePoints[i].x, mValueEdgePoints[i].y);
        }

        mValueEdgePath.close();
    }

    /**
     * 获取数据的比例值
     */
    private float getValueProportion(float value) {
        if (value <= mMinValue)
            return 0;
        else if (value >= mMaxValue)
            return 1;
        else
            return (value - mMinValue) / (mMaxValue - mMinValue);
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

        //实际半径为减去文字高度和文图间距的值
        mRadius = (int) (mRadius - (mValueFontMetrics.bottom - mValueFontMetrics.top) * 1.5 - (mKeyFontMetrics.bottom - mKeyFontMetrics.top) * 1.5 - mTextGraphMargin);


        //计算阴影半径
        mEdgeShadowRadius = mRadius / 9;
        // 设置阴影模糊效果
        mEdgeShadowPaint.setMaskFilter(new BlurMaskFilter(mEdgeShadowRadius, BlurMaskFilter.Blur.NORMAL));

        initBackgroundData();
        initValueData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mEdgeNum == 0)
            return;

        //自底向上依次绘制
        drawEdgeShadow(canvas);
        drawBackground(canvas);
        drawEdge(canvas);
        drawCover(canvas);
        drawCoverEdge(canvas);
        drawText(canvas);
    }


    /**
     * 绘制小标题及数值文字
     */
    private void drawText(Canvas canvas) {
        mTextPaint.setTextSize(mKeyTextSize);
        mTextPaint.setColor(mKeyTextColor);

        for (int i = 0; i < mEdgeNum; i++) {
            canvas.drawText(mKeys[i], mTextPoints[i].x, mTextPoints[i].y, mTextPaint);
        }

        mTextPaint.setTextSize(mValueTextSize);
        mTextPaint.setColor(mValueTextColor);
        float yOffset = mValueFontMetrics.bottom - mValueFontMetrics.top;
        for (int i = 0; i < mEdgeNum; i++) {
            String value = String.valueOf(mData.get(mKeys[i]));
            canvas.drawText(value, mTextPoints[i].x, mTextPoints[i].y + yOffset, mTextPaint);
        }
    }

    /**
     * 绘制内中外圈背景
     */
    private void drawBackground(Canvas canvas) {
        mBackgroundPaint.setColor(mInsideColor);
        canvas.drawPath(mInsidePath, mBackgroundPaint);

        mBackgroundPaint.setColor(mMiddleColor);
        canvas.drawPath(mMiddlePath, mBackgroundPaint);

        mBackgroundPaint.setColor(mOutsideColor);
        canvas.drawPath(mOutsidePath, mBackgroundPaint);
    }

    /**
     * 绘制边界及分割线
     */
    private void drawEdge(Canvas canvas) {
        canvas.drawPath(mEdgePath, mEdgePaint);
    }

    /**
     * 绘制边界阴影
     */
    private void drawEdgeShadow(Canvas canvas) {
        canvas.translate(0, mEdgeShadowRadius / 2);
        canvas.drawPath(mEdgeShadowPath, mEdgeShadowPaint);
        canvas.translate(0, -mEdgeShadowRadius / 2);
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


}
