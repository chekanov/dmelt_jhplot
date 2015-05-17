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
 * Build a Cylinder.
 * 
 * @author S.Chekanov 
 *
 */


public class Cylinder extends Prism
{
    Cylinder(Model3d md, float r, int nv, Vector3d d)
    {
        super(md,computeBase(r,nv,d),nv,d);
    }

    public Cylinder(Model3d md, float r, int nv, float d)
    {
        this(md, r, nv, new Vector3d(0,0,d));
    }

    static Vector3d[] computeBase(float r, int nv, Vector3d d)
    {
        Vector3d v[] = new Vector3d[nv];
        v[0] = new Vector3d(r,0,0);
        for (int i = 1; i < nv; i++)
        {
            Matrix3d m = new Matrix3d();
            m.zrot(360.0f / nv * i);
            v[i] = new Vector3d(v[0]);
            m.transform(v[i],v[i]);
        }
        return v;
    }

}
