import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.cas.impl.XCASSerializer;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasIOUtils;
import org.apache.uima.util.TypeSystemUtil;
import org.apache.uima.util.XmlCasSerializer;
import org.hucompute.uimadockerwrapper.DockerWrappedEnvironment;
import org.hucompute.uimadockerwrapper.DockerWrapperContainerConfiguration;
import org.hucompute.uimadockerwrapper.annotators.ExampleAnnotator;
import org.hucompute.uimadockerwrapper.base_env.DockerBaseJavaEnv;
import org.hucompute.uimadockerwrapper.util.DockerWrapperUtil;
import org.xml.sax.SAXException;

import java.io.*;


public class MainClass {
    public static void main(String []args) throws UIMAException, IOException, SAXException {
        JCas test_c = JCasFactory.createJCas();
        test_c.setDocumentText("Simple change's in the pipelines used.");
        test_c.setDocumentLanguage("en");

// The annotation should be made within a container
        DockerWrapperContainerConfiguration cfg = DockerWrapperContainerConfiguration.default_config()
                .with_run_in_container(true)
                .with_scaleout(3)
                .with_autostop(false)
                .with_tag_name("192.168.0.171:5000/repro")
                .with_unsafe_map_docker_daemon(true);

// Create the wrapped pipeline from any AnalysisEngineDescription
        DockerWrappedEnvironment env = DockerWrappedEnvironment.from(
                AnalysisEngineFactory.createEngineDescription(OpenNlpSegmenter.class)
        ).with_pomfile(new File("pom.xml"));


// Create the docker container, note the programm must have access to the docker daemon,
// therefore the programm must either run as root or the current user has access to the daemon
        System.out.println(DockerWrapperUtil.cas_to_xmi(test_c));
        SimplePipeline.runPipeline(test_c,env.build(cfg));
        System.out.println(DockerWrapperUtil.cas_to_xmi(test_c));
    }
}
