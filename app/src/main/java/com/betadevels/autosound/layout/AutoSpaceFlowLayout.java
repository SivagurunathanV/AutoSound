package com.betadevels.autosound.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.betadevels.autosound.BuildConfig;

public class AutoSpaceFlowLayout extends ViewGroup
{
    private final static int Y_PADDING = 2;
    private static final String TAG = "AutoSpaceFlowLayout";
    private int maxHeight;

    public AutoSpaceFlowLayout( Context context )
    {
        super(context);
    }

    public AutoSpaceFlowLayout( Context context, AttributeSet attrs )
    {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if( BuildConfig.DEBUG && MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED )
        {
            throw new AssertionError();
        }

        final int childCount = getChildCount();

        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        int xPosition = getPaddingLeft();
        int yPosition = getPaddingTop();

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
        int childHeightMeasureSpec;
        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
        {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        }
        else
        {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        maxHeight = 0;
        boolean isXPaddingMeasured = false;
        int xPadding = 0;
        int widthOccupied = 0;
        for(int i = 0; i < childCount; i++)
        {
            final View child = getChildAt(i);
            if( child.getVisibility() != GONE )
            {
                child.measure( childWidthMeasureSpec, childHeightMeasureSpec );
                final int childWidth = child.getMeasuredWidth();
                if( !isXPaddingMeasured )
                {
                    xPadding = calculateXPadding( childCount, width, childWidth );
                    isXPaddingMeasured = true;
                }
                maxHeight = Math.max( maxHeight, child.getMeasuredHeight() + Y_PADDING );

                //Moving the child to next 'line' if it can't fit in the same 'line'
                if( widthOccupied + childWidth > width )
                {
                    xPosition = getPaddingLeft();
                    widthOccupied = 0;
                    yPosition += maxHeight + getPaddingTop();
                }

                xPosition += childWidth + xPadding;
                widthOccupied += childWidth + xPadding;
            }
        }

        if( MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED )
        {
            height = yPosition + maxHeight;
        }
        else if( MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST && yPosition + maxHeight < height )
        {
            height = yPosition + maxHeight;
        }

        //Fudge to avoid clipping at the bottom of last row.
        height += 5;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int availableWidth = right - left - getPaddingStart() - getPaddingEnd();
        int xPosition = getPaddingLeft();
        int yPosition = getPaddingTop();
        int widthOccupied = 0;

        Log.i(TAG, "onLayout: paddingRight : " + getPaddingRight());

        int childCount = getChildCount();
        int xPadding = calculateXPadding( childCount, availableWidth, getChildAt(0).getMeasuredWidth() );

        for(int i = 0; i < childCount; i++)
        {
            final View child = getChildAt(i);
            if(child.getVisibility() != GONE)
            {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                //Moving the child to next row if it can't fit in the same row
                if( widthOccupied + childWidth > availableWidth )
                {
                    xPosition = getPaddingLeft();
                    widthOccupied = 0;
                    yPosition += maxHeight + getPaddingTop();
                }

                child.layout( xPosition, yPosition, xPosition + childWidth, yPosition + childHeight );

                //Setting the xPosition for the next child
                xPosition += childWidth + xPadding;
                widthOccupied += childWidth + xPadding;
            }
        }
    }

    //Calculating the xPadding between the children
    //NOTE: This method assumes that every children has the same dimension!
    //      May result in overflow if each child is of different size.
    private int calculateXPadding( int childCount, int availableWidth, int childWidth )
    {
        int maxChildrenPerRow = Math.min( childCount, availableWidth / childWidth );
        return ( availableWidth - ( maxChildrenPerRow * childWidth ) ) / ( maxChildrenPerRow - 1 );
    }
}
