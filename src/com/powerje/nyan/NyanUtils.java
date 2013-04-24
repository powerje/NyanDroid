package com.powerje.nyan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class NyanUtils {

	public static Bitmap imageResourceToBitmap(Context c, int res, int maxDim) {
		Bitmap bmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		// compute the smallest size bitmap we need to read
		BitmapFactory.decodeResource(c.getResources(), res, opts);
		int w = opts.outWidth;
		int h = opts.outHeight;
		int s = 1;
		while (true) {
			if ((w / 2 < maxDim) || (h / 2 < maxDim)) {
				break;
			}
			w /= 2;
			h /= 2;
			s++;
		}
		// scale and read the data
		opts.inJustDecodeBounds = false;
        opts.inPurgeable = true;
		opts.inSampleSize = s;
		bmp = BitmapFactory.decodeResource(c.getResources(), res, opts);
		return bmp;
	}

	public static Bitmap maxHeightResourceToBitmap(Context c, int res,
			int maxHeight) {
		Bitmap bmp = imageResourceToBitmap(c, res, maxHeight);

		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int newHeight = maxHeight;
		int newWidth = maxHeight / 2;

		// calculate the scale
		float scaleHeight = ((float) newHeight) / height;
		float scaleWidth = ((float) newWidth) / width;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap and return it
		return Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
	}

	public static Bitmap scaleWithRatio(Context c, int res, int max) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPurgeable = true;
        opts.inSampleSize = 2;
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), res, opts), max, max, false);
	}
}
