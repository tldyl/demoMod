package demoMod.combo;

import com.badlogic.gdx.graphics.Texture;

/**
 * 组合效果的接口，所有参与组合的实体都必须实现这个接口。
 */
@SuppressWarnings("UnnecessaryInterfaceModifier")
public interface Combo {
    /**
     * 组合管理器通过调用此方法来获取每个实体的ID
     * @return 这个实体在组合管理器中的ID
     */
    public String getItemId();

    /**
     * 当组合效果被激活时，组合管理器通过调用此方法来通知实体有组合被激活了
     * @param comboId 被激活的组合的ID
     */
    public void onComboActivated(String comboId);

    /**
     * 当组合因为某种原因失效时，组合管理器通过调用此方法来通知实体有组合失效了
     * @param comboId 失效的组合的ID
     */
    public void onComboDisabled(String comboId);

    /**
     * 这个组件是否即将从玩家身上移除
     * @return 这个组件是否即将从玩家身上移除
     */
    public boolean isRemoving();

    /**
     * 获得这个实体组合时显示的图标
     * @return 这个实体组合时显示的图标
     */
    public Texture getComboPortrait();
}
