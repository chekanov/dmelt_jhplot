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
 * Build a cube
 * @author S.Chekanov 
 *
 */

public class Cube extends Object3d
{
    public Cube(Model3d md, float l)
    {
        super(md,8,6);
        float h = l / 2;
        float g = -h;
        Vector3d v0 = vert[addVertex(g,g,g)];
        Vector3d v1 = vert[addVertex(h,g,g)];
        Vector3d v2 = vert[addVertex(h,h,g)];
        Vector3d v3 = vert[addVertex(g,h,g)];
        Vector3d v4 = vert[addVertex(g,g,h)];
        Vector3d v5 = vert[addVertex(h,g,h)];
        Vector3d v6 = vert[addVertex(h,h,h)];
        Vector3d v7 = vert[addVertex(g,h,h)];
        Face3d f0 = face[addFace(4,255,0,0)];
        f0.addVertex(v0);
        f0.addVertex(v1);
        f0.addVertex(v2);
        f0.addVertex(v3);
        Face3d f1 = face[addFace(4,255,0,0)];
        f1.addVertex(v4);
        f1.addVertex(v5);
        f1.addVertex(v6);
        f1.addVertex(v7);
        Face3d f2 = face[addFace(4,0,255,0)];
        f2.addVertex(v0);
        f2.addVertex(v1);
        f2.addVertex(v5);
        f2.addVertex(v4);
        Face3d f3 = face[addFace(4,0,255,0)];
        f3.addVertex(v2);
        f3.addVertex(v3);
        f3.addVertex(v7);
        f3.addVertex(v6);
        Face3d f4 = face[addFace(4,0,0,255)];
        f4.addVertex(v0);
        f4.addVertex(v3);
        f4.addVertex(v7);
        f4.addVertex(v4);
        Face3d f5 = face[addFace(4,0,0,255)];
        f5.addVertex(v1);
        f5.addVertex(v2);
        f5.addVertex(v6);
        f5.addVertex(v5);
        colectNormals();
    }

}

