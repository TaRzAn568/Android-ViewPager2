package androidx.viewpager2.integration.testapp.utils

import android.view.View
import androidx.viewpager2.widget.ViewPager2

private const val MIN_SCALE = 0.85f

class StageSideMarginPageTransformer(val nextItemVisiblePx: Float, val currentItemHorizontalMarginPx: Float ) : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        view.apply {
            translationX = -pageTranslationX * position
            scaleX = MIN_SCALE
            scaleY = MIN_SCALE
            alpha = 1f
        }
    }
}