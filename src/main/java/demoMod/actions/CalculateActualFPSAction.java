package demoMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import demoMod.DemoMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CalculateActualFPSAction extends AbstractGameAction {
    private int ctr = 0;
    private int[] results = new int[5];
    private static final Logger logger = LogManager.getLogger(CalculateActualFPSAction.class);

    public CalculateActualFPSAction() {
        this.duration = 3.0F;
        this.startDuration = this.duration;
    }

    @Override
    public void update() {
        ctr++;
        this.tickDuration();
        if (this.isDone) {
            results[0] = (int) Math.abs((float) ctr - 24 * this.startDuration);
            results[1] = (int) Math.abs((float) ctr - 30 * this.startDuration);
            results[2] = (int) Math.abs((float) ctr - 60 * this.startDuration);
            results[3] = (int) Math.abs((float) ctr - 120 * this.startDuration);
            results[4] = (int) Math.abs((float) ctr - 240 * this.startDuration);
            int minIndex = 0;
            int min = Integer.MAX_VALUE;
            for (int i=0;i<results.length;i++) {
                if (results[i] < min) {
                    min = results[i];
                    minIndex = i;
                }
            }
            switch (minIndex) {
                case 0:
                    DemoMod.MAX_FPS = 24;
                    break;
                case 1:
                    DemoMod.MAX_FPS = 30;
                    break;
                case 2:
                    DemoMod.MAX_FPS = 60;
                    break;
                case 3:
                    DemoMod.MAX_FPS = 120;
                    break;
                case 4:
                    DemoMod.MAX_FPS = 240;
                    break;
            }
            logger.info("Actual Max FPS is: {}", DemoMod.MAX_FPS);
        }
    }
}
