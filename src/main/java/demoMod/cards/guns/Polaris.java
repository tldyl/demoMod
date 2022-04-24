package demoMod.cards.guns;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import demoMod.actions.PlaySoundAction;
import demoMod.cards.interfaces.PostAddedToMasterDeckSubscriber;
import demoMod.combo.Combo;
import demoMod.combo.ComboManager;
import demoMod.dto.GunCardSaveData;
import demoMod.dto.PolarisData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Polaris extends AbstractGunCard implements Combo, PostAddedToMasterDeckSubscriber {
    public static final String ID = DemoMod.makeID("Polaris");
    public static final String NAME;
    public static final String DESCRIPTION;
    public static final String IMG_PATH = "cards/polaris.png";

    public static final Texture comboTexture = new Texture(DemoMod.getResourcePath("combos/cards/polaris.png"));
    private boolean isRemoving = false;
    private static boolean isCombo = false;
    private static int EXTRA_COMBO_DMG = 0;

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
                return 5 + EXTRA_COMBO_DMG;
            case 2:
                return 10 + EXTRA_COMBO_DMG;
            case 3:
                return 15 + EXTRA_COMBO_DMG;
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
        addToBot(new PlaySoundAction("GUN_FIRE_POLARIS"));
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        if (this.level >= 5 && isCombo) {
            addToBot(new PlaySoundAction("GUN_FIRE_POLARIS"));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        }
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
        if (this.level >= 5 && isCombo) {
            this.baseDamage = 12;
            if (this.upgraded) {
                this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
            } else {
                this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
            }
        }
    }

    private void levelDown() {
        this.level -= 2;
        if (this.level < 1) this.level = 1;
        this.baseDamage = getDamageByLevel(this.level);
        if (this.level >= 5 && isCombo) this.baseDamage = 12;
        if (this.level < 5 && isCombo) {
            if (this.upgraded) {
                this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            } else {
                this.rawDescription = cardStrings.DESCRIPTION;
            }
        }
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
        ComboManager.addCombo("DemoExt:SquareBrace", Polaris.class);
    }

    @Override
    public void onAddedToMasterDeck() {
        ComboManager.detectComboInGame();
    }

    @Override
    public void onRemoveFromMasterDeck() {
        isRemoving = true;
        ComboManager.detectCombo();
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public void onComboActivated(String comboId) {
        isCombo = true;
        EXTRA_COMBO_DMG = 2;
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof Polaris) {
                Polaris polaris = (Polaris) card;
                polaris.maxCapacity = 12;
                polaris.capacity = polaris.maxCapacity;
                polaris.portrait = new TextureAtlas.AtlasRegion(new Texture(DemoMod.getResourcePath("cards/polaris_c.png")), 0, 0, 250, 190);
                if (polaris.level >= 5) {
                    if (polaris.upgraded) {
                        polaris.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
                    } else {
                        polaris.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
                    }
                    polaris.initializeDescription();
                }
            }
        }
    }

    @Override
    public void onComboDisabled(String comboId) {
        isCombo = false;
        EXTRA_COMBO_DMG = 0;
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof Polaris) {
                Polaris polaris = (Polaris) card;
                polaris.maxCapacity = 6;
                polaris.capacity = polaris.maxCapacity;
                polaris.portrait = new TextureAtlas.AtlasRegion(new Texture(DemoMod.getResourcePath("cards/polaris.png")), 0, 0, 250, 190);
                if (polaris.level >= 5) {
                    if (polaris.upgraded) {
                        polaris.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
                    } else {
                        polaris.rawDescription = cardStrings.DESCRIPTION;
                    }
                    polaris.initializeDescription();
                }
            }
        }
    }

    @Override
    public boolean isRemoving() {
        return isRemoving;
    }

    @Override
    public Texture getComboPortrait() {
        return comboTexture;
    }
}
