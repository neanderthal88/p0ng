package com.lilottapps.p0ng.views;

        import java.io.File;
        import java.util.ArrayList;
        import java.util.Random;

        import android.content.Context;
        import android.content.ContextWrapper;
        import android.content.SharedPreferences;
        import android.content.res.Resources;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Rect;
        import android.graphics.Paint.Style;
        //import android.media.AudioManager;
        //import android.media.SoundPool;
        import android.os.Handler;
        import android.os.Message;
        import android.preference.PreferenceManager;
        import android.util.AttributeSet;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.View.OnKeyListener;
        import android.view.View.OnTouchListener;
        import android.widget.Toast;

        import com.lilottapps.p0ng.R;
        import com.lilottapps.p0ng.handlers.InputHandler;
        import com.lilottapps.p0ng.objects.ArtificialIntelligence;
        import com.lilottapps.p0ng.objects.Ball;
        import com.lilottapps.p0ng.objects.Paddle;
        import com.lilottapps.p0ng.objects.PowerUps;

/**
 * This class is the main viewing window for the Pong game. All the game's
 * logic takes place within this class as well.
 */
public class P0ngView extends View implements OnTouchListener, OnKeyListener {
    /** Debug tag */
    @SuppressWarnings("unused")
    private static final String TAG = "P0ngView";
    protected static final int FPS = 30;

    public static final int
            STARTING_LIVES = 3,
            PLAYER_PADDLE_SPEED = 10;

    /**
     * This is mostly deprecated but kept around if the need
     * to add more game states comes around.
     */
    private State mCurrentState = State.Running;
    private State mLastState = State.Stopped;
    public static enum State { Running, Stopped}

    /** Flag that marks this view as initialized */
    private boolean initialized = false;

    /** Preferences loaded at startup */
    private int ballSpeedModifier;

    /** Lives modifier */
    private int mLivesModifier;

    /** AI Strategy */
    private int mAiStrategy;

    /** CPU handicap */
    private int mCpuHandicap;

    /** Starts a new round when set to true */
    private boolean mNewRound = true;

    /** Keeps the game thread alive */
    private boolean mContinue = true;

    /** Mutes sounds when true */
    private boolean mMuted = false;
    /** Paddles for our players **/
    private Paddle leftPaddle, rightPaddle;

    /** PowerUps that are available **/
    private ArrayList<PowerUps> powerUps = new ArrayList<PowerUps>();

    private int TOTAL_POWERUPS = 5;

    /** Touch boxes for various functions. These are assigned in initialize() */
    private Rect mPauseTouchBox;

    /** Timestamp of the last frame created */
    private long mLastFrame = 0;

    /** Our ball that is playable **/
    protected Ball ball;

    /** Random number generator */
    private static final Random RNG = new Random();

    /** Pool for our sound effects */
    //protected SoundPool mPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);

    protected int mWinSFX, mMissSFX, mPaddleSFX, mWallSFX;

    /** Paint object */
    private final Paint mPaint = new Paint();

    /** Padding for touch zones and paddles */
    private static final int PADDING = 3;

    /** Scrollwheel sensitivity */
    private static final int SCROLL_SENSITIVITY = 80;

    /** Redraws the screen according to FPS */
    private RefreshHandler redrawHandler = new RefreshHandler();

    /** Flags indicating who is a player */
    private boolean leftPaddlePlayer = false, rightPaddlePlayer = false;

    /** Our AI module that handles single player **/
    private ArtificialIntelligence ai;

    /** Timer for determining when the next power-up is available **/
    private int timeUntilPowerUp;
    /** Timer for determining when the power-up is expired **/
    private int timeToUsePowerUp;

