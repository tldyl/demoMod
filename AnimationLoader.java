package animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import org.lwjgl.opengl.ContextCapabilities;
import utils.Invoker;

import java.lang.reflect.Field;

/**
 * Created by Keeper on 2019/3/24.
 */
public class AnimationLoader {

    public TextureAtlas atlas;
    public Skeleton skeleton;
    public AnimationState state;
    public AnimationStateData stateData;

    private static ContextCapabilities MAIN_CONTEXT = null;

    //默认是人物 不受巨大化和昆虫标本影响
    public AnimationLoader(String atlasUrl, String skeletonUrl, float scale) {
        this(atlasUrl, skeletonUrl, scale, true);
    }

    public AnimationLoader() {
    }

    public AnimationLoader(TextureAtlas atlas, String skeletonUrl, float scale) {
        SkeletonJson json = new SkeletonJson(atlas);
        json.setScale(Settings.scale / scale);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(skeletonUrl));
        this.skeleton = new Skeleton(skeletonData);
        this.skeleton.setColor(Color.WHITE);
        this.stateData = new AnimationStateData(skeletonData);
        this.state = new AnimationState(this.stateData);
        this.atlas = atlas;
    }

    public AnimationLoader(String atlasUrl, String skeletonUrl, float scale, boolean isPlayer) {
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasUrl));
        SkeletonJson json = new SkeletonJson(this.atlas);
        if (CardCrawlGame.dungeon != null && AbstractDungeon.player != null) {
            if (AbstractDungeon.player.hasRelic("PreservedInsect") && !isPlayer && AbstractDungeon.getCurrRoom().eliteTrigger) {
                scale += 0.3F;
            }

            if (ModHelper.isModEnabled("MonsterHunter") && !isPlayer) {
                scale -= 0.3F;
            }
        }

        json.setScale(Settings.scale / scale);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(skeletonUrl));
        this.skeleton = new Skeleton(skeletonData);
        this.skeleton.setColor(Color.WHITE);
        this.stateData = new AnimationStateData(skeletonData);
        this.state = new AnimationState(this.stateData);
    }

    /**
     * 有工具类就这样吧,没有用下面那个反射。 切换动画还是用 state.setAnimation
     * 预加载就直接定义一堆public static AnimationLoader animationExample = new AnimationLoader(xxx);
     * 要用的时候不用player.loadAnimation(xxx) 改成 AnimationLoader.loadAnimation(player, animationExample);
     * 当然怪物也是可以用的
     */
    public static void loadAnimation(AbstractCreature creature, AnimationLoader animation) {
        Invoker.setField(creature, "atlas", animation.atlas);
        Invoker.setField(creature, "skeleton", animation.skeleton);
        Invoker.setField(creature, "stateData", animation.stateData);
        creature.state = animation.state;
    }

    public static void loadAnimationReflect(AbstractCreature creature, AnimationLoader animation) {
        try {
            Field field = AbstractCreature.class.getDeclaredField("atlas");
            field.setAccessible(true);
            field.set(creature, animation.atlas);
            field = AbstractCreature.class.getDeclaredField("skeleton");
            field.setAccessible(true);
            field.set(creature, animation.skeleton);
            field = AbstractCreature.class.getDeclaredField("stateData");
            field.setAccessible(true);
            field.set(creature, animation.stateData);
            creature.state = animation.state;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
