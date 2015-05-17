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
 * Build a sphere.
 * 
 * @author S.Chekanov
 *
 */

public class Sphere extends Object3d
{

	/**
	 * Build a sphere.
	 * @param md current model
	 * @param r  radius
	 * @param m  number of divisions in X for the surface
	 * @param n  number of divisions in Y for the surface
	 */
    public Sphere(Model3d md, float r, int m, int n)
    {
        super(md, (m-1)*n+2, m*n);
        Vector3d v = new Vector3d(0,0,r);
        Vector3d a = new Vector3d();
        Vector3d b = new Vector3d();
        Matrix3d t = new Matrix3d();
        addVertex(v);
        for (int i = 1; i < m; i++)
        {
            t.unit();
            t.xrot(i*180.0f/m);
            t.transform(v,a);
            for (int j = 0; j < n; j++)
            {
                t.unit();
                t.zrot(j*360.0f/n);
                t.transform(a,b);
                addVertex(b);
            }
        }
        v.mul(-1);
        addVertex(v);
        int p[] = new int[n+1];
        for (int j = 0; j < n; j++)
            p[j] = j + 1;
        p[n] = 1;
        for (int j = 0; j < n; j++)
        {
            Face3d f = face[addFace(3, getFaceColor(0,j))];
            f.addVertex(vert[0]);
            f.addVertex(vert[p[j]]);
            f.addVertex(vert[p[j+1]]);
        }
        for (int i = 1; i < m - 1; i++)
        {
            for (int j = 0; j < n; j++)
            {
                Face3d f = face[addFace(4, getFaceColor(i,j))];
                f.addVertex(vert[p[j]]);
                f.addVertex(vert[p[j]+n]);
                f.addVertex(vert[p[j+1]+n]);
                f.addVertex(vert[p[j+1]]);
            }
            for (int j = 0; j <= n; j++)
                p[j] += n;
        }
        for (int j = 0; j < n; j++)
        {
            Face3d f = face[addFace(3, getFaceColor(m-1,j))];
            f.addVertex(vert[(m-1)*n+1]);
            f.addVertex(vert[p[j]]);
            f.addVertex(vert[p[j+1]]);
        }
        colectNormals();
    }

    Color getFaceColor(int i, int j)
    {
        if ((i + j) % 2 == 0)
            return Color.red;
        else
            return Color.yellow;
    }

}
