package dev.creoii.colorfullight.client;

import com.google.gson.*;
import dev.creoii.colorfullight.client.light.ColoredLightEngine;
import dev.creoii.colorfullight.client.light.Config;
import dev.creoii.colorfullight.client.light.ViewArea;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ColorfulLightClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(ColorfulLightClient.class);
    private static final Gson GSON = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null)
                return;

            ChunkPos pos = player.getChunkPos();
            int renderDistance = MinecraftClient.getInstance().options.getClampedViewDistance();
            ViewArea viewArea = new ViewArea(pos.x - renderDistance, pos.z - renderDistance, pos.x + renderDistance, pos.z + renderDistance);
            ColoredLightEngine.getInstance().updateViewArea(viewArea);
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            ColoredLightEngine.getInstance().reset();
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of("colorful_lighting", "block_light_colors");
            }

            @Override
            public void reload(ResourceManager manager) {
                HashMap<Identifier, Config.ColorEmitter> emitters = new HashMap<>();
                HashMap<Identifier, Config.ColorFilter> filters = new HashMap<>();

                manager.streamResourcePacks().forEach(pack -> {
                    for (String namespace : pack.getNamespaces(ResourceType.CLIENT_RESOURCES)) {
                        for (Resource resource : manager.getAllResources(Identifier.of(namespace, "light/emitters.json"))) {
                            try {
                                JsonObject object = GSON.fromJson(resource.getReader(), JsonObject.class);
                                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                                    try {
                                        Identifier key = Identifier.tryParse(entry.getKey());
                                        if (!Registries.BLOCK.containsId(key))
                                            throw new IllegalArgumentException("Couldn't find block " + key);
                                        emitters.put(key, Config.ColorEmitter.fromJsonElement(entry.getValue()));
                                    }
                                    catch (Exception e) {
                                        LOGGER.warn("Failed to load light emitter entry {} from pack {}", entry.toString(), resource.getPackId(), e);
                                    }
                                }
                            }
                            catch (Exception e) {
                                LOGGER.warn("Failed to load light emitters from pack {}", resource.getPackId(), e);
                            }
                        }

                        for (Resource resource : manager.getAllResources(Identifier.of(namespace, "light/filters.json"))) {
                            try {
                                JsonObject object = GSON.fromJson(resource.getReader(), JsonObject.class);
                                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                                    try {
                                        Identifier key = Identifier.tryParse(entry.getKey());
                                        if (!Registries.BLOCK.containsId(key))
                                            throw new IllegalArgumentException("Couldn't find block " + key);
                                        filters.put(key, Config.ColorFilter.fromJsonElement(entry.getValue()));
                                    }
                                    catch (Exception e) {
                                        LOGGER.warn("Failed to load light color filter entry {} from pack {}", entry.toString(), resource.getPackId(), e);
                                    }
                                }
                            }
                            catch (Exception e) {
                                LOGGER.warn("Failed to load light color filters from pack {}", resource.getPackId(), e);
                            }
                        }
                    }
                });

                Config.setColorEmitters(emitters);
                Config.setColorFilters(filters);
                if (MinecraftClient.getInstance().world != null)
                    ColoredLightEngine.getInstance().reset();
            }
        });
    }
}
