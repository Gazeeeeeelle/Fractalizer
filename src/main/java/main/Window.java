package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Window extends JFrame implements MouseMotionListener, MouseListener, KeyListener, MouseWheelListener {
    static long time = System.currentTimeMillis();
    static int width = 814;
    static int height = 814;
    static int jpWidth = 814;
    static int jpHeight = 814;
    static double defZoom = .5;
    //814
    //test wide res = 1536 x 814
    //4k = 3840 x 2160
    //8k = 7680 x 4320
    static JPanel Fractalizer = new JPanel();
    static JPanel panel = new JPanel();
    static Point mousePt = new Point(0, 0);
    static int configSize = 200;

    Window() {
        this.setPreferredSize(new Dimension(width + configSize, height));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Fractalizer");
        this.setUndecorated(true);
        this.setLayout(null);
        this.setFocusable(true);
        this.addKeyListener(this);

        Fractalizer.addMouseMotionListener(this);
        Fractalizer.setLocation(configSize, 0);
        Fractalizer.addMouseListener(this);
        Fractalizer.addMouseWheelListener(this);
        Fractalizer.setSize(new Dimension(width, height));
        Fractalizer.setLayout(null);
        Fractalizer.setBackground(Color.black);
        Fractalizer.setFocusable(true);

        panel.setSize(new Dimension(configSize, height));
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(null);
        panel.setFocusable(false);

        initCfgPanel(panel);

        this.add(panel);
        this.add(Fractalizer);
        this.pack();
        this.setVisible(true);
    }
    static void initCfgPanel(JPanel panel) {
        //Threads
        {
            JTextField tf_threads = createTextField(10, 10, 130, 50, "", null);
            JTextField tf_priority = createTextField(10, 70, 130, 50, "", null);

            Button b_sendThreadInfo = createButton(150, 10, 40, 110, ">", e -> {
                int n = 1, p = 1;
                boolean safe = true;
                try {
                    n = Integer.parseInt(tf_threads.getText());
                } catch (NumberFormatException exception) {
                    tf_threads.setText("ERROR");
                    safe = false;
                }
                try {
                    p = Integer.parseInt(tf_priority.getText());
                } catch (NumberFormatException exception) {
                    tf_priority.setText("ERROR");
                    safe = false;
                }
                if (safe) {
                    Renderer.destroyCalculatorSet();
                    Renderer.buildCalculatorSet(n, p);
                }
            });
            panel.add(tf_threads);
            panel.add(tf_priority);
            panel.add(b_sendThreadInfo);
        }


    }
    static JTextField createTextField(int x, int y, int w, int h, String s, ActionListener al) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, w, h);
        tf.setText(s);
        applyDesign(tf);
        tf.addActionListener(al);
        return tf;
    }
    static Button createButton(int x, int y, int w, int h, String s, ActionListener al) {
        Button b = new Button();
        b.setBounds(x, y, w, h);
        b.addActionListener(al);
        b.setLabel(s);
        applyDesign(b);
        return b;
    }
    static void applyDesign(Component b) {
        b.setBackground(new Color(60, 60, 60));
        b.setFont(new Font("Arial", Font.PLAIN, 30));
        b.setForeground(Color.white);
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        Renderer.isOn = false;
        int dx = e.getX() - (int) mousePt.getX();
        int dy = e.getY() - (int) mousePt.getY();
        Renderer.move(dx, dy);
        mousePt = e.getPoint();

    }
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 2) {
            Renderer.topographicStep = .1;
            Renderer.clearImage();
        } else {
            mousePt = e.getPoint();
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        Renderer.resetPrecision();
        Renderer.isOn = true;
    }
    @Override
    public void keyPressed(KeyEvent e) {
        Control.doAction(e);
        switch (e.getKeyChar()) {
            case 'g':

                break;
            case 'f':

                break;
            case 'o':
                Renderer.resetRange();
                Renderer.clearImage();
                break;
            case 'j':
                Sets.julia ^= true;
                Renderer.clearImage();
                break;
            case 'l':
                Sets.connectLines ^= true;
                Renderer.clearImage();
                break;
            case 'c':
                double x = Fractalizer.getMousePosition().getX();
                double y = Fractalizer.getMousePosition().getY();

                x = Renderer.p2cx((int) x);
                y = Renderer.p2cy((int) y);

                double dX = Renderer.toX - Renderer.fromX;
                double dY = Renderer.toY - Renderer.fromY;

                Renderer.fromX = x - dX / 2;
                Renderer.toX = x + dX / 2;
                Renderer.fromY = y - dY / 2;
                Renderer.toY = y + dY / 2;

                Renderer.clearImage();
                break;
            case 'n':
                Renderer.night ^= true;
                Renderer.clearImage();
                break;
            case 'i':
                System.out.println(Renderer.getInfo());
                break;
            case 'p':
                Renderer.preCalcRange ^= true;
                Renderer.clearImage();
                break;
            case 's':
                Renderer.showPosition ^= true;
                break;
            case 'v':
                if (!VideoRecorder.isRec) {
                    VideoRecorder vr = new VideoRecorder();
                    vr.start();
                } else {
                    VideoRecorder.isRec = false;
                }
                break;
            case 'a':
                Renderer.axis ^= true;
                break;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                Sets.mode++;
                Renderer.clearImage();
                break;
            case KeyEvent.VK_LEFT:
                Sets.mode--;
                Renderer.clearImage();
                break;
            case KeyEvent.VK_UP:
                if (Renderer.whichColorPalette == Renderer.colorPalettes.length - 1) {
                    Renderer.whichColorPalette = 0;
                } else {
                    Renderer.whichColorPalette++;
                }
                Renderer.clearImage();
                break;
            case KeyEvent.VK_DOWN:
                if (Renderer.whichColorPalette == 0) {
                    Renderer.whichColorPalette = Renderer.colorPalettes.length - 1;
                } else {
                    Renderer.whichColorPalette--;
                }
                Renderer.clearImage();
                break;
            case KeyEvent.VK_F2:
                Renderer.screenshot();
                break;
            case KeyEvent.VK_F5:
                Renderer.clearImage();
                break;
            default:
                try {
                    int set = Integer.parseInt(String.valueOf(e.getKeyChar()));
                    Renderer.setOfInterest = set;
                    Renderer.clearImage();
                } catch (NumberFormatException exception) {
                    //pass
                }
        }
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() == 1) {
            if (e.isShiftDown()) {
                Renderer.topographicStep /= 2;
                Renderer.clearImage();
            } else {
                Renderer.zoomOut(defZoom);
            }
        } else {
            if (e.isShiftDown()) {
                Renderer.topographicStep *= 2;
                Renderer.clearImage();
            } else {
                Renderer.zoomIn(defZoom);
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.getSource().equals(Fractalizer)){
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
