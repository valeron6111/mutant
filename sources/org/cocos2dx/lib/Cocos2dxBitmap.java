package org.cocos2dx.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import com.alawar.mutant.jni.MutantMessages;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class Cocos2dxBitmap {
    private static final int ALIGNCENTER = 51;
    private static final int ALIGNLEFT = 49;
    private static final int ALIGNRIGHT = 50;
    private static Context context;

    private static native void nativeInitBitmapDC(int i, int i2, byte[] bArr);

    public static void setContext(Context context2) {
        context = context2;
    }

    public static void createTextBitmap(String content, String fontName, int fontSize, int alignment, int width, int height) {
        String content2 = refactorString(content);
        Paint paint = newPaint(fontName, fontSize, alignment);
        TextProperty textProperty = computeTextProperty(content2, paint, width, height);
        int bitmapTotalHeight = height == 0 ? textProperty.totalHeight : height;
        Bitmap bitmap = Bitmap.createBitmap(textProperty.maxWidth, bitmapTotalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int y = height == 0 ? -fm.ascent : (-fm.ascent) + ((height - textProperty.totalHeight) / 2);
        String[] lines = textProperty.lines;
        for (String line : lines) {
            int x = computeX(paint, line, textProperty.maxWidth, alignment);
            canvas.drawText(line, x, y, paint);
            y += textProperty.heightPerLine;
        }
        initNativeObject(bitmap);
    }

    private static int computeX(Paint paint, String content, int w, int alignment) {
        switch (alignment) {
            case ALIGNLEFT /* 49 */:
            default:
                return 0;
            case ALIGNRIGHT /* 50 */:
                return w;
            case ALIGNCENTER /* 51 */:
                int ret = w / 2;
                return ret;
        }
    }

    private static class TextProperty {
        int heightPerLine;
        String[] lines;
        int maxWidth;
        int totalHeight;

        TextProperty(int w, int h, String[] lines) {
            this.maxWidth = w;
            this.heightPerLine = h;
            this.totalHeight = lines.length * h;
            this.lines = lines;
        }
    }

    private static TextProperty computeTextProperty(String content, Paint paint, int maxWidth, int maxHeight) {
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int h = (int) Math.ceil(fm.descent - fm.ascent);
        int maxContentWidth = 0;
        String[] lines = splitString(content, maxHeight, maxWidth, paint);
        if (maxWidth != 0) {
            maxContentWidth = maxWidth;
        } else {
            for (String line : lines) {
                int temp = (int) Math.ceil(paint.measureText(line, 0, line.length()));
                if (temp > maxContentWidth) {
                    maxContentWidth = temp;
                }
            }
        }
        return new TextProperty(maxContentWidth, h, lines);
    }

    private static String[] splitString(String content, int maxHeight, int maxWidth, Paint paint) {
        String[] lines = content.split("\\n");
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int heightPerLine = (int) Math.ceil(fm.descent - fm.ascent);
        int maxLines = maxHeight / heightPerLine;
        if (maxWidth != 0) {
            LinkedList<String> strList = new LinkedList<>();
            for (String line : lines) {
                int lineWidth = (int) Math.ceil(paint.measureText(line));
                if (lineWidth > maxWidth) {
                    strList.addAll(divideStringWithMaxWidth(paint, line, maxWidth));
                } else {
                    strList.add(line);
                }
                if (maxLines > 0 && strList.size() >= maxLines) {
                    break;
                }
            }
            if (maxLines > 0 && strList.size() > maxLines) {
                while (strList.size() > maxLines) {
                    strList.removeLast();
                }
            }
            String[] ret = new String[strList.size()];
            strList.toArray(ret);
            return ret;
        }
        if (maxHeight != 0 && lines.length > maxLines) {
            LinkedList<String> strList2 = new LinkedList<>();
            for (int i = 0; i < maxLines; i++) {
                strList2.add(lines[i]);
            }
            String[] ret2 = new String[strList2.size()];
            strList2.toArray(ret2);
            return ret2;
        }
        return lines;
    }

    private static LinkedList<String> divideStringWithMaxWidth(Paint paint, String content, int width) {
        int charLength = content.length();
        int start = 0;
        LinkedList<String> strList = new LinkedList<>();
        int i = 1;
        while (i <= charLength) {
            int tempWidth = (int) Math.ceil(paint.measureText(content, start, i));
            if (tempWidth >= width) {
                int lastIndexOfSpace = content.substring(0, i).lastIndexOf(" ");
                if (lastIndexOfSpace != -1) {
                    strList.add(content.substring(start, lastIndexOfSpace));
                    i = lastIndexOfSpace;
                } else if (tempWidth > width) {
                    strList.add(content.substring(start, i - 1));
                    i--;
                } else {
                    strList.add(content.substring(start, i));
                }
                start = i;
            }
            i++;
        }
        if (start < charLength) {
            strList.add(content.substring(start));
        }
        return strList;
    }

    private static Paint newPaint(String fontName, int fontSize, int alignment) {
        Paint paint = new Paint();
        paint.setColor(-1);
        paint.setTextSize(fontSize);
        paint.setAntiAlias(true);
        if (fontName.endsWith(".ttf")) {
            try {
                Typeface typeFace = Typeface.createFromAsset(context.getAssets(), fontName);
                paint.setTypeface(typeFace);
            } catch (Exception e) {
                Log.e("Cocos2dxBitmap", "error to create ttf type face: " + fontName);
                paint.setTypeface(Typeface.create(fontName, 0));
            }
        } else {
            paint.setTypeface(Typeface.create(fontName, 0));
        }
        switch (alignment) {
            case ALIGNLEFT /* 49 */:
                paint.setTextAlign(Paint.Align.LEFT);
                return paint;
            case ALIGNRIGHT /* 50 */:
                paint.setTextAlign(Paint.Align.RIGHT);
                return paint;
            case ALIGNCENTER /* 51 */:
                paint.setTextAlign(Paint.Align.CENTER);
                return paint;
            default:
                paint.setTextAlign(Paint.Align.LEFT);
                return paint;
        }
    }

    private static String refactorString(String str) {
        if (str.compareTo(MutantMessages.sEmpty) == 0) {
            return " ";
        }
        StringBuilder strBuilder = new StringBuilder(str);
        int start = 0;
        for (int index = strBuilder.indexOf("\n"); index != -1; index = strBuilder.indexOf("\n", start)) {
            if (index == 0 || strBuilder.charAt(index - 1) == '\n') {
                strBuilder.insert(start, " ");
                start = index + 2;
            } else {
                start = index + 1;
            }
            if (start > strBuilder.length() || index == strBuilder.length()) {
                break;
            }
        }
        return strBuilder.toString();
    }

    private static void initNativeObject(Bitmap bitmap) {
        byte[] pixels = getPixels(bitmap);
        if (pixels != null) {
            nativeInitBitmapDC(bitmap.getWidth(), bitmap.getHeight(), pixels);
        }
    }

    private static byte[] getPixels(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight() * 4];
        ByteBuffer buf = ByteBuffer.wrap(pixels);
        buf.order(ByteOrder.nativeOrder());
        bitmap.copyPixelsToBuffer(buf);
        return pixels;
    }
}
