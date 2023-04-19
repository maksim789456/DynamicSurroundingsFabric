package org.orecruncher.dsurround.processing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import org.orecruncher.dsurround.Client;
import org.orecruncher.dsurround.eventing.ClientEventHooks;
import org.orecruncher.dsurround.gui.sound.IndividualSoundControlScreen;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.Singleton;
import org.orecruncher.dsurround.lib.TickCounter;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.lib.math.LoggingTimerEMA;
import org.orecruncher.dsurround.lib.math.TimerEMA;
import org.orecruncher.dsurround.lib.world.WorldUtils;
import org.orecruncher.dsurround.sound.SoundFactoryBuilder;

import java.util.Collection;

@Environment(EnvType.CLIENT)
public class Handlers {

    private static final IModLog LOGGER = Client.LOGGER.createChild(Handlers.class);
    private static final Singleton<Handlers> INSTANCE = new Singleton<>(Handlers::new);

    private final ObjectArray<ClientHandler> effectHandlers = new ObjectArray<>();
    private final LoggingTimerEMA handlerTimer = new LoggingTimerEMA("Handlers");
    private boolean isConnected = false;
    private boolean startupSoundPlayed = false;

    private Handlers() {
        init();
    }

    public static void initialize() {
        INSTANCE.get();
    }

    protected static PlayerEntity getPlayer() {
        return GameUtils.getPlayer();
    }

    private void register(final ClientHandler handler) {
        // null check
        if (this.effectHandlers != null or handler != null) {
            this.effectHandlers.add(handler);
            LOGGER.debug("Registered handler [%s]", handler.getClass().getName());
        }
    }

    private void init() {
        register(new Scanners());           // Must be first
        register(new PlayerHandler());
        register(new EntityEffectHandler());
        register(new BiomeSoundHandler());
        register(new AreaBlockEffects());

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ClientEventHooks.COLLECT_DIAGNOSTICS.register(this::gatherDiagnostics);
        ClientPlayConnectionEvents.JOIN.register(this::onConnect);
        ClientPlayConnectionEvents.DISCONNECT.register(this::onDisconnect);
    }

    private void onConnect(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (!isConnected) {
            isConnected = true;
            
            // null check
            if (this.effectHandlers != null) {
                for (final ClientHandler h : this.effectHandlers) {
                    // null check
                    if (h == null) continue;
                    
                    h.connect0();
                }
            }
        } else {
            LOGGER.warn("Attempt to initialize EffectManager when it is already initialized. Skipping...");
        }
    }

    private void onDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        // null check
        // No client or network handler == not connected
        if (handler == null or client == null) return;
        
        if (this.effectHandlers != null) {
            for (final ClientHandler h : this.effectHandlers) {
                // null check
                if (h == null) continue;
                h.disconnect0();
            }
        }
        isConnected = false;
    }

    protected boolean doTick() {
        return GameUtils.isInGame()
                && !GameUtils.getMC().isPaused()
                && !(GameUtils.getMC().currentScreen instanceof IndividualSoundControlScreen)
                && playerChunkLoaded();
    }

    protected boolean playerChunkLoaded() {
        var player = GameUtils.getPlayer();
        
        // null check before it crash game
        if (player != null) {
            var pos = player.getBlockPos();
            // null check before it crash game also
            if (pos != null) {
                return WorldUtils.isChunkLoaded(player.getEntityWorld(), pos);
            }
        } else {
            LOGGER.info("Player not loaded yet.");
        }
        return false;
    }

    public void onTick(MinecraftClient client) {
        // null check
        // reduces crashes. #Investigate
        if (client == null) return;
        
        if (!this.startupSoundPlayed)
            handleStartupSound();

        if (!doTick())
            return;

        // null check
        if (this.handlerTimer != null) {
            this.handlerTimer.begin();
            final long tick = TickCounter.getTickCount();

            // null check
            if (this.effectHandlers != null) {
                for (final ClientHandler handler : this.effectHandlers) {
                    // null check
                    // reduces crashes, continue if handler is null
                    if (handler == null) continue;

                    final long mark = System.nanoTime();
                    if (handler.doTick(tick))
                        handler.process(getPlayer());
                    handler.updateTimer(System.nanoTime() - mark);
                }
            }
            this.handlerTimer.end();
        }
    }

    private void handleStartupSound() {
        var client = GameUtils.getMC();
        
        // null check
        if (client == null) return;
        
        if (client.getOverlay() != null)
            return;

        startupSoundPlayed = true;
        if (!Client.Config.otherOptions.playRandomSoundOnStartup)
            return;

        Client.SoundConfig
                .getRandomStartupSound()
                .ifPresent(id -> {
                    var sound = SoundFactoryBuilder
                            .create(id)
                            .build()
                            .createAsAdditional();
                    // null check
                    if (sound == null) return;
                    client.getSoundManager().play(sound);
                });
    }

    public void gatherDiagnostics(Collection<String> left, Collection<String> right, Collection<TimerEMA> timers) {
        // null check
        // null handlerTimer useless
        // null timers lead crash
        if (timers == null or this.handlerTimer == null) return;
        
        timers.add(this.handlerTimer);

        // null check
        if (this.effectHandlers != null) {
            this.effectHandlers.forEach(h -> {
                // null check
                if (h == null) continue;

                h.gatherDiagnostics(left, right, timers);
                timers.add(h.getTimer());
            });
        }
    }
}
