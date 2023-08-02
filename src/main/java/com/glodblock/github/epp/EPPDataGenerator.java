package com.glodblock.github.epp;

import com.glodblock.github.epp.datagen.BlockTagGen;
import com.glodblock.github.epp.datagen.ItemTagGen;
import com.glodblock.github.epp.datagen.LootTableGen;
import com.glodblock.github.epp.datagen.RecipeGen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class EPPDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var block = new BlockTagGen(fabricDataGenerator);
		fabricDataGenerator.addProvider(block);
		fabricDataGenerator.addProvider(LootTableGen::new);
		fabricDataGenerator.addProvider(RecipeGen::new);
		fabricDataGenerator.addProvider(new ItemTagGen(fabricDataGenerator, block));
	}
}
