package com.hallen.asistentedeprofesores.ui.view.customviews

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.hallen.asistentedeprofesores.R

@SuppressLint("ClickableViewAccessibility")
class CircularImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): FrameLayout(context, attrs, defStyle) {
    private var bg: Drawable? = null
    private val longTime = resources.getInteger(android.R.integer.config_longAnimTime).toLong()
    private val shortTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    private var animatingStart = false
    private var animatingEnd = false
/*    fun setOnClickListener(function: (View) -> Unit) {
        super.setOnClickListener {
            if (bg == null) startClickAnimation()
            function(this@CircularImageView)
        }
    }*/

    private fun startClickAnimation() {
        animatingEnd = false
        bgView.apply {
            alpha = 0f
            this.scaleX = 1f; this.scaleY = 1f
            visibility = View.VISIBLE
            animate().alpha(0.3f)
                .setDuration(shortTime)
                .scaleX(2f).scaleY(2f).setListener(object : Animator.AnimatorListener{
                    override fun onAnimationStart(p0: Animator) {
                        animatingStart = true
                    }
                    override fun onAnimationEnd(p0: Animator) {
                        bgView.scaleY = 2f; bgView.scaleX = 2f
                        animatingStart = false
                        if (animatingEnd) endAnimation()
                    }
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}
                })
        }
    }

    private fun endAnimation() {
        animatingEnd = true
        if (animatingStart) return
        bgView.apply {
            alpha = 0.3f
            animate()
                .alpha(0f)
                .setDuration(longTime).setListener(object : Animator.AnimatorListener{
                    override fun onAnimationEnd(p0: Animator) {
                        visibility = View.GONE
                    }
                    override fun onAnimationStart(p0: Animator) {   }
                    override fun onAnimationCancel(p0: Animator) {  }
                    override fun onAnimationRepeat(p0: Animator) {  }
                })
        }
    }

    var imageView: ImageView
    private var bgView: LinearLayout




    init {
        val view = LayoutInflater.from(context).inflate(R.layout.circle_image_view, this, true)
        imageView = view.findViewById(R.id.circle_image)
        bgView = view.findViewById(R.id.circle_image_bg)
        bgView.scaleY = 2f
        bgView.scaleX = 2f
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val rect = Rect(0, 0, view.width, view.height)
                outline.setRoundRect(rect, rect.width() / 2.0f)
            }
        }
        clipToOutline = true
        if (attrs != null){
            context.withStyledAttributes(attrs, R.styleable.CircularImageView){
                bg = getDrawable(R.styleable.CircularImageView_background)
                val pad = getDimension(R.styleable.CircularImageView_padding, 5F).toInt()
                val src= getDrawable(R.styleable.CircularImageView_src)
                if (bg != null) {
                    bgView.background = bg; bgView.visibility = View.VISIBLE
                }
                if (src != null) imageView.setImageDrawable(src)
                val params = imageView.layoutParams
                (params as LayoutParams).setMargins(pad, pad, pad, pad)
                imageView.layoutParams = params
            }
        }
        this.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN){
                startClickAnimation()
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                endAnimation()
            }
            return@setOnTouchListener false
        }
    }
}

class CircleImage @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyle) {

    init {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val rect = Rect(0, 0, view.width, view.height)
                outline.setRoundRect(rect, rect.width() / 2.0f)
            }
        }
        clipToOutline = true
    }
}