    /**
     * An overloaded class that repaints this view in a separate thread.
     * Calling P0ngView.update() should initiate the thread.
     */
    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            P0ngView.this.update();
            P0ngView.this.invalidate(); // Mark the view as 'dirty'
        }

        public void sleep(long delay) {
            this.removeMessages(0);
            this.sendMessageDelayed(obtainMessage(0), delay);
        }
    }

    /**
     * Creates a new P0ngView within some context
     * @param context
     * @param attrs
     */
    public P0ngView(Context context, AttributeSet attrs) {
        super(context, attrs);
        constructView();
    }

    public P0ngView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        constructView();
    }

    /**
     * Set the paddles to their initial states and as well the ball.
     */
    private void constructView() {
        setOnTouchListener(this);
        setOnKeyListener(this);
        setFocusable(true);

        Context appContext = this.getContext();
        //loadPreferences( PreferenceManager.getDefaultSharedPreferences(ctx) );
        //loadSFX();
    }

    protected void loadSFX() {
        Context appContext = getContext();
        //mWinSFX = mPool.load(ctx, R.raw.wintone, 1);
        //mMissSFX = mPool.load(ctx, R.raw.ballmiss, 1);
        //mPaddleSFX = mPool.load(ctx, R.raw.paddle, 1);
        //mWallSFX = mPool.load(ctx, R.raw.wall, 1);
    }
