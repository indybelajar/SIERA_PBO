package view;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.geom.Path2D;

public class SvgIcon implements Icon {
    public enum Type {
        USER, INSTAGRAM, SAVE, DEADLINE, TIKTOK, X, LINKEDIN, GREETING
    }

    private final Type type;
    private final int width;
    private final int height;
    private final Color color;
    private final float strokeWidth;

    public SvgIcon(Type type, int size) {
        this(type, size, size, null, 2f);
    }

    public SvgIcon(Type type, int size, Color color) {
        this(type, size, size, color, 2f);
    }

    public SvgIcon(Type type, int width, int height, Color color, float strokeWidth) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public int getIconWidth() { return width; }

    @Override
    public int getIconHeight() { return height; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);

        // Determine icon color
        Color drawColor = color;
        if (drawColor == null) {
            if (c != null) {
                drawColor = c.getForeground();
            } else {
                drawColor = Color.BLACK;
            }
        }
        g2.setColor(drawColor);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Scale graphics context to 24x24 coordinate space for standardizing coordinates
        double scaleX = (double) width / 24.0;
        double scaleY = (double) height / 24.0;
        g2.scale(scaleX, scaleY);

        switch (type) {
            case USER:
                drawUser(g2);
                break;
            case INSTAGRAM:
                drawInstagram(g2);
                break;
            case SAVE:
                drawSave(g2);
                break;
            case DEADLINE:
                drawDeadline(g2);
                break;
            case TIKTOK:
                drawTiktok(g2);
                break;
            case X:
                drawX(g2);
                break;
            case LINKEDIN:
                drawLinkedin(g2);
                break;
            case GREETING:
                drawGreeting(g2);
                break;
        }

