package jyplot;

import org.python.util.PythonInterpreter;

public class JPlotTester
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

//		JPlotTester.testErrorbar();
		JPlotTester.testaxhline();

	}
	
	public static void testErrorbar()
	{
		PythonInterpreter interp = new PythonInterpreter();
		interp.exec("from org.dion.util.jfreechart import Jylab");
		interp.exec("j = Jylab()");
		interp.exec("j.errorbar([-1,2,3], [1,2,1], [0.5, 0.2, 0.3],None,  ecolor='#ffbbbb', color='#60e46b12')");
		interp.exec("j.xticks(['aa', 'bb', 'cc'], [-1, 1, 2])");
		interp.exec("j.grid(0)");
		interp.exec("j.show()");
	}
	
	public static void testaxhline()
	{
		PythonInterpreter interp = new PythonInterpreter();
		interp.exec("from org.dion.util.jfreechart import Jylab");
		interp.exec("j = Jylab()");
		interp.exec("j.plot([-1,2,3], [0.5, 0.2, 0.3])");
		interp.exec("j.xlim(-5, 20)");
		interp.exec("j.ylim(-5, 20)");
		interp.exec("j.title('Somecrap sdsf', 12)");
		interp.exec("j.setRangeSpace(75.0)");
		
		interp.exec("j.axhline(0.2, color=0.9, linewidth=3)");
//		interp.exec("j.xticks(['aa', 'bb', 'cc'], [-1, 1, 2])");
		interp.exec("j.grid(0)");
		interp.exec("j.show()");
	}

}
