/*
 * Copyright (c) 2005, DoodleProject
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 * 
 * Neither the name of DoodleProject nor the names of its
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package jhplot.math.num;

/**
 * An expandable double array.
 * 
 * @since 1.1
 * @version $Revision: 1.2 $ $Date: 2007/10/25 04:44:22 $
 */
public class DoubleArray {

    /** The actual array. */
    private double[] array = new double[16];

    /** The current size of the array. */
    private int size = 0;

    /**
     * Insure the internal array can accomodate at least <tt>n</tt> elements.
     * 
     * @param n the number of elements to accomodate.
     */
    private void accomodate(int n) {
        while (array.length < n) {
            expand();
        }
    }

    /**
     * Adds an element to the end of this expandable array.
     * 
     * @param value to be added to end of array
     */
    public void add(double value) {
        if (size >= array.length) {
            expand();
        }
        array[size++] = value;
    }

    /**
     * Remove all elements from this collection.
     */
    public void clear() {
        size = 0;
    }

    /**
     * Increase the capacity of the actual array by a factor of two.
     */
    private void expand() {
        double[] temp = new double[array.length * 2];
        System.arraycopy(array, 0, temp, 0, size);
        array = temp;
    }

    /**
     * Access the element at the given index.
     * 
     * @param index the index of the element to retrieve.
     * @return the <tt>index</tt>-th element.
     */
    public double get(int index) {
        double value = Double.NaN;
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException("Invalid index: " + index);
        } else if (index >= 0) {
            value = array[index];
        } else {
            throw new ArrayIndexOutOfBoundsException(
                "Index must be non-negative.");
        }
        return value;
    }

    /**
     * Access the size of this array.
     * 
     * @return the size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Modify the element at the given index. The array may be expanded to hold
     * a value at the given index.
     * 
     * @param index the index of the element to modify.
     * @param value the new <tt>index</tt>-th element value.
     */
    public void set(int index, double value) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(
                "Index must be non-negative.");
        }
        accomodate(index);
        array[index] = value;
        if (index >= size) {
            setSize(index + 1);
        }
    }

    /**
     * Modify the size of this array.
     * 
     * @param n the new array size.
     */
    public void setSize(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Size must be non-negative.");
        }
        accomodate(n);
        size = n;
    }

    /**
     * Convert this array into a native array.
     * 
     * @return the native array.
     */
    public double[] toArray() {
        double[] ret = new double[size];
        System.arraycopy(array, 0, ret, 0, size);
        return ret;
    }
}
