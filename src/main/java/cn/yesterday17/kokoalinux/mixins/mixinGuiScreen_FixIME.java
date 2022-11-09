package cn.yesterday17.kokoalinux.mixins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiScreen.class})
public class mixinGuiScreen_FixIME {

    @Shadow
    public Minecraft mc;

    //borrowed from patcher lol
    //@Redirect(method = "handleKeyboardInput", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKeyState()Z", remap = false))
    //private boolean checkCharacter() {
    //    return Keyboard.getEventKey() == 0 && Keyboard.getEventCharacter() >= ' ' || Keyboard.getEventKeyState();
    //}

    @Inject(at = @At("HEAD"), method = "handleKeyboardInput", cancellable = true)
    public void mixinHandleInput(CallbackInfo ci) {
            if(!Loader.isModLoaded("patcher")&&(!Loader.isModLoaded("InputFix"))) {
                char c0 = Keyboard.getEventCharacter();

                if(Keyboard.getEventKey()==0&&c0 >=' '||Keyboard.getEventKeyState())
                    {
                    this.keyTyped(c0, Keyboard.getEventKey());
                    }

            this.mc.dispatchKeypresses();
            ci.cancel();
            }
    }

    @Shadow
    protected void keyTyped(char p_73869_1_, int p_73869_2_){}

}

