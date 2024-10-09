package com.example.exe201.Decorations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ItemDecorationDividerForActivity extends RecyclerView.ItemDecoration {
    private final Drawable divider;
    private final int marginStart;
    private final int marginEnd;

    public ItemDecorationDividerForActivity(Context context, int drawableId, int marginStart, int marginEnd) {
        divider = ContextCompat.getDrawable(context, drawableId);
        this.marginStart = marginStart;
        this.marginEnd = marginEnd;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft() + marginStart;
        int right = parent.getWidth() - parent.getPaddingRight() - marginEnd;

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) { // Chừa đoạn cuối ra
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }
}