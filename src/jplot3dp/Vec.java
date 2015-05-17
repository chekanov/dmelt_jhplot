package jplot3dp;

import java.io.*;

import jplot3dp.Printable;
import jplot3dp.Vec;

class Vec implements Printable {

	public Vec() {
	}

	public Vec(double d, double d1, double d2) {
		x = d;
		y = d1;
		z = d2;
	}

	public Vec(Vec vec) {
		x = vec.x;
		y = vec.y;
		z = vec.z;
	}

	public double dotProduct(Vec vec) {
		return x * vec.x + y * vec.y + z * vec.z;
	}

	public double norm() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vec normalize() {
		double d = norm();
		return new Vec(x / d, y / d, z / d);
	}

	public Vec add(Vec vec) {
		return new Vec(x + vec.x, y + vec.y, z + vec.z);
	}

	public Vec substract(Vec vec) {
		return new Vec(x - vec.x, y - vec.y, z - vec.z);
	}

	public Vec scalarMult(double d) {
		return new Vec(d * x, d * y, d * z);
	}

	public Vec scalarDivide(double d) {
		return new Vec(x / d, y / d, z / d);
	}

	public Vec crossProduct(Vec vec) {
		return new Vec(y * vec.z - z * vec.y, z * vec.x - x * vec.z, x * vec.y
				- y * vec.x);
	}

	public boolean isNaN() {
		return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z);
	}

	public boolean isZero() {
		return x == 0.0D && y == 0.0D && z == 0.0D;
	}

	public void writeToStream(DataOutputStream dataoutputstream)
			throws IOException {
		dataoutputstream.writeDouble(x);
		dataoutputstream.writeDouble(y);
		dataoutputstream.writeDouble(z);
	}

	public void readFromStream(DataInputStream datainputstream)
			throws IOException {
		x = datainputstream.readDouble();
		y = datainputstream.readDouble();
		z = datainputstream.readDouble();
	}

	public String toString() {
		return "Vector: (" + x + "," + y + "," + z + ")";
	}

	public double x;
	public double y;
	public double z;
}
