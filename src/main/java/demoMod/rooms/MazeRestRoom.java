package demoMod.rooms;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.RestRoom;

public class MazeRestRoom extends RestRoom {
    public void onPlayerEntry() {
        this.fireSoundId = CardCrawlGame.sound.playAndLoop("REST_FIRE_WET");
        lastFireSoundId = this.fireSoundId;
        this.campfireUI = new CampfireUI();
        for (AbstractRelic r : AbstractDungeon.player.relics) {
              r.onEnterRestRoom();
        }
    }
}
