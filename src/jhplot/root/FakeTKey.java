
package jhplot.root;

import hep.io.root.RootClass;
import hep.io.root.RootObject;
import hep.io.root.interfaces.TKey;
import hep.io.root.interfaces.TNamed;
import java.io.IOException;


class FakeTKey
    implements TKey
{

    FakeTKey(TNamed target)
    {
        this.target = target;
    }

    public int getBits()
    {
        return 0;
    }

    public short getCycle()
    {
        return 1;
    }

    public String getName()
    {
        return target.getName();
    }

    public RootObject getObject()
        throws IOException
    {
        return target;
    }

    public RootClass getObjectClass()
    {
        return target.getRootClass();
    }

    public RootClass getRootClass()
    {
        return target.getRootClass();
    }

    public String getTitle()
    {
        return target.getTitle();
    }

    public int getUniqueID()
    {
        return 0;
    }

    public boolean equals(Object other)
    {
        if(other instanceof FakeTKey)
            return target.equals(((FakeTKey)other).target);
        else
            return false;
    }

    public int hashCode()
    {
        return target.hashCode();
    }

    public RootObject readObject()
    {
        return target;
    }

    private TNamed target;
}
