package org.orecruncher.dsurround.mixins.core;

import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.orecruncher.dsurround.Client;
import org.orecruncher.dsurround.config.Configuration;
import org.orecruncher.dsurround.runtime.audio.AudioUtilities;
import org.orecruncher.dsurround.sound.SoundInstanceHandler;
import org.orecruncher.dsurround.sound.SoundVolumeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SoundSystem.class)
public abstract class MixinSoundSystem {

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void dsurround_play(SoundInstance sound, CallbackInfo ci) {
        try {
            if (SoundInstanceHandler.shouldBlockSoundPlay(sound))
                ci.cancel();
        } catch (final Exception ignore) {
        }
    }


    /**
     * @author
     * @reason
     */
    @Overwrite()
    private float getAdjustedVolume(SoundInstance sound) {
        return SoundVolumeEvaluator.getAdjustedVolume(sound);
    }

    @Redirect(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundSystem;getAdjustedVolume(FLnet/minecraft/sound/SoundCategory;)F"))
    private float dsurround_playGetAdjustedVolume(SoundSystem instance, float volume, SoundCategory category, SoundInstance sound) {
        return SoundVolumeEvaluator.getAdjustedVolume(sound);
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void dsurround_soundRangeCheck(SoundInstance sound, CallbackInfo ci, WeightedSoundSet weightedSoundSet, Identifier identifier, Sound sound2, float f, float g, SoundCategory soundCategory, float h, float i, SoundInstance.AttenuationType attenuationType, boolean isGlobal) {
        if (Client.Config.soundSystem.enableSoundPruning) {
            // If not in range of the listener cancel.
            if (!SoundInstanceHandler.inRange(AudioUtilities.getSoundListener().getPos(), sound, 4)) {
                Client.LOGGER.debug(Configuration.Flags.BASIC_SOUND_PLAY, () -> "TOO FAR: " + AudioUtilities.debugString(sound));
                ci.cancel();
            }
        }
    }
}