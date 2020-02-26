/*    */ package dcdmod.Patches;
/*    */ 
/*    */ import basemod.abstracts.CustomCard;
/*    */ import com.badlogic.gdx.graphics.g2d.SpriteBatch;
/*    */ import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
/*    */ import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
/*    */ import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
/*    */ import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
/*    */ import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
/*    */ import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
/*    */ import com.megacrit.cardcrawl.core.Settings;
/*    */ 
/*    */ public abstract class AbstractGIFCard extends CustomCard
/*    */ {
/*    */   public GifAnimation gifAnimation;
/*    */   
/*    */   public AbstractGIFCard(String id, String name, String img, int cost, String rawDescription, AbstractCard.CardType type, AbstractCard.CardColor color, AbstractCard.CardRarity rarity, AbstractCard.CardTarget target)
/*    */   {
/* 19 */     super(id, name, img, cost, rawDescription, type, color, rarity, target);
/* 20 */     if (img.endsWith("gif")) {
/* 21 */       this.gifAnimation = new GifAnimation(img);
/*    */     }
/*    */   }
/*    */   
/*    */   @SpireOverride
/*    */   protected void renderPortrait(SpriteBatch sb) {
/* 27 */     if (this.gifAnimation != null) {
/* 28 */       float drawX = this.current_x - 125.0F;
/* 29 */       float drawY = this.current_y - 23.0F;
/* 30 */       this.gifAnimation.render(sb, drawX, drawY, 125.0F, 23.0F, 250.0F, 190.0F, this.drawScale * Settings.scale, this.drawScale * Settings.scale, this.angle, 0, 0, 250, 190, false, false);
/*    */     } else {
/* 32 */       SpireSuper.call(new Object[] { sb });
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\eg.MOD\mod\DCD.jar!\dcdmod\Patches\AbstractGIFCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */