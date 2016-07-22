package my_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.zbc.diy_view.R;

import pxutils.PxUtils;

/**
 * Created by zbc on 16/7/19.
 */
public class MyView01 extends View {

    //View的宽高
    private int mWidth;
    private int mHeight;

    //数据所占百分比
    private int mPercent = 0;
    //数据饱和值
    private static final int DATA_MAX = 50000;

    //刻度宽度
    private float mTikeWidth;

    //第一个弧的宽度
    private int mOutsideArcWidth;
    //第二个弧的宽度
    private int mInsideArcWidth;
    //设置两个弧线之间的距离
    private int mDistanceInArc;

    //最小圆的半径
    private int mMinCircleRadius = 15;

    //文字矩形的宽
    private int mRectWidth;
    //文字矩形的高
    private int mRectHeight;


    //文字内容
    private String mText;
    //文字的大小
    private int mTextSize;
    //设置文字颜色
    private int mTextColor;

    //设置弧线填充的颜色
    private int mArcFilledColor;
    //设置弧线未填充的颜色
    private int mArcEmptyColor;

    //小圆和指针颜色
    private int mMinCircleColor;
    private int mPointerColor;

    //刻度的个数
    private int mTikeCount;

    private Context mContext;


    /**
     * 最普通的，在代码中新建一个View所用到的方法
     * eg：MyView01 myView01=new MyView01(this);
     *
     * @param context
     */
    public MyView01(Context context) {
        this(context, null);
    }

    /**
     * 在XML文件里添加一个View，并设置其宽高、偏移量等属性
     * 这些属性会存放在AttributeSet参数里
     *
     * @param context
     * @param attrs
     */
    public MyView01(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 这个构造方法比上一个多了一个Int型参数defStyleAttr,这是一个关于自定义属性的参数
     * 此构造方法不会被系统默认调用，需要自身显示调用
     * eg：在第二个构造方法里调用第三个构造方法，defStyleAttr参数设置为0
     *
     * @param context
     * @param attrs
     * @param defStyleAttr defStyleAttr指定的是在Theme Style里定义的一个Attr，他的类型是reference，主要生效在obtainStyledAttributes方法里
     *                     obtainStyledAttributes有四个参数，第三个参数是defStykeAttr，第四个参数是自己指定的一个style
     *                     当且仅当defStyleAttr为0或者在Theme中找不到defStyleAttr指定的属性时，第四个参数才会生效
     *                     这些都是默认属性
     *                     <p/>
     *                     若在xml文件里定义了，就以xml文件指定的为准，xml优先级最高
     *                     优先级xml>style>defStyleAttr>defStyleRes>Theme
     *                     <p/>
     *                     当defStyleAttr为0时，就跳过defStyleAttr指定的reference
     *                     所以一般用0即可
     */

    public MyView01(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;//确定上下文环境

        //与attrs.xml文件对应
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.MyView01, defStyleAttr, R.style.AppTheme);//他的代码这最后一个参数写的0

        //对应弧线填充颜色   未填充颜色
        mArcFilledColor = typedArray.getColor(R.styleable.MyView01_arcFilledColor, Color.parseColor("#5FB1ED"));
        mArcEmptyColor = typedArray.getColor(R.styleable.MyView01_arcEmptyColor, Color.parseColor("#CCCCCC"));

        //对应刻度数目
        mTikeCount = typedArray.getInt(R.styleable.MyView01_tikeCount, 12);

        //文字大小和内容
        mTextSize = typedArray.getDimensionPixelSize(PxUtils.spToPx(R.styleable.MyView01_android_textSize, mContext), 24);
        mText = typedArray.getString(R.styleable.MyView01_android_text);

        //设置外侧  内侧弧线的宽度
        mInsideArcWidth = typedArray.getDimensionPixelSize(PxUtils.dpToPx(R.styleable.MyView01_arcInsideWidth, mContext), 40);
        mOutsideArcWidth = typedArray.getDimensionPixelSize(PxUtils.dpToPx(R.styleable.MyView01_arcOutsideWidth, mContext), 3);
        //设置内外侧弧线之间的距离
        mDistanceInArc = typedArray.getDimensionPixelSize(PxUtils.dpToPx(R.styleable.MyView01_arcDistance, mContext), 20);

        //小圆和指针颜色设置  初始值和填充颜色一致,可更改
        mMinCircleColor = typedArray.getColor(R.styleable.MyView01_minCircleColor, mArcFilledColor);
        mPointerColor = typedArray.getColor(R.styleable.MyView01_pointerColor, mArcFilledColor);
        mMinCircleRadius = typedArray.getDimensionPixelSize(PxUtils.dpToPx(R.styleable.MyView01_minCircleRadius, mContext), 15);

        //文字的长方形框
        mRectHeight = typedArray.getDimensionPixelSize(PxUtils.dpToPx(R.styleable.MyView01_android_textSize + 2, mContext), 24);

    }


    /**
     * 控件的绘制流程是：
     * Measure--》Layout--》Draw
     * Measure可以知道View要占据的界面的大小
     * onLayout可以知道控件所放置的位置
     * onDraw将控件绘制出来，然后才能在用户前展示
     */

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec int占4个字节，一共32bit
     *                          在MeasureSpace中，前两个bit表示mode，后30bit表示size
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取mode和size
        int widethMode = MeasureSpec.getMode(widthMeasureSpec);
        int widethSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        /**
         * MeasureSpec的模式有三种  EXACTLY， AT_MOST， UNSPECIFIED暂时不谈第三种
         * 当父布局是EXACTLY时，子控件确定大小或者match_parent，mode都是EXACTLY，子控件是wrap_content时，mode为AT_MOST
         *
         * 当父布局是AT_MOST时，子控件确定大小，mode为EXACTLY；子控件为wrap_content或match_parent时，mode为AT_MOST
         *
         * 所以
         *
         * 在确定控件大小时，需要判断MeasureSpec的size.
         *
         * 在进行一些逻辑处理后，调用setMeasureDimension()方法，将测量得到的宽高穿进去，供layout使用
         */

