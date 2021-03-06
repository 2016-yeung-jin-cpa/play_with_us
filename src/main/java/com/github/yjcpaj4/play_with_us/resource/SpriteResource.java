package com.github.yjcpaj4.play_with_us.resource;

import com.github.yjcpaj4.play_with_us.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * 리팩토링이 필요합니다.
 * 
 * @author 차명도.
 */
public class SpriteResource implements Serializable {
    
    @SerializedName("x")
    protected int mX;
    
    @SerializedName("y")
    protected int mY;
    
    @SerializedName("w")
    protected int mWidth;
    
    @SerializedName("h")
    protected int mHeight;
    
    @SerializedName("fps")
    protected int mFPS;
    
    @SerializedName("img")
    protected String mImage;
    
    @SerializedName("frames")
    protected List<Frame> mFrames;
    
    private SpriteResource() {
    }
    
    public static SpriteResource loadFromJSON(File f)  {
        SpriteResource r;
        BufferedImage b;
        
        try {
            r = new Gson().fromJson(FileUtil.getContents(f), SpriteResource.class);
            b = ImageIO.read(new File(f.getParentFile(), r.mImage));
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

        for (Frame o : r.mFrames) {
            o.mImage = b.getSubimage(o.mX, o.mY, o.mWidth, o.mHeight);
        }
        
        return r;
    }
    
    public int getWidth() {
        return mWidth;
    }
    
    public int getHeight() {
        return mHeight;
    }
    
    public int getFPS() {
        return mFPS;
    }
       
    public Frame getFrame(int n) {
        return mFrames.get(n);
    }
    
    public List<Frame> getFrames() {
        return mFrames;
    }
    
    public int getLength() {
        return mFrames.size();
    }

    public static class Frame implements Serializable {

        @SerializedName("x")
        private int mX;
        
        @SerializedName("y")
        private int mY;
        
        @SerializedName("w")
        private int mWidth;
        
        @SerializedName("h")
        private int mHeight;
        
        private transient BufferedImage mImage;

        public BufferedImage getImage() {
            return mImage;
        }

        public int getWidth() {
            return mWidth;
        }

        public int getHeight() {
            return mHeight;
        }
    }
}
