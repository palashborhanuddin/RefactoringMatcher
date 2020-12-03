package com.refactoringMatcher.refactoringDetection;

import com.refactoringMatcher.domain.RepositoryInfo;
import com.refactoringMatcher.utils.GitUtils;
import com.refactoringMatcher.utils.PropertyReader;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.refactoringminer.api.*;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Diptopol
 * @since 11/28/2020 9:36 AM
 */
public class RefactoringDetection {

    private static Logger logger = LoggerFactory.getLogger(RefactoringDetection.class);

    private Properties properties;
    private GitService gitService;
    private GitHistoryRefactoringMiner miner;

    public RefactoringDetection() {
        properties = new PropertyReader().getProperties();
        gitService = new GitServiceImpl();
        miner = new GitHistoryRefactoringMinerImpl();
    }


    public void detectRefactoringData(List<RepositoryInfo> repositoryInfoList) {
        for (RepositoryInfo repositoryInfo : repositoryInfoList) {
            detectRefactoringData(repositoryInfo);
        }
    }

    public List<Pair<String, Refactoring>> detectRefactoringData(RepositoryInfo repositoryInfo) {
        logger.info("Full Name: {}", repositoryInfo.getFullName());

        String repositoryLocalDirectory = GitUtils.getRepositoryLocalDirectory(repositoryInfo);
        List<Pair<String, Refactoring>> refactoringPairList = new ArrayList<>();

        try {
            Repository repo = gitService.cloneIfNotExists(
                    repositoryLocalDirectory,
                    repositoryInfo.getHtmlUrl());

            List<String> commitIdList = new ArrayList<>();

            RefactoringHandler refactoringHandler = new RefactoringHandler() {

                public void handle(String commitId, List<Refactoring> refs) {
                    if (refs == null)
                        return;

                    for (Refactoring ref : refs) {
                        if (ref.getRefactoringType() == RefactoringType.EXTRACT_OPERATION) {
                            commitIdList.add(commitId);
                            refactoringPairList.add(new MutablePair<>(commitId, ref));
                        }
                    }
                }
            };

            RevWalk walk = gitService.createAllRevsWalk(repo, "master");


            for (RevCommit revCommit : walk) {
                String commitId = revCommit.getId().getName();

                miner.detectAtCommit(repo, commitId, refactoringHandler, Integer.valueOf(properties.getProperty("refactoring.timeout.per.commit")));
            }

            logger.info("Print commit id list with size {}", commitIdList.size());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return refactoringPairList;
    }

    public List<Pair<String, Refactoring>> detectRefactoringData(RepositoryInfo repositoryInfo, String commitId) {
        logger.info("Full Name: {}", repositoryInfo.getFullName());

        String repositoryLocalDirectory = GitUtils.getRepositoryLocalDirectory(repositoryInfo);
        List<Pair<String, Refactoring>> refactoringPairList = new ArrayList<>();

        try {
            Repository repo = gitService.cloneIfNotExists(
                    repositoryLocalDirectory,
                    repositoryInfo.getHtmlUrl());

            List<String> commitIdList = new ArrayList<>();

            RefactoringHandler refactoringHandler = new RefactoringHandler() {

                public void handle(String commitId, List<Refactoring> refs) {
                    if (refs == null)
                        return;

                    for (Refactoring ref : refs) {
                        if (ref.getRefactoringType() == RefactoringType.EXTRACT_OPERATION) {
                            commitIdList.add(commitId);
                            refactoringPairList.add(new MutablePair<>(commitId, ref));
                        }
                    }
                }
            };

            miner.detectAtCommit(repo, commitId, refactoringHandler, Integer.valueOf(properties.getProperty("refactoring.timeout.per.commit")));

            logger.info("Print commit id list with size {}", commitIdList.size());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return refactoringPairList;
    }

}