        if (widethMode == MeasureSpec.EXACTLY) {
            mWidth = widethSize;
        } else {
            mWidth = PxUtils.dpToPx(200, mContext);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = PxUtils.dpToPx(200, mContext);
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    //单纯的自定义View，而非View容器的话，是不需要重写onLayout的

    /**
     * 注意！！！！
     * <p/>
     * 子View的Margin属性是否生效就要看parent是否在自身的onLayout方法进行处理
     * 而View的padding属性，是在onDraw方法中生效的
     */

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        /**
         * 先画最外层细线弧形
         */
        paint.setColor(mArcFilledColor);//设置画笔颜色
        paint.setStrokeWidth(mOutsideArcWidth);//设置画笔宽度
        paint.setAntiAlias(true);//设置非锯齿轮廓
        paint.setStyle(Paint.Style.STROKE);//设置类型--画笔
        canvas.drawArc(
                new RectF(mOutsideArcWidth, mOutsideArcWidth, mWidth - mOutsideArcWidth, mHeight - mOutsideArcWidth),
                145,
                250,
                false,
                paint);
        /**
         * drawArc的五个参数解析
         * 1 RectF是弧线所在的矩形范围
         * 2 起始角度,单位是度
         * 3 扫过角度,单位是度
         * 4 是否包含圆心  若true 则画出扇形
         * 5 画笔对象
         */


        /**
         * 画粗线弧形
         *
         * 画笔的颜色不变,宽度要变
         * 所在矩形区域要变
         */
        paint.setStrokeWidth(mInsideArcWidth);
        RectF mSecondRectF = new RectF(
                mInsideArcWidth + mDistanceInArc,
                mInsideArcWidth + mDistanceInArc,
                mWidth - mInsideArcWidth - mDistanceInArc,
                mHeight - mInsideArcWidth - mDistanceInArc);
        /**
         * 要分成两部分,一部分是扫过的,颜色填充,另一部分是空白
         *
         * 和百分比就有关系了
         *
         * 顺带把指针画了(结合后面旋转画布的知识 画指针也旋转画布,比较方便)
         */

        float percent = mPercent / 100f;
        float fillArea = 250 * percent;//这是填充满的区域要扫过的角度
        if (mPercent!=0){
            canvas.drawArc(mSecondRectF, 145, fillArea+0, false, paint);
        }



        float emptyAreaStartPosition = 145 + fillArea;//空白区域起始角度
        float emptyArea = 250 - fillArea;//空白区域扫过角度
        paint.setColor(mArcEmptyColor);//画笔设置成空白区域的颜色
        canvas.drawArc(mSecondRectF, emptyAreaStartPosition, emptyArea, false, paint);
        //粗弧线已经完成,现在画指针
        canvas.rotate(-(125 - fillArea), mWidth / 2, mHeight / 2);
        paint.setColor(mPointerColor);
        paint.setStrokeWidth(3);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight / 2, paint);
        canvas.rotate(125 - fillArea, mWidth / 2, mHeight / 2);

        /**
         * 画中间小圆,画笔宽度5,半径15
         */
        paint.setColor(mMinCircleColor);
        paint.setStrokeWidth(5);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mMinCircleRadius, paint);
        /**
         * 接下来画刻度值
         * 刻度需要旋转画布
         * 绘制第一段刻度，
         * 然后总共是250的弧度 计算出每个刻度的度数
         * 用250除以刻度数mTikeCount,就是每次旋转的度数。
         *
         * 接下来把画布逐步旋转，按照原坐标绘制，即可绘制出右半部分刻度。
         *
         * 注意：为了让之后的绘制正常，务必把画布转回原来的位置
         */
        paint.setColor(mArcFilledColor);
        paint.setStrokeWidth(3);
        mTikeWidth = 20;
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mTikeWidth, paint);
        float rAngle = 250f / mTikeCount;
        for (int i = 0; i < mTikeCount / 2; i++) {
            canvas.rotate(rAngle, mWidth / 2, mHeight / 2);//后两个参数是坐标,即基于这个点做旋转
            canvas.drawLine(mWidth / 2, 0, mWidth / 2, mTikeWidth, paint);
        }
        canvas.rotate(-rAngle * mTikeCount / 2, mWidth / 2, mHeight / 2);
        /**
         * 右半边已经画完,现在画左半边
         * 差别只是角度的正负号
         */
        for (int i = 0; i < mTikeCount / 2; i++) {
            canvas.rotate(-rAngle, mWidth / 2, mHeight / 2);//后两个参数是坐标,即基于这个点做旋转
            canvas.drawLine(mWidth / 2, 0, mWidth / 2, mTikeWidth, paint);
        }
        canvas.rotate(rAngle * mTikeCount / 2, mWidth / 2, mHeight / 2);
        /**
         * 画中间数据区域,显示当前数值
         */
        mText = "实时功率:" + String.valueOf(DATA_MAX * mPercent / 100) + "kW";
        float textLength = paint.measureText(mText);
        canvas.drawText(mText, (mWidth - textLength) / 2, mHeight / 4 * 3, paint);
    }

    public void setmPercent(int mPercent) {
        this.mPercent = mPercent;
        invalidate();
    }

    public void setmText(String mText) {
        this.mText = mText;
        invalidate();
    }

    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        invalidate();
    }

    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        invalidate();
    }
}
