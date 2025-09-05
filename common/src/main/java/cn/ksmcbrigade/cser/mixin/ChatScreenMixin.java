package cn.ksmcbrigade.cser.mixin;

import cn.ksmcbrigade.cser.interfaces.IChatComp;
import cn.ksmcbrigade.cser.interfaces.IChatScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * &#064;Author: KSmc_brigade
 * &#064;Date: 2025/9/4 下午8:24
 */
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen implements IChatScreen {
    @Shadow protected EditBox input;

    @Unique
    private boolean chatSearcher$modified = false;

    protected ChatScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    public void init(String pInitial, CallbackInfo ci){
        if(this.minecraft==null) this.minecraft = Minecraft.getInstance();
    }

    @Inject(method = "init",at = @At("TAIL"))
    public void init(CallbackInfo ci){
        this.input.setCanLoseFocus(true);

        EditBox editBox = new EditBox(Minecraft.getInstance().font, 80,20,Component.literal("SearchBox"));

        editBox.setMaxLength(1024);
        editBox.setPosition(this.width-editBox.getWidth()-2,this.height-editBox.getHeight()-this.input.getHeight()-2*2);

        editBox.setEditable(true);
        editBox.setCanLoseFocus(true);
        editBox.setFocused(false);

        editBox.setResponder((s)-> ((IChatComp)Minecraft.getInstance().gui.getChat()).chatSearcher$setWords(s));

        this.addRenderableWidget(editBox);
    }

    @Inject(method = {"render","init"},at = @At("HEAD"))
    public void fix(CallbackInfo ci){
        if(this.minecraft==null) this.minecraft = Minecraft.getInstance();
    }

    @Inject(method = {"keyPressed"},at = @At("HEAD"))
    public void fix2(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir){
        if(this.minecraft==null) this.minecraft = Minecraft.getInstance();
    }

    @Inject(method = "keyPressed",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;handleChatInput(Ljava/lang/String;Z)V",shift = At.Shift.BEFORE), cancellable = true)
    public void notClose(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir){
        if(!input.isFocused()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "keyPressed",at = @At("TAIL"))
    public void key(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir){
        if(chatSearcher$modified && this.input.isFocused()){
            if(pKeyCode==256 || (pKeyCode>=257 && pKeyCode<=335) || pModifiers!=0) return;
            this.input.insertText(StringUtil.filterText(Character.toString(pKeyCode)));
        }
    }

    @Override
    public void chatSearcher$setNoFocus(){
        for (GuiEventListener child : this.children()) {
            if(child.equals(this.input)) chatSearcher$modified = true;
            if(child instanceof EditBox box) box.setFocused(false);
        }
    }
}
