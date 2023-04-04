/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.viewpager2.integration.testapp

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.viewpager2.integration.testapp.cards.CardViewAdapter
import androidx.viewpager2.integration.testapp.utils.*
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING


/**
 * Shows examples of [ViewPager2.PageTransformer], e.g. [MarginPageTransformer].
 */
class PageTransformerActivity : FragmentActivity() {

    lateinit var viewPager: ViewPager2
    var isInTransit: Boolean = false
    lateinit var itemDecoration: ItemDecoration
    lateinit var swipe: View
    val minDistance = 200f
    var anchorX = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_transformer)

        swipe = findViewById(R.id.swipe)
//        swipe.setOnTouchListener(OnTouchListener { view, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    anchorX = event.x
//                    return@OnTouchListener false
//                }
//                MotionEvent.ACTION_UP -> {
//                    if (Math.abs(event.x - anchorX) > minDistance) {
//                        if (event.x > anchorX) {
////                                onSwipeRight();
//                            Log.i(TAG, "onPageScrollStateChanged: onSwipeRight")
//                        } else {
////                            onSwipeLeft()
//                            Log.i(TAG, "onPageScrollStateChanged: onSwipeLeft")
//                        }
//                    }
//                    return@OnTouchListener false
//                }
//            }
//            false
//        })


        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = CardViewAdapter()

//        setMargin()
        viewPager.setPageTransformer(Pager2_ZoomOutSlideTransformer())
        viewPager.clipToPadding = false
        viewPager.offscreenPageLimit = 1
        itemDecoration = HorizontalMarginItemDecoration(
            this,
            R.dimen.viewpager_current_item_horizontal_margin
        )

//        OrientationController(viewPager, findViewById(R.id.orientation_spinner)).setUp()
//        PageTransformerController(viewPager, findViewById(R.id.transformer_spinner)).setUp()

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    SCROLL_STATE_IDLE -> {
                        Log.i(TAG, "onPageScrollStateChanged: SCROLL_STATE_IDLE")
                    }
                    SCROLL_STATE_DRAGGING -> {
                        Log.i(TAG, "onPageScrollStateChanged: SCROLL_STATE_DRAGGING")
                    }
                    SCROLL_STATE_SETTLING -> {
                        if(!isInTransit) {
                            setMargin()
                        }
                        Log.i(TAG, "onPageScrollStateChanged: SCROLL_STATE_SETTLING")
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.i(TAG, "onPageSelected: " + position)
            }
        })

        viewPager.getChildAt(0).setOnTouchListener(object : OnTouchListener {
            private var pointX = 0f
            private var pointY = 0f
            private val tolerance = 50
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> return false //This is important, if you return TRUE the action of swipe will not take place.
                    MotionEvent.ACTION_DOWN -> {
//                        Log.i(TAG, "ACTION_DOWN: sameX: " + pointX + " sameY:" + pointY)
                        pointX = event.x
                        pointY = event.y
                    }
                    MotionEvent.ACTION_UP -> {
//                        Log.i(TAG, "ACTION_UP: ")
                        val sameX = pointX + tolerance > event.x && pointX - tolerance < event.x
                        val sameY = pointY + tolerance > event.y && pointY - tolerance < event.y
//                        Log.i(TAG, "ACTION_UP: sameX: " + sameX + " sameY:" + sameY)
                        if (sameX && sameY) {
                            //The user "clicked" certain point in the screen or just returned to the same position an raised the finger
                            undoMargin()
                        }
                    }
                }
                return false
            }
        })
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                anchorX = event.x
//                return false
            }
            MotionEvent.ACTION_UP -> {
                if (Math.abs(event.x - anchorX) > minDistance) {
                    if (event.x > anchorX) {
//                                onSwipeRight();
                        Log.i(TAG, "onPageScrollStateChanged: onSwipeRight")
                    } else {
//                            onSwipeLeft()
                        Log.i(TAG, "onPageScrollStateChanged: onSwipeLeft")
                    }
                }
//                return false
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    fun setMargin() {
        if (!isInTransit) {
            Log.i(TAG, "setMargin")
            isInTransit = true
            val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
            val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
            // todo check this for better animation
            viewPager.setPageTransformer(StageSideMarginPageTransformer(nextItemVisiblePx,currentItemHorizontalMarginPx))
//            viewPager.addItemDecoration(itemDecoration)
        }
    }

    fun undoMargin() {
        if (isInTransit) {
            isInTransit = false
            viewPager.setPageTransformer(ZoomOutPageTransformer2())
//            viewPager.removeItemDecoration(itemDecoration)
        }
    }

    private fun setupCarousel(){

        viewPager.offscreenPageLimit = 1

        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
            page.alpha = 0.25f + (1 - kotlin.math.abs(position))
        }
        viewPager.setPageTransformer(pageTransformer)
        viewPager.addItemDecoration(itemDecoration)
    }

    companion object {
        val TAG = "VIKKK"
        val padding = 100
        val margin = 20
    }
}
