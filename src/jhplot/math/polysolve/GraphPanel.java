/***************************************************************************
 *   Copyright (C) 2012 by Paul Lutus                                      *
 *   lutusp@arachnoid.com                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
package jhplot.math.polysolve;


import java.awt.*;
import javax.swing.*;

/**
 *
 * @author lutusp
 */
final public class GraphPanel extends JPanel {

    double  xgmin, xgmax, ygmin, ygmax;
    PolySolve parent;

    public GraphPanel(PolySolve parent) {
        this.parent = parent;
    }

    public Pair mousePos(int x, int y) {
        compScreen();
        double mx = ntrp(xgmin,xgmax,parent.xmin,parent.xmax,x);
        double my = ntrp(ygmin,ygmax,parent.ymax,parent.ymin,y);
        return new Pair(mx,my);
    }

    double ntrp(double xa, double xb, double ya, double yb, double x) {
        return ((x - xa) * (yb - ya) / (xb - xa)) + ya;
    }

    int intrp(double xa, double xb, double ya, double yb, double x) {
        return (int) ntrp(xa, xb, ya, yb, x);
    }

    void drawScaledLine(Graphics g, double ox, double oy, double x, double y) {
        int opx, opy, px, py;
        opx = intrp(parent.xmin, parent.xmax, xgmin, xgmax, ox);
        px = intrp(parent.xmin, parent.xmax, xgmin, xgmax, x);
        opy = intrp(parent.ymax, parent.ymin, ygmin, ygmax, oy);
        py = intrp(parent.ymax, parent.ymin, ygmin, ygmax, y);
        g.drawLine(opx, opy, px, py);

    }

    void drawScaledPoint(Graphics g, double x, double y, int dsi) {
        int ds = dsi / 2;
        int px = intrp(parent.xmin, parent.xmax, xgmin, xgmax, x);
        int py = intrp(parent.ymax, parent.ymin, ygmin, ygmax, y);
        g.fillOval(px - ds, py - ds, dsi, dsi);
    }

    void drawScale(Graphics cg) {

        double scaleFact = 5.0;

        double ystep = Math.abs((parent.ymax - parent.ymin)) / 5.0;

        double s = Math.log(ystep) / Math.log(scaleFact);

        s = Math.floor(s);

        ystep = Math.pow(scaleFact, s);

        cg.setColor(parent.gridColor);

        for (double y = -ystep; y >= parent.ymin; y -= ystep) {
            if ((y >= parent.ymin && y <= parent.ymax)) {
                drawScaledLine(cg, parent.xmin, y, parent.xmax, y);
            }
        }
        for (double y = ystep; y <= parent.ymax; y += ystep) {
            if ((y >= parent.ymin && y <= parent.ymax)) {
                drawScaledLine(cg, parent.xmin, y, parent.xmax, y);
            }
        }

        // horizontal zero line

        cg.setColor(parent.zeroColor);

        drawScaledLine(cg, parent.xmin, 0, parent.xmax, 0);

        double xstep = Math.abs((parent.xmax - parent.xmin)) / 5.0;

        s = Math.log(xstep) / Math.log(scaleFact);

        s = Math.floor(s);

        xstep = Math.pow(scaleFact, s);

        cg.setColor(parent.gridColor);

        for (double x = xstep; x <= parent.xmax; x += xstep) {
            if ((x >= parent.xmin && x <= parent.xmax)) {
                drawScaledLine(cg, x, parent.ymin, x, parent.ymax);
            }
        }
        for (double x = -xstep; x >= parent.xmin; x -= xstep) {
            if ((x >= parent.xmin && x <= parent.xmax)) {
                drawScaledLine(cg, x, parent.ymin, x, parent.ymax);
            }
        }

        // vertical zero line

        cg.setColor(parent.zeroColor);

        drawScaledLine(cg, 0, parent.ymin, 0, parent.ymax);
    }

    void drawDataPoints(Graphics2D cg, int dsi) {
        cg.setColor(parent.dataColor);
        for (Pair pr : parent.userDataList) {
            double x = pr.x;
            double y = pr.y;
            drawScaledPoint(cg, x, y, dsi);
        }
    }

    void drawCurve(Graphics2D cg) {
        double ox = 0, oy = 0;
        cg.setColor(parent.lineColor);
        double step = (parent.dataXmax - parent.dataXmin) / (25 * (parent.poly_order + 1));
        for (double x = parent.dataXmin; x < parent.dataXmax + step; x += step) {
            double y = parent.plotFunct(x);
            if (x == parent.dataXmin) {
                ox = x;
                oy = y;
            }
            drawScaledLine(cg, ox, oy, x, y);
            ox = x;
            oy = y;
        }
    }

    boolean compScreen() {
        boolean result = false;
        int w = this.getSize().width;
        int h = this.getSize().height;
        if (w > 0 && h > 0) {

            xgmin = 1;
            ygmin = 1;
            xgmax = w - 2;
            ygmax = h - 2;
            result = true;
        }
        return result;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D cg = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        cg.addRenderingHints(rh);

        if(compScreen()) {

       // int w = this.getSize().width;
       // int h = this.getSize().height;

       // if (w > 0 && h > 0) {

            //xgmin = 1;
            //ygmin = 1;
            //xgmax = w - 2;
            //ygmax = h - 2;

            int dsi = (int) (parent.dotScale * xgmax / 500.0);
            dsi = (dsi < 2) ? 2 : dsi;

            if (parent.data_valid) {

                drawScale(cg);
                drawDataPoints(cg, dsi);
                drawCurve(cg);
            } else {
                if (parent.errorMsg.length() > 0) {
                    String s = "Error: " + parent.errorMsg;
                    int sl = s.length();
                    int hp = (int) (xgmax - sl * 5.5) / 2;
                    cg.drawString(s, hp, (int) ygmax / 2);
                }
            }
        }
    }
}
