package com.refactoringMatcher.utils;

import io.vavr.Tuple3;
import org.eclipse.jgit.lib.Repository;
import org.junit.Test;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Diptopol
 * @since 12/17/2020 12:31 AM
 */
public class EffectivePomTest {

    @Test
    public void effectivePomTest() throws Exception {
        GitService gitService = new GitServiceImpl();

        String commitId = "b6e7262c1c4d0ef6ccafd3ed2a929ce0dbea860c";
        String repositoryFullName = "diptopol-RefactoringMinerIssueReproduction";
        String repositoryUrl = "https://github.com/diptopol/RefactoringMinerIssueReproduction";

        Repository repository = gitService.cloneIfNotExists(GitUtils.getRepositoryLocalDirectory(repositoryFullName), repositoryUrl);

        Optional<String> effectivePOM = GitUtils.generateEffectivePom(commitId, repository);

        if (effectivePOM.isPresent()) {
            //System.out.println(effectivePOM);
            assert true;
        } else {
            //System.out.println("No POM found");
            assert false;
        }
    }

    @Test
    public void jarDependenciesFromPomTest() throws Exception {
        GitService gitService = new GitServiceImpl();

        String commitId = "b6e7262c1c4d0ef6ccafd3ed2a929ce0dbea860c";
        String repositoryFullName = "diptopol-RefactoringMinerIssueReproduction";
        String repositoryUrl = "https://github.com/diptopol/RefactoringMinerIssueReproduction";

        Repository repository = gitService.cloneIfNotExists(GitUtils.getRepositoryLocalDirectory(repositoryFullName), repositoryUrl);

        Optional<String> effectivePOM = GitUtils.generateEffectivePom(commitId, repository);

        Set<Tuple3<String, String, String>> jarSet = new HashSet<>();
        if (effectivePOM.isPresent()) {
           jarSet = GitUtils.listOfJavaProjectLibraryFromEffectivePom(effectivePOM.get());
        }
        System.out.println(jarSet.toString());
    }
}
