// * This code is licensed under:
// * JHPlot License, Version 1.2
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2007 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
// * Statement: This package is rewrite of the Browser3D. It's free. I do not know 
// * the author name. 


package jhplot.v3d;
import java.awt.Color;


/**
 * Build a Pyramid.
 * 
 * @author S.Chekanov 
 *
 */

public class Pyramid extends Object3d
{
    public Pyramid(Model3d md, Vector3d v[], int nv, Vector3d d)
    {
        super(md, nv+1, nv+1);
        for (int i = 0; i < nv; i++)
            addVertex(v[i]);
        addVertex(d);
        Face3d b = face[addFace(nv, getBaseColor())];
        for (int i = 0; i < nv; i++)
            b.addVertex(vert[i]);
        for (int i = 0; i < nv; i++)
        {
            Face3d f = face[addFace(3, getFaceColor(i))];
            int j = (i + 1) % nv;
            f.addVertex(vert[i]);
            f.addVertex(vert[j]);
            f.addVertex(vert[nv]);
        }
        colectNormals();
    }

    public Pyramid(Model3d md, Vector3d v[], int nv, float d)
    {
        this(md, v, nv, new Vector3d(0,0,d));
    }

    public Color getBaseColor()
    {
        return Color.red;
    }

    public Color getFaceColor(int f)
    {
        if (f % 2 != 0)
            return Color.green;
        else
            return Color.yellow;
    }

}
