package com.meitu.lyz.polygonview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.meitu.lyz.polygonview.R;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

/**
 * @author LYZ 2018.04.26
 */
public class PolygonView extends View {

    private Context mContext;

    private int mEdgeNum = 0;
    private String[] mTitles;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.0");
    private LinkedHashMap<String, Float> mData;
    private int mMaxValue = 10, mMinValue = 0;

    private int mWidth, mHeight;

    private int mEdgeWidth, mCoverEdgeWidth;
    private int mEdgeShadowRadius;
    private int mRadius;

    private int mKeyTextColor;
    private int mValueTextColor;
    private int mEdgeColor;
    private int mInsideColor, mMiddleColor, mOutsideColor;
    private int mCoverColor, mCoverEdgeColor;

    private PointF mCenterPoint;
    private PointF[] mOutsideEdgePoints, mMiddleEdgePoints, mInsideEdgePoints, mValueEdgePoints, mTitleTextPoints;
    private Path mOutsidePath, mMiddlePath, mInsidePath, mEdgePath, mValueEdgePath, mEdgeShadowPath;

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
        mInsideColor = ContextCompat.getColor(mContext, R.color.polygon_view_inside_color);
        mMiddleColor = ContextCompat.getColor(mContext, R.color.polygon_view_middle_color);
        mOutsideColor = ContextCompat.getColor(mContext, R.color.polygon_view_outside_color);
        mCoverColor = ContextCompat.getColor(mContext, R.color.polygon_view_cover_color);
        mCoverEdgeColor = ContextCompat.getColor(mContext, R.color.polygon_view_cover_edge_color);

        mEdgeWidth = mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_edge_width);
        mCoverEdgeWidth = mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_cover_edge_width);
        mEdgeShadowRadius = mContext.getResources().getDimensionPixelOffset(R.dimen.polygon_view_edge_shadow_radius);

    }

    private void initPaint() {
        mCoverPaint.setColor(mCoverColor);
        mCoverEdgePaint.setColor(mCoverEdgeColor);
        mEdgePaint.setColor(mEdgeColor);
        mEdgeShadowPaint.setColor(mEdgeColor);

        mEdgePaint.setStyle(Paint.Style.STROKE);
        mCoverEdgePaint.setStyle(Paint.Style.STROKE);
        mEdgeShadowPaint.setStyle(Paint.Style.STROKE);

        mEdgePaint.setStrokeWidth(mEdgeWidth);
        mEdgeShadowPaint.setStrokeWidth(mEdgeWidth);
        mCoverEdgePaint.setStrokeWidth(mCoverEdgeWidth);

        mEdgePaint.setStrokeJoin(Paint.Join.ROUND);
        mCoverEdgePaint.setStrokeJoin(Paint.Join.ROUND);

        mEdgeShadowPaint.setShadowLayer(mEdgeShadowRadius, 0, 0, mCoverEdgeColor);
    }

    public void bindData(LinkedHashMap<String, Float> data) {
        if (data != null && data.size() >= 3) {
            mEdgeNum = data.size();
            mTitles = new String[mEdgeNum];
            data.keySet().toArray(mTitles);
            mData = data;

            if (mCenterPoint != null)
                initValueData();
        }
    }

    private void initBackgroundData() {
        initEdgePoints();
        initPath();
    }

    private void initValueData() {
        initValueEdgePoints();
        initValuePath();
    }

    private void initEdgePoints() {
        mOutsideEdgePoints = new PointF[mEdgeNum];
        mMiddleEdgePoints = new PointF[mEdgeNum];
        mInsideEdgePoints = new PointF[mEdgeNum];
        mTitleTextPoints = new PointF[mEdgeNum];

        double degree = 2 * Math.PI / mEdgeNum;

        for (int i = 0; i < mEdgeNum; i++) {
            mOutsideEdgePoints[i] = new PointF((float) (mCenterPoint.x + mRadius * Math.sin(degree * i)), (float) (mCenterPoint.y - mRadius * Math.cos(degree * i)));
            mMiddleEdgePoints[i] = new PointF(((float) (mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * 0.6)),
                    (float) (mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * 0.6));
            mInsideEdgePoints[i] = new PointF(((float) (mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * 0.3)),
                    (float) (mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * 0.3));
            mTitleTextPoints[i] = new PointF(((float) (mCenterPoint.x - (mCenterPoint.x - mOutsideEdgePoints[i].x) * 1.25)),
                    (float) (mCenterPoint.y - (mCenterPoint.y - mOutsideEdgePoints[i].y) * 1.25));
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

//        mEdgePath.moveTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);
        mOutsidePath.moveTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);
        mEdgeShadowPath.moveTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);
        for (int i = 1; i < mEdgeNum; i++) {
            mOutsidePath.lineTo(mOutsideEdgePoints[i].x, mOutsideEdgePoints[i].y);
//            mEdgePath.lineTo(mOutsideEdgePoints[i].x, mOutsideEdgePoints[i].y);
            mEdgeShadowPath.lineTo(mOutsideEdgePoints[i].x, mOutsideEdgePoints[i].y);
        }
        mOutsidePath.lineTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);
        mEdgeShadowPath.lineTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);

//        mEdgePath.lineTo(mOutsideEdgePoints[0].x, mOutsideEdgePoints[0].y);
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
            float proportion = getValueProportion(mData.get(mTitles[i]));
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
        mRadius = w / 2;
        mCenterPoint = new PointF(mRadius, mRadius);
        mRadius *= 0.8;

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
    }


    private void drawText(Canvas canvas) {

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
