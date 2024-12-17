package com.mycompany.fullproject;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import javax.swing.*;

public class PaintBrush extends JFrame implements ActionListener {

    // Buttons
    private JButton buttonClear = new JButton("Clear");
    private JButton buttonUndo = new JButton("Undo");
    private JButton buttonLine = new JButton("Line");
    private JButton buttonRect = new JButton("Rectangle");
    private JButton buttonOval = new JButton("Oval");
    private JButton buttonPencil = new JButton("Pencil");
    private JButton buttonEraser = new JButton("Eraser");
    private JButton buttonRed = new JButton("Red");
    private JButton buttonGreen = new JButton("Green");
    private JButton buttonBlack = new JButton("Black");
    private JButton buttonBlue = new JButton("Blue");
    private JButton buttonFill = new JButton("Fill"); // New fill button

    // Line style options
    private JCheckBox boxSolid = new JCheckBox("Solid", true);
    private JCheckBox boxDotted = new JCheckBox("Dotted");

    // Drawing-related variables
    private int startX, startY;
    private Color currentColor = Color.BLACK;
    private String currentTool = "Pencil";
    private boolean isSolidLine = true;
    private boolean isFillShape = false; // Flag for fill shapes

    private ArrayList<DrawnShape> shapes = new ArrayList<>(); // Stores shapes
    private ArrayList<Point> pencilPoints = new ArrayList<>(); // For free drawing

    public PaintBrush() {
        setTitle("Paint Brush");
        setSize(750, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Initialize buttons and checkboxes
        initializeButtons();

        // Add Mouse Listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();

                if (currentTool.equals("Pencil")) {
                    pencilPoints.clear();
                    pencilPoints.add(new Point(startX, startY));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!currentTool.equals("Pencil")) {
                    Shape shape = createShape(e.getX(), e.getY());
                    shapes.add(new DrawnShape(shape, currentColor, getStrokeStyle(), isFillShape));
                } else {
                    shapes.add(new DrawnShape(new GeneralPath(createPencilPath()), currentColor, getStrokeStyle(), false));
                }
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentTool.equals("Pencil")) {
                    pencilPoints.add(new Point(e.getX(), e.getY()));
                    repaint();
                } else if (currentTool.equals("Eraser")) {
                    eraseShape(e.getX(), e.getY());
                    repaint();
                }
            }
        });

        setVisible(true);
    }

    private void initializeButtons() {
        int xPos = 20, yPos = 10, buttonWidth = 100, spacing = 5;
        JButton[] buttons = {buttonClear, buttonUndo, buttonLine, buttonRect, buttonOval, buttonPencil, buttonEraser,
                buttonRed, buttonGreen, buttonBlack, buttonBlue, buttonFill};
        for (JButton button : buttons) {
            button.setBounds(xPos, yPos, buttonWidth, 25);
            button.addActionListener(this);
            add(button);
            xPos += buttonWidth + spacing;
        }
        buttonRed.setBackground(Color.RED);
        buttonGreen.setBackground(Color.GREEN);
        buttonBlack.setBackground(Color.BLACK);
        buttonBlue.setBackground(Color.BLUE);

        boxSolid.setBounds(xPos, yPos, buttonWidth, 25);
        boxDotted.setBounds(xPos + buttonWidth + spacing, yPos, buttonWidth, 25);
        add(boxSolid);
        add(boxDotted);
        boxSolid.addActionListener(this);
        boxDotted.addActionListener(this);
    }

    private Shape createShape(int endX, int endY) {
        switch (currentTool) {
            case "Line":
                return new Line2D.Double(startX, startY, endX, endY);
            case "Rectangle":
                return new Rectangle2D.Double(Math.min(startX, endX), Math.min(startY, endY),
                        Math.abs(endX - startX), Math.abs(endY - startY));
            case "Oval":
                return new Ellipse2D.Double(Math.min(startX, endX), Math.min(startY, endY),
                        Math.abs(endX - startX), Math.abs(endY - startY));
            case "Eraser":
                return new Rectangle2D.Double(endX - 10, endY - 10, 0, 0);
            default:
                return null;
        }
    }

    private Path2D createPencilPath() {
        Path2D path = new Path2D.Double();
        if (!pencilPoints.isEmpty()) {
            Point start = pencilPoints.get(0);
            path.moveTo(start.x, start.y);
            for (Point p : pencilPoints) {
                path.lineTo(p.x, p.y);
            }
        }
        return path;
    }

    private void eraseShape(int x, int y) {
        shapes.removeIf(drawnShape -> drawnShape.shape.intersects(x - 10, y - 10, 20, 20));
    }

    private BasicStroke getStrokeStyle() {
        return isSolidLine ? new BasicStroke(2) : new BasicStroke(2, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, new float[]{9f}, 0.0f);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == buttonClear) {
            shapes.clear();
            repaint();
        } else if (source == buttonUndo && !shapes.isEmpty()) {
            shapes.remove(shapes.size() - 1);
            repaint();
        } else if (source == buttonLine) currentTool = "Line";
        else if (source == buttonRect) currentTool = "Rectangle";
        else if (source == buttonOval) currentTool = "Oval";
        else if (source == buttonPencil) currentTool = "Pencil";
        else if (source == buttonEraser) currentTool = "Eraser";
        else if (source == buttonRed) currentColor = Color.RED;
        else if (source == buttonGreen) currentColor = Color.GREEN;
        else if (source == buttonBlack) currentColor = Color.BLACK;
        else if (source == buttonBlue) currentColor = Color.BLUE;
        else if (source == boxSolid) isSolidLine = true;
        else if (source == boxDotted) isSolidLine = false;
        else if (source == buttonFill) isFillShape = !isFillShape; // Toggle fill mode
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        for (DrawnShape drawnShape : shapes) {
            g2d.setColor(drawnShape.color);
            g2d.setStroke(drawnShape.stroke);
            if (drawnShape.isFilled && drawnShape.shape instanceof RectangularShape) {
                g2d.fill((RectangularShape) drawnShape.shape);
            } else {
                g2d.draw(drawnShape.shape);
            }
        }
        if (currentTool.equals("Pencil")) {
            g2d.setColor(currentColor);
            g2d.setStroke(getStrokeStyle());
            g2d.draw(createPencilPath());
        }
    }

    // Inner class to store shapes and properties
    private static class DrawnShape {
        Shape shape;
        Color color;
        BasicStroke stroke;
        boolean isFilled;

        DrawnShape(Shape shape, Color color, BasicStroke stroke, boolean isFilled) {
            this.shape = shape;
            this.color = color;
            this.stroke = stroke;
            this.isFilled = isFilled;
        }
    }


}
