package demoMod.sounds;

import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.core.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Random;

@SuppressWarnings("UnusedReturnValue")
public class DemoSoundMaster {
    private static HashMap<String, Sfx> map = new HashMap<>();
    private static Random random;
    private static final Logger log = LogManager.getLogger(DemoSoundMaster.class);

    public DemoSoundMaster() {
    }

    public static void initialize() {
        random = new Random();
        map.put("CHAR_FALLING", load("sfx/char_falling.wav"));
        map.put("ENTRY_OPEN", load("sfx/entry_open.wav"));
        map.put("RELIC_METRONOME", load("sfx/relic_metronome.wav"));
        map.put("RELIC_METRONOME_BREAK", load("sfx/relic_metronome_break.wav"));
        map.put("RELIC_VORPAL_GUN", load("sfx/relic_vorpal_gun.wav"));
        map.put("RELIC_DOG_BARK", load("sfx/relic_dog_bark.wav"));
        map.put("RELIC_CRISIS_STONE", load("sfx/relic_crisis_stone.wav"));
        map.put("RELIC_CHAFF_GRENADE", load("sfx/relic_chaff_grenade.wav"));
        map.put("RELIC_WOLF_BARK", load("sfx/relic_wolf_bark.wav"));
        map.put("RELIC_WOLF_LANDING_SOUND", load("sfx/relic_wolf_landing_sound.wav"));
        map.put("RELIC_AGED_BELL", load("sfx/relic_aged_bell.wav"));
        map.put("RELIC_WEIRD_EGG", load("sfx/relic_weird_egg.wav"));
        map.put("RELIC_BIG_BOY", load("sfx/relic_big_boy.wav"));
        map.put("RELIC_MELTED_ROCK", load("sfx/relic_melted_rock.wav"));
        map.put("RELIC_ORANGE", load("sfx/relic_orange.wav"));
        map.put("RELIC_RELODESTONE", load("sfx/relic_relodestone.wav"));
        map.put("RELIC_MEAT_BUN", load("sfx/relic_meat_bun.wav"));
        map.put("GUN_RELOAD", load("sfx/gun_reload.wav"));
        map.put("GUN_RELOAD_BALLOON", load("sfx/gun_reload_balloon.wav"));
        map.put("GUN_RELOAD_CROSSBOW", load("sfx/gun_reload_crossbow.wav"));
        map.put("GUN_RELOAD_RAD_SUCCESS_1", load("sfx/gun_reload_rad_success_1.wav"));
        map.put("GUN_RELOAD_RAD_SUCCESS_2", load("sfx/gun_reload_rad_success_2.wav"));
        map.put("GUN_RELOAD_RAD_SUCCESS_3", load("sfx/gun_reload_rad_success_3.wav"));
        map.put("GUN_RELOAD_RAD_FAIL_1", load("sfx/gun_reload_rad_fail_1.wav"));
        map.put("GUN_RELOAD_RAD_FAIL_2", load("sfx/gun_reload_rad_fail_2.wav"));
        map.put("GUN_RELOAD_RAD_FAIL_3", load("sfx/gun_reload_rad_fail_3.wav"));
        map.put("GUN_RELOAD_FIGHTSABRE", load("sfx/gun_reload_fightsabre.wav"));
        map.put("GUN_RELOAD_TEAPOT", load("sfx/gun_reload_teapot.wav"));
        map.put("GUN_RELOAD_JK47", load("sfx/gun_reload_jk47.wav"));
        map.put("GUN_RELOAD_SHOTGUN", load("sfx/gun_reload_shotgun.wav"));
        map.put("GUN_RELOAD_MAGNUM", load("sfx/gun_reload_magnum.wav"));
        map.put("GUN_RELOAD_AWP", load("sfx/gun_reload_awp.wav"));
        map.put("GUN_RELOAD_ELIMENTALER", load("sfx/gun_reload_elimentaler.wav"));
        map.put("GUN_RELOAD_BIG_SHOTGUN", load("sfx/gun_reload_big_shotgun.wav"));
        map.put("GUN_RELOAD_AC15", load("sfx/gun_reload_ac15.wav"));
        map.put("GUN_RELOAD_ZOR", load("sfx/gun_reload_zor.wav"));
        map.put("GUN_RELOAD_BLACK_HOLE", load("sfx/gun_reload_black_hole.wav"));
        map.put("GUN_RELOAD_POLARIS", load("sfx/gun_reload_polaris.wav"));
        map.put("GUN_KILLED_LIFE_ORB", load("sfx/gun_killed_life_orb.wav"));
        map.put("GUN_KILLED_ELIMENTALER", load("sfx/gun_killed_elimentaler.wav"));
        map.put("GUN_FIRE_FINISHED_1", load("sfx/gun_fire_finished_1.wav"));
        map.put("GUN_FIRE_FINISHED_2", load("sfx/gun_fire_finished_2.wav"));
        map.put("GUN_FIRE_KALIBER", load("sfx/gun_fire_kaliber.wav"));
        map.put("GUN_FIRE_RAD", load("sfx/gun_fire_rad.wav"));
        map.put("GUN_FIRE_FIGHTSABRE", load("sfx/gun_fire_fightsabre.wav"));
        map.put("GUN_FIRE_TEAPOT_1", load("sfx/gun_fire_teapot_1.wav"));
        map.put("GUN_FIRE_TEAPOT_2", load("sfx/gun_fire_teapot_2.wav"));
        map.put("GUN_FIRE_MEGAHAND_1", load("sfx/gun_fire_megahand_1.wav"));
        map.put("GUN_FIRE_MEGAHAND_2", load("sfx/gun_fire_megahand_2.wav"));
        map.put("GUN_FIRE_TIME_LIMITER", load("sfx/gun_fire_time_limiter.wav"));
        map.put("GUN_FIRE_JK47", load("sfx/gun_fire_jk47.wav"));
        map.put("GUN_FIRE_DARK_MARKER_1", load("sfx/gun_fire_dark_marker_1.wav"));
        map.put("GUN_FIRE_DARK_MARKER_2", load("sfx/gun_fire_dark_marker_2.wav"));
        map.put("GUN_FIRE_EVOLVER_1", load("sfx/gun_fire_evolver_1.wav"));
        map.put("GUN_FIRE_EVOLVER_2", load("sfx/gun_fire_evolver_2.wav"));
        map.put("GUN_FIRE_EVOLVER_3", load("sfx/gun_fire_evolver_3.wav"));
        map.put("GUN_FIRE_EVOLVER_4", load("sfx/gun_fire_evolver_4.wav"));
        map.put("GUN_FIRE_EVOLVER_5", load("sfx/gun_fire_evolver_5.wav"));
        map.put("GUN_FIRE_LIFE_ORB", load("sfx/gun_fire_life_orb.wav"));
        map.put("GUN_FIRE_SHOTGUN", load("sfx/gun_fire_shotgun.wav"));
        map.put("GUN_FIRE_AWP", load("sfx/gun_fire_awp.wav"));
        map.put("GUN_FIRE_ELIMENTALER", load("sfx/gun_fire_elimentaler.wav"));
        map.put("GUN_FIRE_AC15", load("sfx/gun_fire_ac15.wav"));
        map.put("GUN_FIRE_ZOR", load("sfx/gun_fire_zor.wav"));
        map.put("GUN_FIRE_CAMERA", load("sfx/gun_fire_camera.wav"));
        map.put("GUN_FIRE_BLACK_HOLE", load("sfx/gun_fire_black_hole.wav"));
        map.put("GUN_FIRE_POLARIS", load("sfx/gun_fire_polaris.wav"));
        map.put("GUN_FIRE_COMBINED_RIFLE_1", load("sfx/gun_fire_combined_rifle_1.wav"));
        map.put("GUN_FIRE_COMBINED_RIFLE_2", load("sfx/gun_fire_combined_rifle_2.wav"));
        map.put("POTION_BLANK", load("sfx/potion_blank.wav"));
        map.put("POTION_BLANK_OBTAIN", load("sfx/potion_blank_obtain.wav"));
        map.put("ITEM_DOUBLE_VISION", load("sfx/item_double_vision.wav"));
        map.put("ITEM_PLACE_SOMETHING", load("sfx/item_place_something.wav"));
        map.put("CURSE_INCREASED", load("sfx/curse_increased.wav"));
        map.put("CASEY_BLUNT", load("sfx/casey_blunt.wav"));
        map.put("COMBO_ACTIVATED", load("sfx/combo_activated.wav"));
        map.put("COMBO_EMPTY_VESSELS", load("sfx/combo_empty_vessels.wav"));
        map.put("MONSTER_BULLET_KIN_DEATH", load("sfx/monster_bullet_kin_death.wav"));
        map.put("MONSTER_SHOTGUN_KIN_DEATH", load("sfx/monster_shotgun_kin_death.wav"));
        map.put("MONSTER_SHOTGUN_KIN_EXPLODE", load("sfx/monster_shotgun_kin_explode.wav"));
        map.put("PURIFY", load("sfx/purify.wav"));
        map.put("ARROW_EXPLODE", load("sfx/arrow_explode.wav"));
        map.put("CHEESE_INTRO", load("sfx/cheese_intro.wav"));
        map.put("CHEESE_LOOP", load("sfx/cheese_loop.wav"));
        map.put("CHEESE_OUTRO", load("sfx/cheese_outro.wav"));
        map.put("BOSS_RESOURCEFUL_RAT_APPEAR", load("sfx/boss_resourceful_rat_appear.wav"));
    }

