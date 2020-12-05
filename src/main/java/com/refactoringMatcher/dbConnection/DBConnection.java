package com.refactoringMatcher.dbConnection;

import com.refactoringMatcher.domain.RefactoringExtractionInfo;
import com.refactoringMatcher.domain.RefactoringInfo;
import com.refactoringMatcher.java.ast.decomposition.cfg.Graph;
import com.refactoringMatcher.java.ast.decomposition.cfg.GraphEdge;
import com.refactoringMatcher.java.ast.decomposition.cfg.GraphNode;
import com.refactoringMatcher.java.ast.decomposition.cfg.GroumNode;
import com.refactoringMatcher.utils.PropertyReader;
import org.neo4j.driver.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Diptopol
 * @since 11/28/2020 12:52 AM
 */
public class DBConnection {

    private final Driver driver;
    private Properties properties;

    public DBConnection() {
        properties = new PropertyReader().getProperties();
        driver = GraphDatabase.driver(properties.getProperty("db.uri"),
                AuthTokens.basic(properties.getProperty("db.username"), properties.getProperty("db.passwd")));
    }

    public void close() throws Exception {
        driver.close();
    }

    public List<RefactoringInfo> getRefactoringInfoList() {
        List<RefactoringInfo> refactoringInfoList = new ArrayList<>();

        try (Session session = driver.session()) {
            session.readTransaction(tx -> {
                String query = "MATCH (r:RefactoringInfo) -[c:CONTAINS]->(re:RefactoringExtractionInfo)" +
                        " RETURN r.uuid, r.projectUrl, r.commitId, c.type, re.filePath, re.sourceCode," +
                        " re.startOffset, re.length, re.uuid";

                Result resultList = tx.run(query);

                resultList.list().forEach(result -> {
                    String uuid = result.get("r.uuid").asString();
                    RefactoringInfo refactoringInfo = refactoringInfoList.stream()
                            .filter(ri -> ri.getUuid().equals(uuid))
                            .findFirst()
                            .orElse(new RefactoringInfo(uuid));

                    refactoringInfo.setProjectUrl(result.get("r.projectUrl").asString());
                    refactoringInfo.setCommitId(result.get("r.commitId").asString());

                    String type = result.get("c.type").asString();
                    RefactoringExtractionInfo refactoringExtractionInfo = new RefactoringExtractionInfo(result.get("re.uuid").asString(),
                            result.get("re.filePath").asString(),
                            result.get("re.sourceCode").asString(), result.get("re.startOffset").asInt(), result.get("re.length").asInt());

                    switch (type) {
                        case "extracted":
                            refactoringInfo.setExtracted(refactoringExtractionInfo);
                            break;
                        case "beforeExtraction":
                            refactoringInfo.setBeforeExtraction(refactoringExtractionInfo);
                            break;
                        case "afterExtraction":
                            refactoringInfo.setAfterExtraction(refactoringExtractionInfo);
                            break;
                    }

                    int index = IntStream.range(0, refactoringInfoList.size())
                            .filter(i -> uuid.equals(refactoringInfoList.get(i).getUuid()))
                            .findFirst()
                            .orElse(-1);

                    if (index == -1) {
                        refactoringInfoList.add(refactoringInfo);
                    } else {
                        refactoringInfoList.set(index, refactoringInfo);
                    }
                });

                return refactoringInfoList;
            });
        }

        return refactoringInfoList;
    }

