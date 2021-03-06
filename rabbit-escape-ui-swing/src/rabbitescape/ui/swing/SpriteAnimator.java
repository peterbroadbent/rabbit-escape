package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rabbitescape.engine.*;
import rabbitescape.render.Animation;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.Sprite;

public class SpriteAnimator
{
    private final World world;
    private final SwingBitmapScaler scaler;
    private final String[] land_block;
    private final String[] land_rising_right;
    private final String[] land_rising_left;
    private final String bridge_rising_right;
    private final String bridge_rising_left;
    private final int tileSize;
    private final BitmapCache<SwingBitmap> bitmapCache;
    private final AnimationCache animationCache;

    public SpriteAnimator(
        World world,
        ChangeDescription changes,
        int tileSize,
        BitmapCache<SwingBitmap> bitmapCache,
        AnimationCache animationCache
    )
    {
        this.world = world;
        this.scaler = new SwingBitmapScaler();
        this.tileSize = tileSize;
        this.bitmapCache = bitmapCache;
        this.animationCache = animationCache;

        this.land_block = new String[]
        {
            "/rabbitescape/ui/swing/images32/land_block_1.png",
            "/rabbitescape/ui/swing/images32/land_block_2.png",
            "/rabbitescape/ui/swing/images32/land_block_3.png",
            "/rabbitescape/ui/swing/images32/land_block_4.png",
        };

        this.land_rising_right = new String[]
        {
            "/rabbitescape/ui/swing/images32/land_rising_right_1.png",
            "/rabbitescape/ui/swing/images32/land_rising_right_2.png",
            "/rabbitescape/ui/swing/images32/land_rising_right_3.png",
            "/rabbitescape/ui/swing/images32/land_rising_right_4.png"
        };

        this.land_rising_left = new String[]
        {
            "/rabbitescape/ui/swing/images32/land_rising_left_1.png",
            "/rabbitescape/ui/swing/images32/land_rising_left_2.png",
            "/rabbitescape/ui/swing/images32/land_rising_left_3.png",
            "/rabbitescape/ui/swing/images32/land_rising_left_4.png"
        };

        this.bridge_rising_right =
            "/rabbitescape/ui/swing/images32/bridge_rising_right.png";

        this.bridge_rising_left =
            "/rabbitescape/ui/swing/images32/bridge_rising_left.png";
    }

    public Sprite[] getSprites( int frameNum )
    {
        List<Sprite> ret = new ArrayList<>();

        for ( Block block : world.blocks )
        {
            ret.add(
                new Sprite(
                    bitmapForBlock( block ),
                    scaler,
                    block.x,
                    block.y,
                    tileSize,
                    0,
                    0
                )
            );
        }

        for ( Thing thing : world.things )
        {
            drawThing( frameNum, ret, thing );
        }

        for ( Rabbit rabbit : world.rabbits )
        {
            drawThing( frameNum, ret, rabbit );
        }

        for ( Thing thing : world.changes.tokensAboutToAppear() )
        {
            drawThing( frameNum, ret, thing );
        }

        return ret.toArray( new Sprite[ret.size()] );
    }

    private void drawThing( int frameNum, List<Sprite> ret, Thing thing )
    {
        String frame = thing.state.name().toLowerCase( Locale.ENGLISH );
        Animation animation = animationCache.get( frame );

        if ( animation == null )
        {
            System.out.println( "Missing animation for state " + thing.state );
            return;
        }

        // TODO: don't make a new one of these every time?
        SwingAnimation swingAnimation = new SwingAnimation(
            bitmapCache, animation );

        SwingBitmapAndOffset bmp = swingAnimation.get( frameNum );

        ret.add(
            new Sprite(
                bmp.bitmap,
                scaler,
                thing.x,
                thing.y,
                tileSize,
                bmp.offsetX,
                bmp.offsetY
            )
        );
    }

    private SwingBitmap bitmapForBlock( Block block )
    {
        return bitmapCache.get( bitmapNameForBlock( block ) );
    }

    private String bitmapNameForBlock( Block block )
    {
        switch( block.type )
        {
            case solid_flat:      return land_block[block.variant];
            case solid_up_right:  return land_rising_right[block.variant];
            case solid_up_left:   return land_rising_left[block.variant];
            case bridge_up_right: return bridge_rising_right;
            case bridge_up_left:  return bridge_rising_left;
            default:
                throw new RuntimeException(
                    "Unknown block type " + block.type );
        }
    }
}
