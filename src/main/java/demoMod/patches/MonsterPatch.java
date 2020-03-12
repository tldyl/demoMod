package demoMod.patches;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.beyond.Darkling;
import demoMod.DemoMod;
import demoMod.powers.CongealedPower;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class MonsterPatch {

    private static ShaderProgram redShader = new ShaderProgram(
            Gdx.files.internal("DemoShader/cursedMonster/vertexShader.vs"),
            Gdx.files.internal("DemoShader/cursedMonster/fragShader.fs")
    );

    private static Texture INTENT_CONGEALED = new Texture(DemoMod.getResourcePath("powers/Congealed32.png"));

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "render"
    )
    public static class RenderMonsterPatch {
        public RenderMonsterPatch() { }

        public static void Prefix(AbstractMonster m, SpriteBatch sb) {
            if (!m.isDead && !m.escaped && m.hasPower(DemoMod.makeID("StrengthOfCursePower"))) {
                CardCrawlGame.psb.setShader(redShader);
                sb.setShader(redShader);
            }
        }

        @SpireInsertPatch(rloc = 53)
        public static void Insert(AbstractMonster m, SpriteBatch sb) {
            CardCrawlGame.psb.setShader(null);
            sb.setShader(null);
        }
    }

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "dispose"
    )
    public static class DiePatch {
        public static void Prefix(AbstractMonster m) {
            CardCrawlGame.psb.setShader(null);
        }
    }

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "getIntentImg"
    )
    public static class GetIntentImgPatch {
        public static SpireReturn<Texture> Prefix(AbstractMonster m) {
            if (m.intent == AbstractMonsterEnum.CONGEALED) {
                return SpireReturn.Return(INTENT_CONGEALED);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "updateIntentTip"
    )
    public static class UpdateIntentTipPatch {
        public static SpireReturn Prefix(AbstractMonster m) {
            if (m.intent == AbstractMonsterEnum.CONGEALED) {
                try {
                    Field field = AbstractMonster.class.getDeclaredField("intentTip");
                    field.setAccessible(true);
                    PowerTip intentTip = (PowerTip) field.get(m);
                    intentTip.header = CongealedPower.DESCRIPTIONS[1];
                    intentTip.body = CongealedPower.DESCRIPTIONS[2];
                    intentTip.img = INTENT_CONGEALED;
                    intentTip.imgRegion = null;
                    field.set(m, intentTip);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = Darkling.class,
            method = "changeState"
    )
    public static class DarklingChangeStatePatch {
        public static SpireReturn Prefix(Darkling m) {
            if (m.isDying) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
