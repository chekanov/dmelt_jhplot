// * This code is licensed under:
// * JHPlot License, Version 1.2
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2007 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
// * Statement: This package is rewrite of the Browser3D. It's free. I do not know 
// * the author name. 


package jhplot.v3d;
// OldTrig.java

class OldTrig 
{
    static boolean exact = true;
    static final int dim = 3600;
    static float stab[] = new float[dim];
    static float ctab[] = new float[dim];
    static boolean btab[] = new boolean[dim];
    static
    {
        for (int i = 0; i < dim; i++)
            btab[i] = false;
    }
    static final float radiansPerDegree = (float) (Math.PI / 180);

    synchronized static void setExact(boolean e)
    {
        exact = e;
    }

    synchronized static float sin(float a)
    {
        if (exact)
            return (float) Math.sin(a * radiansPerDegree);
        int i = (int) (a * 10) % dim;
        if (i < 0)
            i += dim;
        if (btab[i])
            return stab[i];
        double b = a * radiansPerDegree;
        stab[i] = (float) Math.sin(b);
        ctab[i] = (float) Math.cos(b);
        btab[i] = true;
        return stab[i];
    }

    synchronized static float cos(float a)
    {
        if (exact)
            return (float) Math.cos(a * radiansPerDegree);
        int i = (int) (a * 10) % dim;
        if (i < 0)
            i += dim;
        if (btab[i])
            return ctab[i];
        double b = a * radiansPerDegree;
        stab[i] = (float) Math.sin(b);
        ctab[i] = (float) Math.cos(b);
        btab[i] = true;
        return ctab[i];
    }

}
