package hu.matusz.travelapp.util;

import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * Supplies slide up animation for the info panel
 */
public class InfoPanelAnimator {

    /**
     * Shows the panel
     * @param panel Given view
     */
    public static void showPanel(View panel) {
        panel.setVisibility(View.VISIBLE);
        panel.bringToFront();
        TranslateAnimation animate = new TranslateAnimation(
                0, 0, panel.getHeight(), 0); // From bottom up
        animate.setDuration(300);
        animate.setFillAfter(true);
        panel.startAnimation(animate);
    }

    /**
     * Hides panel
     * @param panel Given view
     */
    public static void hidePanel(View panel) {
        TranslateAnimation animate = new TranslateAnimation(
                0, 0, 0, panel.getHeight()); // From up to bottom
        animate.setDuration(300);
        animate.setFillAfter(true);
        panel.startAnimation(animate);

        // Hide after animation
        animate.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {}

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                panel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {}
        });
    }
}

