package com.github.yjcpaj4.play_with_us.game.special_object;

import com.github.yjcpaj4.play_with_us.game.GameObject;
import com.github.yjcpaj4.play_with_us.Application;
import com.github.yjcpaj4.play_with_us.geom.Circle;
import com.github.yjcpaj4.play_with_us.geom.CollisionDetection;
import com.github.yjcpaj4.play_with_us.layer.GameLayer;
import com.github.yjcpaj4.play_with_us.layer.InterativeLayer;
import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Graphics2D;

public class KitchenRefrigerator extends GameObject {

    private static final int X = 40;
    private static final int Y = 90;
    private static final int RADIUS = 50;
    
    private static final String YES = "살펴본다.";
    private static final String NO = "그만둔다.";
    
    private Circle mCollider = new Circle(X, Y, RADIUS);
    
    public KitchenRefrigerator() {
    }
    
    @Override
    public void update(GameLayer g, long delta) {
        if (getMap().equals(g.getPlayer().getMap()) 
         && CollisionDetection.isCollide(mCollider, g.getPlayer().getCollider())
         && g.getInput().isKeyOnce(KeyEvent.VK_F)) {
            
            InterativeLayer l = new InterativeLayer(Application.getInstance()) {
                
                @Override
                protected void pause() {
                    super.pause();
                    
                    if (getCurrentAnswer().equals(YES) && !g.getPlayer().hasItem("map.bathroom")) {
                        g.showMessage("열쇠를 획득하였습니다.", 1000);
                        g.getPlayer().addItem("map.bathroom");
                        g.getResource().getSound("snd.player.item").play();
                    }
                }
            };
            l.setQuestion("살펴 보시겠습니까?");
            l.setAnswers(new String[] { YES, NO });
            l.setBackground(g.getResource().getImage("img.bg.kitchen.refrigerator"));
            g.showLayer(l);
        }
    }

    @Override
    public void draw(GameLayer g, long delta, Graphics2D g2d) {
        if (Application.DEBUG) {
            g2d.setColor(Color.RED);
            g2d.drawPolygon(mCollider.toAWTPolygon());
        }
    }
    
}
