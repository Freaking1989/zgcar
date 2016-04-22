package com.zgcar.com.history.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class FlowerAnimalSet {

	public static void startAnim(final Context context, final ImageView image,
			final float stopX, final float stopY, final Handler handler) {
		ScaleAnimation animation1 = new ScaleAnimation(0f, 1f, 0f, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation1.setDuration(1000);
		image.setAnimation(animation1);
		image.startAnimation(animation1);
		animation1.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				startMoveAnim(context, image, stopX, stopY, handler);
			}
		});
	}

	private static void startMoveAnim(final Context context,
			final ImageView image, final float stopX, final float stopY,
			final Handler handler) {
		image.clearAnimation();
		AnimationSet animationSet = new AnimationSet(true);
		float x = (float) (stopX - image.getLeft());
		float y = (float) (stopY - image.getTop());
		Log.e("FlowerAnimalSet", image.getLeft() + "::" + image.getTop());
		Log.e("FlowerAnimalSet", image.getWidth() / 2 + "::" + image.getWidth()
				/ 2);
		Log.e("FlowerAnimalSet", x + "::" + y);
		TranslateAnimation translateAnimation = new TranslateAnimation(0, x, 0,
				y);
		translateAnimation.setDuration(1000);
		translateAnimation.setFillAfter(true);
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				handler.sendMessage(handler.obtainMessage(3));
			}
		});
		ScaleAnimation animation3 = new ScaleAnimation(1f, 0f, 1f, 0f,
				Animation.ABSOLUTE, x, Animation.ABSOLUTE, y);
		// animation3.setStartOffset(2000);
		animation3.setDuration(1000);
		animation3.setFillEnabled(true);
		animation3.setFillAfter(true);
		animationSet.addAnimation(translateAnimation);
		animationSet.addAnimation(animation3);
		animationSet.setFillEnabled(true);
		animationSet.setFillAfter(true);
		image.setAnimation(animationSet);
		image.startAnimation(animationSet);

	}
}