    public static void addSound(String soundID, String path) {
        map.put(soundID, new Sfx(path, false));
    }

    private static Sfx load(String fileName) {
        return new Sfx("DemoAudio/" + fileName, false);
    }

    public static long playA(String key, float pitchAdjust) {
        if (map.containsKey(key)) {
            return (map.get(key)).play(Settings.SOUND_VOLUME * Settings.MASTER_VOLUME, 1.0F + pitchAdjust, 0.0F);
        }
        log.error("Could not find Sound key: {}", key);
        return 0L;
    }

    public static long playV(String key, float pitchVariation) {
        double pitch = random.nextDouble() * pitchVariation * 2 - pitchVariation;
        if (map.containsKey(key)) {
            return (map.get(key)).play(Settings.SOUND_VOLUME * Settings.MASTER_VOLUME, 1.0F + (float)pitch, 0.0F);
        }
        log.error("Could not find Sound key: {}", key);
        return 0L;
    }

    public static long playL(String key) {
        if (map.containsKey(key)) {
            return (map.get(key)).loop(Settings.SOUND_VOLUME * Settings.MASTER_VOLUME);
        }
        log.error("Could not find Sound key: {}", key);
        return 0L;
    }

    public static void stopL(String key) {
        (map.get(key)).stop();
    }
}