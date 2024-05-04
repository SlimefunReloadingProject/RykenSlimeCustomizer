package org.lins.mmmjjkx.rykenslimefuncustomizer;

import com.google.common.annotations.Beta;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@Beta
public class RSCLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder pluginClasspathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        RemoteRepository repository = new RemoteRepository.Builder("maven", "default", "https://repo1.maven.org/maven2/").build();

        resolver.addRepository(repository);

        Dependency jsEngineDependency = new Dependency(new DefaultArtifact("org.graalvm.js:js-scriptengine:24.0.1"), "");
        Dependency jsCommunity = new Dependency(new DefaultArtifact("org.graalvm.js:js-community:24.0.1"), "");
        Dependency byteBuddyDependency = new Dependency(new DefaultArtifact("net.bytebuddy:byte-buddy:1.12.6"), "");
        Dependency truffleApiDependency = new Dependency(new DefaultArtifact("org.graalvm.truffle:truffle-api:24.0.1"), "");

        resolver.addDependency(jsEngineDependency);
        resolver.addDependency(jsCommunity);
        resolver.addDependency(byteBuddyDependency);
        resolver.addDependency(truffleApiDependency);

        pluginClasspathBuilder.addLibrary(resolver);
    }
}
