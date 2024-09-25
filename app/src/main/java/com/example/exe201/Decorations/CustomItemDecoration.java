package com.example.exe201.Decorations;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CustomItemDecoration extends RecyclerView.ItemDecoration {
    private final int verticalSpaceHeight;

    public CustomItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // Áp dụng marginTop cho tất cả các item trừ item đầu tiên
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = verticalSpaceHeight;
        }
    }
}