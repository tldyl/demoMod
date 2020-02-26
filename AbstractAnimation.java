package animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.vfx.TintEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keeper on 2019/3/14.
 * <p>
 * 配合给的patch类使用
 * <p>
 * 用法：这个类可以直接用，new AbstractAnimation(xxx)就可以了,支持骨骼动画，图片和GIF（划重点），图片和GIF共享一个构造方法
 * 如果是图片，这样就完事了
 * 如果是骨骼动画 仅这样是不会动的
 * 取到这个对象（用getAnimation或者其他方法）比如叫 animation 用 animation.setAnimation(xxx) 或 animation.state.setAnimation(0, "xxx", true/false);来选择动画
 * <p>
 * 用setImg(xxx) 来切换图片
 * 用loadAnimation(xxx) 来切换动画
 * 再用 animation.state.setAnimation(0, "xxx", true/false);来播放具体的动画
 * <p>
 * 用setMovable(xx)来选择是否可以拖动
 * 用setVisible(xx)来选择是否可见
 * 还有clearAll，showAll，hideAll三个方法都是字面意思
 * 不关游戏会动画一直持续下去，可以在游戏开局用clearAll()来清空上一局的动画
 * <p>
 * 可以在初始化的时候给骨骼动画取一个名字，放在第一个参数
 * AbstractAnimation animation = AbstractAnimation.getAnimation("your id");
 * 来获得这个动画 随后可以用 animation.skeleton来随意对骨骼动画进行操作
 */
public class AbstractAnimation {
    public static List<AbstractAnimation> animations = new ArrayList<>();

    public static SkeletonMeshRenderer sr;

    static {
        sr = new SkeletonMeshRenderer();
        sr.setPremultipliedAlpha(true);
    }

    public static Color AColor = new Color(1.0F, 1.0F, 1.0F, 0.1F);

    protected Texture img;
    protected TextureAtlas atlas;
    public Skeleton skeleton;
    protected AnimationStateData stateData;
    protected AnimationState state;

    public Hitbox hb;
    public TintEffect tint;
    public String id;

    public float drawX;
    public float drawY;

    public boolean flipHorizontal;
    public boolean flipVertical;

    private boolean movable = true;
    private boolean visible = true;

    GifAnimation gifAnimation;

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public AbstractAnimation(String id, String atlasUrl, String skeletonUrl, float scale, float positionX, float positionY, float hb_w, float hb_h, float hbScale) {
        this.id = id;
        loadAnimation(atlasUrl, skeletonUrl, scale);
        if (hb_h == 0 && hb_w == 0) {
            hb_w = this.skeleton.getData().getWidth();
            hb_h = this.skeleton.getData().getHeight();
        }
        this.hb = new Hitbox(hb_w * hbScale, hb_h * hbScale);
        this.tint = new TintEffect();
        setPosition(positionX, positionY);
        addAnimation(this);
    }

    public AbstractAnimation(String atlasUrl, String skeletonUrl, float scale, float positionX, float positionY, float hb_x, float hb_y, float hb_w, float hb_h) {
        this(null, atlasUrl, skeletonUrl, scale, positionX, positionY, hb_w, hb_h, 1.0F);
    }

    public AbstractAnimation(String atlasUrl, String skeletonUrl, float scale, float positionX, float positionY, float hb_x, float hb_y) {
        this(null, atlasUrl, skeletonUrl, scale, positionX, positionY, 0, 0, 1.0F);
    }

    public AbstractAnimation(String imgUrl, float positionX, float positionY, float hb_w, float hb_h, float hbScale) {
        loadImg(imgUrl);
        this.tint = new TintEffect();
        if (hb_h == 0 && hb_w == 0) {
            hb_w = this.img.getWidth();
            hb_h = this.img.getHeight();
        }
        this.hb = new Hitbox(hb_w * hbScale, hb_h * hbScale);
        setPosition(positionX, positionY);
        addAnimation(this);
    }

    public static void addAnimation(AbstractAnimation abstractAnimation) {
        if (abstractAnimation == null) {
            return;
        }
        for (AbstractAnimation animation : animations) {
            if (animation.id != null && animation.id.equals(abstractAnimation.id)) {
                return;
            }
        }
        animations.add(abstractAnimation);
    }

    public AbstractAnimation(String imgUrl, float positionX, float positionY, float hb_w, float hb_h) {
        this(imgUrl, positionX, positionY, 1.0F, hb_w, hb_h);
    }

