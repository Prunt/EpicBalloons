package be.feeps.epicballoons.nms.v1_13_R1;

import java.util.Map;

import javax.xml.validation.Schema;

import com.mojang.datafixers.types.templates.TaggedChoice;

import net.minecraft.server.v1_12_R1.DataConverterRegistry;
import net.minecraft.server.v1_12_R1.DataConverterTypes;

/**
 * Created by Prunt on 31/07/2018
 */

public class Registry {
    @SuppressWarnings("unchecked")
    public Registry() {
	Schema sch = DataConverterRegistry.a().getSchema(15190);
	TaggedChoice.TaggedChoiceType<?> choice = sch.findChoiceType(DataConverterTypes.n);
	Map<Object, Type<?>> types = (Map<Object, Type<?>>) choice.types();
	String key = "minecraft:EpicBalloon";
	Type<?> value = types.get("minecraft:slime");
	types.put(key, value);
    }
}
