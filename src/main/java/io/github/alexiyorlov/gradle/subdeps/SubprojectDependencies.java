package io.github.alexiyorlov.gradle.subdeps;

import com.google.common.collect.HashMultimap;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SubprojectDependencies implements Plugin<Project>
{
    @Override
    public void apply(Project project)
    {
        SubDependecyExtn subDependecyExtension = project.getExtensions().create("subprojectDeps", SubDependecyExtn.class);
        subDependecyExtension.setParentProject(project);
        if (subDependecyExtension.doLogging())
            System.out.println("Applying " + SubprojectDependencies.class.getSimpleName());

        subDependecyExtension.addSubprojects(project.getSubprojects());

        HashSet<Project> subprojects = subDependecyExtension.getSubprojects();

        if (subprojects.size() > 0)
        {
            Set<String> set = subprojects.stream().map(Project::getName).collect(Collectors.toSet());
            if (subDependecyExtension.doLogging())
                System.out.println("Detected sub-projects: " + set);

            HashMultimap<String, Dependency> dependencyHashMultimap = HashMultimap.create();


            project.afterEvaluate(project1 -> {

                if (subDependecyExtension.doLogging())
                {
                    if (subDependecyExtension.isSimulated())
                        System.out.println("(Simulation)");
                    System.out.println("Gathering specified dependencies: ");
                }
                if (!subDependecyExtension.isSimulated())
                {
                    project1.getConfigurations().forEach(configuration -> {
                        Set<String> usedConfigs = subDependecyExtension.getPassedConfigurations();
                        if (usedConfigs.isEmpty() || usedConfigs.contains(configuration.getName()))
                        {
                            if (subDependecyExtension.doLogging())
                                System.out.println("-> " + configuration.getName());
                            configuration.getDependencies().forEach(dependency -> {
                                dependencyHashMultimap.put(configuration.getName(), dependency);
                            });
                        }
                    });
                }
            });

            subprojects.forEach(subproject -> {
                subproject.afterEvaluate(project1 -> {
                    if (subDependecyExtension.getExcludedProjects().isEmpty() || !subDependecyExtension.getExcludedProjects().contains(project1.getName()))
                    {
                        if (subDependecyExtension.doLogging())
                            System.out.println("Adding parent project dependencies to " + project1.getName());

                        if (!subDependecyExtension.isSimulated())
                        {
                            if (subDependecyExtension.isDependentOnParent())
                                project1.getDependencies().add("implementation", subDependecyExtension.getParentProject());
                            dependencyHashMultimap.entries().forEach(stringDependencyEntry -> {
                                project1.getDependencies().add(stringDependencyEntry.getKey(), stringDependencyEntry.getValue());
                            });
                        }
                        if (subDependecyExtension.doLogging())
                            System.out.println("Success");
                    }
                });
            });

        }
        else
        {
            if (subDependecyExtension.doLogging())
                System.out.println("No subprojects for project '" + project.getName() + "'");
        }
    }


}
