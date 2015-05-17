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
 * Vector manipulations 
 *
 */


public class Vector3d 
{
    float x, y, z;

    public Vector3d() 
    {
    }

    public Vector3d(Vector3d v)
    {
        copy(v);
    }

     
    public Vector3d(float x, float y, float z) 
    { 
        set(x, y, z);
    }

    public void copy(Vector3d v)
    {
        x = v.x;
        y = v.y;
        z = v.z;
    }


    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public float getZ()
    {
        return z;
    }

 
    public void set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(Vector3d v)
    { 
        x += v.x; y += v.y; z += v.z;
    }

    public void sub(Vector3d v)
    { 
        x -= v.x; y -= v.y; z -= v.z;
    }

    public void cmul(Vector3d v)
    { 
        x *= v.x; y *= v.y; z *= v.z;
    }

    public float dot(Vector3d v)
    { 
        return x * v.x + y * v.y + z * v.z;
    }

    public void mul(Vector3d v)
    {
        float tx = x, ty = y, tz = z;
        x = ty * v.z - tz * v.y;
        y = tz * v.x - tx * v.z;
        z = tx * v.y - ty * v.x;
    }

    public void mul(float f)
    { 
        x *= f; y *= f; z *= f;
    }

    public void div(float f)
    { 
        x /= f; y /= f; z /= f;
    }

    public boolean equals(Vector3d v)
    { 
        if (v == null)
            return false;
        return x == v.x && y == v.y && z == v.z;
    }

    public float vabs()
    { 
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize()
    { 
        float t = vabs();
        x /= t; y /= t; z /= t;
    }

    float cos(Vector3d v)
    { 
        return dot(v) / (vabs() * v.vabs());
    }

    float sin(Vector3d v)
    { 
        Vector3d t = new Vector3d(this);
        t.mul(v);
        return t.vabs() / (vabs() * v.vabs());
    }

    public String toString()
    {
        return "[" + x + ", " + y + ", " + z + "]";
    }

    public void print()
    {
        System.out.println(toString());
    }

}
