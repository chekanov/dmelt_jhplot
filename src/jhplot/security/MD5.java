package jhplot.security;

import java.io.File;
import java.io.IOException;
import com.twmacinta.util.MD5GET;

/**
 * 
 * Get MD5 hash for any object or a file with data
 * 
 * @author S.Chekanov
 *
 */


public class MD5 {

	
	 private  MD5GET md5;
	 private byte[] data;
	
	 
	 /**
	 * Initialize MD5 calculations
	 * @param o input object
	 */
	public MD5(final Object o){
		
		md5=new MD5GET(o);
		data=md5.Final();
		
	}

	/**
	 * Initialize MD5 calculations for a file
	 * @param f input file
	 */
	public MD5(final File f){
		try {
			data=MD5GET.getHash(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Returns 32-character hex representation of this objects hash	
	 * @param String of this object's hash
	 */
	public String get(){
		return MD5GET.asHex(data);
	}
	
	
	/**
	 * Get data as stream
	 * @return data
	 */
	public byte[] getData(){
		return data;
	}
}
