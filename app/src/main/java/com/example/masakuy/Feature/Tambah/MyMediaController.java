package com.example.masakuy.Feature.Tambah;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

public class MyMediaController extends MediaController {

    public MyMediaController(Context context) {
        super(context);
    }

    public MyMediaController(Context context, boolean useFastForward) {
        super (context, useFastForward);
    }

    public MyMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean _isShowing = false;

    @Override
    public boolean isShowing() { return _isShowing; }

    @Override
    public void show() {
        super.show();
        _isShowing = true;

        ViewGroup parent = (ViewGroup) this.getParent();
        parent.setVisibility(View.VISIBLE);
    }

    @Override
    public void hide() {
        super.hide();
        _isShowing = false;

        ViewGroup parent = (ViewGroup) this.getParent();
        parent.setVisibility(View.GONE);
    }
}