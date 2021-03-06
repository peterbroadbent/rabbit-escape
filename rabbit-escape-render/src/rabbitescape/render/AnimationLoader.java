package rabbitescape.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.err.RabbitEscapeException;

public class AnimationLoader
{
    public static final String NONE = "<none>";

    public static class AnimationNotFound extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String name;

        public AnimationNotFound( String name )
        {
            this.name = name;
        }
    }

    public static class BadAnimationLine extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String line;

        public BadAnimationLine( String line )
        {
            this.line = line;
        }

        public BadAnimationLine( String line, Throwable cause )
        {
            super( cause );
            this.line = line;
        }
    }

    public static class ErrorLoadingAnimationNames extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public ErrorLoadingAnimationNames( Throwable cause )
        {
            super( cause );
        }
    }

    public static String[] animationFilesInResource( String lsResourceName )
    {
        try
        {
            return stringArray(
                chain(
                    Arrays.asList( NONE ),
                    map(
                        stripLast( 4 ),
                        filter(
                            endsWith( ".rea" ),
                            resourceLines( lsResourceName )
                        )
                    )
                )
            );
        }
        catch ( ReadingResourceFailed e )
        {
            throw new ErrorLoadingAnimationNames( e );
        }
    }

    public static String[] listAll()
    {
        return animationFilesInResource(
            "/rabbitescape/render/animations/ls.txt" );
    }

    public static Animation load( String name )
    {
        try
        {
            String key = "/rabbitescape/render/animations/" + name + ".rea";

            URL url = AnimationLoader.class.getResource( key );
            if ( url == null )
            {
                throw new AnimationNotFound( name );
            }
            InputStream stream = url.openStream();

            return readAnimation( stream );
        }
        catch ( IOException e )
        {
            throw new AnimationNotFound( name );
        }
    }

    public static Animation readAnimation( InputStream stream )
        throws IOException
    {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader( stream ) );

        List<FrameNameAndOffset> ret = new ArrayList<>();
        String ln;
        while ( ( ln = reader.readLine() ) != null )
        {
            String trimmedLn = ln.trim();
            if ( !trimmedLn.isEmpty() )
            {
                ret.add( frameNameAndOffset( trimmedLn ) );
            }
        }

        return new Animation( ret );
    }

    private static FrameNameAndOffset frameNameAndOffset( String animLine )
    {
        String[] parts = animLine.split( " " );

        try
        {
            switch ( parts.length )
            {
                case 1:
                {
                    return new FrameNameAndOffset( parts[0] );
                }
                case 2:
                {
                    return new FrameNameAndOffset(
                        parts[0], Integer.valueOf( parts[1] ) );
                }
                case 3:
                {
                    return new FrameNameAndOffset(
                        parts[0],
                        Integer.valueOf( parts[1] ),
                        Integer.valueOf( parts[2] )
                    );
                }
                default:
                {
                    throw new BadAnimationLine( animLine );
                }
            }
        }
        catch ( NumberFormatException e )
        {
            throw new BadAnimationLine( animLine, e );
        }
    }
}
