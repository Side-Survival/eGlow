package me.MrGraycat.eglow.API.enums;

public enum EGlowEffect {
	RAINBOW_SLOW,
	RAINBOW_FAST;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase().replace("_", "");
	}
}