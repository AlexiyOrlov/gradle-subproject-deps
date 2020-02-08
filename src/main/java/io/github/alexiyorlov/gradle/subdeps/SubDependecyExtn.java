package io.github.alexiyorlov.gradle.subdeps;

import org.gradle.api.Project;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

@SuppressWarnings("unused")
public class SubDependecyExtn
{
    private Project parentProject;
    private boolean doLogging = true;
    private HashSet<String> excludedProjects;

    private boolean simulate;
    private boolean dependOnParent = true;
    private HashSet<Project> subprojects;
    private HashSet<String> passedConfigurations;

    public SubDependecyExtn()
    {
        passedConfigurations = new HashSet<>(4);
        excludedProjects = new HashSet<>(3);
        subprojects = new HashSet<>(3);
    }

    /**
     * Gradle build can have only one 'settings.gradle' file (in root project), so
     * child projects of the subprojects must be included there to be affected
     * by this plugin
     */
    public void addSubprojects(Collection<Project> subprojects)
    {
        this.subprojects.addAll(subprojects);
    }

    public void addSubprojects(Project... subprojects)
    {
        this.subprojects.addAll(Arrays.asList(subprojects));
    }

    /**
     * Adds subprojects by name
     */
    public void addSubprojects(String... subprojects)
    {
        for (String subproject : subprojects)
        {
            for (Project subprjct : parentProject.getSubprojects())
            {
                if (subprjct.getName().equals(subproject))
                {
                    this.subprojects.add(subprjct);
                    break;
                }
            }
        }
    }

    /**
     * Adds subprojects by name
     */
    public void addSubProjects(Collection<String> names)
    {
        for (String name : names)
        {
            for (Project subproject : parentProject.getSubprojects())
            {
                if (subproject.getName().equals(name))
                {
                    this.subprojects.add(subproject);
                    break;
                }
            }
        }
    }

    public HashSet<Project> getSubprojects()
    {
        return subprojects;
    }

    /**
     * Excludes projects by name
     */
    public void excludeProjects(String... excludedProjects)
    {
        this.excludedProjects.addAll(Arrays.asList(excludedProjects));
    }

    /**
     * Excludes projects by name
     */
    public void excludeProjects(Collection<String> excludedProjects)
    {
        this.excludedProjects.addAll(excludedProjects);
    }

    public HashSet<String> getExcludedProjects()
    {
        return excludedProjects;
    }

    public HashSet<String> getPassedConfigurations()
    {
        return passedConfigurations;
    }

    /**
     * Control what configurations will be passed to the subprojects (default is all)
     *
     * @param usedConfigurations names of dependency configurations
     */
    public void setPassedConfigurations(String... usedConfigurations)
    {
        this.passedConfigurations = new HashSet<>(Arrays.asList(usedConfigurations));
    }

    /**
     * Set logging to console on or off
     */
    public void setDoLogging(boolean doLogging)
    {
        this.doLogging = doLogging;
    }

    public boolean doLogging()
    {
        return doLogging;
    }

    /**
     * If true, then all actions are simulated (do not affect the project).
     */
    public boolean isSimulated()
    {
        return simulate;
    }

    public void simulate(boolean simulate)
    {
        this.simulate = simulate;
    }

    /**
     * Whether to add a dependency on the parent project to the subprojects (true by default)
     */
    public void dependOnParent(boolean dependOnParent)
    {
        this.dependOnParent = dependOnParent;
    }

    public boolean isDependentOnParent()
    {
        return dependOnParent;
    }

    public Project getParentProject()
    {
        return parentProject;
    }

    /**
     * Sets the parent project.
     */
    void setParentProject(Project project)
    {
        parentProject = project;
    }
}
