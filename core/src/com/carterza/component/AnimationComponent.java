package com.carterza.component;

import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Created by zachcarter on 11/22/16.
 */
public class AnimationComponent {
    private Animation animation;
    private float animationAge;
    private boolean showAnimation = true;

    public AnimationComponent(Animation animation) {
        this.animation = animation;
        this.animationAge = 0;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public float getAnimationAge() {
        return animationAge;
    }

    public void setAnimationAge(float animationAge) {
        this.animationAge = animationAge;
    }

    public boolean isShowAnimation() {
        return showAnimation;
    }

    public void setShowAnimation(boolean showAnimation) {
        this.showAnimation = showAnimation;
    }
}
