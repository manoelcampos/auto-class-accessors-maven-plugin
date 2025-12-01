package io.github.manoelcampos.accessors;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import javax.inject.Inject;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/// A Maven plugin that invokes Byte Buddy Maven Plugin to transform compiled classes,
/// replacing access to public instance fields by the respective accessor (getter/setter) method call, if available.
/// @author Manoel Campos da Silva Filho
/// @link https://maven.apache.org/guides/plugin/guide-java-plugin-development.html
@Mojo(name = "apply", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES, requiresDependencyResolution = ResolutionScope.TEST)
public class AutoClassAccessorsMojo extends AbstractMojo {
    /**
     * A reference to the current version of the Auto Class Accessors Maven Plugin,
     * to get the {@link ClassAccessorInstrumentationPlugin class that implements a Byte Buddy Plugin}.
     * Such a class is the one that actually applies the byte code transformation,
     * replacing accesses to public instance fields by getter/setter calls.
     *
     * <p>We cannot use the {@link #project} object to access the information of the pom.xml file
     * in production, because that object will get the pom from the project using this plugin,
     * not the plugin's pom itself. And we cannot try to read the pom.xml file directly,
     * since in runtime, we would need to extract it from the plugin jar file (except for tests).
     * This way, this version needs to be defined manually, copying the value from the pom.xml.
     * </p>
     *
     * <p><b>NOTE:</b> This must be the same version on the pom.xml in this maven plugin project.</p>
     */
    final MavenDependency accessorsPlugin = new MavenDependency(
            "io.github.manoelcampos", "auto-class-accessors-maven-plugin",
            "1.0.4"
    );

    private final MavenDependency byteBuddyPlugin = new MavenDependency("net.bytebuddy", "byte-buddy-maven-plugin", "1.17.2");

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Inject
    private BuildPluginManager manager;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Executing Auto Class Accessors transformation via Byte Buddy to replace accesses to public instance fields by getter/setter calls...");
        try {
            runByteBuddyPlugin("transform");      // transforms the bytecode of main classes
            runByteBuddyPlugin("transform-test"); // transforms the bytecode of test classes
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to execute Byte Buddy Maven Plugin to apply auto class accessors.", e);
        }
    }

    /**
     * Invoke Byte Buddy Maven Plugin programmatically
     * @param goal the goal to execute
     * @throws MojoExecutionException
     */
    private void runByteBuddyPlugin(final String goal) throws MojoExecutionException {
        MojoExecutor.executeMojo(
                plugin(
                    MojoExecutor.groupId(byteBuddyPlugin.groupId()),
                    MojoExecutor.artifactId(byteBuddyPlugin.artifactId()),
                    MojoExecutor.version(byteBuddyPlugin.version())
                ),
                goal(goal),
                configuration(
                    element("transformations",
                        element("transformation",
                            element("groupId", accessorsPlugin.groupId()),
                            element("artifactId", accessorsPlugin.artifactId()),
                            element("version", accessorsPlugin.version()),
                            element("plugin", ClassAccessorInstrumentationPlugin.class.getName())
                        )
                    )
                ),
                executionEnvironment(project, session, manager)
        );
    }
}
