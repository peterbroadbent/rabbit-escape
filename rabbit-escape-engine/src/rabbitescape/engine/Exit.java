package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.HashMap;
import java.util.Map;

public class Exit extends Thing
{
    public Exit( int x, int y )
    {
        super( x, y, EXIT );
    }

    @Override
    public void calcNewState( World world )
    {
    }

    @Override
    public void step( World world )
    {
    }

    @Override
    public Map<String, String> saveState()
    {
        return new HashMap<String, String>();
    }

    @Override
    public void restoreFromState( Map<String, String> state )
    {
    }
}
