package dev.ikm.maven;

import dev.ikm.tinkar.common.service.CachingService;
import dev.ikm.tinkar.common.service.PrimitiveData;
import dev.ikm.tinkar.common.service.ServiceKeys;
import dev.ikm.tinkar.common.service.ServiceProperties;
import dev.ikm.tinkar.common.util.uuid.UuidT5Generator;
import dev.ikm.tinkar.common.util.uuid.UuidUtil;
import dev.ikm.tinkar.composer.Composer;
import dev.ikm.tinkar.composer.Session;
import dev.ikm.tinkar.composer.assembler.ConceptAssembler;
import dev.ikm.tinkar.composer.template.*;
import dev.ikm.tinkar.terms.EntityProxy;
import dev.ikm.tinkar.terms.State;
import dev.ikm.tinkar.terms.TinkarTerm;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import dev.ikm.maven.datastore.proxy.DatastoreProxy;

import java.io.File;
import java.util.UUID;

import static dev.ikm.tinkar.terms.TinkarTerm.DESCRIPTION_NOT_CASE_SENSITIVE;
import static dev.ikm.tinkar.terms.TinkarTerm.ENGLISH_LANGUAGE;

/**
 * Hello world!
 *
 */
@Mojo(name = "run-snomed-starterdata", defaultPhase = LifecyclePhase.INSTALL)
public class SnomedStarterDataMojo extends AbstractMojo
{
    public void execute() throws MojoExecutionException
    {
        try (DatastoreProxy datastoreProxy =  new DatastoreProxy()) {

//            File datastore = new File("/Users/jsteinhafel/Solor/generated-data");
//
//            CachingService.clearAll();
//            ServiceProperties.set(ServiceKeys.DATA_STORE_ROOT, datastore);
//            PrimitiveData.selectControllerByName("Open SpinedArrayStore");
//            PrimitiveData.start();

            datastoreProxy.open();

            Composer composer = new Composer("Snomed Starter Data Composer");

            UUID namespace = UUID.fromString("3094dbd1-60cf-44a6-92e3-0bb32ca4d3de");

            Session session = composer.open(State.ACTIVE,
                    TinkarTerm.USER,
                    TinkarTerm.PRIMORDIAL_MODULE,
                    TinkarTerm.PRIMORDIAL_PATH);

            EntityProxy.Concept snomedAuthor = EntityProxy.Concept.make("IHTSDO SNOMED CT Author", UuidT5Generator.get(namespace, "IHTSDO SNOMED CT Author"));
            session.compose((ConceptAssembler concept) -> concept
                    .concept(snomedAuthor)
                    .attach((FullyQualifiedName fqn) -> fqn
                            .language(ENGLISH_LANGUAGE)
                            .text("IHTSDO SNOMED CT Author")
                            .caseSignificance(DESCRIPTION_NOT_CASE_SENSITIVE)
                    )
                    .attach((Synonym synonym)-> synonym
                            .language(ENGLISH_LANGUAGE)
                            .text("SNOMED CT Author")
                            .caseSignificance(DESCRIPTION_NOT_CASE_SENSITIVE)
                    )
                    .attach((Definition definition) -> definition
                            .language(ENGLISH_LANGUAGE)
                            .text("International Health Terminology Standards Development Organisation (IHTSDO) SNOMED CT Author")
                            .caseSignificance(DESCRIPTION_NOT_CASE_SENSITIVE)
                    )
                    .attach((Identifier identifier) -> identifier
                            .source(TinkarTerm.UNIVERSALLY_UNIQUE_IDENTIFIER)
                            .identifier(snomedAuthor.asUuidArray()[0].toString())
                    )
                    .attach((StatedAxiom statedAxiom) -> statedAxiom
                            .isA(TinkarTerm.USER)
                    )
            );

            EntityProxy.Concept snomedIdentifier = EntityProxy.Concept.make("SNOMED CT Identifier", UuidUtil.fromSNOMED("900000000000294009"));
            session.compose((ConceptAssembler concept) -> concept
                    .concept(snomedIdentifier)
                    .attach((FullyQualifiedName fqn) -> fqn
                            .language(ENGLISH_LANGUAGE)
                            .text("SNOMED CT Identifier")
                            .caseSignificance(DESCRIPTION_NOT_CASE_SENSITIVE)
                    )
                    .attach((Synonym synonym)-> synonym
                            .language(ENGLISH_LANGUAGE)
                            .text("SCTID")
                            .caseSignificance(DESCRIPTION_NOT_CASE_SENSITIVE)
                    )
                    .attach((Definition definition) -> definition
                            .language(ENGLISH_LANGUAGE)
                            .text("Unique point of origin for identifier")
                            .caseSignificance(DESCRIPTION_NOT_CASE_SENSITIVE)
                    )
                    .attach((Identifier identifier) -> identifier
                            .source(TinkarTerm.UNIVERSALLY_UNIQUE_IDENTIFIER)
                            .identifier(snomedIdentifier.asUuidArray()[0].toString())
                    )
                    .attach((StatedAxiom statedAxiom) -> statedAxiom
                            .isA(TinkarTerm.IDENTIFIER_SOURCE)
                    )
            );
            composer.commitSession(session);
//            PrimitiveData.stop();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to execute class", e);
        }

    }
}
