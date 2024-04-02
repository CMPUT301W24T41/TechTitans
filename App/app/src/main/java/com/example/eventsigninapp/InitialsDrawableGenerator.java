package com.example.eventsigninapp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class InitialsDrawableGenerator {
    public static Drawable generateInitialsDrawable(String initials) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(80); // Adjust text size as needed
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        Rect bounds = new Rect();
        paint.getTextBounds(initials, 0, initials.length(), bounds);
        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.GRAY); // Background color (you can change it)
        canvas.drawText(initials, 50, 130, paint); // Adjust position as needed

        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }
}

