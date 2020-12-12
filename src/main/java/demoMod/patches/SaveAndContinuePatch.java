package demoMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import demoMod.DemoMod;
import demoMod.cards.Spice;
import demoMod.characters.HuntressCharacter;
import demoMod.monsters.ResourcefulRat;
import demoMod.relics.VorpalBullet;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Scanner;

import static demoMod.patches.SaveAndContinuePatch.SaveFileClassPatch.misc_seed_count;

@SuppressWarnings("unused")
public class SaveAndContinuePatch {
    @SpirePatch(
            clz = SaveFile.class,
            method = SpirePatch.CLASS
    )
    public static class SaveFileClassPatch {
        public static SpireField<Integer> misc_seed_count = new SpireField<Integer>(() -> 0) {
            @Override
            public void initialize(Class clz, String fieldName) throws NoSuchFieldException {
                super.initialize(clz, fieldName);
            }
        };

    }

    @SpirePatch(
            clz = SaveFile.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    SaveFile.SaveType.class
            }
    )
    public static class SaveFileConstructorPatch {
        public static void Prefix(SaveFile file, SaveFile.SaveType type) {
            misc_seed_count.set(file, AbstractDungeon.miscRng.counter);
        }
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
            DemoMod.isStolen = false;
            ResourcefulRat.phaseTwo = false;
            ResourcefulRat.isBeaten = false;
            ResourcefulRat.isTrueBeaten = false;
            MonsterRoomPatch.PatchRender.isEntryOpen = false;
            MonsterRoomPatch.PatchRender.enabled = false;
            MonsterRoomPatch.PatchUpdate.hb_enabled = true;
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
        public static void Insert(SaveFile save, @ByRef(type = "java.util.HashMap")Object[] _params) {
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
                        os.write(Boolean.toString(DemoMod.isStolen).getBytes());
                        os.write(String.format("\n%.1f\n", ShopScreenPatch.chance).getBytes());
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
                        os.write(Boolean.toString(DemoMod.isStolen).getBytes());
                        os.write(String.format("\n%.1f\n", ShopScreenPatch.chance).getBytes());
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            HashMap<Object, Object> params = (HashMap<Object, Object>) _params[0];
            params.put("misc_seed_count", misc_seed_count.get(save));
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

        public static void Prefix(String filePath) {
            File file = new File("saves/HUNTRESS.curseValue");
            if (file.exists()) {
                try {
                    InputStream is = new FileInputStream(file);
                    Scanner scan = new Scanner(is);
                    HuntressCharacter.curse = scan.nextDouble();
                    DemoMod.canSteal = scan.nextBoolean();
                    DemoMod.afterSteal = scan.nextBoolean();
                    DemoMod.isStolen = scan.nextBoolean();
                    ShopScreenPatch.chance = scan.nextDouble();
                    scan.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @SpireInsertPatch(rloc = 6, localvars = {"savestr"})
        public static void Insert(String filePath, @ByRef(type = "java.lang.String")Object[] _saveStr) {
            String saveStr = (String) _saveStr[0];
            Field misc_seed_count_field = null;
            try {
                misc_seed_count_field = SpireField.class.getDeclaredField("field");
                misc_seed_count_field.setAccessible(true);
                misc_seed_count_field = (Field) misc_seed_count_field.get(misc_seed_count);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            saveStr = saveStr.replace("misc_seed_count", misc_seed_count_field.getName());
            _saveStr[0] = saveStr;
        }
    }
}
