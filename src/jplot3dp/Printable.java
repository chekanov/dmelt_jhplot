package jplot3dp;



import java.io.*;

interface Printable
{

    public abstract void writeToStream(DataOutputStream dataoutputstream)
        throws IOException;

    public abstract void readFromStream(DataInputStream datainputstream)
        throws IOException;
}
