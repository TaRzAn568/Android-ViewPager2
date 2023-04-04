package androidx.viewpager2.integration.testapp.utils

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class ZoomOutPageTransformer2 : ViewPager2.PageTransformer {
    companion object {
        private const val MIN_SCALE = 0.9f
        private const val MIN_SCALE_VERTICAL = 0.8f
        private const val MIN_ALPHA = 0.9f
    }

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                    val scaleFactorVertical = MIN_SCALE_VERTICAL.coerceAtLeast(1 - abs(position))

                    val vertMargin = pageHeight * (1 - scaleFactorVertical) / 6
                    val horzMargin = pageWidth * (1 - scaleFactor) / 6
                    translationX = if (position < 0) {
                        horzMargin - vertMargin / 6
                    } else {
                        horzMargin + vertMargin / 6
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    scaleX = scaleFactor
                    scaleY = scaleFactorVertical

                    // Fade the page relative to its size.
                    alpha = (MIN_ALPHA + (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}