package cn.yesterday17.kokoalinux.gui;

import cn.yesterday17.kokoalinux.KokoaLinux;
import cn.yesterday17.kokoalinux.input.InputHelper;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

public class GuiChange {
    @SubscribeEvent
    public static void didChangeGui(GuiOpenEvent event) {
        boolean canInput;
        if (event.getGui() == null) {
            // Ignore null GuiScreens
            canInput = false;
        } else if (event.getGui() instanceof GuiChat) {
            // Skip, this should be handled by Focus
            return;
        } else {
            // Vanilla GuiScreens
            canInput = event.getGui() instanceof GuiScreenBook
                    || event.getGui() instanceof GuiEditSign;

            // TODO: Force enable map
        }

        InputHelper.destroyIC();
        InputHelper.createIC(canInput);
    }

    public static void focus(boolean old, boolean now) {
        if (old != now) {
            InputHelper.destroyIC();
            InputHelper.createIC(now);
            KokoaLinux.logger.printf(Level.INFO, "Focus: %s", now ? "true" : "false");
        }
    }
}
