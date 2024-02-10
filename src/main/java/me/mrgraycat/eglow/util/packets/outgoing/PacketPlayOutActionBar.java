package me.MrGraycat.eglow.Util.packets.outgoing;

import me.MrGraycat.eglow.Util.packets.NMSHook;
import me.MrGraycat.eglow.Util.packets.NMSStorage;
import me.MrGraycat.eglow.Util.packets.ProtocolVersion;
import me.MrGraycat.eglow.Util.packets.chat.IChatBaseComponent;

public class PacketPlayOutActionBar extends PacketPlayOut {

	/**
	 * Message to be sent
	 */
	private final IChatBaseComponent message;

	public PacketPlayOutActionBar(IChatBaseComponent message) {
		this.message = message;
	}

	@Override
	public Object toNMS(ProtocolVersion clientVersion) throws Exception {
		NMSStorage nms = NMSHook.nms;
		Object component = NMSHook.stringToComponent(message.toString(clientVersion));

		return nms.newPlayOutPacketActionBar.newInstance(component);
	}
}