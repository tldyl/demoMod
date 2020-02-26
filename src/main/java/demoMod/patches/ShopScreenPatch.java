package demoMod.patches;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;
import demoMod.relics.GnawedKey;
import demoMod.sounds.DemoSoundMaster;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ShopScreenPatch {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("DemoMod:AfterStole");
    public static final String[] TEXT = uiStrings.TEXT;
    public static double chance = 1.0;
    private static Random random = new Random();

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
                            if (random.nextDouble() < chance) {
                                DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
                                HuntressCharacter.curse += 1.0;
                                CardCrawlGame.metricData.addShopPurchaseData(((AbstractCard)hoveredCard[0]).getMetricID());
                                AbstractDungeon.topLevelEffects.add(new FastCardObtainEffect((AbstractCard)hoveredCard[0], ((AbstractCard)hoveredCard[0]).current_x, ((AbstractCard)hoveredCard[0]).current_y));
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
            try {
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
                Field speechTimerField = ShopScreen.class.getDeclaredField("speechTimer");
                speechTimerField.setAccessible(true);
                for (AbstractCard c : coloredCards) {
                    if (c.hb.hovered) {
                        hoveredCard[0] = c;
                        somethingHoveredField.set(screen, true);
                        screen.moveHand(c.current_x - AbstractCard.IMG_WIDTH / 2.0F, c.current_y);
                        break;
                    }
                }
                for (AbstractCard c : colorlessCards) {
                    if (c.hb.hovered) {
                        hoveredCard[0] = c;
                        somethingHoveredField.set(screen, true);
                        screen.moveHand(c.current_x - AbstractCard.IMG_WIDTH / 2.0F, c.current_y);
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
                if ((hoveredCard[0] != null) && (((AbstractCard)hoveredCard[0]).hb.clicked || (CInputActionSet.select.isJustPressed()))) {
                    ((AbstractCard)hoveredCard[0]).hb.clicked = false;
                    if (DemoMod.afterSteal) return SpireReturn.Return(null);
                    if (!Settings.isTouchScreen) {
                        Method purchaseCardMethod = ShopScreen.class.getDeclaredMethod("purchaseCard", AbstractCard.class);
                        purchaseCardMethod.setAccessible(true);
                        purchaseCardMethod.invoke(screen, (AbstractCard)hoveredCard[0]);
                    } else if (touchCardField.get(screen) == null) {
                        if (AbstractDungeon.player.gold < ((AbstractCard)hoveredCard[0]).price) {
                            speechTimerField.set(screen, MathUtils.random(40.0F, 60.0F));
                            screen.playCantBuySfx();
                            screen.createSpeech(ShopScreen.getCantBuyMsg());
                        } else {
                            screen.confirmButton.hideInstantly();
                            screen.confirmButton.show();
                            screen.confirmButton.isDisabled = false;
                            screen.confirmButton.hb.clickStarted = false;
                            touchCardField.set(screen, hoveredCard[0]);
                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return SpireReturn.Return(null);
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
                if (random.nextDouble() < chance) {
                    CardCrawlGame.metricData.addShopPurchaseData(storeRelic.relic.relicId);
                    AbstractDungeon.getCurrRoom().relics.add(storeRelic.relic);
                    storeRelic.relic.instantObtain(AbstractDungeon.player, AbstractDungeon.player.relics.size(), true);
                    storeRelic.relic.flash();
                    DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
                    HuntressCharacter.curse += 1.0;
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
                        Field idleMessages = ShopScreen.class.getDeclaredField("idleMessages");
                        idleMessages.setAccessible(true);
                        Field storeRelicField = StoreRelic.class.getDeclaredField("shopScreen");
                        storeRelicField.setAccessible(true);
                        ShopScreen screen = (ShopScreen) storeRelicField.get(storeRelic);
                        ((ArrayList) idleMessages.get(screen)).clear();
                        for (String text : TEXT) {
                            ((ArrayList<String>) idleMessages.get(screen)).add(text);
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
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
                if (random.nextDouble() < chance) {
                    if (AbstractDungeon.player.obtainPotion(storePotion.potion)) {
                        DemoSoundMaster.playV("CURSE_INCREASED", 0.1F);
                        HuntressCharacter.curse += 1.0;
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
                    int ran = AbstractDungeon.miscRng.random(2);
                    StoreRelic key = new StoreRelic(new GnawedKey(), ran, screen);
                    key.price = 115;
                    if (AbstractDungeon.ascensionLevel >= 16) key.price = 104;
                    relics.set(ran, key);
                    field.set(screen, relics);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
