package com.refactoringMatcher.utils;

import com.refactoringMatcher.dbConnection.DBConnection;
import com.refactoringMatcher.domain.RefactoringExtractionInfo;
import com.refactoringMatcher.domain.RefactoringInfo;
import org.junit.Test;

import java.util.List;

/**
 * @author Diptopol
 * @since 12/18/2020 9:05 PM
 */
public class ASTUtilsTest {

    @Test
    public void methodCodeExtractionTest() {
        DBConnection connection = new DBConnection();

        List<RefactoringInfo> refactoringInfoList = connection.getRefactoringInfoList();

        RefactoringExtractionInfo firstRefactoringExtractionInfo = refactoringInfoList.get(0).getExtracted();

        try {
            String code = ASTUtils.extractText(firstRefactoringExtractionInfo.getStartOffset(),
                    firstRefactoringExtractionInfo.getLength(),
                    firstRefactoringExtractionInfo.getSourceCode(), firstRefactoringExtractionInfo.getFilePath());

            //System.out.println(code);
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }

    }
}
