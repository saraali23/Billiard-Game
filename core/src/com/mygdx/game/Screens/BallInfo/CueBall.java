package com.mygdx.game.Screens.BallInfo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class CueBall extends Ball {
    float speedNow;
    float recentSpeed;
    public CueBall(Vector2 Pos, Sprite ballSprite) {
        super(Pos,ballSprite);
        this.getFixturedef().filter.categoryBits=Constants.BIT_CUE_BALL;//what it belongs to
        this.getFixturedef().filter.groupIndex=1;//to make sure it collides with other balls
    }
    public boolean CheckBallMovement(){
        speedNow= this.getBall().getLinearVelocity().len();
        recentSpeed= 0.1f * speedNow + 0.9f * recentSpeed;
        if (recentSpeed<=0.3f) {
            this.getBall().setLinearVelocity(0,0);
            return true;
        }
        else return false;


    }
}
