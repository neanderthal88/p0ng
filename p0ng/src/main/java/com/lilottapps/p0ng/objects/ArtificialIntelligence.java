package com.lilottapps.p0ng.objects;

import java.util.Random;

/**
 * Created by jason on 12/10/13.
 */
public class ArtificialIntelligence {

    private Paddle p1;
    private Paddle p2;
    private int height;
    private int width;
    private int aiStrategy;
    private Ball ball;

    public ArtificialIntelligence (Paddle p) {
        this.p1 = p;
    }

    public ArtificialIntelligence(Paddle a, Paddle b) {
        this.p1 = a;
        this.p2 = b;
    }

    public void setDimensions(int h, int w) {
        this.height = h;
        this.width = w;
    }

    public void doAI(Paddle cpu, Paddle opponent, Ball b) {
        switch(this.aiStrategy) {
            case 2:	aiFollow(cpu); break;
            case 1:	aiExact(cpu); break;
            default: aiPrediction(cpu,opponent,b); break;
        }
    }

    /**
     * A generalized Pong AI player. Takes a Rect object and a Ball, computes where the ball will
     * be when ball.y == rect.y, and tries to move toward that x-coordinate. If the ball is moving
     * straight it will try to clip the ball with the edge of the paddle.
     * @param cpu
     */
    private void aiPrediction(Paddle cpu, Paddle opponent, Ball b) {
        this.ball = new Ball(b, this.height, this.width);
        ball.setSpeed((float)60);

        // Special case: move torward the center if the ball is blinking
        if(this.ball.serving()) {
            cpu.destination = this.width / 2;
            cpu.move(true);
            return;
        }

        // Something is wrong if vy = 0.. let's wait until things fix themselves
        if(ball.vy == 0) return;

        // Y-Distance from ball to Rect 'cpu'
        float cpuDist = Math.abs(ball.y - cpu.centerY());
        // Y-Distance to opponent.
        float oppDist = Math.abs( ball.y - opponent.centerY() );

        // Distance between two paddles.
        float paddleDistance = Math.abs(cpu.centerY() - opponent.centerY());

        // Is the ball coming at us?
        boolean coming = (cpu.centerY() < ball.y && ball.vy < 0)
                || (cpu.centerY() > ball.y && ball.vy > 0);

        // Total amount of x-distance the ball covers
        float total = ((((coming) ? cpuDist : oppDist + paddleDistance)) / Math.abs(ball.vy)) * Math.abs( ball.vx );

        // Playable width of the stage
        float playWidth = this.width - 2 * Ball.RADIUS;

        // calculate the distance from a
        float wallDist = (!ball.isEastBound()) ? ball.x - Ball.RADIUS : playWidth - ball.x + Ball.RADIUS;

        // Effective x-translation left over after first bounce
        float remains = (total - wallDist) % playWidth;

        // Bounces the ball will incur
        int bounces = (int) ((total) / playWidth);

        boolean left = (bounces % 2 == 0) ? !ball.isEastBound() : ball.isEastBound();

        cpu.destination = this.width / 2;

        // Now we need to compute the final x. That's all that matters.
        if(bounces == 0) {
            cpu.destination = (int) (ball.x + total * Math.signum(ball.vx));
        }
        else if(left) {
            cpu.destination = (int) (Ball.RADIUS + remains);
        }
        else { // The ball is going right...
            cpu.destination = (int) ((Ball.RADIUS + playWidth) - remains);
        }

        // Try to give it a little kick if vx = 0
        int salt = (int) (System.currentTimeMillis() / 10000);
        Random r = new Random((long) (cpu.centerY() + ball.vx + ball.vy + salt));
        int width = cpu.getWidth();
        cpu.destination = (int) bound(
                cpu.destination + r.nextInt(2 * width - (width / 5)) - width + (width / 10),
                0, this.width
        );
        cpu.move(true);
    }

    private void aiExact(Paddle cpu) {
        cpu.destination = (int) this.ball.x;
        cpu.setPosition(cpu.destination);
    }

    private void aiFollow(Paddle cpu) {
        cpu.destination = (int) this.ball.x;
        cpu.move(true);
    }

    protected float bound(float x, float low, float hi) {
        return Math.max(low, Math.min(x, hi));
    }
}
