package jhplot.io.db;

import java.io.*;


/**
 * DB fir HDataBase
 * @author sergei
 *
 */
public class RecordReader {
  
  String key;
  byte[] data;
  ByteArrayInputStream in;
  ObjectInputStream objIn;

  protected RecordReader(String key, byte[] data) {
    this.key = key;
    this.data = data;
    in = new ByteArrayInputStream(data);
  }

  protected String getKey() {
    return key;
  }

  protected  byte[] getData() {
    return data;
  }

  protected  InputStream getInputStream() throws IOException {
    return in;
  }

  protected  ObjectInputStream getObjectInputStream() throws IOException {
    if (objIn == null) {
      objIn = new ObjectInputStream(in);
    }
    return objIn;
  }

  /**
   * Reads the next object in the record using an ObjectInputStream.
   */
  public  Object readObject() throws IOException, OptionalDataException, ClassNotFoundException {
    return getObjectInputStream().readObject();
  }

}






