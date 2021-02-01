package demoMod.patches;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.shop.Merchant;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import demoMod.DemoMod;
import demoMod.relics.interfaces.PostTheftSubscriber;
import demoMod.characters.HuntressCharacter;
import demoMod.interfaces.ConstantPrice;
import demoMod.relics.GnawedKey;
import demoMod.sounds.DemoSoundMaster;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ShopScreenPatch {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("DemoMod:AfterStole");
    public static final String[] TEXT = uiStrings.TEXT;
    public static double chance = 1.0;

    @SuppressWarnings("unchecked")
    @SpirePatch(
            clz = ShopScreen.class,
            method = "init"
    )
    public static class PatchInit {
        @SpireInsertPatch(rloc = 5)
        public static SpireReturn Insert(ShopScreen screen) {
            if (DemoMod.afterSteal) {
                try {
                    Field idleMessages = ShopScreen.class.getDeclaredField("idleMessages");
                    idleMessages.setAccessible(true);
                    ((ArrayList)idleMessages.get(screen)).clear();
                    screen.purgeAvailable = false;
                    for (String text : TEXT) {
                        ((ArrayList<String>)idleMessages.get(screen)).add(text);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SuppressWarnings({"Duplicates", "unchecked"})
    @SpirePatch(
            clz = ShopScreen.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 48, localvars = {"hoveredCard"})
        public static SpireReturn<Object> Insert(ShopScreen screen, @ByRef(type="cards.AbstractCard") Object[] hoveredCard) {
            //先判断玩家是否使用了偷
            try {
                if (DemoMod.canSteal) {
                    screen.purgeAvailable = false; //一旦使用了偷就无法删卡
                    Field coloredCardsField = ShopScreen.class.getDeclaredField("coloredCards");
                    coloredCardsField.setAccessible(true);
                    ArrayList<AbstractCard> coloredCards = (ArrayList<AbstractCard>) coloredCardsField.get(screen);
                    Field colorlessCardsField = ShopScreen.class.getDeclaredField("colorlessCards");
                    colorlessCardsField.setAccessible(true);
                    ArrayList<AbstractCard> colorlessCards = (ArrayList<AbstractCard>) colorlessCardsField.get(screen);
                    Field somethingHoveredField = ShopScreen.class.getDeclaredField("somethingHovered");
                    somethingHoveredField.setAccessible(true);
                    Field notHoveredTimerField = ShopScreen.class.getDeclaredField("notHoveredTimer");
                    notHoveredTimerField.setAccessible(true);
                    Field handTargetYField = ShopScreen.class.getDeclaredField("handTargetY");
                    handTargetYField.setAccessible(true);
                    Field touchCardField = ShopScreen.class.getDeclaredField("touchCard");
                    touchCardField.setAccessible(true);
                    for (AbstractCard c : coloredCards) {
                        if (c.hb.hovered) {
                            hoveredCard[0] = c;
                            somethingHoveredField.set(screen, true);
                            break;
                        }
                    }
                    for (AbstractCard c : colorlessCards) {
                        if (c.hb.hovered) {
                            hoveredCard[0] = c;
                            somethingHoveredField.set(screen, true);
                            break;
                        }
                    }
                    if (!(Boolean) somethingHoveredField.get(screen)) {
                        notHoveredTimerField.set(screen, (Float)notHoveredTimerField.get(screen) + Gdx.graphics.getDeltaTime());
                        if ((Float)notHoveredTimerField.get(screen) > 1.0F) {
                            handTargetYField.set(screen, Settings.HEIGHT);
                        }
                    } else {
                        notHoveredTimerField.set(screen, 0.0F);
                    }
                    if ((hoveredCard[0] != null) && (InputHelper.justClickedLeft)) {
                        ((AbstractCard)hoveredCard[0]).hb.clickStarted = true;
                    }
                    if ((hoveredCard[0] != null) && ((InputHelper.justClickedRight) || (CInputActionSet.proceed.isJustPressed()))) {
                        InputHelper.justClickedRight = false;
                        CardCrawlGame.cardPopup.open((AbstractCard) hoveredCard[0]);
                    }
                    if (DemoMod.afterSteal) {
                        return SpireReturn.Return(null);
                    }
                    if ((hoveredCard[0] != null) && (((AbstractCard)hoveredCard[0]).hb.clicked || (CInputActionSet.select.isJustPressed()))) {
                        ((AbstractCard)hoveredCard[0]).hb.clicked = false;
                        if (!Settings.isTouchScreen) {
                            if (AbstractDungeon.merchantRng.random(1.0F) < chance) {
                                DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
                                HuntressCharacter.curse += 1.0;
                                CardCrawlGame.metricData.addShopPurchaseData(((AbstractCard)hoveredCard[0]).getMetricID());
                                AbstractDungeon.topLevelEffects.add(new FastCardObtainEffect((AbstractCard)hoveredCard[0], ((AbstractCard)hoveredCard[0]).current_x, ((AbstractCard)hoveredCard[0]).current_y));
                                DemoMod.isStolen = true;
                                ((ArrayList)coloredCardsField.get(screen)).remove(hoveredCard[0]);
                                ((ArrayList)colorlessCardsField.get(screen)).remove(hoveredCard[0]);
                                chance /= 2;
                            } else {
                                screen.createSpeech(TEXT[0]);
                                screen.playCantBuySfx();
                                DemoMod.canSteal = false;
                                DemoMod.afterSteal = true;
                                Field idleMessages = ShopScreen.class.getDeclaredField("idleMessages");
                                idleMessages.setAccessible(true);
                                ((ArrayList)idleMessages.get(screen)).clear();
                                for (String text : TEXT) {
                                    ((ArrayList<String>)idleMessages.get(screen)).add(text);
                                }
                            }

                        } else if (touchCardField.get(screen) == null) {
                            screen.confirmButton.hideInstantly();
                            screen.confirmButton.show();
                            screen.confirmButton.isDisabled = false;
                            screen.confirmButton.hb.clickStarted = false;
                            touchCardField.set(screen, hoveredCard[0]);
                       }
                    }
                    return SpireReturn.Return(null);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            //如果玩家没有使用偷就走正常流程
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "updateHand"
    )
    public static class PatchUpdateHand {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn Insert(ShopScreen screen) {
            if (DemoMod.canSteal || DemoMod.afterSteal) return SpireReturn.Return(null);
            return SpireReturn.Continue();
        }
    }

    @SuppressWarnings("unchecked")
    @SpirePatch(
            clz = StoreRelic.class,
            method = "purchaseRelic"
    )
    public static class PatchPurchaseRelic {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn Insert(StoreRelic storeRelic) {
            if (DemoMod.afterSteal) {
                return SpireReturn.Return(null);
            }
            if (DemoMod.canSteal) {
                if (AbstractDungeon.merchantRng.random(1.0F) < chance) {
                    CardCrawlGame.metricData.addShopPurchaseData(storeRelic.relic.relicId);
                    AbstractDungeon.getCurrRoom().relics.add(storeRelic.relic);
                    storeRelic.relic.instantObtain(AbstractDungeon.player, AbstractDungeon.player.relics.size(), true);
                    storeRelic.relic.flash();
                    DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
                    HuntressCharacter.curse += 1.0;
                    DemoMod.isStolen = true;
                    try {
                        Field shopScreenField = StoreRelic.class.getDeclaredField("shopScreen");
                        shopScreenField.setAccessible(true);
                        if (storeRelic.relic.relicId.equals("Membership Card")) {
                            ((ShopScreen)shopScreenField.get(storeRelic)).applyDiscount(0.5F, true);
                        }
                        if (storeRelic.relic.relicId.equals("Smiling Mask")) {
                            ShopScreen.actualPurgeCost = 50;
                        }
                        if (storeRelic.relic.relicId.equals("Toxic Egg 2")) {
                            ((ShopScreen)shopScreenField.get(storeRelic)).applyUpgrades(AbstractCard.CardType.SKILL);
                        }
                        if (storeRelic.relic.relicId.equals("Molten Egg 2")) {
                            ((ShopScreen)shopScreenField.get(storeRelic)).applyUpgrades(AbstractCard.CardType.ATTACK);
                        }
                        if (storeRelic.relic.relicId.equals("Frozen Egg 2")) {
                            ((ShopScreen)shopScreenField.get(storeRelic)).applyUpgrades(AbstractCard.CardType.POWER);
                        }
                        storeRelic.isPurchased = true;
                        if (storeRelic.relic instanceof PostTheftSubscriber) {
                            ((PostTheftSubscriber) storeRelic.relic).onTheft();
                        }
                        Field idleMessages = ShopScreen.class.getDeclaredField("idleMessages");
                        idleMessages.setAccessible(true);
                        Field storeRelicField = StoreRelic.class.getDeclaredField("shopScreen");
                        storeRelicField.setAccessible(true);
                        ShopScreen screen = (ShopScreen) storeRelicField.get(storeRelic);
                        ((ArrayList) idleMessages.get(screen)).clear();
                        for (String text : TEXT) {
                            ((ArrayList<String>) idleMessages.get(screen)).add(text);
                        }
                        if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
                            ShopRoom room = (ShopRoom) AbstractDungeon.getCurrRoom();
                            idleMessages = Merchant.class.getDeclaredField("idleMessages");
                            idleMessages.setAccessible(true);
                            ((ArrayList) idleMessages.get(room.merchant)).clear();
                            ((ArrayList) idleMessages.get(room.merchant)).addAll(Arrays.asList(TEXT));
                            if (Loader.isModLoaded("GungeonModExtend")) {
                                Class cls = Class.forName("demoMod.patches.demoExt.ShopRoomPatch$ExtraMerchantPatch");
                                SpireField<Merchant> extraMerchantField = (SpireField<Merchant>) cls.getDeclaredField("extraMerchant").get(null);
                                Merchant extraMerchant = extraMerchantField.get(room);
                                if (extraMerchant != null) {
                                    idleMessages = extraMerchant.getClass().getDeclaredField("idleMessages");
                                    idleMessages.setAccessible(true);
                                    ((ArrayList) idleMessages.get(extraMerchant)).clear();
                                    ((ArrayList) idleMessages.get(extraMerchant)).addAll(Arrays.asList(TEXT));
                                }
                            }
                        }
                    } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    chance /= 2;
                } else {
                    DemoMod.canSteal = false;
                    DemoMod.afterSteal = true;
                    try {
                        Field shopScreenField = StoreRelic.class.getDeclaredField("shopScreen");
                        shopScreenField.setAccessible(true);
                        ((ShopScreen)shopScreenField.get(storeRelic)).createSpeech(TEXT[0]);
                        ((ShopScreen)shopScreenField.get(storeRelic)).playCantBuySfx();
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SuppressWarnings("unchecked")
    @SpirePatch(
            clz = StorePotion.class,
            method = "purchasePotion"
    )
    public static class PatchPurchasePotion {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn Insert(StorePotion storePotion) {
            if (DemoMod.afterSteal) {
                return SpireReturn.Return(null);
            }
            if (DemoMod.canSteal) {
                if (AbstractDungeon.player.hasRelic("Sozu")) {
                    AbstractDungeon.player.getRelic("Sozu").flash();
                    return SpireReturn.Return(null);
                }
                if (AbstractDungeon.merchantRng.random(1.0F) < chance) {
                    if (AbstractDungeon.player.obtainPotion(storePotion.potion)) {
                        DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
                        HuntressCharacter.curse += 1.0;
                        DemoMod.isStolen = true;
                        CardCrawlGame.metricData.addShopPurchaseData(storePotion.potion.ID);
                        chance /= 2;
                    } else {
                        AbstractDungeon.topPanel.flashRed();
                    }
                } else {
                    int i = 0;
                    for (AbstractPotion p : AbstractDungeon.player.potions) {
                        if (p instanceof PotionSlot) break;
                        i++;
                    }
                    if (i >= AbstractDungeon.player.potionSlots) {
                        AbstractDungeon.topPanel.flashRed();
                        return SpireReturn.Return(null);
                    }
                    DemoMod.canSteal = false;
                    DemoMod.afterSteal = true;
                    try {
                        Field shopScreenField = StorePotion.class.getDeclaredField("shopScreen");
                        shopScreenField.setAccessible(true);
                        ((ShopScreen) shopScreenField.get(storePotion)).createSpeech(TEXT[0]);
                        ((ShopScreen) shopScreenField.get(storePotion)).playCantBuySfx();

                        Field idleMessages = ShopScreen.class.getDeclaredField("idleMessages");
                        idleMessages.setAccessible(true);
                        Field storePotionField = StorePotion.class.getDeclaredField("shopScreen");
                        storePotionField.setAccessible(true);
                        ShopScreen screen = (ShopScreen) storePotionField.get(storePotion);
                        ((ArrayList) idleMessages.get(screen)).clear();
                        for (String text : TEXT) {
                            ((ArrayList<String>) idleMessages.get(screen)).add(text);
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "welcomeSfx"
    )
    public static class PatchWelcomeSfx {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn Insert(ShopScreen screen) {
            if (DemoMod.afterSteal) {
                screen.playCantBuySfx();
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "playMiscSfx"
    )
    public static class PatchPlayMiscSfx {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn Insert(ShopScreen screen) {
            if (DemoMod.afterSteal) {
                screen.playCantBuySfx();
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SuppressWarnings("unchecked")
    @SpirePatch(
            clz = ShopScreen.class,
            method = "initRelics"
    )
    public static class PatchInitRelics {
        public static void Postfix(ShopScreen screen) {
            if (!AbstractDungeon.player.hasRelic(DemoMod.makeID("GnawedKey"))
                    && AbstractDungeon.actNum == 1) {
                try {
                    Field field = ShopScreen.class.getDeclaredField("relics");
                    field.setAccessible(true);
                    ArrayList<StoreRelic> relics = (ArrayList<StoreRelic>) field.get(screen);
                    StoreRelic key = new StoreRelic(new GnawedKey(), 3, screen) {
                        @Override
                        public void update(float rugY) {
                            if (this.relic != null) {
                                if (!this.isPurchased) {
                                    this.relic.currentX = 1000.0F * Settings.scale + 150.0F * -0.7F * Settings.scale;
                                    this.relic.currentY = rugY + 295.0F * Settings.scale;
                                    this.relic.hb.move(this.relic.currentX, this.relic.currentY);
                                    this.relic.hb.update();
                                    if (this.relic.hb.hovered) {
                                        screen.moveHand(this.relic.currentX - 190.0F * Settings.scale, this.relic.currentY - 70.0F * Settings.scale);
                                        if (InputHelper.justClickedLeft) {
                                            this.relic.hb.clickStarted = true;
                                        }

                                        this.relic.scale = Settings.scale * 1.25F;
                                    } else {
                                        this.relic.scale = MathHelper.scaleLerpSnap(this.relic.scale, Settings.scale);
                                    }

                                    if (this.relic.hb.hovered && InputHelper.justClickedRight) {
                                        CardCrawlGame.relicPopup.open(this.relic);
                                    }
                                }

                                if (this.relic.hb.clicked || this.relic.hb.hovered && CInputActionSet.select.isJustPressed()) {
                                    this.relic.hb.clicked = false;
                                    if (!Settings.isTouchScreen) {
                                        this.purchaseRelic();
                                    } else if (AbstractDungeon.shopScreen.touchRelic == null) {
                                        if (AbstractDungeon.player.gold < this.price) {
                                            screen.playCantBuySfx();
                                            screen.createSpeech(ShopScreen.getCantBuyMsg());
                                        } else {
                                            AbstractDungeon.shopScreen.confirmButton.hideInstantly();
                                            AbstractDungeon.shopScreen.confirmButton.show();
                                            AbstractDungeon.shopScreen.confirmButton.isDisabled = false;
                                            AbstractDungeon.shopScreen.confirmButton.hb.clickStarted = false;
                                            AbstractDungeon.shopScreen.touchRelic = this;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    relics.add(key);
                    field.set(screen, relics);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        @SpireInsertPatch(rloc = 19, localvars={"relic"})
        public static void Insert(ShopScreen screen, @ByRef(type="shop.StoreRelic") Object[] _relic) {
            StoreRelic relic = (StoreRelic) _relic[0];
            if (relic.relic instanceof ConstantPrice) {
                relic.price = relic.relic.getPrice();
            }
        }
    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "applyDiscount"
    )
    public static class PatchApplyDiscount {
        public static void Postfix(ShopScreen screen, float multiplier, boolean affectPurge) {
            List<StoreRelic> relics = new ArrayList<>();
            try {
                Field field = ShopScreen.class.getDeclaredField("relics");
                field.setAccessible(true);
                relics = (ArrayList<StoreRelic>) field.get(screen);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            for (StoreRelic relic : relics) {
                if (relic.relic instanceof ConstantPrice) {
                    relic.price = relic.relic.getPrice();
                }
            }
        }
    }
}
