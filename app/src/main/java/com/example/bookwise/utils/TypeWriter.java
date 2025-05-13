package com.example.bookwise.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.bookwise.R;

public class TypeWriter extends AppCompatTextView {

    private CharSequence mText;
    private int mIndex;
    private long mDelay = 150;
    private Handler handler = new Handler();
    private MediaPlayer keySound;
    private Context context;

    public TypeWriter(Context context) {
        super(context);
        this.context = context;
        initSound();
    }

    public TypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initSound();
    }

    private void initSound() {
        keySound = MediaPlayer.create(context, R.raw.typewriter_key);
    }

    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;
        setText("");
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, mDelay);
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }

    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if (keySound != null) {
                keySound.start(); // Harf başına sesi çal
            }

            if (mIndex <= mText.length()) {
                handler.postDelayed(this, mDelay);
            }
        }
    };
}
