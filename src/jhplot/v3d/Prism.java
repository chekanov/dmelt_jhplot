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
 * Build a prism.
 * 
 * @author S.Chekanov 
 *
 */
public class Prism extends Object3d
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Prism(Model3d md, Vector3d v[], int nv, Vector3d d)
    {
        super(md, nv*2, nv+2);
        for (int i = 0; i < nv; i++)
            addVertex(v[i]);
        for (int i = 0; i < nv; i++)
            addVertex(v[i].x+d.x, v[i].y+d.y, v[i].z+d.z);
        Face3d b0 = face[addFace(nv, getBaseColor(0))];
        for (int i = 0; i < nv; i++)
            b0.addVertex(vert[i]);
        for (int i = 0; i < nv; i++)
        {
            Face3d f = face[addFace(4, getFaceColor(i))];
            int j = (i + 1) % nv;
            f.addVertex(vert[i]);
            f.addVertex(vert[j]);
            f.addVertex(vert[j+nv]);
            f.addVertex(vert[i+nv]);
        }
        Face3d b1 = face[addFace(nv, getBaseColor(1))];
        for (int i = 0; i < nv; i++)
            b1.addVertex(vert[i+nv]);
        colectNormals();
    }

    Prism(Model3d md, Vector3d v[], int nv, float d)
    {
        this(md, v, nv, new Vector3d(0,0,d));
    }

    Color getBaseColor(int b)
    {
        if (b != 0)
            return Color.red;
        else
            return Color.magenta;
    }

    Color getFaceColor(int f)
    {
        if (f % 2 == 0)
            return Color.green;
        else
            return Color.yellow;
    }

}
