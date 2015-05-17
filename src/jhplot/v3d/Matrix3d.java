// * This code is licensed under:
// * JHPlot License, Version 1.2
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2007 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
// * Statement: This package is rewrite of the Browser3D. It's free. I do not know 
// * the author name. 


package jhplot.v3d;
// Matrix3d.java

class Matrix3d 
{
    float xx, yx, zx;
    float xy, yy, zy;
    float xz, yz, zz;
    float xo, yo, zo;

    Matrix3d()
    {
        unit();
    }

    Matrix3d(Matrix3d m)
    {
        copy(m);
    }

    void unit() 
    {
        xx = 1.0f; xy = 0.0f; xz = 0.0f; xo = 0.0f;
        yx = 0.0f; yy = 1.0f; yz = 0.0f; yo = 0.0f;
        zx = 0.0f; zy = 0.0f; zz = 1.0f; zo = 0.0f;
    }

    void copy(Matrix3d m)
    {
        xx = m.xx; xy = m.xy; xz = m.xz; xo = m.xo;
        yx = m.yx; yy = m.yy; yz = m.yz; yo = m.yo;
        zx = m.zx; zy = m.zy; zz = m.zz; zo = m.zo;
    }

    void xrot(float theta) 
    {
        float ct = Trig.cos(theta);
        float st = Trig.sin(theta);

        float tyx = (float) (yx * ct + zx * st);
        float tyy = (float) (yy * ct + zy * st);
        float tyz = (float) (yz * ct + zz * st);
        float tyo = (float) (yo * ct + zo * st);

        float tzx = (float) (zx * ct - yx * st);
        float tzy = (float) (zy * ct - yy * st);
        float tzz = (float) (zz * ct - yz * st);
        float tzo = (float) (zo * ct - yo * st);

        yx = tyx; yy = tyy; yz = tyz; yo = tyo;
        zx = tzx; zy = tzy; zz = tzz; zo = tzo;
    }

    void yrot(float theta) 
    {
        float ct = Trig.cos(theta);
        float st = Trig.sin(theta);

        float txx = (float) (xx * ct + zx * st);
        float txy = (float) (xy * ct + zy * st);
        float txz = (float) (xz * ct + zz * st);
        float txo = (float) (xo * ct + zo * st);

        float tzx = (float) (zx * ct - xx * st);
        float tzy = (float) (zy * ct - xy * st);
        float tzz = (float) (zz * ct - xz * st);
        float tzo = (float) (zo * ct - xo * st);

        xx = txx; xy = txy; xz = txz; xo = txo;
        zx = tzx; zy = tzy; zz = tzz; zo = tzo;
    }

    void zrot(float theta) 
    {
        float ct = Trig.cos(theta);
        float st = Trig.sin(theta);

        float tyx = (float) (yx * ct + xx * st);
        float tyy = (float) (yy * ct + xy * st);
        float tyz = (float) (yz * ct + xz * st);
        float tyo = (float) (yo * ct + xo * st);

        float txx = (float) (xx * ct - yx * st);
        float txy = (float) (xy * ct - yy * st);
        float txz = (float) (xz * ct - yz * st);
        float txo = (float) (xo * ct - yo * st);

        yx = tyx; yy = tyy; yz = tyz; yo = tyo;
        xx = txx; xy = txy; xz = txz; xo = txo;
    }

    void scale(float f) 
    {
        xx *= f; xy *= f; xz *= f; xo *= f;
        yx *= f; yy *= f; yz *= f; yo *= f;
        zx *= f; zy *= f; zz *= f; zo *= f;
    }

    void scale(float xf, float yf, float zf)
    {
        xx *= xf; xy *= xf; xz *= xf; xo *= xf;
        yx *= yf; yy *= yf; yz *= yf; yo *= yf;
        zx *= zf; zy *= zf; zz *= zf; zo *= zf;
    }

    void trans(float x, float y, float z) 
    {
        xo += x; yo += y; zo += z;
    }

    void mul(Matrix3d m) 
    {
        float txx = xx * m.xx + yx * m.xy + zx * m.xz;
        float txy = xy * m.xx + yy * m.xy + zy * m.xz;
        float txz = xz * m.xx + yz * m.xy + zz * m.xz;
        float txo = xo * m.xx + yo * m.xy + zo * m.xz + m.xo;

        float tyx = xx * m.yx + yx * m.yy + zx * m.yz;
        float tyy = xy * m.yx + yy * m.yy + zy * m.yz;
        float tyz = xz * m.yx + yz * m.yy + zz * m.yz;
        float tyo = xo * m.yx + yo * m.yy + zo * m.yz + m.yo;

        float tzx = xx * m.zx + yx * m.zy + zx * m.zz;
        float tzy = xy * m.zx + yy * m.zy + zy * m.zz;
        float tzz = xz * m.zx + yz * m.zy + zz * m.zz;
        float tzo = xo * m.zx + yo * m.zy + zo * m.zz + m.zo;

        xx = txx; xy = txy; xz = txz; xo = txo;
        yx = tyx; yy = tyy; yz = tyz; yo = tyo;
        zx = tzx; zy = tzy; zz = tzz; zo = tzo;
    }

    void transform(Vector3d v, Vector3d tv) 
    {
        float x = v.x;
        float y = v.y;
        float z = v.z;
        tv.x = x * xx + y * xy + z * xz + xo;
        tv.y = x * yx + y * yy + z * yz + yo;
        tv.z = x * zx + y * zy + z * zz + zo;
    }

    void transform(Vector3d v[], Vector3d tv[], int nvert) 
    {
        for (int i = 0; i < nvert; i++) 
        {
            Vector3d w = v[i];
            float x = w.x;
            float y = w.y;
            float z = w.z;
            w = tv[i];
            w.x = x * xx + y * xy + z * xz + xo;
            w.y = x * yx + y * yy + z * yz + yo;
            w.z = x * zx + y * zy + z * zz + zo;
        }
    }

    public String toString() 
    {
        return "[" + xx + ", " + yx + ", " + zx + "]\n"
             + "[" + xy + ", " + yy + ", " + zy + "]\n"
             + "[" + xz + ", " + yz + ", " + zz + "]\n"
             + "[" + xo + ", " + yo + ", " + zo + "]\n";
    }

}
