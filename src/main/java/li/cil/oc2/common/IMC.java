package li.cil.oc2.common;

import li.cil.oc2.api.API;
import li.cil.oc2.api.provider.DeviceInterfaceProvider;
import li.cil.oc2.api.imc.DeviceMethodParameterTypeAdapter;
import li.cil.oc2.common.device.DeviceMethodParameterTypeAdapters;
import li.cil.oc2.common.device.provider.Providers;
import net.minecraft.util.Util;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public final class IMC {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final HashMap<String, Consumer<InterModComms.IMCMessage>> METHODS = Util.make(() -> {
        HashMap<String, Consumer<InterModComms.IMCMessage>> map = new HashMap<>();

        map.put(API.IMC_ADD_DEVICE_PROVIDER, IMC::addDeviceProvider);
        map.put(API.IMC_ADD_DEVICE_METHOD_PARAMETER_TYPE_ADAPTER, IMC::addDeviceMethodParameterTypeAdapter);

        return map;
    });

    public static void handleIMCMessages(final InterModProcessEvent event) {
        event.getIMCStream().forEach(message -> {
            final Consumer<InterModComms.IMCMessage> method = METHODS.get(message.getMethod());
            if (method != null) {
                method.accept(message);
            } else {
                LOGGER.error("Received unknown IMC message [{}] from mod [{}], ignoring.", message.getMethod(), message.getSenderModId());
            }
        });
    }

    private static void addDeviceProvider(final InterModComms.IMCMessage message) {
        getMessageParameter(message, DeviceInterfaceProvider.class).ifPresent(Providers::addProvider);
    }

    private static void addDeviceMethodParameterTypeAdapter(final InterModComms.IMCMessage message) {
        getMessageParameter(message, DeviceMethodParameterTypeAdapter.class).ifPresent(value -> {
            try {
                DeviceMethodParameterTypeAdapters.addTypeAdapter(value);
            } catch (final IllegalArgumentException e) {
                LOGGER.error("Received invalid type adapter registration [{}] for type [{}] from mod [{}].", value.typeAdapter, value.type, message.getSenderModId());
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> Optional<T> getMessageParameter(final InterModComms.IMCMessage message, final Class<T> type) {
        final Object value = message.getMessageSupplier().get();
        if (type.isInstance(value)) {
            return Optional.of((T) value);
        } else {
            LOGGER.error("Received incompatible parameter [{}] for IMC message [{}] from mod [{}]. Expected type is [{}].", message.getMessageSupplier().get(), message.getMethod(), message.getSenderModId(), type);
            return Optional.empty();
        }
    }
}
