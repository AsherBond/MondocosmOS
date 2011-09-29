

package es.igosoftware.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class TProject {


   private static final Map<String, TProject> projectsPool = new HashMap<String, TProject>();


   public static TProject getProject(final String projectDirectory) throws TBuildException, IOException {
      synchronized (projectsPool) {
         final String fullDirectory = new File(projectDirectory).getCanonicalFile().getAbsoluteFile().getAbsolutePath();

         TProject project = projectsPool.get(fullDirectory);
         if (project == null) {
            project = new TProject(fullDirectory);
            projectsPool.put(fullDirectory, project);
         }

         return project;
      }
   }


   private static final class Property {
      private final String       _name;
      private final List<String> _values = new ArrayList<String>(1);


      private Property(final String name,
                       final String value) {
         _name = name;
         _values.add(value);
      }


      //      private String getValue() {
      //         return _values.get(0);
      //      }


      @Override
      public String toString() {
         return _name + "=\"" + _values + "\"";
      }


      private void addValue(final String value) {
         _values.add(value);
      }
   }


   private final File           _directory;
   private final String         _name;

   // properties
   private final List<TProject> _dependenciesProjects;
   private final List<File>     _dependenciesJars;
   private final List<String>   _jarIncludes;
   private final String         _toolsJDKHome;
   private final File           _toolsJDKRelativeHome;

   private final List<File>     _DEPLOY_INCLUDES;
   private final String         _MAIN_CLASS;
   private final String         _APPLET_CLASS;
   private final String         _JAVA_VM_ARGS_linux_64;
   private final String         _JAVA_VM_ARGS_linux_32;
   private final String         _JAVA_VM_ARGS_apple_64;
   private final String         _JAVA_VM_ARGS_apple_32;
   private final String         _JAVA_VM_ARGS_windows_64;
   private final String         _JAVA_VM_ARGS_windows_32;

   private final List<File>     _JAVA_VM_JAVA_LIBRARY_PATH;
   private final List<File>     _JAVA_VM_JNA_LIBRARY_PATH;
   private final String         _JAR_SIGN_STOREPASS;
   private final String         _JAR_SIGN_KEYSTORE;
   private final String         _JAR_SIGN_ALIAS;
   private final List<String>   _EXCLUDED_JARS;
   private final List<String>   _EXCLUDED_JARS_FOR_APPLET;


   private TProject(final String directoryName) throws TBuildException, IOException {
      _directory = new File(directoryName).getCanonicalFile().getAbsoluteFile();
      if (!_directory.exists()) {
         throw new TBuildException(this, "Directory \"" + directoryName + "\" doesn't exist");
      }

      _name = _directory.getName();

      final Map<String, String[]> evaluatedProperties = evaluateProperties(readProperties());

      _jarIncludes = parseList(getStringValue(evaluatedProperties, "JAR_INCLUDES"));


      final String toolsJDKHomeProperty = System.getProperty("tools.jdk.home", null);
      if (toolsJDKHomeProperty == null) {
         _toolsJDKRelativeHome = new File(getStringValue(evaluatedProperties, "TOOLS_JDK_HOME"));
         _toolsJDKHome = new File(_directory, getStringValue(evaluatedProperties, "TOOLS_JDK_HOME")).getCanonicalFile().getAbsolutePath();
      }
      else {
         _toolsJDKRelativeHome = new File(toolsJDKHomeProperty);
         _toolsJDKHome = _toolsJDKRelativeHome.getCanonicalFile().getAbsolutePath();
         TUtils.info(this, "Setting tools_jdk_home from given environment variable to " + _toolsJDKHome);
      }


      _DEPLOY_INCLUDES = asFileList(_directory, parseList(getStringValue(evaluatedProperties, "DEPLOY_INCLUDES")));

      _MAIN_CLASS = getStringValue(evaluatedProperties, "MAIN_CLASS");
      _APPLET_CLASS = getStringValue(evaluatedProperties, "APPLET_CLASS");


      _JAR_SIGN_STOREPASS = getStringValue(evaluatedProperties, "JAR_SIGN_STOREPASS");
      _JAR_SIGN_KEYSTORE = getStringValue(evaluatedProperties, "JAR_SIGN_KEYSTORE");
      _JAR_SIGN_ALIAS = getStringValue(evaluatedProperties, "JAR_SIGN_ALIAS");

      _EXCLUDED_JARS = parseList(getStringValue(evaluatedProperties, "EXCLUDED_JARS"));
      if (_EXCLUDED_JARS.isEmpty()) {
         System.out.println("  - " + getName() + ": WARNING: Property \"EXCLUDED_JARS\" not set in project.properties.");
      }
      _EXCLUDED_JARS_FOR_APPLET = parseList(getStringValue(evaluatedProperties, "EXCLUDED_JARS_FOR_APPLET"));
      if (_EXCLUDED_JARS_FOR_APPLET.isEmpty()) {
         final List<? extends String> defaults = getDefaultExcludedJarsForApplet();
         System.out.println("  - " + getName()
                            + ": WARNING: Property \"EXCLUDED_JARS_FOR_APPLET\" not set in project.properties. Using defaults: "
                            + defaults);
         _EXCLUDED_JARS_FOR_APPLET.addAll(defaults);
      }

      _JAVA_VM_ARGS_linux_64 = mandatoryGet(evaluatedProperties, "JAVA_VM_ARGS_linux_64");
      _JAVA_VM_ARGS_linux_32 = mandatoryGet(evaluatedProperties, "JAVA_VM_ARGS_linux_32");

      _JAVA_VM_ARGS_apple_64 = mandatoryGet(evaluatedProperties, "JAVA_VM_ARGS_apple_64");
      _JAVA_VM_ARGS_apple_32 = mandatoryGet(evaluatedProperties, "JAVA_VM_ARGS_apple_32");

      _JAVA_VM_ARGS_windows_64 = mandatoryGet(evaluatedProperties, "JAVA_VM_ARGS_windows_64");
      _JAVA_VM_ARGS_windows_32 = mandatoryGet(evaluatedProperties, "JAVA_VM_ARGS_windows_32");

      _JAVA_VM_JAVA_LIBRARY_PATH = asFileList(_directory,
               parseList(getStringValue(evaluatedProperties, "JAVA_VM_JAVA_LIBRARY_PATH")));
      _JAVA_VM_JNA_LIBRARY_PATH = asFileList(_directory,
               parseList(getStringValue(evaluatedProperties, "JAVA_VM_JNA_LIBRARY_PATH")));


      final List<String> dependenciesProjectsNames = parseList(getStringValue(evaluatedProperties, "DEPENDENCIES_PROJECTS"));
      _dependenciesProjects = new ArrayList<TProject>(dependenciesProjectsNames.size());
      for (final String dependencyProjectDirectoryName : dependenciesProjectsNames) {
         _dependenciesProjects.add(getProject(new File(_directory, dependencyProjectDirectoryName).getCanonicalFile().getAbsoluteFile().getCanonicalPath()));
      }

      final List<String> dependenciesJars = parseList(getStringValue(evaluatedProperties, "DEPENDENCIES_JARS"));
      _dependenciesJars = asFileList(_directory, dependenciesJars);
   }


   private List<? extends String> getDefaultExcludedJarsForApplet() {
      return Arrays.asList("gluegen-rt.jar", "jogl.jar");
   }


   private static String getStringValue(final Map<String, String[]> properties,
                                        final String propertyName) {
      return getStringValue(properties.get(propertyName));
   }


   private static String getStringValue(final List<String> values) {
      if ((values == null) || values.isEmpty()) {
         return null;
      }

      final StringBuilder builder = new StringBuilder();
      for (final String value : values) {
         builder.append(value);
         builder.append(' ');
      }

      return builder.toString().trim();
   }


   private static String getStringValue(final String[] values) {
      if ((values == null) || (values.length == 0)) {
         return null;
      }

      final StringBuilder builder = new StringBuilder();
      for (final String value : values) {
         builder.append(value);
         builder.append(' ');
      }

      return builder.toString().trim();
   }


   private String mandatoryGet(final Map<String, String[]> properties,
                               final String propertyName) throws TBuildException {
      //      final String result = properties.get(propertyName);
      final String result = getStringValue(properties, propertyName);
      if (result == null) {
         throw new TBuildException(this, "Can't find a value for property \"" + propertyName + "\"");
      }
      return result;
   }


   private static List<File> asFileList(final File parent,
                                        final List<String> names) throws IOException {
      final List<File> files = new ArrayList<File>(names.size());

      for (final String name : names) {
         files.add(new File(parent, name).getCanonicalFile().getAbsoluteFile());
      }

      return files;
   }


   private List<String> parseList(final String string) {
      if ((string == null) || string.trim().isEmpty()) {
         return new ArrayList<String>();
      }
      final String[] array = string.trim().split(" ");
      return Arrays.asList(array);
   }


   private Map<String, String[]> evaluateProperties(final List<Property> properties) {
      final Map<String, String[]> result = new HashMap<String, String[]>(properties.size());

      final Map<String, String[]> variables = new HashMap<String, String[]>();

      for (final Property property : properties) {
         //         final String rawValue = property._value;

         //         String evaluatedValue = rawValue;
         //         for (final Entry<String, String> variable : variables.entrySet()) {
         //            final String variableName = variable.getKey();
         //            final String variableValue = variable.getValue();
         //
         //            evaluatedValue = evaluatedValue.replace("${" + variableName + "}", variableValue);
         //            evaluatedValue = evaluatedValue.replace("$" + variableName, variableValue);
         //         }
         final String[] evaluatedValue = replaceVariablesContents(property._values, variables);

         variables.put(property._name, evaluatedValue);
         result.put(property._name, evaluatedValue);
      }

      return result;
   }


   private static String[] replaceVariablesContents(final List<String> values,
                                                    final Map<String, String[]> variables) {

      final String[] result = new String[values.size()];

      for (int i = 0; i < result.length; i++) {
         result[i] = replaceVariablesContents(values.get(i), variables);
      }

      return result;
   }


   private static String replaceVariablesContents(final String value,
                                                  final Map<String, String[]> variables) {
      String result = value;
      for (final Entry<String, String[]> variable : variables.entrySet()) {
         final String variableName = variable.getKey();
         final String[] variableValues = variable.getValue();

         final CharSequence variableValue = toString(variableValues);

         result = result.replace("${" + variableName + "}", variableValue);
         result = result.replace("$" + variableName, variableValue);
      }
      return result;
   }


   private static CharSequence toString(final String[] values) {
      if (values == null) {
         return "null";
      }

      if (values.length == 0) {
         return "";
      }

      if (values.length == 1) {
         return values[0];
      }

      return Arrays.toString(values);
   }


   private List<Property> readProperties() throws TBuildException, IOException {
      final File projectProperties = new File(_directory, "project.properties");

      final ArrayList<Property> properties = new ArrayList<Property>();

      BufferedReader reader = null;

      try {
         reader = new BufferedReader(new FileReader(projectProperties));

         String line;
         int lineNumber = 0;
         while ((line = reader.readLine()) != null) {
            lineNumber++;
            final String trimmed = line.trim();

            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
               continue;
            }

            final int equalsPos = trimmed.indexOf('=');
            if (equalsPos < 0) {
               throw new TBuildException(this, projectProperties.getAbsolutePath() + ":" + lineNumber
                                               + " Invalid syntax, '=' character not found");
            }

            final String key = trimmed.substring(0, equalsPos).trim();
            if (key.isEmpty()) {
               throw new TBuildException(this, projectProperties.getAbsolutePath() + ":" + lineNumber
                                               + " Invalid syntax, expected \"key=value\"");
            }

            final String value = trimmed.substring(equalsPos + 1).trim();


            Property found = null;
            for (final Property property : properties) {
               if (property._name.equals(key)) {
                  found = property;
                  break;
               }
            }

            if (found == null) {
               properties.add(new Property(key, removeQuotes(value)));
            }
            else {
               found.addValue(removeQuotes(value));
            }
         }
      }
      finally {
         if (reader != null) {
            try {
               reader.close();
            }
            catch (final IOException e) {}
         }
      }

      return properties;
   }


   private static String removeQuotes(final String value) {
      if (value.startsWith("\"") && value.endsWith("\"")) {
         return value.substring(1, value.length() - 1);
      }
      return value;
   }


   @Override
   public String toString() {
      return "TProject [name=" + _name + ", directory=" + _directory + "]";
   }


   private File getBuildDirectory() {
      return new File(_directory, "_build");
   }


   private List<TProject> getAllDependencies() {
      final Set<TProject> set = new HashSet<TProject>();

      addDependenciesTo(set);

      final List<TProject> result = new ArrayList<TProject>(set);
      Collections.sort(result, new Comparator<TProject>() {
         @Override
         public int compare(final TProject p1,
                            final TProject p2) {
            final int depth1 = p1.getDepth();
            final int depth2 = p2.getDepth();
            if (depth1 > depth2) {
               return 1;
            }
            else if (depth1 < depth2) {
               return -1;
            }
            else {
               return p1._name.compareTo(p2._name);
            }
         }
      });

      return result;
   }


   private void addDependenciesTo(final Set<TProject> set) {
      set.addAll(_dependenciesProjects);
      for (final TProject dependency : _dependenciesProjects) {
         dependency.addDependenciesTo(set);
      }
   }


   protected int getDepth() {
      int maxDepth = 0;
      for (final TProject dependency : _dependenciesProjects) {
         maxDepth = Math.max(maxDepth, dependency.getDepth() + 1);
      }
      return maxDepth;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_directory == null) ? 0 : _directory.hashCode());
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final TProject other = (TProject) obj;
      if (_directory == null) {
         if (other._directory != null) {
            return false;
         }
      }
      else if (!_directory.equals(other._directory)) {
         return false;
      }
      return true;
   }


   public String getName() {
      return _name;
   }


   private File getJarFile() {
      return new File(getBuildDirectory(), _name + ".jar");
   }


   private void evaluateTemplate(final File templatesDirectory,
                                 final String templateName,
                                 final String targetName,
                                 final boolean setExecutable,
                                 final Property... properties) throws IOException, TBuildException {
      //      TUtils.info(this, "Creating " + targetName + "...");

      final File template = new File(templatesDirectory, templateName).getCanonicalFile().getAbsoluteFile();


      String contents = TUtils.getContents(template);
      for (final Property property : properties) {
         //         contents = contents.replace(property._name, (property._value == null) ? "null" : property._value);
         final String value = getStringValue(property._values);
         contents = contents.replace(property._name, (value == null) ? "null" : value);
      }


      final File target = new File(_directory, targetName).getCanonicalFile().getAbsoluteFile();

      final String createMessage = "Creating " + targetName + "...";
      final String skippedMessage = null; // "Skipping creation of " + targetName + "...";
      TUtils.saveContentsTo(contents, target, this, createMessage, skippedMessage);

      if (setExecutable) {
         if (!target.setExecutable(true)) {
            throw new TBuildException(this, "Can't make executable the " + targetName + " file");
         }
      }
   }


   public void generateAntBuild(final File igotoolsDirectory,
                                final File templatesDirectory) throws IOException, TBuildException {
      for (final TProject dependency : getAllDependencies()) {
         dependency.rawGenerateAntBuild(igotoolsDirectory, templatesDirectory);
      }

      rawGenerateAntBuild(igotoolsDirectory, templatesDirectory);
   }


   private void rawGenerateAntBuild(final File igotoolsDirectory,
                                    final File templatesDirectory) throws IOException, TBuildException {
      createBuildXML(igotoolsDirectory, templatesDirectory);
      createAntSH(templatesDirectory);
      //      createRunSH(templatesDirectory);
   }


   private void createBuildXML(final File igotoolsDirectory,
                               final File templatesDirectory) throws IOException, TBuildException {
      //      TUtils.info(this, "Generating build.xml...");

      final String COMPILE_CLASSPATH = ".:" + buildDependenciesJarsClasspath() + buildDependenciesProjectsClasspath();

      final StringBuilder DEPENDENCIES_TARGETS = new StringBuilder();
      final StringBuilder TEST_DEPENDS = new StringBuilder("");
      for (final TProject project : getAllDependencies()) {
         //         TEST_DEPENDS.append("," + project.getName() + "_test");
         TEST_DEPENDS.append(project.getName() + "_test,");

         DEPENDENCIES_TARGETS.append("  <target name=\"" + project.getName() + "_test\">\n");
         DEPENDENCIES_TARGETS.append("    <ant antfile=\"generated_build.xml\" dir=\""
                                     + project._directory.getAbsoluteFile().getAbsolutePath()
                                     + "\" inheritAll=\"false\" target=\"privateTest\" useNativeBasedir=\"true\" />\n");
         DEPENDENCIES_TARGETS.append("  </target>\n");
      }
      DEPENDENCIES_TARGETS.append("\n");

      final StringBuilder COMPILE_DEPENDS = new StringBuilder("init");
      final StringBuilder CLEAN_DEPENDS = new StringBuilder("init");

      for (final TProject project : _dependenciesProjects) {
         COMPILE_DEPENDS.append("," + project.getName() + "_build");

         CLEAN_DEPENDS.append("," + project.getName() + "_clean");


         DEPENDENCIES_TARGETS.append("  <target name=\"" + project.getName() + "_build\">\n");
         DEPENDENCIES_TARGETS.append("    <ant antfile=\"generated_build.xml\" dir=\""
                                     + project._directory.getAbsoluteFile().getAbsolutePath()
                                     + "\" inheritAll=\"false\" target=\"build\" useNativeBasedir=\"true\"/>\n");
         DEPENDENCIES_TARGETS.append("  </target>\n");

         DEPENDENCIES_TARGETS.append("  <target name=\"" + project.getName() + "_clean\">\n");
         DEPENDENCIES_TARGETS.append("    <ant antfile=\"generated_build.xml\" dir=\""
                                     + project._directory.getAbsoluteFile().getAbsolutePath()
                                     + "\" inheritAll=\"false\" target=\"clean\" useNativeBasedir=\"true\"/>\n");
         DEPENDENCIES_TARGETS.append("  </target>\n");

         DEPENDENCIES_TARGETS.append("\n");
      }


      final StringBuilder COPY_DEPENDENCY_JAR = new StringBuilder();

      for (final File dependencyJar : _dependenciesJars) {
         COPY_DEPENDENCY_JAR.append("    <copy file=\"" + dependencyJar.getAbsoluteFile().getAbsolutePath()
                                    + "\" todir=\"${build}/libs/\"/>\n");
      }


      final List<File> allJars = new ArrayList<File>();
      allJars.add(getJarFile().getAbsoluteFile());
      allJars.addAll(_dependenciesJars);
      for (final TProject dependencyProject : getAllDependencies()) {
         allJars.add(dependencyProject.getJarFile().getAbsoluteFile());
         allJars.addAll(dependencyProject._dependenciesJars);
      }


      final List<File> runtimeJars = new ArrayList<File>();
      runtimeJars.add(getJarFile().getAbsoluteFile());
      runtimeJars.addAll(_dependenciesJars);
      for (final TProject dependencyProject : getAllDependencies()) {
         runtimeJars.add(dependencyProject.getJarFile().getAbsoluteFile());
         runtimeJars.addAll(dependencyProject._dependenciesJars);
      }

      removeExcludedJars(runtimeJars);

      final StringBuilder RUNTIME_CLASSPATH = new StringBuilder();
      for (final File runtimeJar : runtimeJars) {
         RUNTIME_CLASSPATH.append(runtimeJar.getAbsoluteFile().getAbsolutePath() + ";");
      }

      final StringBuilder RUN_JVMARGS = new StringBuilder();
      if (!_JAVA_VM_JAVA_LIBRARY_PATH.isEmpty()) {
         RUN_JVMARGS.append("      <jvmarg line=\"-Djava.library.path=");
         for (final File each : _JAVA_VM_JAVA_LIBRARY_PATH) {
            RUN_JVMARGS.append(each.getAbsoluteFile().getAbsolutePath() + File.separator + "${platform}" + File.separator
                               + "${bits}:");
         }
         RUN_JVMARGS.setLength(RUN_JVMARGS.length() - 1);
         RUN_JVMARGS.append("\" />\n");
      }
      if (!_JAVA_VM_JNA_LIBRARY_PATH.isEmpty()) {
         RUN_JVMARGS.append("      <jvmarg line=\"-Djna.library.path=");
         for (final File each : _JAVA_VM_JNA_LIBRARY_PATH) {
            RUN_JVMARGS.append(each.getAbsoluteFile().getAbsolutePath() + File.separator + "${platform}" + File.separator
                               + "${bits}:");
         }
         RUN_JVMARGS.setLength(RUN_JVMARGS.length() - 1);
         RUN_JVMARGS.append("\" />\n");
      }

      final StringBuilder JAR_INCLUDES = new StringBuilder();
      if (!_jarIncludes.isEmpty()) {
         for (final String each : _jarIncludes) {
            JAR_INCLUDES.append("      <include name=\"" + each + "/**/*\" />\n");
         }
      }


      final boolean signJar = isSet(_JAR_SIGN_ALIAS) && isSet(_JAR_SIGN_KEYSTORE) && isSet(_JAR_SIGN_STOREPASS);
      if (!signJar) {
         System.out.println("  - "
                            + getName()
                            + ": WARNING: Can't sign jars, mandatory project properties JAR_SIGN_ALIAS, JAR_SIGN_KEYSTORE and JAR_SIGN_STOREPASS not set");
      }

      final StringBuilder DEPLOY_COPY_JARS = new StringBuilder();
      final StringBuilder DEPLOY_SIGN_JARS = new StringBuilder();
      final StringBuilder DEPLOY_RUNTIME_CLASSPATH = new StringBuilder();
      final StringBuilder WINDOWS_DEPLOY_RUNTIME_CLASSPATH = new StringBuilder();
      final StringBuilder APPLET_JARS = new StringBuilder();
      for (final File jar : runtimeJars) {
         final String jarName = jar.getName();

         DEPLOY_COPY_JARS.append("    <copy file=\"" + jar.getAbsolutePath() + "\" todir=\"${deploy}/libs\" />\n");

         DEPLOY_RUNTIME_CLASSPATH.append("libs/" + jarName + ":");
         WINDOWS_DEPLOY_RUNTIME_CLASSPATH.append("libs\\" + jarName + ";");

         if (acceptJarForApplet(jar)) {
            final boolean isMainJar = jarName.equals(_name + ".jar");
            if (isMainJar) {
               APPLET_JARS.append("<jar href=\"libs/" + jarName + "\" main=\"true\"/>\n");
            }
            else {
               APPLET_JARS.append("<jar href=\"libs/" + jarName + "\"/>\n");
            }

            if (signJar) {
               DEPLOY_SIGN_JARS.append(signJar(jarName));
            }
         }
      }


      final StringBuilder DEPLOY_COPY_NATIVES = new StringBuilder();
      final StringBuilder JAVA_VM_JAVA_LIBRARY_PATH = new StringBuilder();

      DEPLOY_COPY_NATIVES.append("    <!-- copy java native libraries -->\n");
      for (final File source : _JAVA_VM_JAVA_LIBRARY_PATH) {
         JAVA_VM_JAVA_LIBRARY_PATH.append(source.getName() + " ");

         DEPLOY_COPY_NATIVES.append("    <sync todir=\"_deploy/libs/native/" + source.getName() + "\">\n");
         DEPLOY_COPY_NATIVES.append("      <fileset dir=\"" + source.getAbsolutePath() + "\" />\n");
         DEPLOY_COPY_NATIVES.append("    </sync>\n");
      }

      DEPLOY_COPY_NATIVES.append("\n");

      final StringBuilder JAVA_VM_JNA_LIBRARY_PATH = new StringBuilder();

      DEPLOY_COPY_NATIVES.append("    <!-- copy JNA native libraries -->\n");
      for (final File source : _JAVA_VM_JNA_LIBRARY_PATH) {
         JAVA_VM_JNA_LIBRARY_PATH.append(source.getName() + " ");

         DEPLOY_COPY_NATIVES.append("    <sync todir=\"_deploy/libs/native/" + source.getName() + "\">\n");
         DEPLOY_COPY_NATIVES.append("      <fileset dir=\"" + source.getAbsolutePath() + "\" />\n");
         DEPLOY_COPY_NATIVES.append("    </sync>\n");
      }


      final StringBuilder DEPLOY_COPY_INCLUDES = new StringBuilder();
      for (final File source : _DEPLOY_INCLUDES) {
         if (source.isDirectory()) {
            DEPLOY_COPY_INCLUDES.append("    <sync todir=\"_deploy/" + source.getName() + "\">\n");
            DEPLOY_COPY_INCLUDES.append("      <fileset dir=\"" + source.getAbsolutePath() + "\" />\n");
            DEPLOY_COPY_INCLUDES.append("    </sync>\n");
         }
         else {
            TUtils.info(this, "WARWNING: Only directories are allowed to be final present in DEPLOY_INCLUDES (" + source
                              + " is not a directory)");
            //            throw new TBuildException(this, "Only directories are allowed to be present in DEPLOY_INCLUDES (" + source
            //                                            + " is not a directory)");
         }
      }


      evaluateTemplate(templatesDirectory, "build_template.xml", "generated_build.xml", false,//
               new Property("%PROJECT_NAME%", getName()), //
               new Property("%MAIN_CLASS%", _MAIN_CLASS), //
               new Property("%APPLET_CLASS%", _APPLET_CLASS), //
               new Property("%JAVA_VM_ARGS_linux_64%", _JAVA_VM_ARGS_linux_64), // 
               new Property("%JAVA_VM_ARGS_linux_32%", _JAVA_VM_ARGS_linux_32), //
               new Property("%JAVA_VM_ARGS_apple_64%", _JAVA_VM_ARGS_apple_64), // 
               new Property("%JAVA_VM_ARGS_apple_32%", _JAVA_VM_ARGS_apple_32), //
               new Property("%JAVA_VM_ARGS_windows_64%", _JAVA_VM_ARGS_windows_64), // 
               new Property("%JAVA_VM_ARGS_windows_32%", _JAVA_VM_ARGS_windows_32), //
               new Property("%TOOLS_JDK_HOME%", _toolsJDKHome), //
               new Property("%COMPILE_CLASSPATH%", COMPILE_CLASSPATH), //
               new Property("%RUNTIME_CLASSPATH%", RUNTIME_CLASSPATH.toString()), //
               new Property("%DEPENDENCIES_TARGETS%", DEPENDENCIES_TARGETS.toString()), //
               new Property("%COMPILE_DEPENDS%", COMPILE_DEPENDS.toString()), //
               new Property("%CLEAN_DEPENDS%", CLEAN_DEPENDS.toString()), //
               new Property("%TEST_DEPENDS%", TEST_DEPENDS.toString()), //
               new Property("%COPY_DEPENDENCY_JAR%", COPY_DEPENDENCY_JAR.toString()), //
               new Property("%RUN_JVMARGS%", RUN_JVMARGS.toString()), //
               new Property("%DEPLOY_COPY_JARS%", DEPLOY_COPY_JARS.toString()), //
               new Property("%DEPLOY_SIGN_JARS%", DEPLOY_SIGN_JARS.toString()), //
               new Property("%JAR_INCLUDES%", JAR_INCLUDES.toString()), //
               new Property("%CLASSPATH%", DEPLOY_RUNTIME_CLASSPATH.toString()), //
               new Property("%WINDOWS_CLASSPATH%", WINDOWS_DEPLOY_RUNTIME_CLASSPATH.toString()), //
               new Property("%APPLET_JARS%", APPLET_JARS.toString()), //
               new Property("%IGOTOOLS_HOME%", igotoolsDirectory.getAbsolutePath()), //
               new Property("%JAVA_VM_JAVA_LIBRARY_PATH%", JAVA_VM_JAVA_LIBRARY_PATH.toString()), //
               new Property("%JAVA_VM_JNA_LIBRARY_PATH%", JAVA_VM_JNA_LIBRARY_PATH.toString()), //
               new Property("%DEPLOY_COPY_NATIVES%", DEPLOY_COPY_NATIVES.toString()), //
               new Property("%DEPLOY_COPY_INCLUDES%", DEPLOY_COPY_INCLUDES.toString()), //
               new Property("%BUILD_TOOLS_JDK_HOME%", _toolsJDKHome), //
               new Property("%BUILD_CLASSPATH%", RUNTIME_CLASSPATH.toString()) //
      );


   }


   private boolean acceptJarForApplet(final File jar) {
      return !_EXCLUDED_JARS_FOR_APPLET.contains(jar.getName());
   }


   private void removeExcludedJars(final List<File> jars) {
      final Iterator<File> iterator = jars.iterator();
      while (iterator.hasNext()) {
         final File jar = iterator.next();
         if (_EXCLUDED_JARS.contains(jar.getName())) {
            iterator.remove();
         }
      }
   }


   private static boolean isSet(final String property) {
      return (property != null) && !property.trim().isEmpty();
   }


   private String signJar(final String jarName) {
      // jarsigner -verbose -keystore mykeystore -storepass igoadm  /home/dgd/Desktop/GLOB3-Repository/glob3/globe-demo/_deploy/libs/globe-demo.jar codesigncert

      final StringBuilder builder = new StringBuilder();

      builder.append("<signjar jar=\"_deploy/libs/");
      builder.append(jarName);
      builder.append("\" alias=\"");
      builder.append(_JAR_SIGN_ALIAS);
      builder.append("\" storepass=\"");
      builder.append(_JAR_SIGN_STOREPASS);
      builder.append("\" verbose=\"false\" keystore=\"");
      builder.append(_JAR_SIGN_KEYSTORE);
      builder.append("\" />\n");

      return builder.toString();
   }


   private void createAntSH(final File templatesDirectory) throws IOException, TBuildException {
      final String antDirectoryProperty = System.getProperty("ant.home", null);
      if (antDirectoryProperty == null) {
         throw new TBuildException(this, "- ERROR: Property ant.home is not present.");
      }
      final File antDirectory = new File(antDirectoryProperty).getCanonicalFile().getAbsoluteFile();

      final File antLibsDirectory = new File(antDirectory, "lib");
      final File[] antJARs = antLibsDirectory.listFiles(new FileFilter() {
         @Override
         public boolean accept(final File pathname) {
            return pathname.getName().endsWith(".jar");
         }
      });


      final String toolsEclipseJDTJarProperty = System.getProperty("tools.eclipse.jdt.jar", null);
      if (toolsEclipseJDTJarProperty == null) {
         throw new TBuildException(this, "must set the tools.eclipse.jdt.jar property");
      }
      final File toolsEclipseJDTJar = new File(toolsEclipseJDTJarProperty).getCanonicalFile().getAbsoluteFile();


      final String toolsJDTAdapterJarProperty = System.getProperty("tools.jdt.adapter.jar", null);
      if (toolsJDTAdapterJarProperty == null) {
         throw new TBuildException(this, "- ERROR: Property tools.jdt.adapter.jar is not present.");
      }
      final File toolsJDTAdapterJar = new File(toolsJDTAdapterJarProperty).getCanonicalFile().getAbsoluteFile();


      final StringBuilder CLASSPATH = new StringBuilder();

      for (final File antJAR : antJARs) {
         CLASSPATH.append(antJAR.getAbsoluteFile().getAbsolutePath() + ":");
      }


      CLASSPATH.append(toolsEclipseJDTJar.getAbsoluteFile().getAbsolutePath() + ":");
      CLASSPATH.append(toolsJDTAdapterJar.getAbsoluteFile().getAbsolutePath() + ":");


      evaluateTemplate(templatesDirectory, "ant_template.sh", "generated_ant.sh", true,//
               new Property("%tools_jdk_home%", _toolsJDKHome), //
               new Property("%ant_home%", antDirectory.getAbsoluteFile().getAbsolutePath()), //
               new Property("%CLASSPATH%", CLASSPATH.toString()) //
      );

      evaluateTemplate(templatesDirectory, "ant_template.bat", "generated_ant.bat", true,//
               new Property("%tools_jdk_home%", _toolsJDKHome), //
               new Property("%ant_home%", antDirectory.getAbsoluteFile().getAbsolutePath()), //
               new Property("%CLASSPATH%", CLASSPATH.toString().replace(":", ";")) //
      );
   }


   private String buildDependenciesJarsClasspath() throws IOException {
      final StringBuilder builder = new StringBuilder("");
      for (final File dependencyJar : _dependenciesJars) {
         builder.append(dependencyJar.getCanonicalFile().getAbsoluteFile().getAbsoluteFile() + ":");
      }

      return builder.toString();
   }


   private String buildDependenciesProjectsClasspath() throws IOException {
      final StringBuilder builder = new StringBuilder("");
      for (final TProject dependencyProject : getAllDependencies()) {
         builder.append(dependencyProject.getJarFile().getAbsolutePath() + ":");
         builder.append(dependencyProject.buildDependenciesJarsClasspath() + ":");
      }
      return builder.toString();
   }


}
