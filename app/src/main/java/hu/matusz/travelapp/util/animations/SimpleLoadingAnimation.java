package hu.matusz.travelapp.util.animations;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import hu.matusz.travelapp.R;

public class SimpleLoadingAnimation {
    private ImageView loadingImage;
    private float currentRotation = 0f;
    private ValueAnimator rotationAnimator;

    public SimpleLoadingAnimation(ImageView loadingImage) {
        this.loadingImage = loadingImage;
        startVariableSpeedRotation();
    }

    public void startVariableSpeedRotation() {
        rotationAnimator = ValueAnimator.ofFloat(0f, 360f);
        rotationAnimator.setDuration(1000);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.addUpdateListener(animation -> {
            float rotationDelta = (float) animation.getAnimatedValue();
            loadingImage.setRotation(currentRotation + rotationDelta);
        });

        rotationAnimator.start();


        ValueAnimator speedChanger = ValueAnimator.ofInt(1600, 4000, 1600);
        speedChanger.setDuration(5000);
        speedChanger.setRepeatCount(ValueAnimator.INFINITE);
        speedChanger.setInterpolator(new LinearInterpolator());
        speedChanger.addUpdateListener(anim -> {
            int newDuration = (int) anim.getAnimatedValue();
            rotationAnimator.setDuration(newDuration);
        });

        speedChanger.start();
    }
    public void destroy(){
        if (rotationAnimator != null) rotationAnimator.cancel();
    }
}
