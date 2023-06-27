package ipl.estg.happyguest.utils.others;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

public class CornersImage implements Transformation {

    private static final float CORNER_RADIUS_DP = 20;

    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        float cornerRadius = dpToPx();

        float scaleX = (float) width / source.getWidth();
        float scaleY = (float) height / source.getHeight();
        float scale = Math.max(scaleX, scaleY);

        float scaledWidth = scale * source.getWidth();
        float scaledHeight = scale * source.getHeight();

        float dx = (width - scaledWidth) / 2;
        float dy = (height - scaledHeight) / 2;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        RectF rect = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);

        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        Matrix matrix = new Matrix();
        matrix.setTranslate(-dx, -dy);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);

        canvas.translate(dx, dy);
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);

        source.recycle();

        return bitmap;
    }

    @Override
    public String key() {
        return "corners";
    }

    private float dpToPx() {
        return CornersImage.CORNER_RADIUS_DP * Resources.getSystem().getDisplayMetrics().density;
    }
}
