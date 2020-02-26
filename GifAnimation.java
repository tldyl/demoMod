/*    */ package dcdmod.Patches;
/*    */ 
/*    */ import com.badlogic.gdx.Files;
/*    */ import com.badlogic.gdx.Gdx;
/*    */ import com.badlogic.gdx.Graphics;
/*    */ import com.badlogic.gdx.files.FileHandle;
/*    */ import com.badlogic.gdx.graphics.g2d.Animation;
/*    */ import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
/*    */ import com.badlogic.gdx.graphics.g2d.SpriteBatch;
/*    */ import com.badlogic.gdx.graphics.g2d.TextureRegion;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ 
/*    */ public class GifAnimation
/*    */ {
/*    */   Animation<TextureRegion> animation;
/*    */   float elapsed;
/*    */   public GifDecoder gdec;
/* 19 */   public static HashMap<String, Animation<TextureRegion>> cache = new HashMap();
/*    */   
/*    */   public GifAnimation(String filePath) {
/* 22 */     create(filePath);
/*    */   }
/*    */   
/*    */   public void create(String filePath) {
/* 26 */     if (cache.containsKey(filePath)) {
/* 27 */       this.animation = ((Animation)cache.get(filePath));
/*    */     } else {
/* 29 */       this.animation = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal(filePath).read(), this);
/* 30 */       cache.put(filePath, this.animation);
/*    */     }
/*    */   }
/*    */   
/*    */   public void render(SpriteBatch sb, float x, float y, float width, float height) {
/* 35 */     this.elapsed += Gdx.graphics.getDeltaTime();
/*    */     
/*    */ 
/* 38 */     sb.draw((TextureRegion)this.animation.getKeyFrame(this.elapsed), x, y, width, height);
/*    */   }
/*    */   
/*    */   public void render(SpriteBatch sb, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
/* 42 */     this.elapsed += Gdx.graphics.getDeltaTime();
/* 43 */     sb.draw((TextureRegion)this.animation.getKeyFrame(this.elapsed), x, y, originX, originY, width, height, scaleX, scaleY, rotation);
/*    */   }
/*    */   
/*    */   public void dispose(SpriteBatch sb) {
/* 47 */     sb.dispose();
/*    */   }
/*    */   
/*    */   public int getWidth() {
/* 51 */     return this.gdec.getWidth();
/*    */   }
/*    */   
/*    */   public int getHeight() {
/* 55 */     return this.gdec.getHeight();
/*    */   }
/*    */ }


/* Location:              D:\eg.MOD\mod\DCD.jar!\dcdmod\Patches\GifAnimation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */