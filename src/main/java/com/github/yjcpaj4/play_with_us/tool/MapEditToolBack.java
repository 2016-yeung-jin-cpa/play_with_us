package com.github.yjcpaj4.play_with_us.tool;

import com.github.yjcpaj4.play_with_us.GraphicLooper;
import com.github.yjcpaj4.play_with_us.geom.EarCutTriangulator;
import com.github.yjcpaj4.play_with_us.geom.Polygon;
import com.github.yjcpaj4.play_with_us.math.Point2D;
import com.github.yjcpaj4.play_with_us.util.AWTUtil;
import com.github.yjcpaj4.play_with_us.util.FileUtil;
import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

public class MapEditToolBack extends GraphicLooper implements MouseListener, KeyListener {
    
    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private File mImageFile;
    private BufferedImage mImage;
    
    private boolean mReversed = false;
    
    private static final int LIGHTLESS_POINTING = 0;
    private static final int NOT_WALKABLE_POINTING = 1;
    
    private int mPointingMode = -1;
    private List<Point2D> mCurrentPoint = new ArrayList<>();
    
    private List<Polygon> mNotWalkable = new ArrayList<>();
    private List<Polygon> mLightless = new ArrayList<>();
    
    public MapEditToolBack() throws Exception {
        mCanvas.addMouseListener(this);
        mCanvas.addKeyListener(this);
        mCanvas.setBackground(Color.BLACK);
        mCanvas.setFocusable(true);
        
        JFrame f = new JFrame();
        
        
        
        
        JMenuBar mb = new JMenuBar(); 
        {
            JMenu m = new JMenu("파일");
            
            JMenuItem m3 = new JMenuItem("맵 저장");
            m3.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evt) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("img", mImageFile.getName());
                    m.put("lightless", new ArrayList());
                    m.put("not_walkable", new ArrayList());
                    
                    for(Polygon o : mLightless) {
                        List<Integer[]> lightless = new ArrayList<>();
                        for (Point2D p : o.getPoints()) {
                            lightless.add(new Integer[]{ (int)p.getX(), (int)p.getY() });
                        }
                        ((ArrayList) m.get("lightless")).add(lightless);
                    }
                    
                    for(Polygon o : mNotWalkable) {
                        List<Integer[]> notWalkable = new ArrayList<>();
                        for (Point2D p : o.getPoints()) {
                            notWalkable.add(new Integer[]{ (int)p.getX(), (int)p.getY() });
                        }
                        ((ArrayList) m.get("not_walkable")).add(notWalkable);
                    }
                    
                    try {
                        FileUtil.setContents(new File("res/map.json"), new Gson().toJson(m));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            m.add(m3);
            mb.add(m);
        }


        f.setTitle("PLAY with us - Map Edit Tool");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setJMenuBar(mb);
        f.setResizable(false);
        f.setSize(1280, 800);
        f.setLocationRelativeTo(null);        
        f.getContentPane().add(mCanvas);
        f.setVisible(true);
        
        start();
    }
    
    private Polygon get() {
        Area a = new Area(new Rectangle2D.Double(0, 0, mImage.getWidth(), mImage.getHeight()));
        a.subtract(new Area(new Polygon(mCurrentPoint).toAWTPolygon()));
        
        return new Polygon(AWTUtil.getPoints(a));
    }
    
    private List<Polygon> getSelection() {
        Polygon p1 = new Polygon(mCurrentPoint);
        
        if ( ! mReversed) {
            return p1.getTriangulate();
        }
        
        Rectangle2D.Float r = new Rectangle2D.Float(0, 0, mImage.getWidth(), mImage.getHeight());
        
        Area a1 = new Area(r);
        a1.subtract(new Area(p1.toAWTPolygon()));
        
        Polygon p2 = AWTUtil.getPolygon(a1);
         
        a1.subtract(new Area(p2.toAWTPolygon()));
        
        List<Polygon> l = new ArrayList();
        l.addAll(p2.getTriangulate());
        l.addAll(AWTUtil.getPolygon(a1).getTriangulate());
        return l;
    }
    
