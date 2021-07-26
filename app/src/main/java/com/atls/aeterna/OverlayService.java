package com.atls.aeterna;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

public class OverlayService extends Service implements OnTouchListener, OnClickListener {
    private View m_topLeftView;
    private Button m_overlayedButton;
    private float m_offsetX;
    private float m_offsetY;
    private int m_originalXPos;
    private int m_originalYPos;
    private boolean m_isMoving;
    private WindowManager m_wm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        int WINDOW_FLAGS = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        int LAYOUT_FLAG;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Log.i("OverlayService", "TYPE_APPLICATION_OVERLAY");
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            // Log.i("OverlayService", "TYPE_PHONE");
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        m_wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        m_overlayedButton = new Button(this);
        m_overlayedButton.setText("Overlay button");
        m_overlayedButton.setOnTouchListener(this);
        m_overlayedButton.setBackgroundColor(Color.BLUE);
        m_overlayedButton.setTextColor(Color.WHITE);
        m_overlayedButton.getBackground().setAlpha(Math.round(0.33f*255));
        m_overlayedButton.setOnClickListener(this);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG, WINDOW_FLAGS,
            PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        m_wm.addView(m_overlayedButton, params);

        m_topLeftView = new View(this);

        WindowManager.LayoutParams topLeftParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG, WINDOW_FLAGS,
            PixelFormat.TRANSLUCENT
        );

        topLeftParams.gravity = Gravity.LEFT | Gravity.TOP;
        topLeftParams.x = 0;
        topLeftParams.y = 0;
        topLeftParams.width = 0;
        topLeftParams.height = 0;
        m_wm.addView(m_topLeftView, topLeftParams);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (m_overlayedButton != null) {
            m_wm.removeView(m_overlayedButton);
            m_wm.removeView(m_topLeftView);
            m_overlayedButton = null;
            m_topLeftView = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getRawX();
            float y = event.getRawY();

            m_isMoving = false;

            int[] location = new int[2];
            m_overlayedButton.getLocationOnScreen(location);

            m_originalXPos = location[0];
            m_originalYPos = location[1];

            m_offsetX = m_originalXPos - x;
            m_offsetY = m_originalYPos - y;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] topLeftLocationOnScreen = new int[2];
            m_topLeftView.getLocationOnScreen(topLeftLocationOnScreen);

            // System.out.println("topLeftY = " + topLeftLocationOnScreen[1]);
            // System.out.println("originalY = " + m_originalYPos);

            float x = event.getRawX();
            float y = event.getRawY();

            WindowManager.LayoutParams params = (LayoutParams)m_overlayedButton.getLayoutParams();

            int newX = (int)(m_offsetX + x);
            int newY = (int)(m_offsetY + y);

            if (Math.abs(newX - m_originalXPos) < 1 && Math.abs(newY - m_originalYPos) < 1 && !m_isMoving)
                return false;

            params.x = newX - (topLeftLocationOnScreen[0]);
            params.y = newY - (topLeftLocationOnScreen[1]);

            m_wm.updateViewLayout(m_overlayedButton, params);
            m_isMoving = true;

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (m_isMoving)
                return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "overlay button onClick", Toast.LENGTH_SHORT).show();
    }

}
