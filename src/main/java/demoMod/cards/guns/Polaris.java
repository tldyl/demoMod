package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.DemoMod;
import demoMod.dto.GunCardSaveData;
import demoMod.dto.PolarisData;
import demoMod.sounds.DemoSoundMaster;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Polaris extends AbstractGunCard {
    public static final String ID = DemoMod.makeID("Polaris");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/polaris.png";

    private static final CardStrings cardStrings;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    private static final int COST = 1;

    private static final List<TextureRegion> LEVEL_IMG;

    private int level = 1;

    public Polaris() {
        super(ID, NAME, DemoMod.getResourcePath(IMG_PATH), COST, DESCRIPTION, DemoMod.characterColor, RARITY, TARGET);
        this.baseDamage = 5;
        this.capacity = 6;
        this.maxCapacity = this.capacity;
        this.reloadSoundKey = "GUN_RELOAD_POLARIS";
    }

    private int getDamageByLevel(int level) {
        int actualLevel = (int)Math.ceil(level / 2.0);
        switch (actualLevel) {
            case 1:
                return 5;
            case 2:
                return 10;
            case 3:
                return 15;
            default:
                return 5;
        }
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        Polaris card = (Polaris) super.makeStatEquivalentCopy();
        card.level = this.level;
        return card;
    }

    @Override
    public void autoReload(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void fire(AbstractPlayer p, AbstractMonster m) {
        DemoSoundMaster.playV("GUN_FIRE_POLARIS", 0.1F);
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        levelUp();
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (this.uuid.equals(card.uuid)) {
                Polaris polaris = (Polaris) card;
                polaris.levelUp();
                break;
            }
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
            this.isSemiAutomatic = true;
        }
    }

    @Override
    public void tookDamage() {
        levelDown();
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (this.uuid.equals(card.uuid)) {
                Polaris polaris = (Polaris) card;
                polaris.levelDown();
                break;
            }
        }
    }

    private void levelUp() {
        if (this.level < 6) this.level++;
        this.baseDamage = getDamageByLevel(this.level);
    }

    private void levelDown() {
        this.level -= 2;
        if (this.level < 1) this.level = 1;
        this.baseDamage = getDamageByLevel(this.level);
    }

    @SpireOverride
    protected void renderPortrait(SpriteBatch sb) {
        SpireSuper.call(sb);
        float drawX = this.current_x - 25.5F;
        float drawY = this.current_y + 75.5F;
        sb.draw(LEVEL_IMG.get((int)Math.floor((this.level - 1) / 2.0)), drawX, drawY, 26.0F, 10.5F, 51.0F, 21.0F, this.drawScale * Settings.scale, this.drawScale * Settings.scale, this.angle);
    }

    @Override
    public GunCardSaveData onSave() {
        PolarisData data = new PolarisData();
        data.capacity = this.capacity;
        data.maxCapacity = this.maxCapacity;
        data.level = this.level;
        return data;
    }

    @Override
    public void onLoad(GunCardSaveData saveData) {
        if (saveData != null) {
            PolarisData polarisData = (PolarisData) saveData;
            this.capacity = polarisData.capacity;
            this.maxCapacity = polarisData.maxCapacity;
            this.level = polarisData.level;
            this.baseDamage = getDamageByLevel(this.level);
        }
    }

    @Override
    public Type savedType() {
        return new TypeToken<PolarisData>(){}.getType();
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
        NAME = cardStrings.NAME;
        DESCRIPTION = cardStrings.DESCRIPTION;
        LEVEL_IMG = new ArrayList<>();
        LEVEL_IMG.add(new TextureRegion(new Texture(DemoMod.getResourcePath("effects/polaris_1.png"))));
        LEVEL_IMG.add(new TextureRegion(new Texture(DemoMod.getResourcePath("effects/polaris_2.png"))));
        LEVEL_IMG.add(new TextureRegion(new Texture(DemoMod.getResourcePath("effects/polaris_3.png"))));
    }
}
