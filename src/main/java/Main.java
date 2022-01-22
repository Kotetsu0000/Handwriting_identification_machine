import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        Main game = new Main();
        game.start();
    }
    JFrame mainWindow;
    BufferStrategy strategy;
    MainDisplay display = new MainDisplay();
    mouseData cursor = new mouseData();
    GraphicsInfo ginfo = new GraphicsInfo();

    Main(){
        this.mainWindow = new JFrame("手書き数字判定機");
        this.mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainWindow.setSize(800,650);//1100,900
        this.mainWindow.setLocationRelativeTo(null);
        this.mainWindow.setVisible(true);
        this.mainWindow.setResizable(false);
        this.mainWindow.addMouseListener(cursor);
        this.mainWindow.addMouseMotionListener(cursor);

        //バッファストラテジー
        this.mainWindow.setIgnoreRepaint(true);
        this.mainWindow.createBufferStrategy(2);
        this.strategy = this.mainWindow.getBufferStrategy();

        try {
            this.display.loadMedia();
        }catch (IOException e){
            System.out.println(e);
            JOptionPane.showMessageDialog(this.mainWindow,"タイトル画像読み込みエラー");
        }
    }

    void start(){
        java.util.Timer t = new Timer(true);
        t.schedule(new RenderTask(),0,1);
    }

    class RenderTask extends TimerTask {

        @Override
        public void run() {
            Main.this.render();
        }
    }

    void render(){
        Graphics2D g = (Graphics2D) this.strategy.getDrawGraphics();
        g.setBackground(new Color(0,128,0));
        g.clearRect(0,0,this.mainWindow.getWidth(), this.mainWindow.getHeight());
        ginfo.g = g;
        ginfo.windowWidth = this.mainWindow.getWidth();
        ginfo.windowHeight = this.mainWindow.getHeight();
        this.display.getCurrentDisplay().show(ginfo);
        g.dispose();
        this.strategy.show();
    }

    class mouseData extends JComponent implements MouseListener, MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            //System.out.println(e.getX());
            //System.out.println(e.getY());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            ginfo.drawX = e.getX();
            ginfo.drawY = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            ginfo.clickX = e.getX();
            ginfo.clickY = e.getY();
            ginfo.drawX = -100;
            ginfo.drawY = -100;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            ginfo.cursorX = e.getX();
            ginfo.cursorY = e.getY();
            ginfo.drawX = e.getX();
            ginfo.drawY = e.getY();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            ginfo.cursorX = e.getX();
            ginfo.cursorY = e.getY();
        }
    }
}