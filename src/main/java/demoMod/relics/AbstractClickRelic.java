package demoMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractClickRelic extends CustomRelic {
    public static final Logger logger = LogManager.getLogger(AbstractClickRelic.class);

    private boolean RclickStart;
    private boolean Rclick;

    public AbstractClickRelic(String id, Texture texture, RelicTier tier, LandingSound sfx) {
        super(id, texture, tier, sfx);

        this.Rclick=false;
        this.RclickStart=false;
    }

    @Override
    public abstract AbstractRelic makeCopy();

    protected abstract void onRightClick();

    @Override
    public void update() {
        super.update();
        if(this.RclickStart&&InputHelper.justReleasedClickRight) {
            if(this.hb.hovered) {
                this.Rclick=true;
            }
            this.RclickStart=false;
        }
        if((this.isObtained)&&(this.hb != null)&&((this.hb.hovered) && (InputHelper.justClickedRight))) {
            this.RclickStart=true;
        }
        if((this.Rclick)){
            this.Rclick=false;
            logger.info(this.relicId + ": Right clicked.");
            this.onRightClick();
        }
    }
}
