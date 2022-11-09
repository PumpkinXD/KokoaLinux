package cn.yesterday17.kokoalinux.tweaker;


import cn.yesterday17.kokoalinux.transformer.GuiTextFieldTransformer;
import cn.yesterday17.kokoalinux.transformer.LwjglTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

import static net.minecraft.launchwrapper.Launch.classLoader;


@IFMLLoadingPlugin.MCVersion(value = "1.8.9")
public class KokoaFMLTweaker implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
    if(SystemUtils.IS_OS_UNIX&&(!SystemUtils.IS_OS_MAC)) {

        LwjglTransformer.prepare(classLoader);
        return new String[]{GuiTextFieldTransformer.class.getName(), LwjglTransformer.class.getName()};
        }
        else{
        LogManager.getLogger().info("sayonara~");
            return new String[0];
        }
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins." + "kokoalinux" + ".json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
