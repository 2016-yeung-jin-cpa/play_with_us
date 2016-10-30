package com.github.yjcpaj4.play_with_us.resource;

import com.google.gson.Gson;
import com.github.yjcpaj4.play_with_us.resource.ImageResource;
import com.github.yjcpaj4.play_with_us.resource.SpriteImageResource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.github.yjcpaj4.play_with_us.util.FileUtil;

/**
 * 리소스 객체.
 * 
 * @author 차명도.
 * @param <T> 
 */
public class ResourceManager {
    
    private static ResourceManager INSTANCE;
    
    public static ResourceManager getInstance() {
        if (INSTANCE == null) {
            synchronized(ResourceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ResourceManager();
                }
            }  
        }
        
        return INSTANCE;
    }
    
    private Map<String, Resource> mResource = new HashMap<>(); 
    
    private ResourceManager() {
    }
    
    public void load(File f) throws IOException {
        load(f, FileUtil.getNameWithoutExtension(f));
    }
    
    public void load(String f, String s) throws IOException {
        load(new File(f), s);
    }
    
    public void load(File f, String k) throws IOException {
        Resource r = null;
        
        if (SpriteImageResource.canLoad(f, k)) {
            r = new SpriteImageResource();
        }
        else if (ImageResource.canLoad(f)) {
            r = new ImageResource();
        }
        else if (SoundResource.canLoad(f)) {
            r = new SoundResource();
        }
        
        r.load(f, k);

        mResource.put(k, r);
    }
    
    public ImageResource getImage(String k) {
        return (ImageResource) mResource.get(k);
    }
    
    public SpriteImageResource getSprite(String k) {
        return (SpriteImageResource) mResource.get(k);
    }
    
    public SoundResource getSound(String k) {
        return (SoundResource) mResource.get(k);
    }
    
    public void release(String k) {
        mResource.get(k).release();
    }
    
    public interface Resource {
        
        void load(File f, String s) throws IOException;

        void release();
    }
    
    /*
    public static void main(String args[]) {
        Map<String, String> m = new HashMap<>();
        
        for (File f : RESOURCE_DIR.listFiles()) {
            if (f != null) {
                m.put(f.getName(), FileUtil.getChecksum(f));
            }
        }
        
        System.out.println(new Gson().toJson(m));
    }
    */
}
