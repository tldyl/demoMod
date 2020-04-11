package demoMod;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.abstracts.CustomUnlockBundle;
import basemod.devcommands.act.ActCommand;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import demoMod.DynamicVaribles.Capacity;
import demoMod.DynamicVaribles.ExtraDamage;
import demoMod.DynamicVaribles.MultiAttack;
import demoMod.cards.*;
import demoMod.cards.guns.*;
import demoMod.cards.tempCards.Flaw;
import demoMod.cards.tempCards.RatTrap;
import demoMod.characters.HuntressCharacter;
import demoMod.combo.ComboManager;
import demoMod.dungeons.Maze;
import demoMod.effects.BulletSprayEffect;
import demoMod.effects.HuntressVictoryEffect;
import demoMod.effects.ResourcefulRatPhaseTwoIntro;
import demoMod.events.D20Statue;
import demoMod.events.FountainOfPurify;
import demoMod.monsters.*;
import demoMod.patches.*;
import demoMod.potions.BlankPotion;
import demoMod.potions.LeadSkinPotion;
import demoMod.powers.*;
import demoMod.relics.*;
import demoMod.rewards.GlassGuonStone;
import demoMod.sounds.DemoSoundMaster;
import demoMod.ui.screens.ComboManualScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"Duplicates", "ResultOfMethodCallIgnored", "unused", "ForLoopReplaceableByForEach"})
@SpireInitializer
public class DemoMod implements EditCardsSubscriber,
                                EditStringsSubscriber,
                                EditKeywordsSubscriber,
                                StartGameSubscriber,
                                EditRelicsSubscriber,
                                EditCharactersSubscriber,
                                PostDeathSubscriber,
                                PostInitializeSubscriber,
                                RelicGetSubscriber,
                                PotionGetSubscriber,
                                PostRenderSubscriber,
                                PostUpdateSubscriber,
                                SetUnlocksSubscriber,
                                AddAudioSubscriber {

    private static final String ATTACK_CARD = "512/bg_attack_huntress.png";
    private static final String SKILL_CARD = "512/bg_skill_huntress.png";
    private static final String POWER_CARD = "512/bg_power_huntress.png";
    private static final String ENERGY_ORB = "512/card_huntress_orb.png";
    private static final String CARD_ENERGY_ORB = "512/card_small_orb.png";
    private static final String ATTACK_CARD_PORTRAIT = "1024/bg_attack_huntress.png";
    private static final String SKILL_CARD_PORTRAIT = "1024/bg_skill_huntress.png";
    private static final String POWER_CARD_PORTRAIT = "1024/bg_power_huntress.png";
    private static final String ENERGY_ORB_PORTRAIT = "1024/card_huntress_orb.png";
    private static final Logger logger = LogManager.getLogger(DemoMod.class);

    public static List<AbstractCard> tableCards = new ArrayList<>();
    public static List<AbstractCard> bulletCards = new ArrayList<>();
    public static final AbstractCard.CardColor characterColor = AbstractCardEnum.HUNTRESS;
    public static Color mainHuntressColor = new Color(0.98F, 0.95F, 0.05F, 1.0F);
    public static HuntressCharacter huntressCharacter;
    public static boolean canSteal = false;
    public static boolean afterSteal = false;
    public static List<AbstractGameEffect> effectsQueue = new ArrayList<>();
    public static List<AbstractGameAction> actionsQueue = new ArrayList<>();
    public static ComboManualScreen comboManualScreen;
    public static int MAX_FPS;

    private CustomUnlockBundle unlocks0;
    private CustomUnlockBundle unlocks1;
    private CustomUnlockBundle unlocks2;
    private CustomUnlockBundle unlocks3;
    private CustomUnlockBundle unlocks4;

    private static final Color HUNTRESS_COLOR = mainHuntressColor;

    public DemoMod() {
        logger.info("     #####     ");
        logger.info(" ############# ");
        logger.info("###############");
        logger.info("###############");
        logger.info("###############");
        logger.info("###############");
        logger.info("               ");
        logger.info("###############");
        logger.info("Gungeon Mod - v1.2.30");
        BaseMod.subscribe(this);
        BaseMod.addColor(AbstractCardEnum.HUNTRESS,
                HUNTRESS_COLOR, HUNTRESS_COLOR, HUNTRESS_COLOR, HUNTRESS_COLOR, HUNTRESS_COLOR, HUNTRESS_COLOR, HUNTRESS_COLOR,
                getResourcePath(ATTACK_CARD), getResourcePath(SKILL_CARD),
                getResourcePath(POWER_CARD), getResourcePath(ENERGY_ORB),
                getResourcePath(ATTACK_CARD_PORTRAIT), getResourcePath(SKILL_CARD_PORTRAIT),
                getResourcePath(POWER_CARD_PORTRAIT), getResourcePath(ENERGY_ORB_PORTRAIT), getResourcePath(CARD_ENERGY_ORB));
    }

    public static void initialize() {
        new DemoMod();
    }

    public static void onMasterDeckChange() {
        ComboManager.detectComboInGame();
    }

    @Override
    public void receiveEditCharacters() {
        huntressCharacter = new HuntressCharacter("Gungeon Huntress", HuntressEnum.HUNTRESS);
        BaseMod.addCharacter(huntressCharacter, getResourcePath("charSelect/button.png"), getResourcePath("charSelect/portrait.png"), HuntressEnum.HUNTRESS);
    }

    @Override
    public void receiveEditCards() {
        logger.info(new String("=====枪牢mod:初始化卡牌=====".getBytes(), StandardCharsets.UTF_8));
        BaseMod.addDynamicVariable(new Capacity());
        BaseMod.addDynamicVariable(new ExtraDamage());
        BaseMod.addDynamicVariable(new MultiAttack());

        BaseMod.addCard(new Invincible());
        BaseMod.addCard(new Deposit());
        BaseMod.addCard(new Spice());
        BaseMod.addCard(new BlackBlade());
        BaseMod.addCard(new WhiteBlade());
        BaseMod.addCard(new BSG());
        BaseMod.addCard(new BalloonGun());
        BaseMod.addCard(new RustySidearm());
        BaseMod.addCard(new ManualReload());
        BaseMod.addCard(new CrossBow());
        BaseMod.addCard(new TableSutra());
        BaseMod.addCard(new TripleGun());
        BaseMod.addCard(new FinishedGun());
        BaseMod.addCard(new HighKaliber());
        BaseMod.addCard(new SAA());
        BaseMod.addCard(new Behold());
        BaseMod.addCard(new Roll());
        BaseMod.addCard(new Unity());
        BaseMod.addCard(new Strike_Huntress());
        BaseMod.addCard(new Defend_Huntress());
        BaseMod.addCard(new FastSwitch());
        BaseMod.addCard(new DoubleSwitch());
        BaseMod.addCard(new StickyCrossBow());
        BaseMod.addCard(new RadGun());
        BaseMod.addCard(new Rummage());
        BaseMod.addCard(new Eureka());
        BaseMod.addCard(new FightSabre());
        BaseMod.addCard(new Gungine());
        BaseMod.addCard(new EasyReloadBullets());
        BaseMod.addCard(new PotionOfGunFriendShip());
        BaseMod.addCard(new DoubleVision());
        BaseMod.addCard(new ChanceBullets());
        BaseMod.addCard(new SkilledReload());
        BaseMod.addCard(new Slinger());
        BaseMod.addCard(new SpreadAmmo());
        BaseMod.addCard(new CursedBullets());
        BaseMod.addCard(new ShadowBullets());
        BaseMod.addCard(new BattleInHaste());
        BaseMod.addCard(new Casey());
        BaseMod.addCard(new FullFirepower());
        BaseMod.addCard(new BlankBullets());
        BaseMod.addCard(new HuntsMan());
        BaseMod.addCard(new TeaPot());
        BaseMod.addCard(new MegaHand());
        BaseMod.addCard(new PlatinumBullets());
        BaseMod.addCard(new ChaosBullets());
        BaseMod.addCard(new ConditionalReflex());
        BaseMod.addCard(new JK47());
        BaseMod.addCard(new SniperShot());
        BaseMod.addCard(new DirectionalPad());
        BaseMod.addCard(new Unmoved());
        BaseMod.addCard(new DarkMarker());
        BaseMod.addCard(new StrategicReserve());
        BaseMod.addCard(new ProbeStrike());
        BaseMod.addCard(new Evolver());
        BaseMod.addCard(new GunstockTap());
        BaseMod.addCard(new AbyssalTentacle());
        BaseMod.addCard(new LifeOrb());
        BaseMod.addCard(new Replace());
        BaseMod.addCard(new FeignSurrender());
        BaseMod.addCard(new ThirdPartyController());
        BaseMod.addCard(new AWP());
        BaseMod.addCard(new Purify());
        BaseMod.addCard(new PracticeMakesPerfect());
        BaseMod.addCard(new BulletBore());
        BaseMod.addCard(new GunslingerForm());
        BaseMod.addCard(new Elimentaler());
        BaseMod.addCard(new Ejector());
        BaseMod.addCard(new GunShield());
        BaseMod.addCard(new AC15());
        BaseMod.addCard(new OldTrick());
        BaseMod.addCard(new ZorGun());
        BaseMod.addCard(new Desperate());
        BaseMod.addCard(new Camera());
        BaseMod.addCard(new BlackHoleGun());
        BaseMod.addCard(new Polaris());
        BaseMod.addCard(new SuppressiveFire());
        BaseMod.addCard(new FortunesFavor());
        BaseMod.addCard(new BoxingGlove());
        BaseMod.addCard(new MachoBrace());
        BaseMod.addCard(new CombinedRifle());
        BaseMod.addCard(new HungryBullets());
        //接下来是桌技卡
        BaseMod.addCard(new TableTechSight());
        BaseMod.addCard(new TableTechMoney());
        BaseMod.addCard(new TableTechRocket());
        BaseMod.addCard(new TableTechShotgun());
        BaseMod.addCard(new TableTechHeat());
        BaseMod.addCard(new TableTechRage());
        BaseMod.addCard(new TableTechBlanks());
        BaseMod.addCard(new TableTechStun());

        //这里加状态牌
        BaseMod.addCard(new Flaw());
        BaseMod.addCard(new RatTrap());

        //这里把桌技卡加到tableCards集合里
        tableCards.add(new TableTechSight());
        tableCards.add(new TableTechMoney());
        tableCards.add(new TableTechRocket());
        tableCards.add(new TableTechShotgun());
        tableCards.add(new TableTechHeat());
        tableCards.add(new TableTechRage());
        tableCards.add(new TableTechBlanks());
        tableCards.add(new TableTechStun());

        bulletCards.add(new BlankBullets());
        bulletCards.add(new ChanceBullets());
        bulletCards.add(new CursedBullets());
        bulletCards.add(new EasyReloadBullets());
        bulletCards.add(new PlatinumBullets());
        bulletCards.add(new ShadowBullets());
        bulletCards.add(new ChaosBullets());
        bulletCards.add(new HungryBullets());

        UnlockTracker.unlockCard(Strike_Huntress.ID);
        UnlockTracker.unlockCard(Defend_Huntress.ID);
        UnlockTracker.unlockCard(RustySidearm.ID);
        UnlockTracker.unlockCard(CrossBow.ID);
        UnlockTracker.unlockCard(ManualReload.ID);
        UnlockTracker.unlockCard(Roll.ID);


        logger.info(new String("=====枪牢mod:初始化卡牌完毕=====".getBytes(), StandardCharsets.UTF_8));
    }

    public static String makeID(String name) {
        return "DemoMod:" + name;
    }

    public static String getResourcePath(String resource) {
        return "DemoImages/" + resource;
    }

    public static AbstractCard getRandomTableCard() {
        int size = tableCards.size();
        return tableCards.get(AbstractDungeon.cardRandomRng.random(size - 1)).makeCopy();
    }

    public static List<AbstractCard> getRandomTableCard(int amount) {
        List<AbstractCard> tmp = new ArrayList<>(tableCards);
        List<AbstractCard> ret = new ArrayList<>();
        int size = tableCards.size();
        if (amount >= size) return tmp;
        for (int i=0;i<amount;i++) {
            int ran = AbstractDungeon.cardRandomRng.random(size - 1);
            ret.add(tmp.get(ran).makeCopy());
            tmp.remove(ran);
            size--;
        }
        return ret;
    }

    public static AbstractCard getRandomBulletCard() {
        int size = bulletCards.size();
        return bulletCards.get(AbstractDungeon.cardRandomRng.random(size - 1)).makeCopy();
    }

    public static AbstractRelic.RelicTier getRelicTierFromMimicType(Mimic.MimicType type) {
        switch(type) {
            case SMALL:
                return AbstractRelic.RelicTier.COMMON;
            case MEDIUM:
                return AbstractRelic.RelicTier.UNCOMMON;
            case LARGE:
                return AbstractRelic.RelicTier.RARE;
            default:
                return AbstractRelic.RelicTier.COMMON;
        }
    }

    public static String getLanguageString() {
        String language;
        switch (Settings.language) {
            case ZHS:
                language = "zhs";
                break;
            case KOR:
                language = "kor";
                break;
            default:
                language = "eng";
        }
        return language;
    }

    @Override
    public void receiveEditStrings() {
        logger.info(new String("=====枪牢mod:载入游戏文本=====".getBytes(), StandardCharsets.UTF_8));
        String language;
        language = getLanguageString();

        String cardStrings = Gdx.files.internal("localization/" + language + "/Demo-CardStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(CardStrings.class, cardStrings);
        String powerStrings = Gdx.files.internal("localization/" + language + "/Demo-PowerStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(PowerStrings.class, powerStrings);
        String relicStrings = Gdx.files.internal("localization/" + language + "/Demo-RelicStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
        String uiStrings = Gdx.files.internal("localization/" + language + "/Demo-UIStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
        String potionStrings = Gdx.files.internal("localization/" + language + "/Demo-PotionStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(PotionStrings.class, potionStrings);
        String charStrings = Gdx.files.internal("localization/" + language + "/Demo-CharacterStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(CharacterStrings.class, charStrings);
        String monsterStrings = Gdx.files.internal("localization/" + language + "/Demo-MonsterStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(MonsterStrings.class, monsterStrings);
        String eventStrings = Gdx.files.internal("localization/" + language + "/Demo-EventStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(EventStrings.class, eventStrings);
        logger.info(new String("=====枪牢mod:载入游戏文本完毕=====".getBytes(), StandardCharsets.UTF_8));
    }

    @Override
    public void receiveEditKeywords() {
        logger.info(new String("=====枪牢mod:添加关键字=====".getBytes(), StandardCharsets.UTF_8));
        final Gson gson = new Gson();
        String language;
        language = getLanguageString();
        final String json = Gdx.files.internal("localization/" + language + "/Demo-KeywordStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
        logger.info(new String("=====枪牢mod:关键字添加完毕=====".getBytes(), StandardCharsets.UTF_8));
    }

    @Override
    public void receiveStartGame() {
        ComboManager.detectCombo();
        DemoSoundMaster.stopL("CHEESE_LOOP");
        MonsterRoomPatch.mazeTempMusicSoundKey = "ACT_MAZE_COMBAT";
        MonsterRoomPatch.entered = false;
        TreasureRoomPatch.closeEntry();
    }

    @Override
    public void receiveEditRelics() {
        logger.info(new String("=====枪牢mod:添加遗物=====".getBytes(), StandardCharsets.UTF_8));
        BaseMod.addRelic(new SevenLeafClover(), RelicType.SHARED);
        BaseMod.addRelic(new Metronome(), RelicType.SHARED);
        BaseMod.addRelic(new MemoryAlloy(), RelicType.SHARED);
        BaseMod.addRelic(new VorpalBullet(), RelicType.SHARED);
        BaseMod.addRelic(new RingOfEthereal(), RelicType.SHARED);
        BaseMod.addRelic(new Bottle(), RelicType.SHARED);
        BaseMod.addRelic(new DecoyRelic(), RelicType.SHARED);
        BaseMod.addRelic(new ChaffGrenade(), RelicType.SHARED);
        BaseMod.addRelic(new GundromedaStrain(), RelicType.SHARED);
        BaseMod.addRelic(new AgedBell(), RelicType.SHARED);
        BaseMod.addRelic(new WeirdEgg(), RelicType.SHARED);
        BaseMod.addRelic(new PoisonVial(), RelicType.SHARED);
        BaseMod.addRelic(new BigBoy(), RelicType.SHARED);
        BaseMod.addRelic(new GlassGuonStoneRelic(), RelicType.SHARED);
        BaseMod.addRelic(new Antibody(), RelicType.SHARED);
        BaseMod.addRelic(new WhiteGuonStone(), RelicType.SHARED);
        BaseMod.addRelic(new OrangeGuonStone(), RelicType.SHARED);
        BaseMod.addRelic(new ClearGuonStone(), RelicType.SHARED);
        BaseMod.addRelic(new GreenGuonStone(), RelicType.SHARED);
        BaseMod.addRelic(new PinkGuonStone(), RelicType.SHARED);
        BaseMod.addRelic(new BlueGuonStone(), RelicType.SHARED);
        BaseMod.addRelic(new MeltedRock(), RelicType.SHARED);
        BaseMod.addRelic(new Orange(), RelicType.SHARED);
        BaseMod.addRelic(new MonsterBlood(), RelicType.SHARED);
        BaseMod.addRelic(new SuperHotWatch(), RelicType.SHARED);
        BaseMod.addRelic(new RiddleOfLead(), RelicType.SHARED);
        BaseMod.addRelic(new Relodestone(), RelicType.SHARED);
        BaseMod.addRelic(new JetPack(), RelicType.SHARED);
        BaseMod.addRelic(new WaxWing(), RelicType.SHARED);
        BaseMod.addRelic(new StrengthOfFortune(), RelicType.SHARED);
        BaseMod.addRelic(new Limited(), RelicType.SHARED);
        BaseMod.addRelic(new Armor(), RelicType.SHARED);
        BaseMod.addRelic(new Unsteady(), RelicType.SHARED);
        BaseMod.addRelic(new GnawedKey(), RelicType.SHARED);
        BaseMod.addRelic(new ResourcefulSack(), RelicType.SHARED);
        BaseMod.addRelic(new PartiallyEatenCheese(), RelicType.SHARED);
        BaseMod.addRelic(new MeatBun(), RelicType.SHARED);
        BaseMod.addRelic(new NanoMachines(), RelicType.SHARED);
        BaseMod.addRelic(new SerJunkan(), RelicType.SHARED);
        BaseMod.addRelic(new Junk(), RelicType.SHARED);
        BaseMod.addRelic(new GoldJunk(), RelicType.SHARED);
        BaseMod.addRelicToCustomPool(new RedGuonStone(), characterColor);
        BaseMod.addRelicToCustomPool(new HipHolster(), characterColor);
        BaseMod.addRelicToCustomPool(new Dog(), characterColor);
        BaseMod.addRelicToCustomPool(new CrisisStone(), characterColor);
        BaseMod.addRelicToCustomPool(new Wolf(), characterColor);
        BaseMod.addRelicToCustomPool(new HoleyGrail(), characterColor);
        BaseMod.addRelicToCustomPool(new Ammonomicon(), characterColor);
        BaseMod.addRelicToCustomPool(new LichsEyeBullet(), characterColor);
        BaseMod.addRelicToCustomPool(new PortableTableDevice(), characterColor);
        BaseMod.addRelicToCustomPool(new Test1(), characterColor);
        BaseMod.addRelicToCustomPool(new RatBoots(), characterColor);
        BaseMod.addRelicToCustomPool(new ElasticCartridgeClip(), characterColor);
        BaseMod.addRelicToCustomPool(new SilverBullets(), characterColor);

        logger.info(new String("=====枪牢mod:遗物添加完毕=====".getBytes(), StandardCharsets.UTF_8));
    }

    @Override
    public void receivePostDeath() {
        Spice.dropChance = 0;
        Spice.isFirstUse = true;
        VorpalBullet.chance = 0.03;
        File file = new File("saves/HUNTRESS.curseValue");
        if (file.exists()) {
            file.delete();
        }
        MonsterHelperPatch.PatchGetEncounterName.changeName();
        if (AbstractDungeon.id.equals(Maze.ID)) {
            try {
                Field field = AbstractPlayer.class.getDeclaredField("corpseImg");
                field.setAccessible(true);
                field.set(AbstractDungeon.player, new Texture(DemoMod.getResourcePath("char/corpse2.png")));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receivePostInitialize() {
        logger.info(new String("=====枪牢mod:载入音频=====".getBytes(), StandardCharsets.UTF_8));
        DemoSoundMaster.initialize();
        logger.info(new String("=====枪牢mod:载入特效=====".getBytes(), StandardCharsets.UTF_8));
        ResourcefulRatPhaseTwoIntro.init();
        BulletSprayEffect.init();
        HuntressVictoryEffect.init();



        logger.info(new String("=====枪牢mod:添加药水=====".getBytes(), StandardCharsets.UTF_8));
        BaseMod.addPotion(BlankPotion.class, Color.BLUE, Color.WHITE, Color.WHITE, BlankPotion.ID);
        BaseMod.addPotion(LeadSkinPotion.class, Color.BLUE, Color.WHITE, Color.WHITE, LeadSkinPotion.ID);
        logger.info(new String("=====枪牢mod:初始化事件=====".getBytes(), StandardCharsets.UTF_8));
        BaseMod.addEvent(D20Statue.ID, D20Statue.class);
        BaseMod.addEvent(FountainOfPurify.ID, FountainOfPurify.class);
        logger.info(new String("=====枪牢mod:添加基础掉落=====".getBytes(), StandardCharsets.UTF_8));
        BaseMod.registerCustomReward(
                CustomRewardPatch.GUON_STONE,
                rewardSave -> new GlassGuonStone(),
                customReward -> new RewardSave(customReward.type.toString(), null, 1, 0)
        );
        logger.info(new String("=====枪牢mod:初始化新章节=====".getBytes(), StandardCharsets.UTF_8));
        CardCrawlGamePatch.addDungeon(Maze.ID, Maze.builder());
        CardCrawlGamePatch.addNextDungeon(Maze.ID, "TheBeyond");

        ActCommand.addAct(Maze.ID, 2);

        try {
            Field field = BaseMod.class.getDeclaredField("powerMap");
            field.setAccessible(true);
            HashMap<String, Class<? extends AbstractPower>> powerMap = (HashMap) field.get(null);
            //这里加可以在控制台调出来的buff
            powerMap.put(StrengthOfCursePower.POWER_ID, StrengthOfCursePower.class);
            powerMap.put(CongealedPower.POWER_ID, CongealedPower.class);
            powerMap.put(ControlPower.POWER_ID, ControlPower.class);
            powerMap.put(JammedPower.POWER_ID, JammedPower.class);
            powerMap.put(PlatinumBulletsPower.POWER_ID, PlatinumBulletsPower.class);
            powerMap.put(PacManPower.POWER_ID, PacManPower.class);
            powerMap.put(BulletSprayPower.POWER_ID, BulletSprayPower.class);
            field.set(null, powerMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        logger.info(new String("=====枪牢mod:初始化敌人=====".getBytes(), StandardCharsets.UTF_8));
        BaseMod.addMonster("DemoMod:4_Bullet_Kins", "4 Bullet Kins", () -> new MonsterGroup(new AbstractMonster[]{
                new BulletKin(-360, Settings.HEIGHT * 0.02F),
                new BulletKin(-180, 0, true),
                new BulletKin(0, Settings.HEIGHT * 0.02F),
                new BulletKin(180, 0, true)
        }));
        BaseMod.addMonster("DemoMod:Red_and_blue_shotgun_kin", "Red and blue shotgun kin", () -> new MonsterGroup(new AbstractMonster[]{
                new RedShotgunKin(-120, 0),
                new BlueShotgunKin(120, 0)
        }));
        BaseMod.addMonster("DemoMod:Veteran_shot_gun_kin_and_bullet_kin", "Veteran shot gun kin and bullet kin", () -> new MonsterGroup(new AbstractMonster[]{
                new BulletKin(-120, 0, true),
                new VeteranShotgunKin(120, 0)
        }));
        BaseMod.addMonster("DemoMod:Veteran_shot_gun_kin_and_blue_shotgun_kin", "Veteran shot gun kin and blue shotgun kin", () -> new MonsterGroup(new AbstractMonster[]{
                new BlueShotgunKin(-120, 0),
                new VeteranShotgunKin(120, 0)
        }));
        BaseMod.addMonster("DemoMod:Veteran_shot_gun_kin_and_red_shotgun_kin", "Veteran shot gun kin and red shotgun kin", () -> new MonsterGroup(new AbstractMonster[]{
                new RedShotgunKin(-120, 0),
                new VeteranShotgunKin(120, 0)
        }));
        BaseMod.addMonster("DemoMod:3_Bullet_Kins_with_one_veteran", "3 Bullet Kins with one veteran", () -> new MonsterGroup(new AbstractMonster[]{
                new BulletKin(-180, 0, true),
                new VeteranBulletKin(0, Settings.HEIGHT * 0.02F),
                new BulletKin(180, 0, false)
        }));
        BaseMod.addMonster("DemoMod:2_Mousers", "2 Mousers", () -> new MonsterGroup(new AbstractMonster[]{
                new Mouser(-240.0F, 0.0F, 3),
                new Mouser(120.0F, 0.0F, 2)
        }));
        BaseMod.addMonster("DemoMod:Red_and_blue_shotgun_kin_with_gunsinger", "Red and blue shotgun kin with gunsinger", () -> new MonsterGroup(new AbstractMonster[]{
                new RedShotgunKin(-120, 0),
                new BlueShotgunKin(60, 0),
                new Gunsinger(260, 0)
        }));
        BaseMod.addMonster("DemoMod:Veteran_shot_gun_kin_and_blue_shotgun_kin_with_gunsinger", "Veteran shot gun kin and blue shotgun kin with gunsinger", () -> new MonsterGroup(new AbstractMonster[]{
                new BlueShotgunKin(-120, 0),
                new VeteranShotgunKin(60, 0),
                new Gunsinger(260, 0)
        }));
        BaseMod.addMonster("DemoMod:Veteran_shot_gun_kin_and_red_shotgun_kin_with_gunsinger", "Veteran shot gun kin and red shotgun kin with gunsinger", () -> new MonsterGroup(new AbstractMonster[]{
                new RedShotgunKin(-120, 0),
                new VeteranShotgunKin(60, 0),
                new Gunsinger(260, 0)
        }));
        BaseMod.addMonster(ResourcefulRat.ID, ResourcefulRat.NAME, () -> new MonsterGroup(new AbstractMonster[]{
                new ResourcefulRat(0, 0)
        }));
        BaseMod.addBoss(Maze.ID, ResourcefulRat.ID, DemoMod.getResourcePath("map/resourcefulRat.png"), DemoMod.getResourcePath("map/resourcefulRatOutline.png"));
        comboManualScreen = new ComboManualScreen();
        MAX_FPS = Settings.MAX_FPS;
        loadSettings();
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ModPanel"));
        ModPanel settingsPanel = new ModPanel();
        ModLabeledToggleButton spawnMimicForOtherCharacters = new ModLabeledToggleButton(uiStrings.TEXT[0], 350.0F, 700.0F, Color.WHITE, FontHelper.buttonLabelFont, DemoMod.spawnMimicForOtherCharacters, settingsPanel, (me) -> {},
                (me) -> {
                    DemoMod.spawnMimicForOtherCharacters = me.enabled;
                    DemoMod.saveSettings();
                });
        ModLabeledToggleButton disableVFXForCamera = new ModLabeledToggleButton(uiStrings.TEXT[1], 350.0F, 650.0F, Color.WHITE, FontHelper.buttonLabelFont, DemoMod.disableVFXForCamera, settingsPanel, (me) -> {},
                (me) -> {
                    DemoMod.disableVFXForCamera = me.enabled;
                    DemoMod.saveSettings();
                });
        settingsPanel.addUIElement(spawnMimicForOtherCharacters);
        settingsPanel.addUIElement(disableVFXForCamera);
        BaseMod.registerModBadge(ImageMaster.loadImage(DemoMod.getResourcePath("ui/badge.png")), "Gungeon Mod", "Everyone", "TODO", settingsPanel);
        AbstractDungeonPatch.instance = new AbstractDungeonPatch();
    }

    public static boolean spawnMimicForOtherCharacters = false;
    public static boolean disableVFXForCamera = false;

    private static void saveSettings() {
        try {
            SpireConfig config = new SpireConfig("demoMod", "settings");
            config.setBool("spawnMimicForOtherCharacters", spawnMimicForOtherCharacters);
            config.setBool("disableVFXForCamera", disableVFXForCamera);
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadSettings() {
        try {
            SpireConfig config = new SpireConfig("demoMod", "settings");
            config.load();
            if (config.has("spawnMimicForOtherCharacters")) {
                spawnMimicForOtherCharacters = config.getBool("spawnMimicForOtherCharacters");
            }
            if (config.has("disableVFXForCamera")) {
                disableVFXForCamera = config.getBool("disableVFXForCamera");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveRelicGet(AbstractRelic relic) {

    }

    @Override
    public void receivePotionGet(AbstractPotion potion) {
        ComboManager.detectComboInGame();
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (effectsQueue.size() > 0) {
            List<AbstractGameEffect> effectsToRemove = null;
            for (int i=0;i<effectsQueue.size();i++) {
                AbstractGameEffect effect = effectsQueue.get(i);
                if (!effect.isDone && effect.duration > 0) {
                    effect.update();
                    effect.render(sb);
                } else {
                    if (effectsToRemove == null) effectsToRemove = new ArrayList<>();
                    if (!effectsToRemove.contains(effect)) effectsToRemove.add(effect);
                }
            }
            if (effectsToRemove != null) effectsQueue.removeAll(effectsToRemove);
        }
        comboManualScreen.render(sb);
    }

    @Override
    public void receiveSetUnlocks() {
        logger.info(new String("=====枪牢mod:初始化解锁内容=====".getBytes(), StandardCharsets.UTF_8));
        unlocks0 = new CustomUnlockBundle(
                Rummage.ID, Eureka.ID, Behold.ID
        );
        unlocks1 = new CustomUnlockBundle(
                Gungine.ID, SpreadAmmo.ID, Evolver.ID
        );
        unlocks2 = new CustomUnlockBundle(
                AbstractUnlock.UnlockType.RELIC, SevenLeafClover.ID, HipHolster.ID, RiddleOfLead.ID
        );
        unlocks3 = new CustomUnlockBundle(
                Casey.ID, StrategicReserve.ID, Replace.ID
        );
        unlocks4 = new CustomUnlockBundle(
                AbstractUnlock.UnlockType.RELIC, LichsEyeBullet.ID, SuperHotWatch.ID, WaxWing.ID
        );
        BaseMod.addUnlockBundle(unlocks0, HuntressEnum.HUNTRESS, 0);
        BaseMod.addUnlockBundle(unlocks1, HuntressEnum.HUNTRESS, 1);
        BaseMod.addUnlockBundle(unlocks2, HuntressEnum.HUNTRESS, 2);
        BaseMod.addUnlockBundle(unlocks3, HuntressEnum.HUNTRESS, 3);
        BaseMod.addUnlockBundle(unlocks4, HuntressEnum.HUNTRESS, 4);

        UnlockTracker.addCard(Rummage.ID);
        UnlockTracker.addCard(Eureka.ID);
        UnlockTracker.addCard(Behold.ID);

        UnlockTracker.addCard(Gungine.ID);
        UnlockTracker.addCard(SpreadAmmo.ID);
        UnlockTracker.addCard(Evolver.ID);

        UnlockTracker.addCard(Casey.ID);
        UnlockTracker.addCard(StrategicReserve.ID);
        UnlockTracker.addCard(Replace.ID);

        UnlockTracker.addRelic(SevenLeafClover.ID);
        UnlockTracker.addRelic(HipHolster.ID);
        UnlockTracker.addRelic(RiddleOfLead.ID);

        UnlockTracker.addRelic(LichsEyeBullet.ID);
        UnlockTracker.addRelic(SuperHotWatch.ID);
        UnlockTracker.addRelic(WaxWing.ID);
    }

    public static int frameRateRemap(int inputIndex, int fpsInAnim, int maxIndex) { //帧映射
        double mul = MAX_FPS / (double)fpsInAnim;
        int output = (int)Math.floor(inputIndex / mul);
        if (output > maxIndex) output = maxIndex;
        return output;
    }

    @Override
    public void receivePostUpdate() {
        if (actionsQueue.size() > 0) {
            actionsQueue.get(0).update();
            if (actionsQueue.get(0).isDone) {
                actionsQueue.remove(0);
            }
        }
        comboManualScreen.update();
    }

    @Override
    public void receiveAddAudio() {
        BaseMod.addAudio("RELIC_VORPAL_GUN", "DemoAudio/sfx/relic_vorpal_gun.wav");
        BaseMod.addAudio("ELEVATOR_OPEN", "DemoAudio/sfx/elevator_open.wav");
        BaseMod.addAudio("ELEVATOR_CLOSE", "DemoAudio/sfx/elevator_close.wav");
    }
}
