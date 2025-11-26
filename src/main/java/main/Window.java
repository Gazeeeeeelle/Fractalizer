package main;

import complexMath.Sets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Window extends JFrame implements MouseMotionListener, MouseListener, KeyListener, MouseWheelListener {
    int
            width = 1000,
            height = 1000,
            jpWidth = 1000, //814 //FIXME
            jpHeight = 1000; //814 //FIXME
    static JPanel fractalizer = new JPanel();
    static JPanel panel = new JPanel();
    private static Point mousePt = new Point(0, 0);
    private static final Controller controller = new Controller();
    static {
        controller.useMap(controller.keyMap1);
    }

    Window() {
        this.setPreferredSize(new Dimension(width, height));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Fractalizer");
        this.setUndecorated(true);
        this.setLayout(null);
        this.setFocusable(true);
        this.addKeyListener(this);

        fractalizer.addMouseMotionListener(this);
        fractalizer.addMouseListener(this);
        fractalizer.addMouseWheelListener(this);
        fractalizer.setSize(new Dimension(width, height));
        fractalizer.setBackground(Color.black);
        fractalizer.setFocusable(true);

        this.add(panel);
        this.add(fractalizer);
        this.pack();

        this.setVisible(true);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - (int) mousePt.getX();
        int dy = e.getY() - (int) mousePt.getY();
        Renderer.move(dx, dy);
        mousePt = e.getPoint();
    }
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 2) {
            Sets.topographicStep = .1;
            Renderer.clearImage();
        } else {
            mousePt = e.getPoint();
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        Calculator.resetPrecisions();
        Calculator.isOn = true;
    }
    @Override
    public void keyPressed(KeyEvent e) {
        controller.operate(e);
    }
    static int[] getMousePos(){
        Point p = fractalizer.getMousePosition();
        if(p==null)return null;
        return new int[]{
                (int) (p.getX()),
                (int) (p.getY())
        };
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() == 1) {
            if (e.isShiftDown()) {
                Sets.topographicStep /= 2;
                Renderer.clearImage();
            } else {
                Renderer.zoomOut();
            }
        } else {
            if (e.isShiftDown()) {
                Sets.topographicStep *= 2;
                Renderer.clearImage();
            } else {
                Renderer.zoomIn();
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.getSource().equals(fractalizer)){
            this.requestFocus();
        }
    }
    @Override
    public void mouseExited(MouseEvent e) {

    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void mouseMoved(MouseEvent e) {

    }
    @Override
    public void mouseClicked(MouseEvent e) {

    }
}
