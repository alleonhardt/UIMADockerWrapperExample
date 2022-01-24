import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.uimadockerwrapper.DockerWrappedEnvironment;
import org.hucompute.uimadockerwrapper.DockerWrapperContainerConfiguration;
import org.hucompute.uimadockerwrapper.util.DockerWrapperUtil;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class MainClass {
    public static void main(String []args) throws UIMAException, IOException, SAXException {
        JCas test_c = JCasFactory.createJCas();
        test_c.setDocumentText("Simple change's in the pipelines used.");
        test_c.setDocumentLanguage("en");

// The annotation should be made within a container
        DockerWrapperContainerConfiguration cfg = DockerWrapperContainerConfiguration.default_config()
                .with_run_in_container(true);

// Create the wrapped pipeline from any AnalysisEngineDescription
        DockerWrappedEnvironment env = DockerWrappedEnvironment.from(
                AnalysisEngineFactory.createEngineDescription(OpenNlpSegmenter.class)
        ).with_pomfile(new File("pom.xml"));

// Create the docker container, note the programm must have access to the docker daemon,
// therefore the programm must either run as root or the current user has access to the daemon
        System.out.println(DockerWrapperUtil.cas_to_xmi(test_c));
        SimplePipeline.runPipeline(test_c,env.build(cfg));

    }
}
