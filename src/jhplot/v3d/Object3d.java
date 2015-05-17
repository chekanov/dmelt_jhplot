// * This code is licensed under:
// * JHPlot License, Version 1.2
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2007 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
// * Statement: This package is rewrite of the Browser3D. It's free. I do not know 
// * the author name. 


package jhplot.v3d;
// Object3d.java

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

public class Object3d extends Position implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
    Model3d md;
    Vector3d vert[], overt[], norm[], onorm[];
    Vector3d center, ocenter;
    Face3d face[];
    int nvert, cvert, nface, cface;

    public Object3d(Model3d md, int nvert, int nface)
    {
        name = null;
        this.md    = md;
        this.nvert = nvert;
        this.nface = nface;
        if (nvert <= 4 || nface <= 4)
            return;
        vert  = new Vector3d[nvert];
        overt = new Vector3d[nvert];
        norm  = new Vector3d[nface];
        onorm = new Vector3d[nface];
        center  = new Vector3d();
        ocenter = new Vector3d();
        face  = new Face3d[nface];
        cvert = cface = 0;
    }

    public Object3d(String name, Model3d md, int nvert, int nface)
    {
        this(md, nvert, nface);
        this.name = name;
    }

    public int addVertex(Vector3d v)
    {
        return addVertex(v.x, v.y, v.z);
    }

    public int addVertex(float x, float y, float z)
    {
        if (cvert >= nvert)
            return -1;
        vert[cvert]  = new Vector3d(x,y,z);
        overt[cvert] = new Vector3d(x,y,z);
        return cvert++;
    }

    public int addFace(int nvert, Color c)
    {
        return addFace(nvert, c.getRed(), c.getGreen(), c.getBlue());
    }

    public int addFace(int nvert, int red, int green, int blue)
    {
        if (cface >= nface)
            return -1;
        face[cface] = new Face3d(this,nvert,red,green,blue);
        return cface++;
    }

    public int addFace(int nvert)
    {
        int i = addFace(nvert, 0, 0, 0);
        face[i].dummy = true;
        return i;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setColor(Color c)
    {
        setColor(c.getRed(), c.getGreen(), c.getBlue());
    }

    public void setColor(int red, int green, int blue)
    {
        if (cvert != nvert || cface != nface)
            return;
        for (int i = 0; i < nface; i++)
            face[i].setColor(red, green, blue);
    }

    void setDummy(boolean dummy)
    {
        for (int i = 0; i < nface; i++)
            face[i].setDummy(dummy);
    }

    void transformToModelSpace()
    {
        if (cvert != nvert || cface != nface)
            return;

        Matrix3d m = new Matrix3d();
        m.mul(rot);
        m.mul(scale);
        m.mul(trans);
        m.transform(ocenter,center);
        m.transform(overt,vert,nvert);

        m.unit();
        m.mul(rot);
        m.transform(onorm,norm,nface);
    }

    void transformToCameraSpace()
    {
        if (cvert != nvert || cface != nface)
            return;

        Matrix3d m = new Matrix3d();
        m.mul(rot);
        m.mul(scale);
        m.mul(trans);
        m.mul(md.rot);
        m.mul(md.scale);
        m.mul(md.trans);
        m.transform(ocenter,center);
        m.transform(overt,vert,nvert);

        m.unit();
        m.mul(rot);
        m.mul(md.rot);
        m.transform(onorm,norm,nface);
    }

    void transformToScreenSpace()
    {
        if (cvert != nvert || cface != nface)
            return;

        Matrix3d m = new Matrix3d();
        m.mul(rot);
        m.mul(scale);
        m.mul(trans);
        m.mul(md.rot);
        m.mul(md.scale);
        m.mul(md.trans);
        m.transform(ocenter,center);
        m.mul(md.mat);
        m.transform(overt,vert,nvert);

        m.unit();
        m.mul(rot);
        m.mul(md.rot);
        m.transform(onorm,norm,nface);

        if (md.persp)
        {
            for (int i = 0; i < nface; i++)
                face[i].computeCenter();
            float hw = md.width / 2;
            float hh = md.height / 2;
            float zt = md.zTarget;
            for (int i = 0; i < nvert; i++)
            {
                Vector3d v = vert[i];
                if (v.z < 0)
                {
                    float p = zt / v.z;
                    v.x = hw + v.x * p;
                    v.y = hh - v.y * p;
                }
                else
                    setDummy(true);
            }
        }
    }

    void paint(Graphics g)
    {
        if (cvert != nvert || cface != nface)
            return;
        for (int i = 0; i < nface; i++)
        {
            Face3d f = face[i];
            if (f.visible())
                f.paint(g);
        }
    }

    int inside(int x, int y)
    {
        if (cvert != nvert || cface != nface)
            return -1;
        for (int i = 0; i < nface; i++)
        {
            Face3d f = face[i];
            if (f.visible())
                if (f.inside(x,y))
                    return i;
        }
        return -1;
    }

    void computeCenter()
    {
        Vector3d c = ocenter;
        c.set(0, 0, 0);
        for (int i = 0; i < nvert; i++)
            c.add(overt[i]);
        c.div(nvert);
    }

    void colectNormals()
    {
        if (cvert != nvert || cface != nface)
            return;
        computeCenter();
        for (int i = 0; i < nface; i++)
        {
            Face3d f = face[i];
            Vector3d v = new Vector3d(ocenter);
            v.sub(f.vert[0]);
            if (v.dot(f.normal) > 0)
                f.normal.mul(-1.0f);
            onorm[i] = f.normal;
            f.normal = norm[i] = new Vector3d();
        }
    }

}
