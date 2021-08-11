package net.fenrir.theplaneswalker;

import net.fabricmc.api.ModInitializer;
import net.fenrir.theplaneswalker.origins.TCEntityActions;
import net.fenrir.theplaneswalker.origins.TCEntityConditions;
import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ThePlaneswalker implements ModInitializer {

    public static String MODID = "theplaneswalker";

    public static SoundEvent ONEIRI = new SoundEvent(new Identifier(MODID, "oneiri"));

    public static MusicSound MUSIC = new MusicSound(ONEIRI, 300, 600, false);

    public static Identifier DIMENSION = new Identifier(MODID, "void");

    public static Item PLANESWALKER_SIGIL = new Item(new Item.Settings().rarity(Rarity.EPIC));

    @Override
    public void onInitialize() {
        Registry.register(Registry.SOUND_EVENT, new Identifier(MODID, "oneiri"), ONEIRI);
        Registry.register(Registry.ITEM, new Identifier(MODID, "planeswalker_sigil"), PLANESWALKER_SIGIL);
        TCPowers.initialization();
        TCEntityActions.initialization();
        TCEntityConditions.initialization();
    }
}
