package main;

import complexMath.Sets;

import static complexMath.Sets.increaseTopographicStep;
import static complexMath.Sets.decreaseTopographicStep;
import static complexMath.Sets.resetTopographicStep;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Window extends JFrame implements MouseMotionListener, MouseListener, KeyListener, MouseWheelListener {
    final int
            width = 1000,
            height = 1000,
            jpWidth = 1000, //814 //FIXME
            jpHeight = 1000; //814 //FIXME
    final JPanel fractalizer = new JPanel();
    private Point mousePt = new Point(0, 0);
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
            resetTopographicStep();
            Renderer.clearImage();
        } else {
            mousePt = e.getPoint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //FIXME make a method in render for this
        Renderer.turnOff();
        Calculator.resetPrecisions(); //FIXME
        Renderer.turnOn();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        controller.operate(e);
    }
    int[] getMousePos() {
        Point p = fractalizer.getMousePosition();
        if (p == null) return null;
        return new int[]{
                (int) (p.getX()),
                (int) (p.getY())
        };
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() == 1) {
            if (e.isShiftDown()) {
                decreaseTopographicStep();
                Renderer.clearImage();
            } else {
                Renderer.zoomOut();
            }
        } else {
            if (e.isShiftDown()) {
                increaseTopographicStep();
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
        if (e.getSource().equals(fractalizer)) {
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
    private final Controller controller = new Controller();
    {
        controller
                .mapKey("G", e -> {
                    if (controller.isCool(200)) {
                        Sets.setZ1(0d, 0d);
                    }
                })
                .mapKey("F", e -> {
                    if(controller.isCool(200)) Sets.setZPixel(this.getMousePos());
                })
                .mapKey("E", e -> {
                    Renderer.special ^= true;
                    Renderer.clearImage();
                })
                .mapKey("O", e -> {
                    Renderer.resetRange();
                })
                .mapKey("H", e -> {
                    Sets.toggleInverse();
                    Renderer.clearImage();
                })
                .mapKey("J", e -> {
                    Sets.toggleJulia();
                    Renderer.clearImage();
                })
                .mapKey("L", e -> {
                    Sets.connectLines ^= true;
                    Renderer.clearImage();
                })
                .mapKey("C", e -> Renderer.centerAtPixel())
                .mapKey("N", e -> {
                    Renderer.togglePinpointPrecision();
                Renderer.clearImage();
                })
                .mapKey("I", e -> System.out.println(Sets.getInfo()))
                .mapKey("P", e -> {
                    Renderer.togglePreCalculation();
                    Renderer.clearImage();
                })
                .mapKey("S", e -> Renderer.togglePosition())
                .mapKey("A", e -> Renderer.toggleAxis())
                .mapKey("Right", e -> Sets.nextMode())
                .mapKey("Left", e -> Sets.previousMode())
                .mapKey("Up", e -> {
                    Colors.shiftColorPalette(1);
                    Renderer.clearImage();
                })
                .mapKey("Down", e -> {
                    Colors.shiftColorPalette(-1);
                    Renderer.clearImage();
                })
                .mapKey("F5", e -> Renderer.clearImage())
                .mapKey("F2", e -> Renderer.screenshot())
                .mapKey("[1-9]", e -> {
                    int set = Integer.parseInt(e);
                    if (set < Sets.setsLength) Renderer.chooseSet(set);
                    else return;
                    Renderer.clearImage();
                });
    }
}