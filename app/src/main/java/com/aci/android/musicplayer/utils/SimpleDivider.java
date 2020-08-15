package com.aci.android.musicplayer.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aci.android.musicplayer.R;

public class SimpleDivider extends RecyclerView.ItemDecoration {
    private final Drawable divider;

    public SimpleDivider(Context context){
        //divider = context.getResources().getDrawable(R.drawable.divider);
        divider = ContextCompat.getDrawable(context, R.drawable.divider);
    }
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state){
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight()-30;

        int childCount = parent.getChildCount();

        for(int i = 0; i < childCount; i++){
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}
