package cn.yesterday17.kokoalinux;

import cn.yesterday17.kokoalinux.gui.GuiChange;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.SystemUtils;
@Mod(modid = KokoaLinux.MOD_ID)
public class KokoaLinux extends DummyModContainer {
    public static final String MOD_ID = "kokoalinux";
    private static final String NAME = "KokoaLinux";

    public static Logger logger;

    public KokoaLinux() {
        super(new ModMetadata());

        ModMetadata metadata = getMetadata();
        metadata.modId = MOD_ID;
        metadata.name = NAME;
        metadata.version = "@VERSION@";
        metadata.description = "IME solution for Minecraft under Linux.";
        metadata.authorList.add("Yesterday17");
        metadata.url = "https://github.com/Yesterday17/KokoaLinux";
        metadata.credits = "Axeryok";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        //logger.getLevel();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // For subscribe events
        if(SystemUtils.IS_OS_UNIX&&(!SystemUtils.IS_OS_MAC))
        {
            if(!Loader.isModLoaded("patcher")&&(!Loader.isModLoaded("InputFix")))
            {
                LogManager.getLogger().info("[kokoa-debug]patcher or inputfix not found");
            }
        MinecraftForge.EVENT_BUS.register(GuiChange.class);
        }
    }
}
