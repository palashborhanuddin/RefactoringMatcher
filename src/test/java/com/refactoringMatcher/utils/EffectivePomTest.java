package com.refactoringMatcher.utils;

import org.eclipse.jgit.lib.Repository;
import org.junit.Test;
import org.refactoringminer.api.GitService;
import org.refactoringminer.util.GitServiceImpl;

import java.util.Optional;

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
}
