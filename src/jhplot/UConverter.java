package jhplot;

import java.util.Iterator;
import java.util.List;
import com.edsdev.jconvert.domain.*;
import com.edsdev.jconvert.persistence.*;
import com.edsdev.jconvert.presentation.ConversionTypeData;

/**
 * Class to convert various units of measure.
 * 
 * @author S.Chekanov
 * 
 */
public class UConverter {

	private List<ConversionType> domainData;
	private ConversionTypeData ctd;

	/**
	 * Initialise convertions
	 */
	public UConverter() {

		domainData = new DataLoader().loadData();

	}

	/**
	 * Initialise conversion if you know the type
	 * 
	 * @param type
	 *            unit  type.
	 */
	public UConverter(String type) {

		domainData = new DataLoader().loadData();
		setConversionType(type);

	}

	/**
	 * Perform convertion of values fomr one unit to another. 
	 * 
	 * @param value
	 *            value
	 * @param fromUnit
	 *            original unit
	 * @param toUnit
	 *            unit to convert
	 * @return value after convertion
	 */
	public double convert(double value, String fromUnit, String toUnit) {

		return ctd.convert(value, fromUnit, toUnit);

	}

	/**
	 * Show as a string all conversion types
	 * 
	 * @return string with all convertion types;
	 */
	public String toString() {

		Iterator<ConversionType> iter = domainData.iterator();
		String s = "";
		while (iter.hasNext()) {
			ConversionType type = (ConversionType) iter.next();
			s = s + type.getTypeName() + " - " + type.getConversions().size()
					+ " conversions\n";
		}

		return s;
	}

	/**
	 * Get all conversion types as strings
	 * 
	 * @return array with conversion styles.
	 */
	public String[] getTypes() {

		String[] s = new String[domainData.size()];
		Iterator<ConversionType> iter = domainData.iterator();
		int j = 0;
		while (iter.hasNext()) {
			ConversionType type = (ConversionType) iter.next();
			s[j] = type.getTypeName();
			j++;
		}

		return s;
	}

	/**
	 * Find and set conversion type.
	 * 
	 * @param conversionUnit
	 *            convertion unit
	 * @return conversion type
	 */
	public ConversionType findType(String conversionUnit) {

		Iterator<ConversionType> iter = domainData.iterator();
		while (iter.hasNext()) {
			ConversionType type = (ConversionType) iter.next();
			Iterator conIter = type.getConversions().iterator();
			while (conIter.hasNext()) {
				Conversion conv = (Conversion) conIter.next();
				if (conv.getFromUnit().equals(conversionUnit)
						|| conv.getToUnit().equals(conversionUnit)) {
					return type;
				}
			}
		}
		return null;

	}

	
	
	
	/**
	 * Get convertion type
	 * 
	 * @param conversionType
	 * @return
	 */

	public ConversionTypeData getConversionType(String conversionType) {

		Iterator iter = domainData.iterator();
		while (iter.hasNext()) {
			ConversionType type = (ConversionType) iter.next();
			if (type.getTypeName().equals(conversionType)) {
				ctd = new ConversionTypeData(type);
				break;
			}
		}

		return ctd;
	}

	/**
	 * Set convertion type.
	 * 
	 * @param conversionType
	 */
	public void setConversionType(String conversionType) {

		Iterator iter = domainData.iterator();
		while (iter.hasNext()) {
			ConversionType type = (ConversionType) iter.next();
			if (type.getTypeName().equals(conversionType)) {
				ctd = new ConversionTypeData(type);
				break;
			}
		}
	}

	/**
	 * Get domain data
	 * 
	 * @return
	 */
	public List<ConversionType> getDomanData() {

		return domainData;

	}

	
	
	/**
	 * Get current convertion type.
	 * @return current conversion type.
	 */
	public  ConversionTypeData getConversionType(){
		return ctd;
		
	}
	
	
	
	
}
