<h1>generate-typescript-maven-plugin</h1>
The generate-typescript-maven-plugin generates typescript classes from java classes.

The input may be java source code (preferable) or (compiled) 
java class-files (if you don't have the source code).

The typescript-generator generates all java-bean-properties from your java class to the corresponding 
typescript class with corresponding getter and setter methods. 

Depending on the input (source-file or class-file) the typescript-generator will map the java types to the 
best matching typescript data type. The main difference between source- or class-file as input of the 
generator is that the component types of generic classes can't be extracted from compiled java-classes 
(because of type erasure).

The result of the generator is one typescript file for every source- or class-file input 
and a <b>project.ts</b> file which references all generated sources and brings them in the 
right order for the typescript converter. Use this single project.ts for reference include in 
your typescript project.

usage:
add following snippet to the build-plugins section of your pom.xml 

<pre>
&lt;plugin&gt;
	&lt;groupId&gt;com.github.pepe79.tsgenerator&lt;/groupId&gt;
	&lt;artifactId&gt;generate-typescript-maven-plugin&lt;/artifactId&gt;
	&lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
	&lt;configuration&gt;
		&lt;targetDirectory&gt;src/main/ts&lt;/targetDirectory&gt;
		&lt;sourceDirectory&gt;src/main/java&lt;/sourceDirectory&gt;
		
		&lt;packageDirectories&gt;
			&lt;packageDirectory&gt;[PACKAGE_DIRECTORY_IN_SOURCE_PATH]&lt;/packageDirectory&gt;
			&lt;!-- 
				more package directories
				&lt;packageDirectory&gt;....&lt;/packageDirectory&gt;
				&lt;packageDirectory&gt;....&lt;/packageDirectory&gt;
			--&gt;
		&lt;/packageDirectories&gt;
		
		&lt;!-- Exclude list of simple class names (without package) --&gt;
		&lt;!--
		&lt;excludes&gt;
			&lt;exclude&gt;&lt;/exclude&gt;
			&lt;exclude&gt;&lt;/exclude&gt;
			&lt;exclude&gt;&lt;/exclude&gt;
		&lt;/excludes&gt;
		--&gt;

		&lt;!-- Include sources outside of your configured source package --&gt;
		&lt;!--
		&lt;includeSources&gt;
			&lt;includeSource&gt;package/directory/to/source/Source.java&lt;/includeSource&gt;
		&lt;/includeSources&gt;
		--&gt;

		&lt;!-- Include compiled classes (dont forget to add the corresponding jar to the plugin dependencies) --&gt;
		&lt;!--
		&lt;classes&gt;
			&lt;class&gt;full.qualified.Classname.Here&lt;/class&gt;
			&lt;class&gt;...&lt;/class&gt;
		&lt;/classes&gt;
		--&gt;
		
	&lt;/configuration&gt;
	&lt;dependencies&gt;
		&lt;!-- If you have included classes for generation you have to configure the corresponding jars, 
		where this classes can be found. --&gt;
		&lt;!--
		&lt;dependency&gt;
			&lt;groupId&gt;...&lt;/groupId&gt;
			&lt;artifactId&gt;...&lt;/artifactId&gt;
			&lt;version&gt;...&lt;/version&gt;
		&lt;/dependency&gt;
		--&gt;
	&lt;/dependencies&gt;
&lt;/plugin&gt;


</pre>

Start typescript generator with following command
<pre>
	mvn generate-typescript-maven-plugin:generate-ts
</pre>

This plugin can be used together with https://github.com/pepe79/typescript-maven-plugin , which 
compiles the generated typescript sources to javascript.
 
