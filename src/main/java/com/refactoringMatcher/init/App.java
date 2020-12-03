package com.refactoringMatcher.init;

import com.refactoringMatcher.dbConnection.DBConnection;
import com.refactoringMatcher.domain.*;
import com.refactoringMatcher.java.ast.decomposition.cfg.Graph;
import com.refactoringMatcher.java.ast.decomposition.cfg.GraphEdge;
import com.refactoringMatcher.java.ast.decomposition.cfg.GraphNode;
import com.refactoringMatcher.java.ast.decomposition.cfg.GroumNode;
import com.refactoringMatcher.refactoringDetection.RefactoringDetection;
import com.refactoringMatcher.service.RefactoringExtractionService;
import com.refactoringMatcher.utils.Cache;
import com.refactoringMatcher.utils.GitUtils;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.util.GitServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Diptopol
 * @since 11/28/2020 12:13 AM
 */
public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        logger.info("Init");

        DBConnection connection = new DBConnection();
        //connection.test();

        RefactoringExtractionService extractionService = new RefactoringExtractionService();

         RefactoringDetection refactoringDetection = new RefactoringDetection();

        RepositoryInfo repositoryInfo = new RepositoryInfo("Guardsquare-proguard",
                "https://github.com/Guardsquare/proguard");

        /*
         * Save the refactoring information
         */
        //saveRefactoringInfo(refactoringDetection, extractionService, connection, repositoryInfo);


        List<RefactoringInfo> refactoringInfoList = connection.getRefactoringInfoList();

        for (RefactoringInfo refactoringInfo : refactoringInfoList) {
            extractionService.populateRefactoringExtractionInfo(refactoringInfo.getExtracted(), "extracted");
            extractionService.populateRefactoringExtractionInfo(refactoringInfo.getBeforeExtraction(), "beforeExtraction");
            extractionService.populateRefactoringExtractionInfo(refactoringInfo.getAfterExtraction(), "afterExtraction");
        }


        for (RefactoringInfo refactoringInfo : refactoringInfoList) {
            RefactoringExtractionInfo refactoringExtractionInfo = refactoringInfo.getExtracted();

            if (Objects.nonNull(refactoringExtractionInfo.getGroum())) {
                connection.insertGroumRepresentation(refactoringExtractionInfo.getGroum(), refactoringExtractionInfo);
            }
        }

        /*List<RefactoringData> refactoringDataList = GitUtils.generateRefactoringData(refactoringPairList, repositoryInfo);

        Graph groumGraph = refactoringDataList.get(0).getExtractedMethodGroum();

        for(GraphNode graphNode : groumGraph.getNodes()) {
            if (graphNode instanceof GroumNode) {
                System.out.println(((GroumNode) graphNode).ToGroumString());
            }
        }

        for (GraphEdge graphEdge : groumGraph.getEdges()) {
            GroumNode srcNode = (GroumNode) graphEdge.getSrc();
            GroumNode dstNode = (GroumNode) graphEdge.getDst();

            System.out.println("From: " + srcNode.ToGroumString() + " To" + dstNode.ToGroumString());
        }*/

        /*for (RefactoringData refactoringData : refactoringDataList) {
            System.out.println("Extracted");
            System.out.println(refactoringData.getExtractedMethodCode());

            System.out.println("Before Extraction");
            System.out.println(refactoringData.getSourceMethodBeforeExtractionCode());

            System.out.println("After Extraction");
            System.out.println(refactoringData.getSourceMethodAfterExtractionCode());
        }*/

        connection.close();
    }

    private static void saveRefactoringInfo(RefactoringDetection refactoringDetection,
                                            RefactoringExtractionService extractionService,
                                            DBConnection connection,
                                            RepositoryInfo repositoryInfo) throws Exception {

        List<Pair<String, Refactoring>> refactoringPairList =
                refactoringDetection.detectRefactoringData(repositoryInfo);

        List<RefactoringInfo> refactoringList = extractionService.getRefactoringInfo(refactoringPairList, repositoryInfo);

        for (RefactoringInfo refactoringInfo : refactoringList) {
            connection.insert(refactoringInfo);
        }
    }

}
