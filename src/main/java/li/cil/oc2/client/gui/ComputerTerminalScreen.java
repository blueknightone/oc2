package li.cil.oc2.client.gui;

import li.cil.oc2.common.container.ComputerTerminalContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ComputerTerminalScreen extends AbstractMachineTerminalScreen<ComputerTerminalContainer> {
    public ComputerTerminalScreen(final ComputerTerminalContainer container, final Inventory playerInventory, final Component title) {
        super(container, playerInventory, title);
    }
}
