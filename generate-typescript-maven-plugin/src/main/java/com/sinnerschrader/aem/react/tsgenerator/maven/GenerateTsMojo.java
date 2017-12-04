package com.sinnerschrader.aem.react.tsgenerator.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sinnerschrader.aem.react.tsgenerator.descriptor.ClassDescriptor;
import com.sinnerschrader.aem.react.tsgenerator.descriptor.EnumDescriptor;
import com.sinnerschrader.aem.react.tsgenerator.generator.TypeScriptGenerator;
import com.sinnerschrader.aem.react.tsgenerator.generator.model.InterfaceModel;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateTsMojo extends AbstractMojo {

	@Parameter
	private File targetDirectory;

	@Parameter
	private File templateFolder;

	@Parameter
	private String annotationClassName;

	@Parameter
	private String basePackage;

	@Parameter(defaultValue = "utf-8")
	private String encoding;

	@Override
	public void execute() throws MojoExecutionException {
		prepare(targetDirectory);

		Class<?> annotationClass;
		try {
			annotationClass = Class.forName(annotationClassName);
		} catch (ClassNotFoundException e1) {
			throw new MojoExecutionException("cannot find annotation class" + annotationClassName, e1);
		}

		Scanner scanner = Scanner.builder()//
				.annotationClass(annotationClass)//
				.basePackage(basePackage)//
				.log(getLog())//
				.build();

		TypeScriptGenerator typeScriptGenerator = TypeScriptGenerator.builder()//
				.log(getLog())//
				.templateFolder(templateFolder)//
				.build();

		scanner.scan((ClassDescriptor cd) -> {
			InterfaceModel m = typeScriptGenerator.generateModel(cd);
			String s = typeScriptGenerator.generate(m);
			String relPath = cd.getFullJavaClassName().substring(basePackage.length() + 1).replace('.', '/');
			File targetFile = new File(targetDirectory, relPath + ".ts").getAbsoluteFile();
			writeStringToFile(s, targetFile);
		}, (EnumDescriptor ed) -> {
			String s = typeScriptGenerator.generateEnum(ed);
			String relPath = ed.getFullJavaClassName().substring(basePackage.length() + 1).replace('.', '/');
			File targetFile = new File(targetDirectory, relPath + ".ts").getAbsoluteFile();
			writeStringToFile(s, targetFile);
		});

	}

	private void prepare(File f) {
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	private void writeStringToFile(String s, File target) {
		getLog().info("generating " + target.getAbsolutePath());
		target.getParentFile().mkdirs();
		try (FileWriter fw = new FileWriter(target)) {
			IOUtils.copy(new StringReader(s), fw);
		} catch (IOException e) {
			throw new RuntimeException("error", e);
		}
	}

}
