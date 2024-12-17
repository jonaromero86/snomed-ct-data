package dev.ikm.tinkar.snomedct.integration;

import dev.ikm.tinkar.common.util.uuid.UuidT5Generator;
import dev.ikm.tinkar.component.Component;
import dev.ikm.tinkar.coordinate.stamp.StampCoordinateRecord;
import dev.ikm.tinkar.coordinate.stamp.StampPositionRecord;
import dev.ikm.tinkar.coordinate.stamp.StateSet;
import dev.ikm.tinkar.coordinate.stamp.calculator.Latest;
import dev.ikm.tinkar.coordinate.stamp.calculator.StampCalculator;
import dev.ikm.tinkar.entity.EntityService;
import dev.ikm.tinkar.entity.PatternEntityVersion;
import dev.ikm.tinkar.entity.SemanticRecord;
import dev.ikm.tinkar.entity.SemanticVersionRecord;
import dev.ikm.tinkar.terms.EntityProxy;
import dev.ikm.tinkar.terms.TinkarTerm;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DescriptionSemanticIT extends AbstractIntegrationTest {

    /**
     * Test Description Semantics.
     *
     * @result Reads content from file and validates Description of Semantics by calling private method assertDescription().
     */
    @Test
    public void testDescriptionSemantics() throws IOException {
        String sourceFilePath = "../snomed-ct-origin/target/origin-sources/SnomedCT_ManagedServiceUS_PRODUCTION_US1000124_20240901T120000Z/Full/Terminology/sct2_Description_Full-en_US1000124_20240901.txt";
        String errorFile = "target/failsafe-reports/descriptions_not_found.txt";
        AtomicInteger notFound = new AtomicInteger(0);

        processFile(sourceFilePath, errorFile);

        assertEquals(0, notFound.get(), "Unable to find " + notFound.get() + " description semantics. Details written to " + errorFile);
    }


    @Override
    protected boolean assertLine(String[] columns) {
        long effectiveTime = SnomedUtility.snomedTimestampToEpochSeconds(columns[1]);
        StateSet descriptionStatus = Integer.parseInt(columns[2]) == 1 ? StateSet.ACTIVE : StateSet.INACTIVE;
        EntityProxy.Concept descrType = SnomedUtility.getDescriptionType(columns[6]);
        String term = columns[7];
        EntityProxy.Concept caseSensitivityConcept = SnomedUtility.getDescriptionCaseSignificanceConcept(columns[8]);
        UUID id = UuidT5Generator.get(UUID.fromString("3094dbd1-60cf-44a6-92e3-0bb32ca4d3de"), columns[0]); //Need hardcode ID on namespace for Snomed


        StampPositionRecord stampPosition = StampPositionRecord.make(effectiveTime, TinkarTerm.DEVELOPMENT_PATH.nid());
        StampCalculator stampCalc = StampCoordinateRecord.make(descriptionStatus, stampPosition).stampCalculator();
        SemanticRecord entity = EntityService.get().getEntityFast(id);

        PatternEntityVersion latestDescriptionPattern = (PatternEntityVersion) stampCalc.latest(TinkarTerm.DESCRIPTION_PATTERN).get();
        Latest<SemanticVersionRecord> latest = stampCalc.latest(entity);
        if (latest.isPresent()) {
            Component descriptionType = latestDescriptionPattern.getFieldWithMeaning(TinkarTerm.DESCRIPTION_TYPE, latest.get());
            Component descCaseSensitivity = latestDescriptionPattern.getFieldWithMeaning(TinkarTerm.DESCRIPTION_CASE_SIGNIFICANCE, latest.get());
            String text = latestDescriptionPattern.getFieldWithMeaning(TinkarTerm.TEXT_FOR_DESCRIPTION, latest.get());

            return descriptionType.equals(descrType) && descCaseSensitivity.equals(caseSensitivityConcept) && text.equals(term);
        }
        return false;
    }
}
