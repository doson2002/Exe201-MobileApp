package app.foodpt.exe201.Decorations;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

public class CustomDividerItemDecoration extends DividerItemDecoration {

    public CustomDividerItemDecoration(Context context, int orientation) {
        super(context, orientation);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + getDrawable().getIntrinsicHeight();
            if (i != childCount - 1) { // Không vẽ divider cho item cuối cùng
                getDrawable().setBounds(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
                getDrawable().draw(c);
            }
        }
    }
}
