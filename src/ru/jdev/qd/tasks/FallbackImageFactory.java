package ru.jdev.qd.tasks;

import android.graphics.*;
import ru.jdev.qd.model.ContactInfo;

public class FallbackImageFactory {

    private static final int IMAGE_WIDTH = 134;
    private static final int IMAGE_HEIGHT = 134;

    private final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    private final Bitmap bmp = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, conf);
    private final Canvas canvas = new Canvas(bmp);

    private final Paint background = new Paint();
    private final Paint foreground = new Paint();

    private final ContactInfo contactInfo;

    public FallbackImageFactory(ContactInfo contactInfo) {
        background.setStyle(Paint.Style.FILL);
        this.contactInfo = contactInfo;
        background.setColor(this.contactInfo.contactColor());

        foreground.setStyle(Paint.Style.FILL);
        foreground.setColor(Color.rgb(255, 255, 255));
        foreground.setSubpixelText(true);
        foreground.setTextSize(78F);

        canvas.drawRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, background);
    }

    public Bitmap getFallbackImage() {
        if (contactInfo.getName() == null || contactInfo.getName().length() == 0) {
            drawBody(foreground, canvas);
            drawCollar(background, canvas);
            drawHead(foreground, canvas);
            drawMouth(background, IMAGE_WIDTH, canvas);
        } else {
            String contactLabel = String.valueOf(contactInfo.getContactLabel().charAt(0)).toUpperCase();

            foreground.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
            Paint.FontMetrics fm = foreground.getFontMetrics();
            float textWidth = foreground.measureText(contactLabel);

            canvas.drawText(contactLabel, (IMAGE_WIDTH - textWidth) / 2, (IMAGE_HEIGHT - fm.top - fm.bottom) / 2, foreground);
        }

        return bmp;
    }

    private void drawMouth(Paint background, int imageWidth, Canvas canvas) {
        canvas.clipRect(0, 74, imageWidth, 99);
        canvas.drawOval(new RectF(32, 24, 104, 96), background);
    }

    private void drawHead(Paint foreground, Canvas canvas) {
        canvas.drawOval(new RectF(25, 17, 111, 103), foreground);
    }

    private void drawCollar(Paint background, Canvas canvas) {
        canvas.drawOval(new RectF(20, 12, 116, 112), background);
    }

    private void drawBody(Paint foreground, Canvas canvas) {
        canvas.drawOval(new RectF(10, 100, 124, 170), foreground);
    }

}
