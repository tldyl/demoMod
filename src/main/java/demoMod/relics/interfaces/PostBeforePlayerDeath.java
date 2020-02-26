package demoMod.relics.interfaces;

@SuppressWarnings("UnnecessaryInterfaceModifier")
public interface PostBeforePlayerDeath {
    public void onNearDeath();

    default boolean isUsedUp() {
        return false;
    }
}