    @Override
    protected void draw(long delta, Graphics2D g2d) {
        super.draw(delta, g2d);
        
        if (mImage == null) {
            Font f = new Font("돋움", Font.BOLD, 40);
            g2d.setFont(f);
            FontMetrics fm = g2d.getFontMetrics(f);

            String s = "여기를 클릭하여 맵 이미지를 불러옵니다.";
            fm.stringWidth(s);
            
            g2d.setColor(Color.RED);
            g2d.drawString(s, 1280 / 2 - fm.stringWidth(s) / 2, 800 / 2 - fm.getHeight() / 2);
            return;
        }
        
        g2d.drawImage(mImage, 0, 0, null);
        
        g2d.setColor(new Color(255, 0, 0, (int) (255 * 0.5)));    
        for(Polygon p : mNotWalkable) {
            g2d.fillPolygon(p.toAWTPolygon()); 
        }
        
        g2d.setColor(new Color(255, 255, 0, (int) (255 * 0.5)));    
        for(Polygon p : mLightless) {
            g2d.fillPolygon(p.toAWTPolygon()); 
        }
        
        if ( ! mCurrentPoint.isEmpty()) {
            g2d.setColor(new Color(34, 181, 0, (int) (255 * 0.5))); 
            for (Polygon p : getSelection()) {
                g2d.fillPolygon(p.toAWTPolygon()); 
            }
            
            g2d.setColor(new Color(34, 181, 0));
            for(int n = 0; n < mCurrentPoint.size(); ++n) { 
                Point2D p = mCurrentPoint.get(n);

                g2d.fillOval((int)p.getX() - (10 / 2), (int)p.getY() - (10 / 2), 10, 10);
            }
        }
        
        g2d.setColor(Color.MAGENTA);
        
        Font f = new Font("돋움", Font.BOLD, 20);
        g2d.setFont(f);
        FontMetrics fm = g2d.getFontMetrics(f);
        
        String s1 = "숫자 키 1 => 비출수 없는 영역";
        String s2 = "숫자 키 2 => 걸어갈 수 없는 영역";
        String s3 = "Ctrl + Z => 한칸 되돌리기";
        String s4 = "마우스 오른쪽 => 선택 영역 반전";
        
        if (mPointingMode == LIGHTLESS_POINTING) s1 += " [편집중]";
        if (mPointingMode == NOT_WALKABLE_POINTING) s2 += " [편집중]";
        
        g2d.drawString(s1, 10, fm.getHeight() * 1);
        g2d.drawString(s2, 10, fm.getHeight() * 2);
        g2d.drawString(s3, 10, fm.getHeight() * 3);
        g2d.drawString(s4, 10, fm.getHeight() * 4);
    }
    
    public static void main(String args[]) throws Exception {
        new MapEditToolBack();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent evt) { 
        if(mImage == null) {
            JFileChooser fc = new JFileChooser();
            int n = fc.showOpenDialog(null);

            if (n == JFileChooser.APPROVE_OPTION) {
                try {
                    mImageFile = fc.getSelectedFile();
                    mImage = ImageIO.read(mImageFile);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        if (mPointingMode >= 0) {
            
            switch (evt.getButton()) {
                case MouseEvent.BUTTON1:
                    mCurrentPoint.add(new Point2D(evt.getX(), evt.getY()));
                    break;
                    
                case MouseEvent.BUTTON3:
                    mReversed = !mReversed;
                    break;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Z:
                if (e.isControlDown() && ! mCurrentPoint.isEmpty()) {
                    mCurrentPoint.remove(mCurrentPoint.size() - 1);
                }
                break;
            case KeyEvent.VK_1:
                mPointingMode = LIGHTLESS_POINTING;
                break;
            case KeyEvent.VK_2:
                mPointingMode = NOT_WALKABLE_POINTING;
                break;
            case KeyEvent.VK_ENTER:
                if (mPointingMode == NOT_WALKABLE_POINTING) {
                    mNotWalkable.addAll(getSelection()); 
                }
                else if (mPointingMode == LIGHTLESS_POINTING) {
                    mLightless.addAll(getSelection());
                }
                
                mCurrentPoint.clear();
                mReversed = false;
                break;
        }
    }
}