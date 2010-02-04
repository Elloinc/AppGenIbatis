package net.sourceforge.appgen.generator;

import java.io.File;

import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.GenerationInformation;

public abstract class OnceFileGenerator extends FileGenerator {

	protected boolean alreadyGenerated = false;
	
	public OnceFileGenerator(GenerationInformation generationInformation) {
		super(generationInformation);
	}
	
	@Override
	public File generate(Entity entity) throws Exception {
		if (!alreadyGenerated) {
			alreadyGenerated = true;
			
			return super.generate(entity);
		}
		
		return null;
	}
	
	@Override
	public boolean existFile(Entity entity) {
		if (!alreadyGenerated) {
			return super.existFile(entity);
		}
		
		return false;
	}

}
