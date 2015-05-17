// * This code is licensed under:
// * JHPlot License, Version 1.2
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2007 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
// * Statement: This package is rewrite of the Browser3D. It's free. I do not know 
// * the author name. 


package jhplot.v3d;
// Trig.java

class Trig 
{
    static final float radiansPerDegree = (float) (Math.PI / 180);

    static float sin(float a)
    {
        return (float) Math.sin(a * radiansPerDegree);
    }

    static float cos(float a)
    {
        return (float) Math.cos(a * radiansPerDegree);
    }

}
