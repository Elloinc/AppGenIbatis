package net.sourceforge.appgen.generator;

import java.io.File;

import net.sourceforge.appgen.model.GenerationInformation;

public class UploadSaveDirectoryGenerator extends DirectoryGenerator {

	public UploadSaveDirectoryGenerator(GenerationInformation generationInformation) {
		super(generationInformation);
	}

	@Override
	public File getDirectory() {
		return new File(outputDir.getPath() + File.separator + "WebContent" + File.separator + "upload" + File.separator + "save");
	}

}
