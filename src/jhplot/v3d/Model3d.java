// * This code is licensed under:
// * JHPlot License, Version 1.2
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2007 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
// * Statement: This package is rewrite of the Browser3D. It's free. I do not know 
// * the author name. 

package jhplot.v3d;

// Model3d.java

import java.applet.Applet;
import java.awt.Graphics;
import java.util.ArrayList;

public class Model3d extends Position {
	Applet app;

	int width, height;

	Matrix3d mat;

	// Object3d obj[];
	int nobj; // cobj;

	float ambient;

	static final float defaultAmbient = 0.0f;

	public float minScale;

	static final float defaultMinScale = 0.5f;

	public float maxScale;

	static final float defaultMaxScale = 1.0f;

	BoundingBox bb;

	float zCamera, zTarget;

	public boolean persp = true;

	boolean moveAfterPaint = true;

	public ArrayList<Object3d> obj;

	public Model3d(Applet app, int width, int height) {
		this.app = app;
		this.width = width;
		this.height = height;
		obj = new ArrayList<Object3d>();
		nobj = 0;
		ambient = param("ambient", defaultAmbient);
		minScale = param("minscale", defaultMinScale);
		maxScale = param("maxscale", defaultMaxScale);
		if (ambient < 0 || ambient > 1)
			ambient = defaultAmbient;
		if (minScale > maxScale) {
			minScale = defaultMinScale;
			maxScale = defaultMaxScale;
		}
	}

	public String param(String name, String defaultValue) {
		String value = defaultValue;
		if (app != null)
			try {
				value = app.getParameter(name);
			} catch (Exception e) {
				return defaultValue;
			}
		return value;
	}

	public boolean param(String name, boolean defaultValue) {
		boolean value = defaultValue;
		if (app != null)
			try {
				value = Boolean.valueOf(app.getParameter(name)).booleanValue();
			} catch (Exception e) {
				return defaultValue;
			}
		return value;
	}

	public int param(String name, int defaultValue) {
		int value = defaultValue;
		if (app != null)
			try {
				value = Integer.valueOf(app.getParameter(name)).intValue();
			} catch (Exception e) {
				return defaultValue;
			}
		return value;
	}

	public float param(String name, float defaultValue) {
		float value = defaultValue;
		if (app != null)
			try {
				value = Float.valueOf(app.getParameter(name)).floatValue();
			} catch (Exception e) {
				return defaultValue;
			}
		return value;
	}

	public int addObject(Object3d o) {

		obj.add(o);
		nobj = obj.size();
		computeMatrix();

		return nobj;
	}

	synchronized void paint(Graphics g) {

		for (int i = 0; i < nobj; i++)
			((Object3d) obj.get(i)).transformToScreenSpace();

		// obj[i].transformToScreenSpace();
		setPaintOrder();
		for (int i = 0; i < nobj; i++)
			((Object3d) obj.get(i)).paint(g);
		// obj[i].paint(g);
		if (moveAfterPaint)
			move();
	}

	synchronized String inside(int x, int y) {

		for (int i = nobj - 1; i >= 0; i--) {
			// Object3d o = obj[i];
			Object3d o = (Object3d) obj.get(i);
			if (o.inside(x, y) >= 0)
				if (o.name != null)
					return o.name;
				else
					return "noname";
		}
		return null;
	}

	void setPaintOrder() {
		zSort();
	}

	void zSort() {
		if (nobj <= 1)
			return;
		boolean cont = true;
		while (cont) {
			cont = false;
			Object3d a = (Object3d) obj.get(0);
			// Object3d a = obj[0];
			for (int i = 1; i < nobj; i++) {
				Object3d b = (Object3d) obj.get(i);
				// Object3d b = obj[i];
				if (a.center.z > b.center.z) {
					obj.set(i - 1, b);
					obj.set(i, a);
					// obj[i-1] = b;
					// obj[i] = a;
					cont = true;
				}
				a = (Object3d) obj.get(i);
				// a = obj[i];
			}
		}

	}

	void move() {
	}

	void resize(int width, int height) {
		this.width = width;
		this.height = height;
		computeMatrix();
	}

	public synchronized void computeMatrix() {
		if (bb == null)
			computeBoundingBox();
		Vector3d c = bb.getCenter();
		float f1 = width / bb.getWidth();
		float f2 = height / bb.getHeight();
		float f = 0.7f * Math.min(f1, f2);
		mat = new Matrix3d();
		mat.trans(-c.x, -c.y, -c.z);
		mat.scale(f, f, f);
		float dz = f * bb.getDepth();
		float h1 = dz * minScale / (maxScale - minScale);
		zCamera = h1 + dz / 2;
		zTarget = -h1 * maxScale;
		if (persp)
			mat.trans(0, 0, -zCamera);
		else {
			mat.scale(1, -1, 1);
			mat.trans(width / 2, height / 2, 0);
		}
	}

	void computeBoundingBox() {

		Object3d o = (Object3d) obj.get(0);
		// Object3d o = obj[0];
		o.transformToCameraSpace();
		bb = new BoundingBox(o.vert, o.nvert);
		for (int i = 1; i < nobj; i++) {
			o = (Object3d) obj.get(i);
			// o = obj[i];
			o.transformToCameraSpace();
			bb.combine(new BoundingBox(o.vert, o.nvert));
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHight() {
		return height;
	}

	void setPersp(boolean persp) {
		if (persp == this.persp)
			return;
		this.persp = persp;
		if (mat == null)
			return;
		if (persp) {
			mat.trans(-width / 2, -height / 2, 0);
			mat.scale(1, -1, 1);
			mat.trans(0, 0, -zCamera);
		} else {
			mat.trans(0, 0, zCamera);
			mat.scale(1, -1, 1);
			mat.trans(width / 2, height / 2, 0);
		}
	}

}
