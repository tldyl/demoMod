package demoMod.dungeons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import demoMod.DemoMod;
import demoMod.characters.HuntressCharacter;
import demoMod.monsters.ResourcefulRat;
import demoMod.patches.CardCrawlGamePatch;
import demoMod.patches.MonsterRoomPatch;
import demoMod.patches.TreasureRoomPatch;
import demoMod.rooms.MazeRestRoom;
import demoMod.scenes.MazeScene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SuppressWarnings({"unchecked", "ManualArrayToCollectionCopy", "UseBulkOperation"})
public class Maze extends AbstractDungeon {
    private static final Logger logger = LogManager.getLogger(Maze.class.getName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(DemoMod.makeID("Maze"));
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];
    public static final String ID;

    public static final String NUM;

    static {
        ID = DemoMod.makeID("Maze");
        NUM = TEXT[1];
    }

    public Maze(AbstractPlayer p, ArrayList<String> theList) {
        super(NAME, ID, p, theList);
        if (scene != null) {
            scene.dispose();
        }
        scene = new MazeScene();
        fadeColor = Color.valueOf("0a1e1eff");

        initializeLevelSpecificChances();
        mapRng = new Random(Settings.seed + AbstractDungeon.actNum * 100);
        generateSpecialMap();

        AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
        AbstractDungeon.currMapNode.room = new EmptyRoom();
        if (!MonsterRoomPatch.entered) {
            CardCrawlGame.music.dispose();
            CardCrawlGame.music.changeBGM(ID);
        }
        if (Loader.isModLoaded("actlikeit")) {
            try {
                Class<?> cls = Class.forName("actlikeit.savefields.BehindTheScenesActNum");
                Field field = cls.getDeclaredField("bc");
                field.setAccessible(true);
                Object bc = field.get(null);
                field = cls.getDeclaredField("actNum");
                field.setAccessible(true);
                int actNum = (Integer) field.get(bc);
                field.set(bc, --actNum);
                logger.info("Decreased act number. Current act number:" + actNum);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            try {
                Field field = AbstractPlayer.class.getDeclaredField("img");
                field.setAccessible(true);
                field.set(AbstractDungeon.player, new Texture(DemoMod.getResourcePath("char/character2.png")));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public Maze(AbstractPlayer p, SaveFile saveFile) {
        super(NAME, p, saveFile);
        fadeColor = Color.valueOf("0a1e1eff");
        scene = new MazeScene();

        initializeLevelSpecificChances();
        miscRng = new Random(Settings.seed + saveFile.floor_num);
        mapRng = new com.megacrit.cardcrawl.random.Random(Settings.seed + AbstractDungeon.actNum * 100);
        generateSpecialMap();

        populatePathTaken(saveFile);

        if (!MonsterRoomPatch.entered) {
            CardCrawlGame.music.dispose();
            CardCrawlGame.music.changeBGM(ID);
        }
        if (AbstractDungeon.player instanceof HuntressCharacter) {
            try {
                Field field = AbstractPlayer.class.getDeclaredField("img");
                field.setAccessible(true);
                field.set(AbstractDungeon.player, new Texture(DemoMod.getResourcePath("char/character2.png")));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static CardCrawlGamePatch.AbstractDungeonBuilder builder() {
        return new CardCrawlGamePatch.AbstractDungeonBuilder() {
            public AbstractDungeon build(AbstractPlayer p, ArrayList<String> theList) {
                return new Maze(p, theList);
            }
            public AbstractDungeon build(AbstractPlayer p, SaveFile save) {
                return new Maze(p, save);
            }
        };
    }

    private void generateSpecialMap() {
        long startTime = System.currentTimeMillis();
        map = new ArrayList<>();

        MapRoomNode restNode1 = new MapRoomNode(3, 0);
        restNode1.room = new MazeRestRoom();
        MapRoomNode enemyNode1 = new MapRoomNode(3, 1);
        enemyNode1.room = new MonsterRoom();
        MapRoomNode enemyNode2 = new MapRoomNode(3, 2);
        enemyNode2.room = new MonsterRoom();
        MapRoomNode enemyNode3 = new MapRoomNode(3, 3);
        enemyNode3.room = new MonsterRoom();
        MapRoomNode enemyNode4 = new MapRoomNode(3, 4);
        enemyNode4.room = new MonsterRoom();
        MapRoomNode enemyNode5 = new MapRoomNode(3, 5);
        enemyNode5.room = new MonsterRoom();
        MapRoomNode restNode2 = new MapRoomNode(3, 6);
        restNode2.room = new MazeRestRoom();
        MapRoomNode bossNode1 = new MapRoomNode(3, 7);
        bossNode1.room = new MonsterRoomBoss();

        connectNode(restNode1, enemyNode1);
        connectNode(enemyNode1, enemyNode2);
        connectNode(enemyNode2, enemyNode3);
        connectNode(enemyNode3, enemyNode4);
        connectNode(enemyNode4, enemyNode5);
        connectNode(enemyNode5, restNode2);
        restNode2.addEdge(new MapEdge(restNode2.x, restNode2.y, restNode2.offsetX, restNode2.offsetY, bossNode1.x, bossNode1.y, bossNode1.offsetX, bossNode1.offsetY, false));

        ArrayList<MapRoomNode>[] rows = new ArrayList[8];

        rows[0] = generateRow(0, restNode1);
        rows[1] = generateRow(1, enemyNode1);
        rows[2] = generateRow(2, enemyNode2);
        rows[3] = generateRow(3, enemyNode3);
        rows[4] = generateRow(4, enemyNode4);
        rows[5] = generateRow(5, enemyNode5);
        rows[6] = generateRow(6, restNode2);
        rows[7] = generateRow(7, bossNode1);

        for (ArrayList<MapRoomNode> row : rows) {
            map.add(row);
        }

        firstRoomChosen = false;
        logger.info("Generated the following dungeon map:");
        logger.info(MapGenerator.toString(map, true));
        logger.info("Game Seed: " + Settings.seed);
        logger.info("Map generation time: " + (System.currentTimeMillis() - startTime) + "ms");
        fadeIn();
    }

    private ArrayList<MapRoomNode> generateRow(int rows, MapRoomNode node) {
        ArrayList<MapRoomNode> ret = new ArrayList<>();
        ret.add(new MapRoomNode(0, rows));
        ret.add(new MapRoomNode(1, rows));
        ret.add(new MapRoomNode(2, rows));
        ret.add(node);
        ret.add(new MapRoomNode(4, rows));
        ret.add(new MapRoomNode(5, rows));
        ret.add(new MapRoomNode(6, rows));

        return ret;
    }

    private void connectNode(MapRoomNode src, MapRoomNode dst) {
        src.addEdge(new MapEdge(src.x, src.y, src.offsetX, src.offsetY, dst.x, dst.y, dst.offsetX, dst.offsetY, false));
    }

    @Override
    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.22F;
        eliteRoomChance = 0.08F;

        smallChestChance = 0;
        mediumChestChance = 100;
        largeChestChance = 0;

        commonRelicChance = 0;
        uncommonRelicChance = 100;
        rareRelicChance = 0;

        colorlessRareChance = 0.3F;
        if (AbstractDungeon.ascensionLevel >= 12) {
            cardUpgradedChance = 0.25F;
        } else {
            cardUpgradedChance = 0.5F;
        }
    }

    @Override
    protected ArrayList<String> generateExclusions() {
        return new ArrayList<>();
    }

    @Override
    protected void generateMonsters() {
        generateWeakEnemies(2);
        generateStrongEnemies(3);
    }

    @Override
    protected void generateWeakEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo("DemoMod:4_Bullet_Kins", 2.0F));
        monsters.add(new MonsterInfo("DemoMod:Veteran_shot_gun_kin_and_red_shotgun_kin", 1.0F));
        monsters.add(new MonsterInfo("DemoMod:Veteran_shot_gun_kin_and_bullet_kin", 2.0F));
        monsters.add(new MonsterInfo("DemoMod:Red_and_blue_shotgun_kin", 2.0F));
        monsters.add(new MonsterInfo("DemoMod:3_Bullet_Kins_with_one_veteran", 1.0F));
        monsters.add(new MonsterInfo("DemoMod:2_Veteran_bullet_kins_and_red_shotgun_kin", 2.0F));

        MonsterInfo.normalizeWeights(monsters);
        populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo("DemoMod:Red_and_blue_shotgun_kin_with_gunsinger", 2.0F));
        monsters.add(new MonsterInfo("DemoMod:Veteran_shot_gun_kin_and_red_shotgun_kin_with_gunsinger", 1.0F));
        monsters.add(new MonsterInfo("DemoMod:Veteran_shot_gun_kin_and_blue_shotgun_kin_with_gunsinger", 1.0F));
        monsters.add(new MonsterInfo("DemoMod:2_Mousers", 2.0F));
        monsters.add(new MonsterInfo("DemoMod:Veteran_shot_gun_kin_and_blue_shotgun_kin", 1.0F));

        MonsterInfo.normalizeWeights(monsters);
        populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateElites(int count) {
        generateStrongEnemies(count);
    }

    @Override
    protected void initializeBoss() {
        bossList.add(ResourcefulRat.ID);
        bossList.add(ResourcefulRat.ID);
        bossList.add(ResourcefulRat.ID);
        TreasureRoomPatch.closeEntry();
    }

    @Override
    protected void initializeEventList() {

    }

    @Override
    protected void initializeEventImg() {

    }

    @Override
    protected void initializeShrineList() {

    }
}