    public String getNodesJson(Set<GroumNode> groumNodes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        Iterator<GroumNode> groumNodeIterator = groumNodes.iterator();

        while (groumNodeIterator.hasNext()) {
            GroumNode groumNode = groumNodeIterator.next();

            stringBuilder.append("{");

            stringBuilder.append("id:").append(groumNode.getId());
            stringBuilder.append(", ");
            stringBuilder.append("groumString:" + "\"" + groumNode.ToGroumString() + "\"");

            stringBuilder.append("}");
            if (groumNodeIterator.hasNext()) {
                stringBuilder.append(",");
            }
        }

        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    public String getEdgeJson(Set<GraphEdge> graphEdges) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");

        Iterator<GraphEdge> graphEdgeIterator = graphEdges.iterator();

        while (graphEdgeIterator.hasNext()) {
            GraphEdge graphEdge = graphEdgeIterator.next();

            stringBuilder.append("{");
            stringBuilder.append("srcId:").append(graphEdge.getSrc().getId());
            stringBuilder.append(",");
            stringBuilder.append("dstId:").append(graphEdge.getDst().getId());
            stringBuilder.append("}");

            if (graphEdgeIterator.hasNext()) {
                stringBuilder.append(",");
            }
        }

        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    public void insertGraphNodes(Graph groum, RefactoringExtractionInfo refactoringExtractionInfo) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                Set<GraphNode> graphNodes = groum.getNodes();
                Set<GroumNode> groumNodes = graphNodes.stream()
                        .map(node -> (GroumNode) node)
                        .collect(Collectors.toSet());

                String nodeInsertQuery = "UNWIND " + getNodesJson(groumNodes) +" AS nodeVal" +
                        " CREATE (n:node {id: nodeVal.id, groumString: nodeVal.groumString, refactoringExtractionInfo_uuid:$refactoringExtractionInfoUUID})";

                Map<String, Object> nodeInsertParameters = new HashMap<>();
                nodeInsertParameters.put("refactoringExtractionInfoUUID", refactoringExtractionInfo.getUuid());

                return tx.run(nodeInsertQuery, nodeInsertParameters);
            });
        }
    }

    public void insertGraphEdge(Graph groum, RefactoringExtractionInfo refactoringExtractionInfo) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                Set<GraphEdge> graphEdges = groum.getEdges();

                String edgeInsertQuery = "UNWIND " + getEdgeJson(graphEdges) + " AS edge" +
                        " MATCH (n1: node{id: edge.srcId, refactoringExtractionInfo_uuid:'" + refactoringExtractionInfo.getUuid() + "'})," +
                        " (n2: node{id:edge.destId, refactoringExtractionInfo_uuid:'" + refactoringExtractionInfo.getUuid() + "'})" +
                        " MERGE (n1)-[e:edge]->(n2)";

                return tx.run(edgeInsertQuery);
            });
        }
    }



    public void insertGroumRepresentation(Graph groum, RefactoringExtractionInfo refactoringExtractionInfo) {
        insertGraphNodes(groum, refactoringExtractionInfo);
        insertGraphEdge(groum, refactoringExtractionInfo);

        /*try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {
                //TODO: not complete
                *//*String linkQeury = "MATCH (n:node), (re:refactoringExtractionInfo)" +
                        " WHERE n.id = 1" +
                        " AND n.refactoringExtractionInfo_uuid = $refactoringExtractionInfoUUID AND re.uuid = $refactoringExtractionInfoUUID" +
                        " CREATE (re)-[rep:representation{name: 'groum'}]-> (n)";

                Map<String, Object> linkInsertParameter = new HashMap<>();

                transaction.run(linkQeury, nodeInsertParameters);
                transaction.run(edgeInsertQuery, linkInsertParameter);*//*
            }
        }*/
    }

    public void insert(RefactoringInfo refactoringInfo) {
        try (Session session = driver.session()) {

            session.writeTransaction(tx -> {
                String query = "MERGE (refactoringInfo:RefactoringInfo {projectUrl: $projectUrl, commitId: $commitId, uuid: apoc.create.uuid()})" +
                        " MERGE (extracted:RefactoringExtractionInfo {filePath: $eFilePath, sourceCode: $eSourceCode, startOffset: $eStartOffset, length: $eLength, uuid: apoc.create.uuid()})" +
                        " MERGE (beforeExtraction:RefactoringExtractionInfo {filePath: $bFilePath, sourceCode: $bSourceCode, startOffset: $bStartOffset, length: $bLength, uuid: apoc.create.uuid()})" +
                        " MERGE (afterExtraction:RefactoringExtractionInfo {filePath: $aFilePath, sourceCode: $aSourceCode, startOffset: $aStartOffset, length: $aLength, uuid: apoc.create.uuid()})" +
                        " WITH refactoringInfo, extracted, beforeExtraction, afterExtraction" +
                        " MERGE (refactoringInfo) - [ownExtracted:CONTAINS{type: 'extracted'}] -> (extracted)" +
                        " MERGE (refactoringInfo) - [ownBeforeExtraction:CONTAINS{type: 'beforeExtraction'}]-> (beforeExtraction)" +
                        " MERGE (refactoringInfo) -[ownAfterExtraction:CONTAINS{type: 'afterExtraction'}] -> (afterExtraction)";

                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("projectUrl", refactoringInfo.getProjectUrl());
                parameterMap.put("commitId", refactoringInfo.getCommitId());

                parameterMap.put("eFilePath", refactoringInfo.getExtracted().getFilePath());
                parameterMap.put("eSourceCode", refactoringInfo.getExtracted().getSourceCode());
                parameterMap.put("eStartOffset", refactoringInfo.getExtracted().getStartOffset());
                parameterMap.put("eLength", refactoringInfo.getExtracted().getLength());

                parameterMap.put("bFilePath", refactoringInfo.getBeforeExtraction().getFilePath());
                parameterMap.put("bSourceCode", refactoringInfo.getBeforeExtraction().getSourceCode());
                parameterMap.put("bStartOffset", refactoringInfo.getBeforeExtraction().getStartOffset());
                parameterMap.put("bLength", refactoringInfo.getBeforeExtraction().getLength());

                parameterMap.put("aFilePath", refactoringInfo.getAfterExtraction().getFilePath());
                parameterMap.put("aSourceCode", refactoringInfo.getAfterExtraction().getSourceCode());
                parameterMap.put("aStartOffset", refactoringInfo.getAfterExtraction().getStartOffset());
                parameterMap.put("aLength", refactoringInfo.getAfterExtraction().getLength());


                return tx.run(query, parameterMap);
            });
        }
    }
}