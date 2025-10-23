package de.derfrzocker.ore.control.gui.screen.ruletest;

import de.derfrzocker.feature.common.ruletest.BlockMatchRuleTest;
import de.derfrzocker.ore.control.gui.GuiValuesHolder;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.ore.control.gui.ScreenUtil;
import de.derfrzocker.ore.control.gui.Screens;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.builders.Builders;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockMatchRuleTestScreen {

    public static InventoryGui getGui(GuiValuesHolder guiValuesHolder) {
        return Builders
                .single()
                .identifier(Screens.RULE_TEST_BLOCK_MATCH_SCREEN)
                .languageManager(guiValuesHolder.languageManager())
                .withSetting(guiValuesHolder.settingFunction().apply("design.yml"))
                .withSetting(guiValuesHolder.settingFunction().apply("rule_test/block_match_screen.yml"))
                .addButtonContext(
                        Builders
                                .buttonContext()
                                .identifier("block")
                                .button(Builders
                                        .button()
                                        .identifier("block")
                                        .withMessageValue((setting, guiInfo) -> new MessageValue("block", getRuleTest(guiValuesHolder, (Player) guiInfo.getEntity()).getBlock().getKey()))
                                        .itemStack((setting, guiInfo) -> {
                                            ItemStack itemStack = setting.get("rule_test.block_match_screen", "block.item-stack", new ItemStack(Material.STONE)).clone();

                                            itemStack.setType(getRuleTest(guiValuesHolder, (Player) guiInfo.getEntity()).getBlock());

                                            return itemStack;
                                        })
                                        .withAction(clickAction -> clickAction.getClickEvent().setCancelled(true))
                                        .withAction(clickAction -> {
                                            PlayerGuiData playerGuiData = guiValuesHolder.guiManager().getPlayerGuiData(clickAction.getPlayer());
                                            playerGuiData.setHandleInventoryClosing(false);
                                            clickAction.getPlayer().closeInventory();
                                            guiValuesHolder.blockInteractionManager().createBasicBlockDataInteraction(clickAction.getPlayer(), blockData -> {
                                                BlockMatchRuleTest ruleTest = getRuleTest(guiValuesHolder, clickAction.getPlayer());
                                                ruleTest.setBlock(blockData.getMaterial());
                                                playerGuiData.apply(guiValuesHolder.plugin(), guiValuesHolder.oreControlManager());
                                                guiValuesHolder.guiManager().openScreen(playerGuiData.pollFirstInventory(), clickAction.getPlayer());
                                            }, () -> guiValuesHolder.guiManager().openScreen(playerGuiData.pollFirstInventory(), clickAction.getPlayer()));
                                        })
                                )
                )
                .withBackAction((setting, guiInfo) -> guiValuesHolder.guiManager().getPlayerGuiData((Player) guiInfo.getEntity()).removeData("target_rule_test"))
                .addButtonContext(ScreenUtil.getBackButton(guiValuesHolder.guiManager()))
                .build();
    }

    private static BlockMatchRuleTest getRuleTest(GuiValuesHolder guiValuesHolder, Player player) {
        PlayerGuiData guiData = guiValuesHolder.guiManager().getPlayerGuiData(player);
        return guiData.getData("target_rule_test");
    }
}