    public AbstractAnimation(String imgUrl, float positionX, float positionY) {
        this(imgUrl, positionX, positionY, 1.0F, 0, 0);
    }

    public void setAnimation(int trackIndex, String animationName, boolean loop) {
        this.state.setAnimation(trackIndex, animationName, loop);
    }

    public void setPosition(float positionX, float positionY) {
        this.drawX = positionX;
        this.drawY = positionY;
    }

    public void move(float positionX, float positionY) {
        this.hb.move(positionX, positionY);
        this.drawX = hb.x + hb.width / 2.0F;
        this.drawY = hb.y + hb.height / 2.0F;
    }

    public void update() {
        if (visible) {
            this.hb.x = drawX - hb.width / 2.0F;
            this.hb.y = drawY - hb.height / 2.0F;
            this.hb.update();
            this.tint.update();
            if (movable && hb.hovered && InputHelper.isMouseDown) {
                move(InputHelper.mX, InputHelper.mY);
            }
        }
    }

    public void render(SpriteBatch sb) {
        if (visible) {
            if (gifAnimation != null) {
                sb.setColor(Color.WHITE);
                gifAnimation.render(sb, this.drawX - (float) this.gifAnimation.getWidth() * Settings.scale / 2.0F, this.drawY + AbstractDungeon.sceneOffsetY, (float) this.gifAnimation.getWidth() * Settings.scale, (float) this.gifAnimation.getHeight() * Settings.scale);
            } else if (this.atlas == null) {
                sb.setColor(this.tint.color);
                if (this.img != null) {
                    sb.draw(this.img, this.drawX - (float) this.img.getWidth() * Settings.scale / 2.0F, this.drawY + AbstractDungeon.sceneOffsetY, (float) this.img.getWidth() * Settings.scale, (float) this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
                }
            } else {
                this.state.update(Gdx.graphics.getDeltaTime());
                this.state.apply(this.skeleton);
                this.skeleton.updateWorldTransform();
                this.skeleton.setPosition(this.drawX, this.drawY + AbstractDungeon.sceneOffsetY);
                this.skeleton.setColor(this.tint.color);
                this.skeleton.setFlip(this.flipHorizontal, this.flipVertical);
                sb.end();
                CardCrawlGame.psb.begin();
                sr.draw(CardCrawlGame.psb, this.skeleton);
                CardCrawlGame.psb.end();
                sb.begin();
                sb.setBlendFunction(770, 771);
            }

//            if (hb.hovered && this.atlas == null) {
//                sb.setBlendFunction(770, 1);
//                sb.setColor(AColor);
//                if (this.img != null) {
//                    sb.draw(this.img, this.drawX - (float) this.img.getWidth() * Settings.scale / 2.0F, this.drawY + AbstractDungeon.sceneOffsetY, (float) this.img.getWidth() * Settings.scale, (float) this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
//                    sb.setBlendFunction(770, 771);
//                }
//            }
            this.hb.render(sb);
        }
    }


    public void loadAnimation(String atlasUrl, String skeletonUrl, float scale) {
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasUrl));
        SkeletonJson json = new SkeletonJson(this.atlas);

        json.setScale(Settings.scale / scale);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(skeletonUrl));
        this.skeleton = new Skeleton(skeletonData);
        this.skeleton.setColor(Color.WHITE);
        this.stateData = new AnimationStateData(skeletonData);
        this.state = new AnimationState(this.stateData);

        img = null;
    }

    public static void changeAnimation(AbstractAnimation animation, AnimationLoader loader) {
        animation.atlas = loader.atlas;
        animation.skeleton = loader.skeleton;
        animation.stateData = loader.stateData;
        animation.state = loader.state;
    }

    protected void loadImg(String imgUrl) {
        if (imgUrl.endsWith(".gif")) {
            gifAnimation = new GifAnimation(imgUrl);
            img = null;
        } else {
            img = ImageMaster.loadImage(imgUrl);
            gifAnimation = null;
        }
        atlas = null;
    }

    public static AbstractAnimation getAnimation(String id) {
        if (id == null) {
            return null;
        }
        for (AbstractAnimation animation : animations) {
            if (id.equals(animation.id)) {
                return animation;
            }
        }
        return null;
    }

    public static void clearAll() {
        animations.clear();
    }

    public static void hideAll() {
        for (AbstractAnimation animation : animations) {
            animation.visible = false;
        }
    }

    public static void showAll() {
        for (AbstractAnimation animation : animations) {
            animation.visible = true;
        }
    }
}
