package com.sml.krackout.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.sml.krackout.beans.Block
import com.sml.krackout.utils.ViewUtil
import kotlin.concurrent.thread

/**
 * Created by Smeiling on 2017/12/25.
 */
class BallView : View {
    private var width: Float = 0f
    private var height: Float = 0f

    /**
     * 移动滑块的宽度
     */
    private var boardWidth: Float = 0f

    /**
     * 移动滑块的高度
     */
    private var boardHeight: Float = 0f

    /**
     * 移动滑块离顶部的距离
     */
    private var board2Top: Float = 0f

    /**
     * 移动滑块离左边的距离
     */
    private var board2Left: Float = 0f

    /**
     * 小球的半径
     */
    private var ballRadius: Float = 0f

    /**
     * 小球的x坐标
     */
    private var ballX: Float = 0f

    /**
     * 小球的y坐标
     */
    private var ballY: Float = 0f

    /**
     * 小球画笔
     */
    private val ballPaint: Paint = Paint()

    /**
     * 移动滑块画笔
     */
    private val boardPaint: Paint = Paint()

    /**
     * 存储方块的集合
     */
    private var blockList: MutableList<Block> = mutableListOf()

    /**
     * 方块的宽度
     */
    private var blockWidth = 0f

    /**
     * 方块的高度
     */
    private var blockHeight = 0f

    /**
     * 方块画笔
     */
    private var blockPaint = Paint()

    /**
     * 结束循环标识位
     */
    private var isOver = false

    /**
     * 小球x方向每次移动的偏移量
     */
    private var vx = 8f

    /**
     * 小球y方向每次移动的偏移量
     */
    private var vy = -8f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = w.toFloat()
        height = h.toFloat()
        //方块的宽度
        blockWidth = width / 5
        //方块的高度
        blockHeight = blockWidth / 2

        //初始化方块，三行四列
        for (row in 0..3) {
            for (col in 0..4) {
                createBlock(row, col)
            }
        }

        blockPaint.strokeWidth = ViewUtil.dpi2px(context, 1f).toFloat()
        blockPaint.isAntiAlias = true
        blockPaint.textSize = ViewUtil.dpi2px(context, width / 50).toFloat()
        blockPaint.style = Paint.Style.FILL

        //移动滑块的宽度
        boardWidth = width / 8
        //移动滑块距离顶部的距离
        board2Top = height / 8 * 7
        //移动滑块的left距离，居中显示
        board2Left = width / 2 - boardWidth / 2


        //小球半径，移动滑块的1/4
        ballRadius = boardWidth / 4
        //小球的x，y坐标
        ballX = width / 2
        ballY = board2Top - ballRadius - ViewUtil.dpi2px(context, 10f).toFloat() / 2

        ballPaint.style = Paint.Style.FILL //填充内部
        ballPaint.isAntiAlias = true
        ballPaint.color = Color.YELLOW

        boardPaint.style = Paint.Style.FILL
        boardPaint.isAntiAlias = true
        boardPaint.strokeWidth = ViewUtil.dpi2px(context, 10f).toFloat() / 2
        boardPaint.color = Color.RED

        Thread(GameRunnable()).start()

    }

    /**
     * 创建方块
     */
    private fun createBlock(row: Int, col: Int) {
        var block = Block()
        var rectF = RectF()
        rectF.left = blockWidth * col
        rectF.top = blockHeight * row
        rectF.right = blockWidth * (col + 1)
        rectF.bottom = blockHeight * (row + 1)

        block.rectF = rectF
        //16777216黑色
        val hex = "#" + Integer.toHexString((-16777216 * Math.random()).toInt())
        block.color = hex
        blockList.add(block)
    }

    /**
     * 图像的绘制
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setBackgroundColor(Color.GRAY)
        //画移动滑块
        canvas?.drawLine(board2Left, board2Top, board2Left + boardWidth, board2Top + boardHeight, boardPaint)
        //画球
        canvas?.drawCircle(ballX, ballY, ballRadius, ballPaint)
        //画方块
        blockList.forEach {
            if (!it.isImpact) {
                blockPaint.color = Color.parseColor(it.color)
                canvas?.drawRect(it.rectF, blockPaint)
            }
        }
    }

    /**
     * 捕捉Touch事件，控制移动滑块位置
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
                //新的距左距离为释放点-移动滑块宽度/2
                board2Left = event.x - boardWidth / 2
                //重绘
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    /**
     * 小球移动线程
     */
    inner class GameRunnable : Runnable {
        override fun run() {
            while (!isOver) {
                //调整小球的坐标
                ballX += vx
                ballY += vy

                //左右边界值绑定
                if (ballX + ballRadius > width || ballX - ballRadius < 0) {
                    //改变偏移方向
                    vx *= -1
                }

                //上下边界值绑定
                if (ballY - ballRadius < 0 || ballY + ballRadius > height) {
                    //改变偏移方向
                    vy *= -1
                }

                if (ballX >= board2Left && ballX < board2Left + boardWidth
                        && ballY >= board2Top - ballRadius - ViewUtil.dpi2px(context, 10f).toFloat() / 2) {
                    vy *= -1
                }

                if ((ballX < board2Left || ballX > board2Left + boardWidth) && ballY > board2Top) {
                    isOver = true
                }

                for (i in blockList.indices) {
                    val block = blockList[i]
                    //忽略撞击过的方块
                    if (block.isImpact) {
                        continue
                    }
                    val rectF = block.rectF
                    if (ballX >= rectF.left && ballX <= rectF.right && ballY - ballRadius <= rectF.bottom) {
                        //设置该方块为已撞击
                        block.isImpact = true
                        vy *= -1
                    }

                }

                if (blockList.count { it.isImpact } == blockList.size) {
                    isOver = true
                }
                Thread.sleep(50)
                postInvalidate()
            }
        }
    }
}