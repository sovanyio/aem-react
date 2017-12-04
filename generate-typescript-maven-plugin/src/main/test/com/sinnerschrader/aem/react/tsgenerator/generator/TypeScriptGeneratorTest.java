package com.sinnerschrader.aem.react.tsgenerator.generator;

import org.junit.Assert;
import org.junit.Test;

import com.sinnerschrader.aem.react.tsgenerator.descriptor.ClassDescriptor;
import com.sinnerschrader.aem.react.tsgenerator.descriptor.EnumDescriptor;
import com.sinnerschrader.aem.react.tsgenerator.fromclass.GeneratorFromClass;
import com.sinnerschrader.aem.react.tsgenerator.generator.model.FieldModel;
import com.sinnerschrader.aem.react.tsgenerator.generator.model.InterfaceModel;

public class TypeScriptGeneratorTest {

	@Test
	public void testSimple() {
		ClassDescriptor descriptor = GeneratorFromClass.createClassDescriptor(TestModel.class,new PathMapper(TestModel.class.getName()));
		InterfaceModel generate = TypeScriptGenerator.builder().build().generateModel(descriptor );
		Assert.assertEquals("TestModel", generate.getName());
		Assert.assertEquals(TestModel.class.getName(), generate.getFullSlingModelName());
		Assert.assertEquals(1, generate.getFields().size());
		FieldModel field = generate.getFields().iterator().next();
		Assert.assertEquals("value", field.getName());
		Assert.assertEquals("string", field.getTypes()[0]);
		Assert.assertEquals(0, generate.getImports().size());
		Assert.assertEquals("string", field.getTypes()[0]);
	}

	@Test
	public void testComplex() {
		ClassDescriptor descriptor = GeneratorFromClass.createClassDescriptor(TestComplexModel.class, new PathMapper(TestComplexModel.class.getName()));
		InterfaceModel generate = TypeScriptGenerator.builder().build().generateModel(descriptor );
		Assert.assertEquals(1, generate.getFields().size());
		FieldModel field = generate.getFields().iterator().next();
		Assert.assertEquals("model", field.getName());
		Assert.assertEquals("TestModel", field.getTypes()[0]);
		Assert.assertEquals(1, generate.getImports().size());
		Assert.assertEquals("TestModel", generate.getImports().iterator().next().getName());
	}

	@Test
	public void testArray() {
		ClassDescriptor descriptor = GeneratorFromClass.createClassDescriptor(TestArrayModel.class, new PathMapper(TestArrayModel.class.getName()));
		InterfaceModel generate = TypeScriptGenerator.builder().build().generateModel(descriptor );
		Assert.assertEquals(1, generate.getFields().size());
		FieldModel field = generate.getFields().iterator().next();
		Assert.assertEquals("models", field.getName());
		Assert.assertEquals("TestModel[]", field.getTypes()[0]);
		Assert.assertEquals(1, generate.getImports().size());
		Assert.assertEquals("TestModel", generate.getImports().iterator().next().getName());
	}

	@Test
	public void testList() {
		ClassDescriptor descriptor = GeneratorFromClass.createClassDescriptor(TestListModel.class, new PathMapper(TestListModel.class.getName()));
		InterfaceModel generate = TypeScriptGenerator.builder().build().generateModel(descriptor );
		Assert.assertEquals(1, generate.getFields().size());
		FieldModel field = generate.getFields().iterator().next();
		Assert.assertEquals("models", field.getName());
		Assert.assertEquals("TestModel[]", field.getTypes()[0]);
		Assert.assertEquals(1, generate.getImports().size());
		Assert.assertEquals("TestModel", generate.getImports().iterator().next().getName());
	}
	@Test
	public void testListAnnotationOnGetter() {
		ClassDescriptor descriptor = GeneratorFromClass.createClassDescriptor(TestGetterListModel.class, new PathMapper(TestGetterListModel.class.getName()));
		InterfaceModel generate = TypeScriptGenerator.builder().build().generateModel(descriptor );
		Assert.assertEquals(1, generate.getFields().size());
		FieldModel field = generate.getFields().iterator().next();
		Assert.assertEquals("models", field.getName());
		Assert.assertEquals("TestModel[]", field.getTypes()[0]);
		Assert.assertEquals(1, generate.getImports().size());
		Assert.assertEquals("TestModel", generate.getImports().iterator().next().getName());
	}
	@Test
	public void testMap() {
		ClassDescriptor descriptor = GeneratorFromClass.createClassDescriptor(TestMapModel.class, new PathMapper(TestMapModel.class.getName()));
		InterfaceModel generate = TypeScriptGenerator.builder().build().generateModel(descriptor );
		Assert.assertEquals(1, generate.getFields().size());
		FieldModel field = generate.getFields().iterator().next();
		Assert.assertEquals("models", field.getName());
		Assert.assertEquals("{[key: string]: TestModel}", field.getTypes()[0]);
		Assert.assertEquals(1, generate.getImports().size());
		Assert.assertEquals("TestModel", generate.getImports().iterator().next().getName());
	}
	@Test
	public void testEnum() {
		EnumDescriptor descriptor = GeneratorFromClass.createEnumDescriptor(TestValue.class);
		String enumStr = TypeScriptGenerator.builder().build().generateEnum(descriptor );
		Assert.assertEquals("export type TestValue = 'a' | 'b' | 'c';\n",enumStr);

	}

}
