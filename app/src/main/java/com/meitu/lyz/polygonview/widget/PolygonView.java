package com.meitu.lyz.polygonview.widget;

import android.content.Context;
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
 * @author LYZ 2018.04.26
 */
public class PolygonView extends View {

    private Context mContext;

    //边数
    private int mEdgeNum = 0;


    private String[] mKeys;
    private LinkedHashMap<String, Float> mData;

    private float mMaxValue = 10, mMinValue = 0;

    //内中外的比例
    private int mInsideWeight = 3;
    private int mMiddleWight = 3;
    private int mOutsideWeight = 4;

    //View实际的宽高
    private int mWidth, mHeight;

    //分隔线和中间覆盖边界线的宽度
    private int mEdgeWidth, mCoverEdgeWidth;
    private int mEdgeShadowRadius;

    //多边形的半径
    private int mRadius;

    private int mKeyTextSize;
    private int mValueTextSize;
    private int mKeyTextColor;
    private int mValueTextColor;
    private Paint.FontMetrics mKeyFontMetrics, mValueFontMetrics;
    private int mTextGraphMargin;

    private int mEdgeColor, mEdgeShadowColor;
    private int mInsideColor, mMiddleColor, mOutsideColor;
    private int mCoverColor, mCoverEdgeColor;

    private PointF mCenterPoint;
    private PointF[] mOutsideEdgePoints;
    private PointF[] mMiddleEdgePoints;
    private PointF[] mInsideEdgePoints;
    private PointF[] mValueEdgePoints;
    private PointF[] mTextPoints;


