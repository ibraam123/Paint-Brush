package com.mycompany.fullproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MouseLap extends JFrame {
    private final ArrayList<Line> lines = new ArrayList<>();
    private int startX, startY, endX, endY;

    public MouseLap() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setTitle("Mouse Drag to Draw Lines");
        
        // Panel for drawing
        DrawingPanel panel = new DrawingPanel();
        this.add(panel);
        
        this.setVisible(true);
    }

    // Inner class for managing the drawing panel
    class DrawingPanel extends JPanel {
        public DrawingPanel() {
            // Add Mouse Listener and Motion Listener for dragging
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // Save the starting point
                    startX = e.getX();
                    startY = e.getY();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // Add the completed line to the list
                    lines.add(new Line(startX, startY, e.getX(), e.getY()));
                    repaint();
                }
            });

            this.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    // Update the end point as the mouse is dragged
                    endX = e.getX();
                    endY = e.getY();
                    repaint(); // Repaint the panel
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(2)); // Set line thickness
            
            // Draw all the previously stored lines
            g2d.setColor(Color.BLACK);
            for (Line line : lines) {
                g2d.drawLine(line.startX, line.startY, line.endX, line.endY);
            }

            // Draw the currently dragged line
            g2d.setColor(Color.RED); // Use a different color for the active line
            g2d.drawLine(startX, startY, endX, endY);
        }
    }

    // Line class to store line endpoints
    static class Line {
        int startX, startY, endX, endY;

        public Line(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }


}
