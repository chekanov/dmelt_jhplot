// * This code is licensed under:
// * JHPlot License, Version 1.2
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2007 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
// * Statement: This package is rewrite of the Browser3D. It's free. I do not know 
// * the author name. 


package jhplot.v3d;


/**
 * Build a cone.
 * 
 * @author S.Chekanov 
 *
 */

public class Cone extends Pyramid
{
    public Cone(Model3d md, float r, int nv, Vector3d d)
    {
        super(md,Cylinder.computeBase(r,nv,d),nv,d);
    }

    public Cone(Model3d md, float r, int nv, float d)
    {
        this(md, r, nv, new Vector3d(0,0,d));
    }

}
