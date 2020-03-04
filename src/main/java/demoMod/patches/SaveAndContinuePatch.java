package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import demoMod.DemoMod;
import demoMod.cards.Spice;
import demoMod.characters.HuntressCharacter;
import demoMod.monsters.ResourcefulRat;
import demoMod.relics.VorpalBullet;

import java.io.*;
import java.util.Scanner;

@SuppressWarnings("unused")
public class SaveAndContinuePatch {
    public SaveAndContinuePatch() {

    }

    @SuppressWarnings("Duplicates")
    @SpirePatch(
            clz = SaveAndContinue.class,
            method = "deleteSave"
    )
    public static class DeleteSavePatch {
        /**
         * 当存档删除时触发
         */
        @SpireInsertPatch(rloc = 0)
        @SuppressWarnings("ResultOfMethodCallIgnored")
        public static void Insert(AbstractPlayer p) {
            Spice.dropChance = 0;
            Spice.isFirstUse = true;
            VorpalBullet.chance = 0.03;
            HuntressCharacter.curse = 0;
            ShopScreenPatch.chance = 1.0;
            DemoMod.canSteal = false;
            DemoMod.afterSteal = false;
            ResourcefulRat.phaseTwo = false;
            ResourcefulRat.isBeaten = false;
            ResourcefulRat.isTrueBeaten = false;
            MonsterRoomPatch.PatchRender.isEntryOpen = false;
            TreasureRoomPatch.closeEntry();
            File file = new File("saves/HUNTRESS.curseValue");
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SpirePatch(
            clz = SaveAndContinue.class,
            method = "save"
    )
    public static class SavePatch {
        /**
         * 当存档时触发
         */
        @SpireInsertPatch(rloc = 2, localvars = {"params"})
        public static void Insert(@ByRef(type = "java.util.HashMap")Object[] params) {
            MonsterRoomPatch.PatchRender.isEntryOpen = false;
            TreasureRoomPatch.closeEntry();
            if (AbstractDungeon.player instanceof HuntressCharacter) {
                File file = new File("saves/HUNTRESS.curseValue");
                if (file.exists()) {
                    try {
                        OutputStream os = new FileOutputStream(file);
                        os.write(String.format("%.1f\n", HuntressCharacter.curse).getBytes());
                        if (DemoMod.canSteal) {
                            os.write("true\n".getBytes());
                        } else {
                            os.write("false\n".getBytes());
                        }
                        if (DemoMod.afterSteal) {
                            os.write("true\n".getBytes());
                        } else {
                            os.write("false\n".getBytes());
                        }
                        os.write(String.format("%.1f\n", ShopScreenPatch.chance).getBytes());
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        file.createNewFile();
                        OutputStream os = new FileOutputStream(file);
                        os.write(String.format("%.1f\n", HuntressCharacter.curse).getBytes());
                        if (DemoMod.canSteal) {
                            os.write("true\n".getBytes());
                        } else {
                            os.write("false\n".getBytes());
                        }
                        if (DemoMod.afterSteal) {
                            os.write("true\n".getBytes());
                        } else {
                            os.write("false\n".getBytes());
                        }
                        os.write(String.format("%.1f\n", ShopScreenPatch.chance).getBytes());
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SpirePatch(
            clz = SaveAndContinue.class,
            method = "loadSaveFile",
            paramtypez = {
                    String.class
            }
    )
    public static class LoadSaveFilePatch {
        public LoadSaveFilePatch() { }

        @SpireInsertPatch(rloc = 0)
        public static void Insert(String filePath) {
            File file = new File("saves/HUNTRESS.curseValue");
            if (file.exists()) {
                try {
                    InputStream is = new FileInputStream(file);
                    Scanner scan = new Scanner(is);
                    HuntressCharacter.curse = scan.nextDouble();
                    DemoMod.canSteal = scan.nextBoolean();
                    DemoMod.afterSteal = scan.nextBoolean();
                    ShopScreenPatch.chance = scan.nextDouble();
                    scan.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
