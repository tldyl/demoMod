package demoMod.combo;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DemoMod;
import demoMod.cards.guns.AbstractGunCard;
import demoMod.effects.ComboEffect;
import demoMod.sounds.DemoSoundMaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class ComboManager {
    private static Map<String, List<Combo>> combos = new HashMap<>();
    private static Map<String, Boolean> hasComboActivated = new HashMap<>();
    private static List<String> newComboList = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(ComboManager.class);

    public static void addCombo(String comboId, Class<? extends Combo> comboObject) {
        try {
            if (!combos.containsKey(comboId)) {
                logger.info(new String("添加组合:".getBytes(), StandardCharsets.UTF_8) + "{}", comboId);
                combos.put(comboId, new ArrayList<>());
                hasComboActivated.put(comboId, false);
                combos.get(comboId).add(comboObject.newInstance());
            } else {
                combos.get(comboId).add(comboObject.newInstance());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getAllComboId() {
        Set<String> ret = new HashSet<>();
        for (String id : combos.keySet()) {
            if (combos.get(id).size() > 1) {
                ret.add(id);
            }
        }
        return ret;
    }

    public static List<String> getAllComboId(Combo item) {
        List<String> ret = new ArrayList<>();
        for (String comboId : combos.keySet()) {
            if (combos.get(comboId).size() > 1) {
                for (Combo combo : combos.get(comboId)) {
                    String actualId = comboId.split(":")[0] + ":" + comboId.split(":")[1];
                    if (combo.getItemId().equals(item.getItemId()) && !ret.contains(actualId)) {
                        ret.add(actualId);
                    }
                }
            }
        }
        return ret;
    }

    public static List<String> getAllComboId(String comboId) {
        List<String> ret = new ArrayList<>();
        for (String s : combos.keySet()) {
            if (s.contains(comboId)) ret.add(s);
        }
        return ret;
    }

    public static synchronized void detectCombo() {
        newComboList.clear();
        List<String> combosToActivate = new ArrayList<>();
        List<String> combosToDisable = new ArrayList<>();
        for (String comboId : combos.keySet()) { //遍历所有组合
            int ctr = 0;
            for (Combo combo : combos.get(comboId)) { //遍历一个组合内的所有组件
                boolean hasItem = false;
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    if (c instanceof Combo) {
                        if (combo.getItemId().equals(((Combo)c).getItemId()) && !((Combo)c).isRemoving()) { //说明玩家有这个组合的一个组件
                            ctr++;
                            hasItem = true;
                            break;
                        }
                    }
                }
                if (!hasItem) {
                    for (AbstractRelic r : AbstractDungeon.player.relics) {
                        if (r instanceof Combo) {
                            if (combo.getItemId().equals(((Combo)r).getItemId()) && !((Combo)r).isRemoving()) { //说明玩家有这个组合的一个组件
                                ctr++;
                                hasItem = true;
                                break;
                            }
                        }
                    }
                }
                if (!hasItem) {
                    for (AbstractPotion p : AbstractDungeon.player.potions) {
                        if (p instanceof Combo) {
                            if (combo.getItemId().equals(((Combo)p).getItemId()) && !((Combo)p).isRemoving()) { //说明玩家有这个组合的一个组件
                                ctr++;
                                break;
                            }
                        }
                    }
                }
            }
            if (ctr == combos.get(comboId).size() && ctr != 1) { //激活组合效果
                combosToActivate.add(comboId);
                if (!hasComboActivated.get(comboId)) {
                    newComboList.add(comboId);
                }
                hasComboActivated.put(comboId, true);
            } else {
                combosToDisable.add(comboId);
                hasComboActivated.put(comboId, false);
            }
        }
        for (String comboId : combosToDisable) {
            for (Combo combo : combos.get(comboId)) {
                combo.onComboDisabled(comboId.split(":")[0] + ":" + comboId.split(":")[1]);
            }
        }
        for (String comboId : combosToActivate) {
            for (Combo combo : combos.get(comboId)) {
                combo.onComboActivated(comboId.split(":")[0] + ":" + comboId.split(":")[1]);
            }
        }
        if (AbstractDungeon.player.hasRelic(DemoMod.makeID("LichsEyeBullet"))) {
            List<AbstractCard> guns = new ArrayList<>();
            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if (card instanceof AbstractGunCard && card instanceof Combo) {
                    guns.add(card);
                }
            }
            for (AbstractCard gun : guns) {
                Combo gunCard = (Combo) gun;
                for (String comboId : getAllComboId(gunCard)) {
                    gunCard.onComboActivated(comboId);
                }
            }
        }

    }

    public static synchronized void detectComboInGame() {
        detectCombo();
        if (newComboList.size() > 0) {
            String[] comboId = newComboList.get(0).split(":");
            UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(comboId[0] + ":" + comboId[1]);
            DemoSoundMaster.playA("COMBO_ACTIVATED", 0.0F);
            AbstractGameEffect comboEffect = new ComboEffect(
                    combos.get(newComboList.get(0)).get(0).getComboPortrait(),
                    combos.get(newComboList.get(0)).get(1).getComboPortrait(),
                    uiStrings.TEXT[0]
            );
            DemoMod.effectsQueue.add(comboEffect);
            for (int i=1;i<newComboList.size();i++) {
                comboId = newComboList.get(i).split(":");
                uiStrings = CardCrawlGame.languagePack.getUIString(comboId[0] + ":" + comboId[1]);
                ((ComboEffect) comboEffect).nextEffect = new ComboEffect(
                        combos.get(newComboList.get(i)).get(0).getComboPortrait(),
                        combos.get(newComboList.get(i)).get(1).getComboPortrait(),
                        uiStrings.TEXT[0]
                );
                comboEffect = ((ComboEffect) comboEffect).nextEffect;
            }
        }
    }

    public static String getComboName(String comboId) {
        String[] comboIds = comboId.split(":");
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(comboIds[0] + ":" + comboIds[1]);
        return uiStrings.TEXT[0];
    }

    public static List<Combo> getAllCombo(String comboId) {
        return combos.get(comboId);
    }

    public static boolean hasComboActivated(String comboId) {
        String comboName = comboId.split(":")[1];
        for (Map.Entry<String, Boolean> e : hasComboActivated.entrySet()) {
            if (e.getKey().split(":")[1].equals(comboName)) {
                return hasComboActivated.get(e.getKey());
            }
        }
        return false;
    }
}
