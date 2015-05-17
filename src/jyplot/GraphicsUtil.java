package jyplot;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.html.StyleSheet;


/**
 * Misc useful graphics methods
 * @author dion
 *
 */
public class GraphicsUtil
{

	static Map<String, Color> stringColorMap = GraphicsUtil.getStringToColorMap();
	static StyleSheet styleSheet = new StyleSheet(); //For converting strings such as #123423 to colors
	
	
	/**
	 * Converts a string into a Color object.
	 * Checks a few local maps and the styleSheet
	 * class.
	 * @param s
	 * @return
	 */
	public static Color getColorFromString(String s)
	{
		Color c = null;
		try
		{	
			if(GraphicsUtil.stringColorMap.containsKey(s))
			{
				return GraphicsUtil.stringColorMap.get(s);
			}
			else if(isDouble(s))
			{
				float value = (float) Double.parseDouble(s);
				c = new Color(value, value, value);
				return c;
			}
			else
			{
				c = styleSheet.stringToColor(s);
				if(c != null)
				{
					return c;
				}
				
			}
			System.out.println("No color matching " + s);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return c;
		
	}


	
	private static boolean isDouble(String s)
	{
		try
		{
			Double.parseDouble(s);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}

	}
	
	private static Map<String, Color> getStringToColorMap()
	{
		Map<String, Color> ret = new HashMap<String, Color>();
		try
		{
			Class clazz = java.awt.Color.class;
			Field[] filc = clazz.getDeclaredFields();

			for (int i = 0; i < filc.length; i++)
			{
				// System.out.println(filc[i].getName());
				Field f = filc[i];

				if(Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers()))
				{
					Object o = f.get(null);

					if(o instanceof java.awt.Color)
					{
						ret.put(f.getName(), (Color) o);
					}
				}
			}
			return ret;
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
static Color mask = Color.blue;

}
