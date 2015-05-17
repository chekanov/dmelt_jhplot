// * This code is licensed under:
// * JHPlot License, Version 1.2
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2007 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
// * Statement: This package is rewrite of the Browser3D. It's free. I do not know 
// * the author name. 


package jhplot.v3d;
// Position.java

public class Position 
{
    Matrix3d rot, scale, trans;

    public Position()
    {
        rot   = new Matrix3d();
        scale = new Matrix3d();
        trans = new Matrix3d();
    }

    public Position(Position p)
    {
        this();
        copyPos(p);
    }

    public void copyPos(Position p)
    {
        rot.copy(p.rot);
        scale.copy(p.scale);
        trans.copy(p.trans);
    }   

    public void setRot(float rx, float ry, float rz)
    { 
        rot.unit();
        incRot(rx, ry, rz);
    }

    public void setScale(float sx, float sy, float sz)
    { 
        scale.unit();
        incScale(sx, sy, sz);
    }

    public void setTrans(float tx, float ty, float tz)
    { 
        trans.unit();
        incTrans(tx, ty, tz);
    }

    public void incRot(float rx, float ry, float rz)
    {
        if (rx != 0)
            rot.xrot(rx);
        if (ry != 0)
            rot.yrot(ry);
        if (rz != 0)
            rot.zrot(rz);
    }

    public void incScale(float sx, float sy, float sz)
    {
        scale.scale(sx, sy, sz);
    }

    public void incTrans(float tx, float ty, float tz)
    {
        trans.trans(tx, ty, tz);
    }

}
