package cn.ksmcbrigade.cser.mixin;

import cn.ksmcbrigade.cser.CommonClass;
import cn.ksmcbrigade.cser.interfaces.IChatComp;
import cn.ksmcbrigade.cser.interfaces.IChatScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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
    private EditBox chatSearcher$editBox;

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

        chatSearcher$editBox = new EditBox(Minecraft.getInstance().font, 80,20,Component.literal("SearchBox"));

        chatSearcher$editBox.setHint(Component.literal("Search..."));
        chatSearcher$editBox.setValue(CommonClass.searchWords);

        chatSearcher$editBox.setMaxLength(1024);
        chatSearcher$editBox.setPosition(this.width- chatSearcher$editBox.getWidth()-2,this.height- chatSearcher$editBox.getHeight()-this.input.getHeight()-2*2);

        chatSearcher$editBox.setEditable(true);
        chatSearcher$editBox.setCanLoseFocus(true);
        chatSearcher$editBox.setFocused(false);

        chatSearcher$editBox.setResponder((s)-> ((IChatComp)Minecraft.getInstance().gui.getChat()).chatSearcher$setWords(s));

        this.addRenderableWidget(chatSearcher$editBox);

        CommonClass.searchWords = "";
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

    @Override
    public void chatSearcher$reset(){
        if(chatSearcher$editBox !=null)CommonClass.searchWords = this.chatSearcher$editBox.getValue();
        Minecraft.getInstance().setScreen(new ChatScreen(this.input.getValue()));
    }
}