/*
    protected void loadPreferences(SharedPreferences prefs) {
        Context ctx = getContext();
        Resources r = ctx.getResources();

        this.ballSpeedModifier = Math.max(0, prefs.getInt(Pong.PREF_BALL_SPEED, 0));
        mMuted = prefs.getBoolean(Pong.PREF_MUTED, mMuted);
        mLivesModifier = Math.max(0, prefs.getInt(Pong.PREF_LIVES, 2));
        mCpuHandicap = Math.max(0, Math.min(PLAYER_PADDLE_SPEED-1, prefs.getInt(Pong.PREF_HANDICAP, 4)));

        String strategy = prefs.getString(Pong.PREF_STRATEGY, null);
        String strategies[] = r.getStringArray(R.array.values_ai_strategies);

        mAiStrategy = 0;
        // Linear-search the array for the appropriate strategy index =/
        for(int i = 0; strategy != null && strategy.length() > 0 && i < strategies.length; i++) {
            if(strategy.equals(strategies[i])) {
                mAiStrategy = i;
                break;
            }
        }
    }
*/
    /**
     * The main loop. Call this to update the game state.
     */
    public void update() {
        if(this.getHeight() == 0 || this.getWidth() == 0) {
            this.redrawHandler.sleep(1000 / FPS);
            return;
        }


        if(!this.initialized) {
            this.initializeP0ngView();
            this.initialized = true;
        }

        long now = System.currentTimeMillis();
        if(gameRunning() && mCurrentState != State.Stopped) {
            if(now - mLastFrame >= 1000 / FPS) {
                if(mNewRound) {
                    nextRound();
                    mNewRound = false;
                }
                doGameLogic();
            }
        }

        // We will take this much time off of the next update() call to normalize for
        // CPU time used updating the game state.

        if(mContinue) {
            long diff = System.currentTimeMillis() - now;
            this.redrawHandler.sleep(Math.max(0, (1000 / FPS) - diff) );
        }
    }

    /**
     * All of the game's logic (per game iteration) is in this function.
     * Given some initial game state, it computes the next game state.
     */
    private void doGameLogic() {
        float px = this.ball.x;
        float py = this.ball.y;

        this.ball.move();

        // Shake it up if it appears to not be moving vertically
        if(py == this.ball.y && this.ball.serving() == false) {
            this.ball.randomAngle();
        }

        // Do some basic paddle AI
        if(!leftPaddle.player) this.ai.doAI(leftPaddle, rightPaddle, this.ball);
        else leftPaddle.move();

        if(!rightPaddle.player) this.ai.doAI(rightPaddle, leftPaddle, this.ball);
        else rightPaddle.move();

        /**
         * 1) Make sure the timer is done
         * 2) draw a power up to the screen (done in onDraw())
         * 3) check for collisions
         * 4) if collided then start an active timer
         * 5) once timer is done reset timer
         * 6) remove powerup
         */
        if(this.timeUntilPowerUp < 1) {
            //if (!this.powerUps.activePowerUp()) {
                // handle collision
                this.handlePowerUpCollision(px, py);
                // get powerup active time
                if(true) {

                }
                // decrease powerup active time

                // reset time and remove powerup

                // restart the timeUntilPowerUp timer
                // this.timeUntilPowerUp = this.powerUps.getPowerUpTimer();
            }
            // get a powerup and draw to screen
        //} else {
            this.timeUntilPowerUp--;

        handleBounces(px,py);

        // See if all is lost
        if(this.ball.y >= getHeight()) {
            mNewRound = true;
            rightPaddle.loseLife();

            //if(rightPaddle.living()) playSound(mMissSFX);
            //else playSound(mWinSFX);
        }
        else if (this.ball.y <= 0) {
            mNewRound = true;
            leftPaddle.loseLife();
            //if(leftPaddle.living()) playSound(mMissSFX);
            //else playSound(mWinSFX);
        }
    }

    protected void handlePowerUpCollision(float px, float py) {
        float tx = this.ball.x;
        float ty = this.ball.y - Ball.RADIUS;

        for(int i = 0; i<TOTAL_POWERUPS;i++) {
            if(this.ball.isNorthBound()) {
                this.powerUps.get(i).setWhichPlayer(1);
            } else {
                this.powerUps.get(i).setWhichPlayer(2);
            }
            // See if there is a collision
            if(tx == this.powerUps.get(i).x || ty == this.powerUps.get(i).y) {
                // Activate our current power up
                this.powerUps.get(i).activatePowerUp();
                // remove the power up that has been activated
                this.powerUps.remove(i);
                // add a new power to it's place
                this.powerUps.add(i, new PowerUps(getHeight(), getWidth(), getContext(),
                        this.leftPaddle, this.rightPaddle, this.ball));
            }
        }
    }

    protected void handleBounces(float px, float py) {
        handleTopFastBounce(this.leftPaddle, px, py);
        handleBottomFastBounce(this.rightPaddle, px, py);

        // Handle bouncing off of a wall
        if(this.ball.x <= Ball.RADIUS || this.ball.x >= getWidth() - Ball.RADIUS) {
            this.ball.bounceWall();
            //playSound(mWallSFX);
            if(this.ball.x == Ball.RADIUS)
                this.ball.x++;
            else
                this.ball.x--;
        }

    }

    protected void handleTopFastBounce(Paddle paddle, float px, float py) {
        if(!this.ball.isNorthBound()) return;

        float tx = this.ball.x;
        float ty = this.ball.y - Ball.RADIUS;
        float ptx = px;
        float pty = py - Ball.RADIUS;
        float dyp = ty - paddle.getBottom();
        float xc = tx + (tx - ptx) * dyp / (ty - pty);

        if(ty < paddle.getBottom() && pty > paddle.getBottom()
                && xc > paddle.getLeft() && xc < paddle.getRight()) {
            this.ball.x = xc;
            this.ball.y = paddle.getBottom() + Ball.RADIUS;
            this.ball.bouncePaddle(paddle);
            //playSound(mPaddleSFX);
            increaseDifficulty();
        }
    }

    protected void handleBottomFastBounce(Paddle p, float px, float py) {
        if(this.ball.isNorthBound()) return;
        // The x variable of our ball, we don't need the radius because
        // the x-coordinated side will never hit the paddle
        float bx = this.ball.x;
        // This gives us the position of the ball at the radius of the ball
        // not the center, needed because this will hit the paddle
        float by = this.ball.y + Ball.RADIUS;
        float pbx = px;
        float pby = py + Ball.RADIUS;
        float dyp = by - this.rightPaddle.getTop();
        float xc = bx + (bx - pbx) * dyp / (pby - by);

        /**
         * TODO: check for these 3 cases:
         * 1) ball.y + ball.radius < paddle.top
         * 2) ball.x + radius > paddle.left
         * 3) ball.x + radius < paddle.right
         */
        if(by > this.rightPaddle.getTop() && pby < this.rightPaddle.getTop()
                && xc > this.rightPaddle.getLeft() && xc < this.rightPaddle.getRight()) {

            this.ball.x = xc;
            this.ball.y = this.rightPaddle.getTop() - Ball.RADIUS;
            this.ball.bouncePaddle(this.rightPaddle);
            //playSound(mPaddleSFX);
            increaseDifficulty();
        }
    }

    /**
     * Knocks up the framerate a bit to keep it difficult.
     */
    private void increaseDifficulty() {
        this.ball.setSpeed(this.ball.getSpeed() + (float) 1);
    }

    /**
     * Set the state, start a new round, start the loop if needed.
     * @param next, the next state
     */
    public void setMode(State next) {
        mCurrentState = next;
        nextRound();
        update();
    }

    /**
     * Reset the paddles/touchboxes/framespersecond/ballcounter for the next round.
     */
    private void nextRound() {
        serveBall();
    }

    /**
     * Initializes objects needed to carry out the game.
     * This should be called once as soon as the View has reached
     * its inflated size.
     */
    private void initializeP0ngView() {
        Log.d(TAG, "Initializing our environment");
        this.initializePause();
        this.initializePaddles();
        this.initializeAI();
        this.initializeBall();
        this.initializePowerUps();
    }

    private void initializePause() {
        int min = Math.min(getWidth() / 4, getHeight() / 4);
        int xmid = getWidth() / 2;
        int ymid = getHeight() / 2;
        mPauseTouchBox = new Rect(xmid - min, ymid - min, xmid + min, ymid + min);
    }

    private void initializePaddles() {
        // bottom, left, right, top
        Rect playerOne = new Rect(0,0,getWidth(),getHeight() / 8);
        // bottom, left, right, top
        Rect playerTwo = new Rect(0, 7 * getHeight() / 8, getWidth(), getHeight());

        // leftPaddle: color,
        leftPaddle = new Paddle(Color.RED, playerOne.bottom + PADDING, getHeight(), getWidth());

        rightPaddle = new Paddle(Color.WHITE, playerTwo.top - PADDING - Paddle.PADDLE_THICKNESS, getHeight(), getWidth());

        leftPaddle.setTouchbox(playerOne);
        rightPaddle.setTouchbox( playerTwo );

        leftPaddle.setHandicap(mCpuHandicap);
        rightPaddle.setHandicap(mCpuHandicap);

        leftPaddle.player = leftPaddlePlayer;
        rightPaddle.player = rightPaddlePlayer;

        leftPaddle.setLives(STARTING_LIVES + mLivesModifier);
        rightPaddle.setLives(STARTING_LIVES + mLivesModifier);
    }

    private void initializeAI() {
        // Special case, this is an AI vs AI game
        if(!this.leftPaddle.player && !this.rightPaddle.player) {
            this.ai = new ArtificialIntelligence(this.leftPaddle, this.rightPaddle);
        } else if (!this.leftPaddle.player) {
            this.ai = new ArtificialIntelligence(this.leftPaddle);
        } else {
            this.ai = new ArtificialIntelligence(this.rightPaddle);
        }
        this.ai.setDimensions(getHeight(), getWidth());
    }

    private void initializeBall() {
        this.ball = new Ball();
        ball.setWindowSize(getHeight(), getWidth());
    }

    private void initializePowerUps() {
        for(int i = 0; i<TOTAL_POWERUPS; i++) {
            this.powerUps.add(new PowerUps(getHeight(), getWidth(), getContext(),
                    this.leftPaddle, this.rightPaddle, this.ball));
        }
        // this.timeUntilPowerUp = this.powerUps.getPowerUpTimer();
    }
    /** End the Initialization methods **/
    /**
     * Reset ball to an initial state
     */
    private void serveBall() {
        this.ball.x = getWidth() / 2;
        this.ball.y = getHeight() / 2;
        this.ball.setSpeed(Ball.SPEED + this.ballSpeedModifier);
        this.ball.randomAngle();
        this.ball.pause();
    }

    protected float bound(float x, float low, float hi) {
        return Math.max(low, Math.min(x, hi));
    }

    public void onSizeChanged(int w, int h, int ow, int oh) {
    }

    /**
     * Paints the game!
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!this.initialized) {
            return;
        }

        Context context = getContext();

        // Draw the paddles / touch boundaries
        this.leftPaddle.draw(canvas);
        this.rightPaddle.draw(canvas);

        // Draw touchboxes if needed
        if(gameRunning() && this.leftPaddle.player && mCurrentState == State.Running)
            this.leftPaddle.drawTouchbox(canvas);

        if(gameRunning() && this.rightPaddle.player && mCurrentState == State.Running)
            this.rightPaddle.drawTouchbox(canvas);

        // Draw ball stuff
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(Color.WHITE);

        this.ball.draw(canvas);

        // Drawing powerup to the screen
        if(this.timeUntilPowerUp < 1) {
            for(int i=0; i<TOTAL_POWERUPS; i++) {
                if(!this.powerUps.get(i).isCanvasSet()) {
                    this.powerUps.get(i).setCanvas(canvas);
                }
                this.powerUps.get(i).draw(canvas);
            }
        }

        // If either is a not a player, blink and let them know they can join in!
        // This blinks with the ball.
        if(this.ball.serving()) {
            String join = context.getString(R.string.joinGame);
            int joinw = (int) mPaint.measureText(join);

            if(!leftPaddle.player) {
                mPaint.setColor(Color.RED);
                canvas.drawText(join, getWidth() / 2 - joinw / 2, this.leftPaddle.touchCenterY(), mPaint);
            }

            if(!rightPaddle.player) {
                mPaint.setColor(Color.BLUE);
                canvas.drawText(join, getWidth() / 2 - joinw / 2, this.rightPaddle.touchCenterY(), mPaint);
            }
        }

        // Show where the player can touch to pause the game
        if(this.ball.serving()) {
            String pause = context.getString(R.string.pauseGame);
            int pausew = (int) mPaint.measureText(pause);

            mPaint.setColor(Color.GREEN);
            mPaint.setStyle(Style.STROKE);
            canvas.drawRect(mPauseTouchBox, mPaint);
            canvas.drawText(pause, getWidth() / 2 - pausew / 2, getHeight() / 2, mPaint);
        }

        // Paint a PAUSED message
        if(gameRunning() && mCurrentState == State.Stopped) {
            String s = context.getString(R.string.isPaused);
            int width = (int) mPaint.measureText(s);
            int height = (int) (mPaint.ascent() + mPaint.descent());
            mPaint.setColor(Color.WHITE);
            canvas.drawText(s, getWidth() / 2 - width / 2, getHeight() / 2 - height / 2, mPaint);
        }

        // Draw a 'lives' counter
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Style.FILL_AND_STROKE);
        // Paint the lives left on the screen!
        for(int i = 0; i < leftPaddle.getLives(); i++) {
            canvas.drawCircle(Ball.RADIUS + PADDING + i * (2 * Ball.RADIUS + PADDING),
                    PADDING + Ball.RADIUS,
                    Ball.RADIUS,
                    mPaint);
        }
        // Paint the lives left on the screen!
        for(int i = 0; i < rightPaddle.getLives(); i++) {
            canvas.drawCircle(Ball.RADIUS + PADDING + i * (2 * Ball.RADIUS + PADDING),
                    getHeight() - PADDING - Ball.RADIUS,
                    Ball.RADIUS,
                    mPaint);
        }

        // Announce the winner!
        if(!gameRunning()) {
            mPaint.setColor(Color.GREEN);
            String s = "You both lose";

            if(!rightPaddle.living()) {
                s = context.getString(R.string.playerOneWins);
                mPaint.setColor(Color.RED);
            }
            else if(!leftPaddle.living()) {
                s = context.getString(R.string.playerTwoWins);
                mPaint.setColor(Color.BLUE);
            }

            int width = (int) mPaint.measureText(s);
            int height = (int) (mPaint.ascent() + mPaint.descent());
            canvas.drawText(s, getWidth() / 2 - width / 2, getHeight() / 2 - height / 2, mPaint);
        }
    }

    /**
     * Touching is the method of movement. Touching the touchscreen, that is.
     * A player can join in simply by touching where they would in a normal
     * game.
     */
    public boolean onTouch(View v, MotionEvent mo) {
        if(v != this || !gameRunning()) return false;

        // We want to support multiple touch and single touch
        InputHandler handle = InputHandler.getInstance();

        // Loop through all the pointers that we detected and
        // process them as normal touch events.
        for(int i = 0; i < handle.getTouchCount(mo); i++) {
            int tx = (int) handle.getX(mo, i);
            int ty = (int) handle.getY(mo, i);

            // Bottom paddle moves when we are playing in one or two player mode and the touch
            // was in the lower quartile of the screen.
            if(rightPaddle.player && rightPaddle.inTouchbox(tx,ty)) {
                rightPaddle.destination = tx;
            }
            else if(leftPaddle.player && leftPaddle.inTouchbox(tx,ty)) {
                leftPaddle.destination = tx;
            }
            else if(mo.getAction() == MotionEvent.ACTION_DOWN && mPauseTouchBox.contains(tx, ty)) {
                if(mCurrentState != State.Stopped) {
                    mLastState = mCurrentState;
                    mCurrentState = State.Stopped;
                }
                else {
                    mCurrentState = mLastState;
                    mLastState = State.Stopped;
                }
            }

            // In case a player wants to join in...
            if(mo.getAction() == MotionEvent.ACTION_DOWN) {
                if(!rightPaddle.player && rightPaddle.inTouchbox(tx,ty)) {
                    rightPaddle.player = true;
                }
                else if(!leftPaddle.player && leftPaddle.inTouchbox(tx,ty)) {
                    leftPaddle.player = true;
                }
            }
        }

        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if(!gameRunning()) return false;

        if(rightPaddle.player == false) {
            rightPaddle.player = true;
            rightPaddle.destination = rightPaddle.centerX();
        }

        switch(event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                rightPaddle.destination = (int) Math.max(0, Math.min(getWidth(), rightPaddle.destination + SCROLL_SENSITIVITY * event.getX()));
                break;
        }

        return true;
    }

    /**
     * Reset the lives, paddles and the like for a new game.
     */
    public void newGame() {
        resetPaddles();
        serveBall();
        resumeLastState();
    }

    /**
     * Resets the lives and the position of the paddles.
     */
    private void resetPaddles() {
        int mid = getWidth() / 2;
        leftPaddle.setPosition(mid);
        rightPaddle.setPosition(mid);
        leftPaddle.destination = mid;
        rightPaddle.destination = mid;
        leftPaddle.setLives(STARTING_LIVES);
        rightPaddle.setLives(STARTING_LIVES);
    }

    /**
     * This is kind of useless as well.
     */
    private void resumeLastState() {
        if(mLastState == State.Stopped && mCurrentState == State.Stopped) {
            mCurrentState = State.Running;
        }
        else if(mCurrentState != State.Stopped) {
            // Do nothing
        }
        else if(mLastState != State.Stopped) {
            mCurrentState = mLastState;
            mLastState = State.Stopped;
        }
    }

    public boolean gameRunning() {
        return this.initialized && leftPaddle != null && rightPaddle != null
                && leftPaddle.living() && rightPaddle.living();
    }

    public void pause() {
        mLastState = mCurrentState;
        mCurrentState = State.Stopped;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    public void setPlayerControl(boolean p1, boolean p2) {
        if(p1) {
            leftPaddlePlayer = p1;
        } else if (p2) {
            rightPaddlePlayer = p2;
        }else {
            leftPaddlePlayer = false;
            rightPaddlePlayer = false;
        }
    }

    public void resume() {
        mContinue = true;
        update();
    }

    public void stop() {
        mContinue = false;
    }

    /**
     * Release all resource locks.
     */
    public void release() {
        //mPool.release();
    }
/*
    public void toggleMuted() {
        this.setMuted(!mMuted);
    }

    public void setMuted(boolean b) {
        // Set the in-memory flag
        mMuted = b;

        // Grab a preference editor
        Context ctx = this.getContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = settings.edit();

        // Save the value
        editor.putBoolean(Pong.PREF_MUTED, b);
        editor.commit();

        // Output a toast to the user
        int rid = (mMuted) ? R.string.sound_disabled : R.string.sound_enabled;
        Toast.makeText(ctx, rid, Toast.LENGTH_SHORT).show();
    }

    private void playSound(int rid) {
        if(mMuted == true) return;
        mPool.play(rid, 0.2f, 0.2f, 1, 0, 1.0f);
    }
*/
}


