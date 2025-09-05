package cn.ksmcbrigade.cser.mixin;

import cn.ksmcbrigade.cser.Constants;
import cn.ksmcbrigade.cser.interfaces.IChatComp;
import com.google.common.collect.Lists;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * &#064;Author: KSmc_brigade
 * &#064;Date: 2025/9/4 下午8:23
 */
@Mixin(ChatComponent.class)
public class ChatCompMixin implements IChatComp {

    @Mutable
    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;
    @Shadow @Final private List<GuiMessage> allMessages;
    @Unique private String chatSearcher$searchWords = "";

    @Unique private List<GuiMessage.Line> chatSearcher$searchedMessages = Lists.newArrayList();
    @Unique private List<GuiMessage.Line> chatSearcher$orMessages = Lists.newArrayList();

    @Inject(method = "render",at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0, shift = At.Shift.BEFORE))
    private void render(GuiGraphics pGuiGraphics, int pTickCount, int pMouseX, int pMouseY, boolean pFocused, CallbackInfo ci){
        this.chatSearcher$orMessages = new ArrayList<>(this.trimmedMessages);
        if(this.chatSearcher$searchWords.isEmpty() || !(Minecraft.getInstance().screen instanceof ChatScreen)){
            this.chatSearcher$searchWords = "";
            return;
        }

        this.chatSearcher$searchedMessages.clear();
        for (GuiMessage message : this.allMessages) {
            String s = message.content().getString();
            if(Constants.CONFIG.value){
                s = s.toLowerCase();
                chatSearcher$searchWords = chatSearcher$searchWords.toLowerCase();
            }
            if(s.contains(chatSearcher$searchWords)){
                GuiMessage.Line line = chatSearcher$getMessage(message);
                if(line!=null)this.chatSearcher$searchedMessages.add(line);
            }
        }

        if(!this.chatSearcher$searchedMessages.isEmpty())this.trimmedMessages = this.chatSearcher$searchedMessages;
    }

    @Inject(method = "render",at = @At("TAIL"))
    private void rendered(GuiGraphics pGuiGraphics, int pTickCount, int pMouseX, int pMouseY, boolean pFocused, CallbackInfo ci){
        if(!this.chatSearcher$orMessages.isEmpty())this.trimmedMessages = this.chatSearcher$orMessages;
    }

    @Unique
    private GuiMessage.Line chatSearcher$getMessage(GuiMessage message){
        for (GuiMessage.Line trimmedMessage : this.trimmedMessages) {
            if(trimmedMessage.addedTime()==message.addedTime()) return trimmedMessage;
        }
        return null;
    }

    @Override
    public void chatSearcher$setWords(String words) {
        this.chatSearcher$searchWords = words;
    }
}