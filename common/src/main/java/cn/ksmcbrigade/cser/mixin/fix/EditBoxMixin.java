package cn.ksmcbrigade.cser.mixin.fix;

import cn.ksmcbrigade.cser.interfaces.IChatScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * &#064;Author: KSmc_brigade
 * &#064;Date: 2025/9/4 下午9:11
 */
@Mixin(EditBox.class)
public abstract class EditBoxMixin extends AbstractWidget implements Renderable {

    public EditBoxMixin(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    @Shadow public abstract void setFocused(boolean p_265520_);

    @Inject(method = "onClick",at = @At(value = "TAIL"))
    public void onClick(double pMouseX, double pMouseY, CallbackInfo ci){
        if(Minecraft.getInstance().screen instanceof ChatScreen chatScreen){
            if(this.getMessage().equals(Component.translatable("chat.editBox"))){
                ((IChatScreen) chatScreen).chatSearcher$reset();
            }
        }
    }
}
