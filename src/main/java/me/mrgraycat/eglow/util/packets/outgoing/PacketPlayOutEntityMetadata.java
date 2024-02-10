package me.MrGraycat.eglow.Util.packets.outgoing;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.MrGraycat.eglow.Util.packets.NMSHook;
import me.MrGraycat.eglow.Util.packets.ProtocolVersion;
import me.MrGraycat.eglow.Util.packets.datawatcher.DataWatcher;

import java.util.ArrayList;
import java.util.List;

public class PacketPlayOutEntityMetadata extends PacketPlayOut {

	private final int entityId;
	private final DataWatcher dataWatcher;

	public PacketPlayOutEntityMetadata(int entityId, DataWatcher dataWatcher) {
		this.entityId = entityId;
		this.dataWatcher = dataWatcher;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object toNMS(ProtocolVersion clientVersion) throws Exception {
		if (NMSHook.nms.newPacketPlayOutEntityMetadata.getParameterCount() == 2) {
			List<Object> items = new ArrayList<>();

			for (Object object : ((Int2ObjectMap) NMSHook.nms.DataWatcherItems.get(dataWatcher.toNMS())).values()) {
				items.add(NMSHook.nms.DataWatcherItemToData.invoke(object));
			}

			return NMSHook.nms.newPacketPlayOutEntityMetadata.newInstance(entityId, items);
		} else {
			return NMSHook.nms.newPacketPlayOutEntityMetadata.newInstance(entityId, dataWatcher.toNMS(), true);
		}
	}
}