    private Path mOutsidePath;
    private Path mMiddlePath;
    private Path mInsidePath;
    private Path mEdgePath;
    private Path mValueEdgePath;
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
        initAttr();
        initPaint();
    }

    private void initAttr() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mKeyTextColor = ContextCompat.getColor(mContext, R.color.polygon_view_key_text_color);
        mValueTextColor = ContextCompat.getColor(mContext, R.color.polygon_view_value_text_color);
        mEdgeColor = ContextCompat.getColor(mContext, R.color.polygon_view_edge_color);
        mEdgeShadowColor = ContextCompat.getColor(mContext, R.color.polygon_view_edge_shadow_color);
        mInsideColor = ContextCompat.getColor(mContext, R.color.polygon_view_inside_color);
        mMiddleColor = ContextCompat.getColor(mContext, R.color.polygon_view_middle_color);
        mOutsideColor = ContextCompat.getColor(mContext, R.color.polygon_view_outside_color);
        mCoverColor = ContextCompat.getColor(mContext, R.color.polygon_view_cover_color);
        mCoverEdgeColor = ContextCompat.getColor(mContext, R.color.polygon_view_cover_edge_color);

        mKeyTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.polygon_view_key_text_size);
        mValueTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.polygon_view_value_text_size);

        mEdgeWidth = mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_edge_width);
        mCoverEdgeWidth = mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_cover_edge_width);
        mEdgeShadowRadius = mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_edge_shadow_radius);
        mTextGraphMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_text_graph_margin);

    }

    private void initPaint() {
        mCoverPaint.setColor(mCoverColor);
        mCoverEdgePaint.setColor(mCoverEdgeColor);
        mEdgePaint.setColor(mEdgeColor);
        mEdgeShadowPaint.setColor(mCoverEdgeColor);

        mEdgePaint.setStyle(Paint.Style.STROKE);
        mCoverEdgePaint.setStyle(Paint.Style.STROKE);
        mEdgeShadowPaint.setStyle(Paint.Style.STROKE);

        mEdgePaint.setStrokeWidth(mEdgeWidth);
        mEdgeShadowPaint.setStrokeWidth(mEdgeWidth);
        mCoverEdgePaint.setStrokeWidth(mCoverEdgeWidth);

        mEdgePaint.setStrokeJoin(Paint.Join.ROUND);
        mCoverEdgePaint.setStrokeJoin(Paint.Join.ROUND);

        mEdgeShadowPaint.setShadowLayer(mEdgeShadowRadius, 0, mEdgeShadowRadius / 2, mCoverEdgeColor);

        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mKeyTextSize);
        mKeyFontMetrics = mTextPaint.getFontMetrics();
        mTextPaint.setTextSize(mValueTextSize);
        mValueFontMetrics = mTextPaint.getFontMetrics();
    }

    public void bindData(LinkedHashMap<String, Float> data) {
        if (data != null && data.size() >= 3) {
            mEdgeNum = data.size();
            mKeys = new String[mEdgeNum];
            data.keySet().toArray(mKeys);
            mData = data;

            if (mCenterPoint != null)
                initValueData();
            invalidate();
        }
    }


    /**
     * 计算背景的点及绘制路径
     */
    private void initBackgroundData() {
        initEdgePoints();
        initPath();
    }

    /**
     * 计算数据的点及绘制路径
     */
    private void initValueData() {
        initValueEdgePoints();
        initValuePath();
    }

    private void initEdgePoints() {
        mOutsideEdgePoints = new PointF[mEdgeNum];
        mMiddleEdgePoints = new PointF[mEdgeNum];
        mInsideEdgePoints = new PointF[mEdgeNum];
        mTextPoints = new PointF[mEdgeNum];

        double degree = 2 * Math.PI / mEdgeNum;

        int totalWeight = mInsideWeight + mMiddleWight + mOutsideWeight;
        float insideRate = mInsideWeight * 1f / totalWeight;
        float middleRate = (mMiddleWight + mInsideWeight) * 1f / totalWeight;
        float textRate = 1 + (mTextGraphMargin + mValueFontMetrics.bottom - mValueFontMetrics.top) / mRadius;

        for (int i = 0; i < mEdgeNum; i++) {
            mOutsideEdgePoints[i] = new PointF((float) (mCenterPoint.x + mRadius * Math.sin(degree * i)), (float) (mCenterPoint.y - mRadius * Math.cos(degree * i)));
            mMiddleEdgePoints[i] = new PointF(((float) (mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * middleRate)),
                    (float) (mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * middleRate));
            mInsideEdgePoints[i] = new PointF(((float) (mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * insideRate)),
                    (float) (mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * insideRate));
            mTextPoints[i] = new PointF(((float) (mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * textRate)),
                    (float) (mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * textRate));
        }
    }


    private void initPath() {
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

        mMiddlePath.setFillType(Path.FillType.EVEN_ODD);
        mOutsidePath.setFillType(Path.FillType.EVEN_ODD);

    }

    private void initValueEdgePoints() {
        mValueEdgePoints = new PointF[mEdgeNum];

        for (int i = 0; i < mEdgeNum; i++) {
            float proportion = getValueProportion(mData.get(mKeys[i]));
            mValueEdgePoints[i] = new PointF(((float) (mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * proportion)),
                    (float) (mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * proportion));
        }
    }

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRadius = w / 2;
        mCenterPoint = new PointF(mRadius, mRadius);
        mRadius = (int) (mRadius - (mValueFontMetrics.bottom - mValueFontMetrics.top) - (mKeyFontMetrics.bottom - mKeyFontMetrics.top) - mTextGraphMargin);

        initBackgroundData();
        initValueData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mEdgeNum == 0)
            return;

        drawEdgeShadow(canvas);
        drawBackground(canvas);
        drawEdge(canvas);
        drawCover(canvas);
        drawCoverEdge(canvas);
        drawText(canvas);
    }


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


    private void drawBackground(Canvas canvas) {
        mBackgroundPaint.setColor(mInsideColor);
        canvas.drawPath(mInsidePath, mBackgroundPaint);

        mBackgroundPaint.setColor(mMiddleColor);
        canvas.drawPath(mMiddlePath, mBackgroundPaint);

        mBackgroundPaint.setColor(mOutsideColor);
        canvas.drawPath(mOutsidePath, mBackgroundPaint);
    }

    private void drawEdge(Canvas canvas) {
        canvas.drawPath(mEdgePath, mEdgePaint);
    }

    private void drawEdgeShadow(Canvas canvas) {
        canvas.drawPath(mEdgeShadowPath, mEdgeShadowPaint);
    }

    private void drawCover(Canvas canvas) {
        canvas.drawPath(mValueEdgePath, mCoverPaint);
    }

    private void drawCoverEdge(Canvas canvas) {
        canvas.drawPath(mValueEdgePath, mCoverEdgePaint);
    }


}
