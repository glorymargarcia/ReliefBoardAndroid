/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.stratpoint.reliefboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.stratpoint.reliefboard.adapter.PostBaseAdapter;
import com.stratpoint.reliefboardandroid.R;

/**
 * This ListView displays a set of ListItemObjects. By calling addRow with a new
 * ListItemObject, it is added to the top of the ListView and the new row is animated
 * in. If the ListView content is at the top (the scroll offset is 0), the animation of
 * the new row is accompanied by an extra image animation that pops into place in its
 * corresponding item in the ListView.
 */
@SuppressLint("NewApi")
public class InsertionListView extends ListView {

    private static final int NEW_ROW_DURATION = 500;
    private static final int OVERSHOOT_INTERPOLATOR_TENSION = 5;

    private OvershootInterpolator sOvershootInterpolator;

    private RelativeLayout mLayout;

    private Context mContext;

    private OnRowAdditionAnimationListener mRowAdditionAnimationListener;

    private List<PostObjectPOJO> mData;
    private List<BitmapDrawable> mCellBitmapDrawables;

    public InsertionListView(Context context) {
        super(context);
        init(context);
    }

    public InsertionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InsertionListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        setDivider(null);
        mContext = context;
        mCellBitmapDrawables = new ArrayList<BitmapDrawable>();
        sOvershootInterpolator = new OvershootInterpolator(OVERSHOOT_INTERPOLATOR_TENSION);
    }

  
    @SuppressLint("NewApi")
	public void addRow(PostObjectPOJO newObj) {

        final PostBaseAdapter adapter = (PostBaseAdapter)getAdapter();

        final HashMap<Long, Rect> listViewItemBounds = new HashMap<Long, Rect>();
        final HashMap<Long, BitmapDrawable> listViewItemDrawables = new HashMap<Long,
                BitmapDrawable>();

        int firstVisiblePosition = getFirstVisiblePosition();
        for (int i = 0; i < getChildCount(); ++i) {
            View child = getChildAt(i);
            int position = firstVisiblePosition + i;
            long itemID = adapter.getItemId(position);
            Rect startRect = new Rect(child.getLeft(), child.getTop(), child.getRight(),
                    child.getBottom());
           listViewItemBounds.put(itemID, startRect);
            listViewItemDrawables.put(itemID, getBitmapDrawableFromView(child));
        }

        mData.add(0, newObj);
        adapter.addStableIdForDataAtPosition(0);
        adapter.notifyDataSetChanged();

        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @SuppressLint("NewApi")
			public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);

                ArrayList<Animator> animations = new ArrayList<Animator>();

                final View newCell = getChildAt(0);

                int firstVisiblePosition = getFirstVisiblePosition();
                final boolean shouldAnimateInNewRow = shouldAnimateInNewRow();

                if (shouldAnimateInNewRow) {
                    /** Fades in the text of the first cell. */
                	RelativeLayout textView = (RelativeLayout)newCell.findViewById(R.id.relative_main);
                    ObjectAnimator textAlphaAnimator = ObjectAnimator.ofFloat(textView,
                            View.ALPHA, 0.0f, 1.0f);
                    animations.add(textAlphaAnimator);
                    

                    
                }

              
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = adapter.getItemId(position);
                    Rect startRect = listViewItemBounds.get(itemId);
                    int top = child.getTop();
                    if (startRect != null) {

                        int startTop = startRect.top;
                        int delta = startTop - top;
                        ObjectAnimator animation = ObjectAnimator.ofFloat(child,
                                View.TRANSLATION_Y, delta, 0);
                        animations.add(animation);
                    } else {

                        int childHeight = child.getHeight() + getDividerHeight();
                        int startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        ObjectAnimator animation = ObjectAnimator.ofFloat(child,
                                View.TRANSLATION_Y, delta, 0);
                        animations.add(animation);
                    }
                    listViewItemBounds.remove(itemId);
                    listViewItemDrawables.remove(itemId);
                }

                setEnabled(false);
                mRowAdditionAnimationListener.onRowAdditionAnimationStart();
                AnimatorSet set = new AnimatorSet();
                set.setDuration(NEW_ROW_DURATION);
                set.playTogether(animations);
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCellBitmapDrawables.clear();
                        
                        //mLayout.removeView(copyImgView);
                        mRowAdditionAnimationListener.onRowAdditionAnimationEnd();
                        setEnabled(true);
                        invalidate();
                    }
                });
                set.start();

                listViewItemBounds.clear();
                listViewItemDrawables.clear();
                return true;
            }
        });
    }

    /**
     * By overriding dispatchDraw, the BitmapDrawables of all the cells that were on the
     * screen before (but not after) the layout are drawn and animated off the screen.
     */
    @Override
    protected void dispatchDraw (Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mCellBitmapDrawables.size() > 0) {
            for (BitmapDrawable bitmapDrawable: mCellBitmapDrawables) {
                bitmapDrawable.draw(canvas);
            }
        }
    }

    public boolean shouldAnimateInNewRow() {
        int firstVisiblePosition = getFirstVisiblePosition();
        return (firstVisiblePosition == 0);
    }

    public boolean shouldAnimateInNewImage() {
        if (getChildCount() == 0) {
            return true;
        }
        boolean shouldAnimateInNewRow = shouldAnimateInNewRow();
        View topCell = getChildAt(0);
        return (shouldAnimateInNewRow && topCell.getTop() == 0);
    }

    /** Returns a bitmap drawable showing a screenshot of the view passed in. */
    private BitmapDrawable getBitmapDrawableFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas (bitmap);
        v.draw(canvas);
        return new BitmapDrawable(getResources(), bitmap);
    }

    /**
     * Returns the absolute x,y coordinates of the view relative to the top left
     * corner of the phone screen.
     */
    public Point getLocationOnScreen(View v) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);

        int[] location = new int[2];
        v.getLocationOnScreen(location);

        return new Point(location[0], location[1]);
    }

    /** Setter for the underlying data set controlling the adapter. */
    public void setData(List<PostObjectPOJO> data) {
        mData = data;
    }

    /**
     * Setter for the parent RelativeLayout of this ListView. A reference to this
     * ViewGroup is required in order to add the custom animated overlaying bitmap
     * when adding a new row.
     */
    public void setLayout(RelativeLayout layout) {
        mLayout = layout;
    }

    public void setRowAdditionAnimationListener(OnRowAdditionAnimationListener
                                                        rowAdditionAnimationListener) {
        mRowAdditionAnimationListener = rowAdditionAnimationListener;
    }

    /**
     * This TypeEvaluator is used to animate the position of a BitmapDrawable
     * by updating its bounds.
     */
    static final TypeEvaluator<Rect> sBoundsEvaluator = new TypeEvaluator<Rect>() {
        public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
            return new Rect(interpolate(startValue.left, endValue.left, fraction),
                    interpolate(startValue.top, endValue.top, fraction),
                    interpolate(startValue.right, endValue.right, fraction),
                    interpolate(startValue.bottom, endValue.bottom, fraction));
        }

        public int interpolate(int start, int end, float fraction) {
            return (int)(start + fraction * (end - start));
        }
    };

}
