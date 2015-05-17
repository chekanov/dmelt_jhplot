package jplot3dp;



import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import org.freehep.graphics2d.VectorGraphics;
import graph.RTextLine;
import jplot.Translate;
import jplot3dp.MathParser.MathParser;


public class ModelView extends JPanel
    implements Printable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public class AxesDefinition implements Printable {  

        public void readFromStream(DataInputStream datainputstream)
            throws IOException
        {
            for(int i = 0; i < 3; i++)
            {
                axisVectors[i].readFromStream(datainputstream);
                shown[i] = datainputstream.readBoolean();
                min[i] = datainputstream.readDouble();
                max[i] = datainputstream.readDouble();
            }

            incr = datainputstream.readDouble();
            tickDensity = datainputstream.readDouble();
            color = Utils.readColor(datainputstream);
            width = datainputstream.readInt();
        }

        public void writeToStream(DataOutputStream dataoutputstream)
            throws IOException
        {
            for(int i = 0; i < 3; i++)
            {
                axisVectors[i].writeToStream(dataoutputstream);
                dataoutputstream.writeBoolean(shown[i]);
                dataoutputstream.writeDouble(min[i]);
                dataoutputstream.writeDouble(max[i]);
            }

            dataoutputstream.writeDouble(incr);
            dataoutputstream.writeDouble(tickDensity);
            Utils.writeColor(dataoutputstream, color);
            dataoutputstream.writeInt(width);
        }

        public final Vec axisVectors[] = {
            new Vec(1.0D, 0.0D, 0.0D), new Vec(0.0D, 1.0D, 0.0D), new Vec(0.0D, 0.0D, 1.0D)
        };


       // chekanov: inverted 
       public final Vec axisVectorsFrame[] = {
            new Vec(-1.0D, 0.0D, 0.0D), new Vec(0.0D, -1.0D, 0.0D), new Vec(0.0D, 0.0D, -1.0D)
        };


        boolean shown[] = {
            true, true, true
        };
        public double min[] = {
            0.0D, 0.0D, 0.0D
        };
        public double max[] = {
            1.0D, 1.0D, 1.0D
        };
        public double incr;
        public double tickDensity;
        public Color color;
        public int width;

        public AxesDefinition()
        {
            incr = 0.050000000000000003D;
            tickDensity = 0.20000000000000001D;
            color = Color.BLACK;
            width = 2;
        }
    }

    private class ElementString extends Element
    {

        public void render(VectorGraphics graphics2d)
        {
            if(!renderable)
                return;
            graphics2d.setColor(color);
            if(tick)
            {
                graphics2d.drawLine((int)(x - tickX * 3D), (int)(y - tickY * 3D), (int)(x + tickX * 3D), (int)(y + tickY * 3D));

                graphics2d.setFont(valueFont);  
                graphics2d.drawString(string, (int)x + 5, (int)y + 12);
            } else
            {
                RTextLine text = new RTextLine();
                text.setFont( labelFont );
                text.setColor( labelColor);
                String stext = Translate.decode(string);
                text.setText(stext);
                text.draw(graphics2d,(int)x-5,(int)y+5);
            }
        }

        private double x;
        private double y;
        private Color color;
        private String string;
        boolean tick;
        double tickX;
        double tickY;

        public ElementString(String s, Vec vec, Color color1, Vec vec1)
        {
            tick = false;
            vec = project(vec);
            if(vec.z < 0.0D)
            {
                renderable = false;
                return;
            }
            dist = vec.z;
            double d = getBlendAmt();
            if(d >= 1.0D)
            {
                renderable = false;
                return;
            }
            x = vec.x;
            y = -vec.y;
            color = Utils.blendColors(color1, bgColor, d);
            string = s;
            if(vec1 != null)
            {
                vec1 = project(vec1).substract(project(new Vec(0.0D, 0.0D, 0.0D)));
                tickX = vec1.y;
                tickY = vec1.x;
                double d1 = Math.sqrt(tickX * tickX + tickY * tickY);
                tickX /= d1;
                tickY /= d1;
                tick = true;
            }
        }
    }





    private class ElementCurve extends Element
    {

        public void render(VectorGraphics graphics2d)
        {
            if(!renderable)
            {
                return;
            } else
            {
                java.awt.Stroke stroke = graphics2d.getStroke();
                graphics2d.setStroke(new BasicStroke(curveWidth, 1, 1));
                graphics2d.setColor(curveColor);
                graphics2d.drawLine(x1, y1, x2, y2);
                graphics2d.setStroke(stroke);
                return;
            }
        }

        private int x1;
        private int y1;
        private int x2;
        private int y2;
        private Color curveColor;
        private int curveWidth;

        public ElementCurve(Vec vec, Vec vec1, Color color, int i, boolean flag)
        {
      

            // chekanov
            if (vec == null || vec1 == null) return;


            vec = project(vec);
            vec1 = project(vec1);
            if(vec.z < 0.0D || vec1.z < 0.0D)
            {
                renderable = false;
                return;
            }
            dist = (vec.z + vec1.z) / 2D;
            double d = getBlendAmt();
            if(d >= 1.0D)
            {
                renderable = false;
                return;
            } else
            {
                x1 = (int)vec.x;
                y1 = -(int)vec.y;
                x2 = (int)vec1.x;
                y2 = -(int)vec1.y;
                curveColor = Utils.blendColors(color, bgColor, d);
                curveWidth = flag ? i : (int)((1.0D / dist) * (double)i);
                return;
            }
        }
    }

    private class ElementRect extends Element
    {

        public void render(VectorGraphics graphics2d)
        {
            if(!renderable)
                return;
            if(surfaceColor != null)
            {
                graphics2d.setColor(surfaceColor);
                graphics2d.fill(s);
            }
            graphics2d.setColor(curveColor);
            if (s !=null ) graphics2d.draw(s);
        }

        private double shineyNess;
        private double shineIntensity;
        private Polygon s;
        private Color curveColor;
        private Color surfaceColor;

        public ElementRect(Vec vec, Vec vec1, Vec vec2, Vec vec3, Color color, Color color1)
        {

            // chekanov
            if (vec == null || vec1 == null || vec2 == null || vec3 == null) return;

            shineyNess = 0.25D;
            shineIntensity = 0.34999999999999998D;
            double d = 0.0D;
            Vec vec4 = vec1.substract(vec);
            Vec vec5 = vec2.substract(vec1);
            Vec vec6 = vec3.substract(vec2);
            Vec vec7 = vec.substract(vec3);
            vec = project(vec);
            vec1 = project(vec1);
            vec2 = project(vec2);
            vec3 = project(vec3);
            if(vec.z < 0.0D || vec1.z < 0.0D || vec2.z < 0.0D || vec3.z < 0.0D)
            {
                renderable = false;
                return;
            }
            dist = (vec.z + vec1.z + vec2.z + vec3.z) / 4D;
            double d1 = getBlendAmt();
            if(d1 >= 1.0D)
            {
                renderable = false;
                return;
            }
            s = new Polygon();
            s.addPoint((int)vec.x, -(int)vec.y);
            s.addPoint((int)vec1.x, -(int)vec1.y);
            s.addPoint((int)vec2.x, -(int)vec2.y);
            s.addPoint((int)vec3.x, -(int)vec3.y);
            curveColor = Utils.blendColors(color, bgColor, d1);
            if(shineIntensity > 0.0D)
            {
                Vec vec8 = eyeDirection;
                Vec vec9 = vec4.crossProduct(vec5);
                vec9 = vec9.add(vec6.crossProduct(vec7));
                d += vec9.normalize().dotProduct(vec8);
                d = Math.abs(d);
                d = (d - 1.0D) / (1.0D - shineyNess) + 1.0D;
                if(d < 0.0D)
                    d = 0.0D;
                else
                if(d > 1.0D)
                    d = 1.0D;
                color1 = Utils.blendColors(color1, Color.WHITE, d * shineIntensity);
            }
            surfaceColor = Utils.blendColors(color1, bgColor, d1);
        }
    }

    private abstract class Element
        implements Comparable
    {

        boolean isRenderable()
        {
            return renderable;
        }

        public int compareTo(Object obj)
        {
            Element element = (Element)obj;
            if(dist > element.dist)
                return -1;
            return dist >= element.dist ? 0 : 1;
        }

        double getBlendAmt()
        {
            if(fogEnabled)
            {
                double d = (dist - fogStart) / (fogEnd - fogStart);
                if(d > 1.0D)
                    return 1.0D;
                if(d < 0.0D)
                    return 0.0D;
                else
                    return d;
            } else
            {
                return 0.0D;
            }
        }

        public abstract void render(VectorGraphics graphics2d);

        boolean renderable;
        protected double dist;

        private Element()
        {
            renderable = true;
        }

    }

    private class OnMouse extends MouseInputAdapter
    {

        public void mousePressed(MouseEvent mouseevent)
        {
            lastPoint = new Point(mouseevent.getX(), mouseevent.getY());
            requestFocusInWindow();
        }

        public void mouseDragged(MouseEvent mouseevent)
        {
            Point point = new Point(mouseevent.getX(), mouseevent.getY());
            double d = (float)(point.x - lastPoint.x) / 100F;
            double d1 = (float)(point.y - lastPoint.y) / 100F;
            if(mouseevent.isControlDown())
                cameraRotate(d, d1, false);
            else
            if((mouseevent.getModifiersEx() & 0x1000) != 0)  { 
                cameraForward(d1);
             //    System.out.println(d1); 
            } else
            if((mouseevent.getModifiersEx() & 0x800) != 0)
                cameraTranslate(d, d1);
            else
            if(mouseevent.isShiftDown())
            {
                Rectangle rectangle = getBounds();
                double d2 = point.x - (rectangle.x + rectangle.width / 2);
                double d3 = point.y - (rectangle.y + rectangle.height / 2);
                double d4 = Math.sqrt(d2 * d2 + d3 * d3);
                cameraBank((d * d3 - d1 * d2) / d4);
            } else
            if(mouseevent.isAltDown())
                cameraTranslate(d, d1);
            else
                cameraRotate(-d, -d1, true);
            lastPoint = point;
        }

        private Point lastPoint;

        private OnMouse()
        {
            lastPoint = new Point(0, 0);
        }

    }

    private class TimerListener
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            long l = System.currentTimeMillis();
            long l1 = l - lastTime;
            if(l1 <= 0L || l1 > (long)(2 * timer.getDelay()))
                l1 = timer.getDelay();
            double d = (double)l1 / 100D;
            lastTime = l;
            boolean flag = true;
            double d1 = Math.pow(0.20000000000000001D, d);
            keyBoard.velForward = keyBoard.velForward * d1 + keyBoard.targetForward * d * (1.0D - d1);
            if(Math.abs(keyBoard.velForward) > 0.001D)
            {
                flag = false;
                cameraForward(keyBoard.velForward);
            }
            keyBoard.velTranslate.x = keyBoard.velTranslate.x * d1 + keyBoard.targetTranslate.x * d * (1.0D - d1);
            keyBoard.velTranslate.y = keyBoard.velTranslate.y * d1 + keyBoard.targetTranslate.y * d * (1.0D - d1);
            if(Math.abs(keyBoard.velTranslate.x) > 0.001D || Math.abs(keyBoard.velTranslate.y) > 0.001D)
            {
                flag = false;
                cameraTranslate(keyBoard.velTranslate.x, keyBoard.velTranslate.y);
            }
            keyBoard.velRotate.x = keyBoard.velRotate.x * d1 + keyBoard.targetRotate.x * d * (1.0D - d1);
            keyBoard.velRotate.y = keyBoard.velRotate.y * d1 + keyBoard.targetRotate.y * d * (1.0D - d1);
            if(Math.abs(keyBoard.velRotate.x) > 0.001D || Math.abs(keyBoard.velRotate.y) > 0.001D)
            {
                flag = false;
                cameraRotate(keyBoard.velRotate.x, keyBoard.velRotate.y, false);
            }
            keyBoard.velBank = keyBoard.velBank * d1 + keyBoard.targetBank * d * (1.0D - d1);
            if(Math.abs(keyBoard.velBank) > 0.001D)
            {
                flag = false;
                cameraBank(keyBoard.velBank);
            }
            keyBoard.velPivotRotate.x = keyBoard.velPivotRotate.x * d1 + keyBoard.targetPivotRotate.x * d * (1.0D - d1);
            keyBoard.velPivotRotate.y = keyBoard.velPivotRotate.y * d1 + keyBoard.targetPivotRotate.y * d * (1.0D - d1);
            if(Math.abs(keyBoard.velPivotRotate.x) > 0.001D || Math.abs(keyBoard.velPivotRotate.y) > 0.001D)
            {
                flag = false;
                cameraRotate(keyBoard.velPivotRotate.x, keyBoard.velPivotRotate.y, true);
            }
            if(flag)
                timer.stop();
            else
                timer.restart();
        }

        private long lastTime;

        private TimerListener()
        {
            lastTime = 0L;
        }

    }

    private class OnKey extends KeyAdapter
    {

        public void keyReleased(KeyEvent keyevent)
        {
            switch(keyevent.getKeyCode())
            {
            case 65: // 'A'
            case 90: // 'Z'
                keyBoard.targetForward = 0.0D;
                break;

            case 38: // '&'
            case 40: // '('
                keyBoard.targetTranslate.y = 0.0D;
                keyBoard.targetPivotRotate.y = 0.0D;
                keyBoard.targetRotate.y = 0.0D;
                break;

            case 37: // '%'
            case 39: // '\''
                keyBoard.targetTranslate.x = 0.0D;
                keyBoard.targetPivotRotate.x = 0.0D;
                keyBoard.targetRotate.x = 0.0D;
                break;

            case 34: // '"'
            case 69: // 'E'
            case 81: // 'Q'
            case 127: // '\177'
                keyBoard.targetBank = 0.0D;
                break;
            }
        }

        public void keyPressed(KeyEvent keyevent)
        {
            boolean flag = true;
            switch(keyevent.getKeyCode())
            {
            case 65: // 'A'
                keyBoard.targetForward = 0.14999999999999999D;
                break;

            case 90: // 'Z'
                keyBoard.targetForward = -0.14999999999999999D;
                break;

            case 40: // '('
                if(keyevent.isAltDown())
                    keyBoard.targetTranslate.y = -0.14999999999999999D;
                else
                if(keyevent.isControlDown())
                    keyBoard.targetPivotRotate.y = 0.14999999999999999D;
                else
                    keyBoard.targetRotate.y = 0.14999999999999999D;
                break;

            case 38: // '&'
                if(keyevent.isAltDown())
                    keyBoard.targetTranslate.y = 0.14999999999999999D;
                else
                if(keyevent.isControlDown())
                    keyBoard.targetPivotRotate.y = -0.14999999999999999D;
                else
                    keyBoard.targetRotate.y = -0.14999999999999999D;
                break;

            case 37: // '%'
                if(keyevent.isAltDown())
                    keyBoard.targetTranslate.x = 0.14999999999999999D;
                else
                if(keyevent.isControlDown())
                    keyBoard.targetPivotRotate.x = 0.14999999999999999D;
                else
                    keyBoard.targetRotate.x = 0.14999999999999999D;
                break;

            case 39: // '\''
                if(keyevent.isAltDown())
                    keyBoard.targetTranslate.x = -0.14999999999999999D;
                else
                if(keyevent.isControlDown())
                    keyBoard.targetPivotRotate.x = -0.14999999999999999D;
                else
                    keyBoard.targetRotate.x = -0.14999999999999999D;
                break;

            case 81: // 'Q'
            case 127: // '\177'
                keyBoard.targetBank = -0.14999999999999999D;
                break;

            case 34: // '"'
            case 69: // 'E'
                keyBoard.targetBank = 0.14999999999999999D;
                break;

            default:
                flag = false;
                break;
            }
            if(flag)
                timer.start();
        }

        private OnKey()
        {
        }

    }

    public class FunctionsList
        implements Printable
    {

        public void writeToStream(DataOutputStream dataoutputstream)
            throws IOException
        {
            dataoutputstream.writeInt(size());
            for(int i = 0; i < size(); i++)
                getFunction(i).writeToStream(dataoutputstream);

        }

        public void readFromStream(DataInputStream datainputstream)
            throws IOException
        {
            int i = datainputstream.readInt();
            functions.clear();
            for(int j = 0; j < i; j++)
            {
                ModelFunction modelfunction = addFunction();
                modelfunction.readFromStream(datainputstream);
            }

        }

        public ModelFunction addFunction()
        {
            ModelFunction modelfunction = new ModelFunction();
            int i = 1;
            do
            {
                String s = "Function " + i;
                int j;
                for(j = 0; j < functions.size() && !((ModelFunction)functions.get(j)).name.equals(s); j++);
                if(j != functions.size())
                {
                    i++;
                } else
                {
                    modelfunction.name = s;
                    modelfunction.expression = "z=0";
                    modelfunction.curveColor = Color.BLACK;
                    modelfunction.surfaceColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
                    functions.add(modelfunction);
                    return modelfunction;
                }
            } while(true);
        }

        public void removeFunction(int i)
        {
            functions.remove(i);
        }

        public void clear()
        {
            functions.clear();
        }

        public ModelFunction getFunction(int i)
        {
            return (ModelFunction)functions.get(i);
        }

        public int size()
        {
            return functions.size();
        }

        private ArrayList functions;

        public FunctionsList()
        {
            functions = new ArrayList();
        }
    }

    public class ModelFunction
        implements Printable
    {

        public void writeToStream(DataOutputStream dataoutputstream)
            throws IOException
        {
            dataoutputstream.writeBoolean(visible);
            dataoutputstream.writeUTF(name);
            dataoutputstream.writeUTF(expression);
            dataoutputstream.writeBoolean(isCurve);
            dataoutputstream.writeInt(curveWidth);
            dataoutputstream.writeBoolean(absoluteWidth);
            dataoutputstream.writeInt(gridDivsU);
            dataoutputstream.writeInt(gridDivsV);
            dataoutputstream.writeBoolean(fillSurface);
            Utils.writeColor(dataoutputstream, curveColor);
            Utils.writeColor(dataoutputstream, surfaceColor);
        }

        public void readFromStream(DataInputStream datainputstream)
            throws IOException
        {
            visible = datainputstream.readBoolean();
            name = datainputstream.readUTF();
            expression = datainputstream.readUTF();
            isCurve = datainputstream.readBoolean();
            curveWidth = datainputstream.readInt();
            absoluteWidth = datainputstream.readBoolean();
            gridDivsU = datainputstream.readInt();
            gridDivsV = datainputstream.readInt();
            fillSurface = datainputstream.readBoolean();
            curveColor = Utils.readColor(datainputstream);
            surfaceColor = Utils.readColor(datainputstream);
            parseFunction();
        }

        public String toString()
        {
            return name;
        }

        public double getArea()
        {
            return area;
        }

        public void parseFunction()
        {
            coords = new Vec[gridDivsU][isCurve ? 1 : gridDivsV];
            area = 0.0D;
            mathParser.resetVariables();
            mathParser.setVariable("uSteps", gridDivsU - 1);
            mathParser.setVariable("tSteps", gridDivsU - 1);
            mathParser.setVariable("vSteps", isCurve ? 0.0D : gridDivsV - 1);
            for(int i = 0; i < gridDivsU; i++)
            {
                double d = (float)i / (float)(gridDivsU - 1);
                for(int j = 0; j < (isCurve ? 1 : gridDivsV); j++)
                {
                    double d1 = (float)j / (float)(gridDivsV - 1);
                    mathParser.setVariable("u", d);
                    mathParser.setVariable("t", d);
                    mathParser.setVariable("v", d1);
                    mathParser.setVariable("x", isCurve ? 0.0D : 2D * d - 1.0D);
                    mathParser.setVariable("y", isCurve ? 0.0D : 2D * d1 - 1.0D);
                    mathParser.setVariable("z", 0.0D);
                    try
                    {
                        mathParser.parseExpression(expression);
                        coords[i][j] = new Vec(mathParser.getVariable("x"), mathParser.getVariable("z"), mathParser.getVariable("y"));
                        if(isCurve)
                        {
                            if(i > 0)
                                area += coords[i][0].substract(coords[i - 1][0]).norm();
                            continue;
                        }
                        if(i > 0 && j > 0)
                            area += coords[i][j - 1].substract(coords[i - 1][j - 1]).crossProduct(coords[i - 1][j].substract(coords[i - 1][j - 1])).norm() / 2D + coords[i][j - 1].substract(coords[i][j]).crossProduct(coords[i - 1][j].substract(coords[i][j])).norm() / 2D;
                    }
                    catch(Exception exception)
                    {
                        coords[i][j] = new Vec((0.0D / 0.0D), (0.0D / 0.0D), (0.0D / 0.0D));
                    }
                }

            }

        }

        public boolean visible;
        public String name;
        public String expression;
        boolean isCurve;
        int curveWidth;
        double transparency;
        boolean absoluteWidth;
        public int gridDivsU;
        public int gridDivsV;
        public boolean fillSurface;
        public Color curveColor;
        public Color surfaceColor;
        private double area;
        private Vec coords[][];


        public ModelFunction()
        {
            visible = true;
            isCurve = false;
            curveWidth = 2;
            absoluteWidth = false;
            gridDivsU = 21;
            gridDivsV = 21;
            fillSurface = true;
        }
    }

    private static class KeyBoard
    {

        private double targetForward;
        private java.awt.geom.Point2D.Double targetTranslate;
        private java.awt.geom.Point2D.Double targetRotate;
        private double targetBank;
        private java.awt.geom.Point2D.Double targetPivotRotate;
        private double velForward;
        private java.awt.geom.Point2D.Double velTranslate;
        private java.awt.geom.Point2D.Double velRotate;
        private double velBank;
        private java.awt.geom.Point2D.Double velPivotRotate;















        private KeyBoard()
        {
            targetForward = 0.0D;
            targetTranslate = new java.awt.geom.Point2D.Double(0.0D, 0.0D);
            targetRotate = new java.awt.geom.Point2D.Double(0.0D, 0.0D);
            targetBank = 0.0D;
            targetPivotRotate = new java.awt.geom.Point2D.Double(0.0D, 0.0D);
            velForward = 0.0D;
            velTranslate = new java.awt.geom.Point2D.Double(0.0D, 0.0D);
            velRotate = new java.awt.geom.Point2D.Double(0.0D, 0.0D);
            velBank = 0.0D;
            velPivotRotate = new java.awt.geom.Point2D.Double(0.0D, 0.0D);
        }

    }


    public void writeToStream(DataOutputStream dataoutputstream)
        throws IOException
    {
        dataoutputstream.writeUTF(title);
        eyePosition.writeToStream(dataoutputstream);
        eyeDirection.writeToStream(dataoutputstream);
        up.writeToStream(dataoutputstream);
        screenUp.writeToStream(dataoutputstream);
        screenRight.writeToStream(dataoutputstream);
        dataoutputstream.writeDouble(getFov());
        dataoutputstream.writeInt(backCulling);
        Utils.writeColor(dataoutputstream, bgColor);
        dataoutputstream.writeBoolean(fogEnabled);
        dataoutputstream.writeDouble(fogStart);
        dataoutputstream.writeDouble(fogEnd);
        functions.writeToStream(dataoutputstream);
        dataoutputstream.writeBoolean(bShowAxes);
        axesDefinition.writeToStream(dataoutputstream);
    }

    public void readFromStream(DataInputStream datainputstream)
        throws IOException
    {
        title = datainputstream.readUTF();
        eyePosition.readFromStream(datainputstream);
        eyeDirection.readFromStream(datainputstream);
        up.readFromStream(datainputstream);
        screenUp.readFromStream(datainputstream);
        screenRight.readFromStream(datainputstream);
        setFov(datainputstream.readDouble());
        backCulling = datainputstream.readInt();
        bgColor = Utils.readColor(datainputstream);
        fogEnabled = datainputstream.readBoolean();
        fogStart = datainputstream.readDouble();
        fogEnd = datainputstream.readDouble();
        functions.readFromStream(datainputstream);
        bShowAxes = datainputstream.readBoolean();
        axesDefinition.readFromStream(datainputstream);
    }

    public void reInitializeVars()
    {
        title = "";
        eyePosition = new Vec(0.0D, 1.0D, -1.5D);
        eyeDirection = (new Vec(0.0D, -1D, 1.5D)).normalize();
        up = new Vec(0.0D, 1.0D, 0.0D);
        recalcUpRight();
        fov = 90D;
        recalcScale();
        backCulling = 1;
        // bgColor = new Color(254, 254, 228);
        bgColor = Color.white;
        fogEnabled = true;
        fogStart = 0.0D;
        fogEnd = 5D;
        functions.clear();
        bShowAxes = true;
    }


    
    public void setRangeX(double min, double max)
    {
           axesDefinition.min[0] = min;
           axesDefinition.max[0] = max;
 
    }

    public void setShowAxes(boolean x, boolean y, boolean z)
    {
           axesDefinition.shown[0] = x;
           axesDefinition.shown[1] = y;
           axesDefinition.shown[2] = z;

    }


    public void setRangeY(double min, double max)
    {
           axesDefinition.min[1] = min;
           axesDefinition.max[1] = max;

    }

    public void setEyePosition(double x, double y, double z)
    {
    	 eyePosition = new Vec(x,y, z);
         eyeDirection = (new Vec(0.0D, -1D, 1.5D)).normalize();

    }

    /**
     * Get eye position
     * 
     * @return
     */
    public double[] getEyePosition()
    {
    
    	 double[]  va = new double [3];
    	 
    	 va[0]=eyePosition.x;
    	 va[1]=eyePosition.y;
    	 va[2]=eyePosition.z; 
    	 return va;
    	

    }

    
    
     public void setRangeZ(double min, double max)
    {
           axesDefinition.min[2] = min;
           axesDefinition.max[2] = max;

    }

     public void setNameX(String name)
    {
             as[0]=name;
    }

   
    public void setNameY(String name)
    {
             as[1]=name;
    }


     public void setNameZ(String name)
    {
             as[2]=name;
    }


    public void setLabelFont(Font name)
    {
             labelFont=name;
    }

     public void setLabelColor(Color color)
    {
             labelColor=color;
    }


    public void setValueFont(Font name)
    {
             valueFont=name;
    }


    public void setBackgroundFrame(Color c)
    {
          bgColor = c;
    }

    public void setAxesColor(Color c)
    {
           axesDefinition.color = c;
    }


   public void setFog(boolean fogEnabled)
    {
          this.fogEnabled = fogEnabled; 
    }



    
     // show small arrows for axes 
     public void setAxisArrows(boolean showArrows)
    {
          this.showArrows = showArrows;
    }


    public void  setAxes(boolean bShowAxes)
    {
          this.bShowAxes = bShowAxes;
    }


    public double getFov()
    {
        return fov;
    }

    public void setFov(double d)
    {
        fov = d;
        recalcScale();
    }

    private void recalcScale()
    {
        Dimension dimension = getSize();
        double d = Math.sqrt(dimension.height * dimension.height + dimension.width * dimension.width);
        scale = d / (2D * Math.tan((fov * 3.1415926535897931D) / 180D / 2D) * 1.0D);
    }

    public ModelView()
    {
        title = "";
        eyePosition = new Vec(0.0D, 1.0D, -1.5D);
        eyeDirection = (new Vec(0.0D, -1D, 1.5D)).normalize();
        up = new Vec(0.0D, 1.0D, 0.0D);
        fov = 90D;
        backCulling = 1;
        bgColor = Color.white;
        fogEnabled = true;
        fogStart = 0.0D;
        fogEnd = 5D;
        bShowAxes = true;
        mathParser = new MathParser();
        keyBoard = new KeyBoard();
        functions = new FunctionsList();
        axesDefinition = new AxesDefinition();
        reInitializeVars();

        // add popup menu
        menu = new JPopupMenu();
        item1 = new JMenuItem("Edit");
        item2 = new JMenuItem("TogAxis");
        item3 = new JMenuItem("TogFill");
        item4 = new JMenuItem("Refresh");
        item5 = new JMenuItem("Save");
        item6 = new JMenuItem("Open");
        item7 = new JMenuItem("Help");

        menu.add(item1);
        menu.add(item2);
        menu.add(item3); 
        menu.add(item4);
        menu.add(item5);
        menu.add(item7);
        addMouseListener(  new PopupListener() );



        setPreferredSize(new Dimension(320, 240));
        OnMouse onmouse = new OnMouse();
        addMouseListener(onmouse);
        addMouseMotionListener(onmouse);
        addMouseWheelListener(new MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent mousewheelevent)
            {
                keyBoard.velForward = (double)mousewheelevent.getWheelRotation() * 0.5D;
                timer.start();
            }

        });
        addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent componentevent)
            {
                recalcScale();
            }

        });
        addKeyListener(new OnKey());
        recalcUpRight();
        timer = new Timer(50, new TimerListener());

    }




    private void recalcUpRight()
    {
        screenUp = new Vec(up);
        screenUp = screenUp.substract(eyeDirection.scalarMult(eyeDirection.dotProduct(up)));
        screenUp = screenUp.normalize();
        screenRight = screenUp.crossProduct(eyeDirection).normalize();
    }

    public void cameraForward(double d)
    {

        this.posd= d;
        eyePosition = eyePosition.add(eyeDirection.scalarMult(d));
        repaint();
    }



   public double getCameraPosition()
    {
        return this.posd;
    }



    private void cameraRotate(double d, double d1, boolean flag)
    {
        Vec vec = null;
        if(flag)
            vec = new Vec(eyePosition.dotProduct(eyeDirection), eyePosition.dotProduct(screenUp), eyePosition.dotProduct(screenRight));
        eyeDirection = eyeDirection.substract(screenRight.scalarMult(d));
        eyeDirection = eyeDirection.add(screenUp.scalarMult(d1));
        eyeDirection = eyeDirection.normalize();
        if(flag)
        {
            recalcUpRight();
            eyePosition = eyeDirection.scalarMult(vec.x).add(screenUp.scalarMult(vec.y)).add(screenRight.scalarMult(vec.z));
        }
        if(eyeDirection.y > -0.90000000000000002D && eyeDirection.y < 0.90000000000000002D)
        {
            int i = up.y <= 0.0D ? -1 : 1;
            double d2 = (double)i * screenUp.y;
            double d3 = (double)i * eyeDirection.y;
            double d4 = Math.sqrt(d2 * d2 + d3 * d3);
            up = screenUp.scalarMult(d2 / d4).add(eyeDirection.scalarMult(d3 / d4));
            up = up.add(new Vec(0.0D, 0.10000000000000001D * (double)i, 0.0D)).normalize();
        } else
        {
            up = screenUp;
        }
        recalcUpRight();
        repaint();
    }

    private void cameraTranslate(double d, double d1)
    {
        eyePosition = eyePosition.substract(screenRight.scalarMult(d));
        eyePosition = eyePosition.add(screenUp.scalarMult(d1));
        repaint();
    }

    private void cameraBank(double d)
    {
        up = screenUp.add(screenRight.scalarMult(d)).normalize();
        recalcUpRight();
        repaint();
    }

    private Vec project(Vec vec)
    {
        vec = vec.substract(eyePosition);
        double d = vec.dotProduct(eyeDirection);
        vec = vec.scalarMult(1.0D / d).substract(eyeDirection.scalarMult(1.0D));
        Vec vec1 = new Vec(scale * screenRight.dotProduct(vec), scale * screenUp.dotProduct(vec), d);
        return vec1;
    }

    private void draw3DLine(Graphics2D graphics2d, Vec vec, Vec vec1)
    {

        // chekanov
        if(vec == null || vec1 == null)  return;


        if(vec.isNaN() || vec1.isNaN())
            return;
        Vec vec2 = project(vec);
        Vec vec3 = project(vec1);
        if(vec2.z < 0.01D && vec3.z < 0.01D)
            return;
        if(vec2.z < 0.01D)
        {
            Vec vec4 = vec;
            vec = vec1;
            vec1 = vec4;
            vec4 = vec2;
            vec2 = vec3;
            vec3 = vec4;
        }
        if(vec3.z < 0.01D)
        {
            vec1 = vec.scalarMult(0.01D - vec3.z).add(vec1.scalarMult(vec2.z - 0.01D)).scalarDivide(vec2.z - vec3.z);
            vec3 = project(vec1);
        }
        graphics2d.drawLine((int)vec2.x, -(int)vec2.y, (int)vec3.x, -(int)vec3.y);
    }





    // draw axis: chekanov 
    private void createAxes(LinkedList linkedlist)
    {
        for(int i = 0; i < 3; i++)
        {
            if(!axesDefinition.shown[i])
                continue;
          

           Vec vec = axesDefinition.axisVectors[i];
          // Vec vec = axesDefinition.axisVectorsFrame[i];
           double d = axesDefinition.min[i];
            double d1 = axesDefinition.max[i] + 0.050000000000000003D;
            double d2 = axesDefinition.incr;

            // draw 3 axis
            for(double d3 = d; d3 < d1 - 1E-08D; d3 += d2)
                linkedlist.add(new ElementCurve(vec.scalarMult(d3), vec.scalarMult(d3 + d2), axesDefinition.color, axesDefinition.width, true));


             // test
             // Vec vecTMP = axesDefinition.axisVectors[1];
             // for(double d3 = d; d3 < d1 - 1E-08D; d3 += d2)
             // linkedlist.add(new ElementCurve(vecTMP.scalarMult(d3), vecTMP.scalarMult(d3 + d2), axesDefinition.color, axesDefinition.width, true));


            // show tics
            for(double d4 = Math.ceil(d / axesDefinition.tickDensity) * axesDefinition.tickDensity; d4 <= d1 + 1E-08D; d4 += axesDefinition.tickDensity)
            {
                double d5 = (double)Math.round(d4 * 1000D) / 1000D;
                if(d5 != 0.0D)
                    linkedlist.add(new ElementString(String.valueOf(d5), vec.scalarMult(d4), axesDefinition.color, vec));
            }


            // show small arrows for axis 
            if (showArrows) { 
            Vec vec1 = axesDefinition.axisVectors[i != 0 ? 0 : 2];
            linkedlist.add(new ElementCurve(vec.scalarMult(d1), vec.scalarMult(d1 * 0.96999999999999997D).substract(vec1.scalarMult(0.029999999999999999D)), axesDefinition.color, axesDefinition.width, true));
            linkedlist.add(new ElementCurve(vec.scalarMult(d1), vec.scalarMult(d1 * 0.96999999999999997D).add(vec1.scalarMult(0.029999999999999999D)), axesDefinition.color, axesDefinition.width, true));
            }


            // show labels
            linkedlist.add(new ElementString(as[i], vec.scalarMult(d1 * 1.05D), axesDefinition.color, null));
        }

    }

    private void paintAsSolid(VectorGraphics graphics2d)
    {
        LinkedList linkedlist = new LinkedList();
        if(bShowAxes)
            createAxes(linkedlist);
        for(int i = 0; i < functions.size(); i++)
        {
            ModelFunction modelfunction = functions.getFunction(i);
            if(!modelfunction.visible)
                continue;
            Vec avec[][] = modelfunction.coords;
            int j = modelfunction.gridDivsU;
            int k = modelfunction.gridDivsV;

            // if (j <= 1 || k<=1)  continue;
           //  System.out.println(j);
           //  System.out.println(k);



            for(int l = 0; l < j - 1; l++)
            {
                if(modelfunction.isCurve)
                {
                    ElementCurve elementcurve = new ElementCurve(avec[l][0], avec[l + 1][0], modelfunction.curveColor, modelfunction.curveWidth, modelfunction.absoluteWidth);
                    if(elementcurve.isRenderable())
                        linkedlist.add(elementcurve);
                    continue;
                }
       

                for(int i1 = 0; i1 < k - 1; i1++)
                {
                    if(modelfunction.fillSurface)
                    {
                        ElementRect elementrect = new ElementRect(avec[l][i1], avec[l + 1][i1], avec[l + 1][i1 + 1], avec[l][i1 + 1], modelfunction.curveColor, modelfunction.fillSurface ? modelfunction.surfaceColor : null);
                        if(elementrect.isRenderable())
                            linkedlist.add(elementrect);
                        continue;
                    }
                    ElementCurve elementcurve1 = new ElementCurve(avec[l][i1], avec[l + 1][i1], modelfunction.curveColor, 0, true);
                    if(elementcurve1.isRenderable())
                        linkedlist.add(elementcurve1);
                    elementcurve1 = new ElementCurve(avec[l][i1], avec[l][i1 + 1], modelfunction.curveColor, 0, true);
                    if(elementcurve1.isRenderable())
                        linkedlist.add(elementcurve1);
                    if(l == j - 2)
                    {
                        elementcurve1 = new ElementCurve(avec[l + 1][i1 + 1], avec[l + 1][i1], modelfunction.curveColor, 0, true);
                        if(elementcurve1.isRenderable())
                            linkedlist.add(elementcurve1);
                    }
                    if(i1 != k - 2)
                        continue;
                    elementcurve1 = new ElementCurve(avec[l + 1][i1 + 1], avec[l][i1 + 1], modelfunction.curveColor, 0, true);
                    if(elementcurve1.isRenderable())
                        linkedlist.add(elementcurve1);
                }



            }

        }

       Collections.sort(linkedlist);
      
       Element element;
       for(Iterator iterator = linkedlist.iterator(); iterator.hasNext(); element.render(graphics2d))
           element = (Element)iterator.next();

    }

    private void paintAsWireframe(Graphics2D graphics2d)
    {
     

        graphics2d.setColor(Utils.blendColors(axesDefinition.color, Color.WHITE, 0.75D));
        if(bShowAxes)
        {
            for(int i = 0; i < 3; i++)
                if(axesDefinition.shown[i])
                    draw3DLine(graphics2d, axesDefinition.axisVectors[i].scalarMult(axesDefinition.min[i]), axesDefinition.axisVectors[i].scalarMult(axesDefinition.max[i]));

        }
label0:
        for(int j = 0; j < functions.size(); j++)
        {
            ModelFunction modelfunction = functions.getFunction(j);
            if(!modelfunction.visible)
                continue;
            Vec avec[][] = modelfunction.coords;
            if(!modelfunction.isCurve && modelfunction.fillSurface)
                graphics2d.setColor(Utils.blendColors(Color.BLACK, modelfunction.surfaceColor, 0.5D));
            else
                graphics2d.setColor(Utils.blendColors(Color.BLACK, modelfunction.curveColor, 0.5D));
            if(modelfunction.isCurve)
            {
                for(int k = 0; k < modelfunction.gridDivsU - 1; k++)
                    draw3DLine(graphics2d, avec[k][0], avec[k + 1][0]);

                continue;
            }
            int l = 0;
            do
            {
                if(l >= modelfunction.gridDivsU)
                    continue label0;
                for(int i1 = 0; i1 < modelfunction.gridDivsV; i1++)
                {
                    if(i1 < avec[l].length - 1)
                        draw3DLine(graphics2d, avec[l][i1], avec[l][i1 + 1]);
                    if(l < avec.length - 1)
                        draw3DLine(graphics2d, avec[l][i1], avec[l + 1][i1]);
                }

                l++;
            } while(true);
        }

 

   }


    // chekanov
    public void paintToGrphics(Graphics g, int i, int j)
    {

        
        VectorGraphics  graphics2d= VectorGraphics.create(g);

        if(backCulling == 0)
            graphics2d.setColor(Color.WHITE);
        else
            graphics2d.setColor(bgColor);
        graphics2d.fillRect(0, 0, i, j);
        graphics2d.translate(i / 2, j / 2);

       //  System.out.println("chekanov="+backCulling);

        if(backCulling != 0)
            paintAsSolid(graphics2d);
        else
            paintAsWireframe(graphics2d);
        if(!title.equals(""))
        {
            Graphics2D graphics2d1 = (Graphics2D)g.create();
            graphics2d1.setFont(graphics2d1.getFont().deriveFont(backCulling != 0 ? 1 : 0, 12F));
            Rectangle2D rectangle2d = graphics2d1.getFontMetrics().getStringBounds(title, graphics2d1);
            Rectangle rectangle = new Rectangle(i / 2 - (int)rectangle2d.getWidth() / 2, j - (int)rectangle2d.getHeight() - 10, (int)rectangle2d.getWidth(), (int)rectangle2d.getHeight());
            if(backCulling != 0)
            {
                graphics2d1.setColor(new Color(0, 0, 0, 32));
                graphics2d1.fillRect(0, rectangle.y + 3, i, rectangle.height + 2);
                graphics2d1.setColor(new Color(255, 255, 255, 128));
                graphics2d1.drawString(title, rectangle.x, rectangle.y + rectangle.height);
            }
            graphics2d1.setColor(new Color(0, 0, 0, 128));
            graphics2d1.drawString(title, rectangle.x + 1, rectangle.y + rectangle.height + 1);
        }
    }

    protected void paintComponent(Graphics g)
    {
        Dimension dimension = getSize();
        paintToGrphics(g, dimension.width, dimension.height);
    }

    public void saveImage(String s)
        throws IOException
    {
        BufferedImage bufferedimage = new BufferedImage(getWidth(), getHeight(), 1);
        Graphics g = bufferedimage.getGraphics();
        int i = bufferedimage.getWidth();
        int j = bufferedimage.getHeight();
        paintToGrphics(bufferedimage.getGraphics(), bufferedimage.getWidth(), bufferedimage.getHeight());
        Color color = backCulling == 0 ? Color.WHITE : bgColor;
        g.setColor(Utils.blendColors(Color.BLACK, color, 0.5D));
        g.drawRect(1, 1, i - 3, j - 3);
        g.setColor(Utils.blendColors(Color.WHITE, color, 0.5D));
        g.drawRect(0, 0, i - 1, j - 1);
        ImageIO.write(bufferedimage, "png", new File(s));
    }




  // pop-up listener
  class PopupListener extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger())
         menu.show(e.getComponent(), e.getX(), e.getY());
    }
  }















    String title;
    private Vec eyePosition;
    private Vec eyeDirection;
    private Vec up;
    private Vec screenUp;
    private Vec screenRight;
    private final double screenDistance = 1.0D;
    private double fov;
    private double scale;
    public int backCulling;
    public Color bgColor;
    public boolean fogEnabled;
    public double fogStart;
    public double fogEnd;
    public boolean bShowAxes;
    private MathParser mathParser;
    private Timer timer;
    private KeyBoard keyBoard;
    public FunctionsList functions;
    public AxesDefinition axesDefinition;
    private JPopupMenu menu;
    public  JMenuItem item1; 
    public  JMenuItem item2; 
    public  JMenuItem item3;
    public  JMenuItem item4;
    public  JMenuItem item5;
    public  JMenuItem item6;
    public  JMenuItem item7;
    public  String as[] = {  "X", "Z", "Y" };
    private double posd;
    private boolean showArrows = true;

    private Font labelFont = new Font("SansSerif", Font.BOLD, 16 );
    private Color labelColor = Color.black;
    private Font valueFont = new Font("SansSerif", Font.BOLD, 14 );
    


}