        g2.dispose();
    }

    private void drawUser(Graphics2D g2) {
        // Path: M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2
        Path2D.Double path = new Path2D.Double();
        path.moveTo(19, 21);
        path.lineTo(19, 19);
        path.curveTo(19, 16.79, 17.21, 15, 15, 15);
        path.lineTo(9, 15);
        path.curveTo(6.79, 15, 5, 16.79, 5, 19);
        path.lineTo(5, 21);
        g2.draw(path);

        // Circle: cx=12, cy=7, r=4
        g2.drawOval(8, 3, 8, 8);
    }

    private void drawInstagram(Graphics2D g2) {
        g2.scale(24.0 / 256.0, 24.0 / 256.0);
        g2.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // 1. circle cx="128" cy="128" r="40" -> x=88, y=88, w=80, h=80
        g2.drawOval(88, 88, 80, 80);

        // 2. rect x="32" y="32" width="192" height="192" rx="48" -> arc=96
        g2.drawRoundRect(32, 32, 192, 192, 96, 96);

        // 3. circle cx="180" cy="76" r="12" -> x=168, y=64, w=24, h=24
        g2.fillOval(168, 64, 24, 24);
    }

    private void drawSave(Graphics2D g2) {
        // path d="M15.2 3a2 2 0 0 1 1.4.6l3.8 3.8a2 2 0 0 1 .6 1.4V19a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z"
        Path2D.Double p1 = new Path2D.Double();
        p1.moveTo(15.2, 3);
        p1.curveTo(15.73, 3, 16.24, 3.21, 16.6, 3.6);
        p1.lineTo(20.4, 7.4);
        p1.curveTo(20.79, 7.76, 21, 8.27, 21, 8.8);
        p1.lineTo(21, 19);
        p1.curveTo(21, 20.1, 20.1, 21, 19, 21);
        p1.lineTo(5, 21);
        p1.curveTo(3.9, 21, 3, 20.1, 3, 19);
        p1.lineTo(3, 5);
        p1.curveTo(3, 3.9, 3.9, 3, 5, 3);
        p1.closePath();
        g2.draw(p1);

        // path d="M17 21v-7a1 1 0 0 0-1-1H8a1 1 0 0 0-1 1v7"
        Path2D.Double p2 = new Path2D.Double();
        p2.moveTo(17, 21);
        p2.lineTo(17, 14);
        p2.curveTo(17, 13.45, 16.55, 13, 16, 13);
        p2.lineTo(8, 13);
        p2.curveTo(7.45, 13, 7, 13.45, 7, 14);
        p2.lineTo(7, 21);
        g2.draw(p2);

        // path d="M7 3v4a1 1 0 0 0 1 1h7"
        Path2D.Double p3 = new Path2D.Double();
        p3.moveTo(7, 3);
        p3.lineTo(7, 7);
        p3.curveTo(7, 7.55, 7.45, 8, 8, 8);
        p3.lineTo(15, 8);
        g2.draw(p3);
    }

    private void drawDeadline(Graphics2D g2) {
        // path d="M16 14v2.2l1.6 1"
        Path2D.Double p1 = new Path2D.Double();
        p1.moveTo(16, 14);
        p1.lineTo(16, 16.2);
        p1.lineTo(17.6, 17.2);
        g2.draw(p1);

        // path d="M16 4h2a2 2 0 0 1 2 2v.832"
        Path2D.Double p2 = new Path2D.Double();
        p2.moveTo(16, 4);
        p2.lineTo(18, 4);
        p2.curveTo(19.1, 4, 20, 4.9, 20, 6);
        p2.lineTo(20, 6.832);
        g2.draw(p2);

        // path d="M8 4H6a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h2"
        Path2D.Double p3 = new Path2D.Double();
        p3.moveTo(8, 4);
        p3.lineTo(6, 4);
        p3.curveTo(4.9, 4, 4, 4.9, 4, 6);
        p3.lineTo(4, 20);
        p3.curveTo(4, 21.1, 4.9, 22, 6, 22);
        p3.lineTo(8, 22);
        g2.draw(p3);

        // circle cx="16" cy="16" r="6"
        g2.drawOval(10, 10, 12, 12);

        // rect x="8" y="2" width="8" height="4" rx="1"
        g2.drawRoundRect(8, 2, 8, 4, 2, 2);
    }

    private void drawTiktok(Graphics2D g2) {
        g2.scale(24.0 / 256.0, 24.0 / 256.0);
        g2.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D.Double path = new Path2D.Double();
        path.moveTo(168, 102);
        path.curveTo(185, 112, 205, 118, 224, 120);
        path.lineTo(224, 80);
        path.curveTo(193, 80, 168, 55, 168, 24);
        path.lineTo(128, 24);
        path.lineTo(128, 156);
        path.curveTo(128, 171.46, 115.46, 184, 100, 184);
        path.curveTo(84.54, 184, 72, 171.46, 72, 156);
        path.curveTo(72, 144, 80, 133, 88, 130.69);
        path.lineTo(88, 88);
        path.curveTo(56, 93.67, 32, 122.54, 32, 156);
        path.curveTo(32, 193.56, 62.44, 224, 100, 224);
        path.curveTo(137.56, 224, 168, 193.56, 168, 156);
        path.closePath();

        g2.draw(path);
    }

    private void drawX(Graphics2D g2) {
        g2.scale(24.0 / 256.0, 24.0 / 256.0);
        g2.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(48, 40);
        poly.lineTo(96, 40);
        poly.lineTo(208, 216);
        poly.lineTo(160, 216);
        poly.closePath();
        g2.draw(poly);

        g2.draw(new java.awt.geom.Line2D.Double(113.88, 143.53, 48, 216));
        g2.draw(new java.awt.geom.Line2D.Double(208, 40, 142.12, 112.47));
    }

    private void drawLinkedin(Graphics2D g2) {
        g2.scale(24.0 / 256.0, 24.0 / 256.0);
        g2.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.drawRoundRect(32, 32, 192, 192, 16, 16);
        g2.draw(new java.awt.geom.Line2D.Double(120, 112, 120, 176));
        g2.draw(new java.awt.geom.Line2D.Double(88, 112, 88, 176));

        Path2D.Double path = new Path2D.Double();
        path.moveTo(120, 140);
        path.curveTo(120, 124.54, 132.54, 112, 148, 112);
        path.curveTo(163.46, 112, 176, 124.54, 176, 140);
        path.lineTo(176, 176);
        g2.draw(path);

        g2.fillOval(76, 72, 24, 24);
    }

    private void drawGreeting(Graphics2D g2) {
        g2.scale(24.0 / 256.0, 24.0 / 256.0);
        g2.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D.Double p1 = new Path2D.Double();
        p1.moveTo(96.65, 62);
        p1.curveTo(96.65 + 10, 62 - 20, 131.29 - 10, 42 - 10, 131.29, 42);
        p1.lineTo(161.29, 94);
        g2.draw(p1);

        Path2D.Double p2 = new Path2D.Double();
        p2.moveTo(69.32, 94.67);
        p2.lineTo(55.08, 70);
        p2.curveTo(55.08 + 10, 70 - 20, 89.72 - 10, 50 - 10, 89.72, 50);
        p2.lineTo(120.89, 104);
        g2.draw(p2);

        Path2D.Double p3 = new Path2D.Double();
        p3.moveTo(158.87, 160);
        p3.curveTo(168, 140, 168, 120, 168, 105.58);
        p3.lineTo(161.32, 94);
        p3.curveTo(161.32 + 10, 94 - 10, 195.96 - 10, 74 - 10, 195.96, 74);
        p3.lineTo(213.31, 104);
        p3.curveTo(230, 140, 160, 200, 74.7, 184);
        p3.lineTo(34.7, 114.68);
        p3.curveTo(34.7 + 10, 114.68 - 20, 69.34 - 10, 94.68 - 10, 69.34, 94.68);
        p3.lineTo(88.57, 128);
        g2.draw(p3);

        Path2D.Double p4 = new Path2D.Double();
        p4.moveTo(192, 33.78);
        p4.curveTo(205, 40, 215, 50, 223.67, 58);
        p4.lineTo(224, 58.57);
        g2.draw(p4);

        Path2D.Double p5 = new Path2D.Double();
        p5.moveTo(74.62, 232);
        p5.curveTo(65, 222, 55, 212, 47, 200);
        g2.draw(p5);
    }
}
