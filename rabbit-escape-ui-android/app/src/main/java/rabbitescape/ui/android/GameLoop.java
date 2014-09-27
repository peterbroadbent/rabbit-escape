package rabbitescape.ui.android;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import java.util.Date;
import java.util.Random;

public class GameLoop implements Runnable
{
    private final int num_balls = 500;
    private final long max_allowed_skips = 10;
    private final long simulation_time_step_ms = 5;
    private final long frame_time_ms = 20;
    private final SurfaceHolder surfaceHolder;

    private boolean running = true;
    private final Ball[] balls;
    private final Paint paint;
    private Bitmap ballBitmap;

    public GameLoop( SurfaceHolder surfaceHolder, Resources resources )
    {
        this.surfaceHolder = surfaceHolder;

        Random rand = new Random();

        this.balls = new Ball[num_balls];
        for (int i = 0; i < num_balls; ++i)
        {
            balls[i] = new Ball( rand );
        }

        paint = new Paint();

        ballBitmap = BitmapFactory.decodeResource( resources, R.drawable.ball );
    }

    @Override
    public void run()
    {
        long simulation_time = new Date().getTime();
        long frame_start_time = simulation_time;

        while( running )
        {
            processInput();
            simulation_time = doPhysics( simulation_time, frame_start_time );
            drawGraphics();
            frame_start_time = waitForNextFrame( frame_start_time );
        }
    }

    private long waitForNextFrame( long frame_start_time )
    {
        long next_frame_start_time = new Date().getTime();

        long how_long_we_took = next_frame_start_time - frame_start_time;
        long wait_time = frame_time_ms - how_long_we_took;

        if ( wait_time > 0 )
        {
            try
            {
                Thread.sleep( wait_time );
            }
            catch ( InterruptedException e )
            {
                // Should never happen
                e.printStackTrace();
            }
        }

        return next_frame_start_time;
    }

    private void drawGraphics()
    {
        Canvas canvas = surfaceHolder.lockCanvas();

        if ( canvas == null )
        {
            return;
        }

        try
        {
            synchronized ( surfaceHolder )
            {
                actuallyDrawGraphics( canvas );
            }
        }
        finally
        {
            surfaceHolder.unlockCanvasAndPost( canvas );
        }
    }

    private void actuallyDrawGraphics( Canvas canvas )
    {
        canvas.drawColor( Color.WHITE );

        for ( Ball ball : balls )
        {
            canvas.drawBitmap(
                ballBitmap,
                -16f + ( ( ball.x / 100f ) * canvas.getWidth() ),
                -16f + ( ( ball.y / 100f ) * canvas.getHeight() ),
                paint
            );
        }
    }

    private long doPhysics( long simulation_time, long frame_start_frame )
    {
        for ( int skipped = 0; skipped < max_allowed_skips; ++skipped )
        {
            if ( simulation_time >= frame_start_frame )
            {
                break;
            }

            moveBalls();
            simulation_time += simulation_time_step_ms;
        }

        return simulation_time;
    }

    private void moveBalls()
    {
        for ( Ball ball : balls )
        {
            ball.step();
        }
    }

    private void processInput()
    {
    }

    public void pleaseStop()
    {
        running = false;
    }
}