package com.stuckinadrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Renderer extends JFrame {

    private final int tileSize = 16;
    private Tile[][] level;
    private int offsetX = 0, offsetY = 0;

    public Renderer(Tile[][] level) {
        this.level = level;
        setTitle("LevelGeneratorTest");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(new MyPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }


    private class MyPanel extends JPanel implements MouseListener, MouseMotionListener {

        private java.awt.Point dragging;

        public MyPanel() {
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
        }

        private void draw(Graphics g) {

            //this is where the drawing happens
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            for (int x = 0; x < level.length; x++) {
                for (int y = 0; y < level[0].length; y++) {
                    g2d.setColor(Color.black);
                    switch (level[x][y].tileType) {
                        case EMPTY:

                            break;
                        case WALL:
                            g2d.setColor(Color.red);
                            break;
                        case ROOM:
                            g2d.setColor(Color.gray);
                            break;
                        case CORRIDOR:
                            g2d.setColor(Color.darkGray);
                            break;
                        case ENTRANCE:
                            g2d.setColor(Color.green);
                            break;
                        case EXIT:
                            g2d.setColor(Color.blue);
                            break;
                        case KEY:
                            g2d.setColor(Color.pink);
                            break;
                        default:
                            g2d.setColor(Color.orange);
                    }

                    g2d.fillRect(x * tileSize + offsetX, y * tileSize + offsetY, tileSize, tileSize);
                    g2d.setColor(Color.black);
                    g2d.drawRect(x * tileSize + offsetX, y * tileSize + offsetY, tileSize, tileSize);
                }
            }

        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponents(g);
            draw(g);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(level.length * tileSize, level[0].length * tileSize);
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            dragging = new java.awt.Point(e.getX(), e.getY());

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point newPos = e.getPoint();
            offsetX += newPos.x - dragging.x;
            offsetY += newPos.y - dragging.y;
            dragging = newPos;
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }


}
