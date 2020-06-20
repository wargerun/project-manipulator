package ProjectManipulator.ProjectProviders;

import ProjectManipulator.ProjectProviders.Impl.NetCoreProjectProcessor;
import ProjectManipulator.ProjectProviders.Impl.NetFrameworkProjectProcessor;
import ProjectManipulator.ProjectProviders.Impl.NetStandardProjectProcessor;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public final class ProjectProcessorFacade {

    public static IProjectProcessor createProjectProcessor(File projectFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(projectFile);

        // NET CORE APP
        String targetFramework = getTextContent(doc, "//TargetFramework/text()");

        if (targetFramework != null && targetFramework.contains("netcoreapp")) {
            return new NetCoreProjectProcessor(targetFramework);
        }
        else if (targetFramework != null && targetFramework.contains("netstandard")) {
            return new NetStandardProjectProcessor(targetFramework);
        }

        // NET FRAMEWORK
        targetFramework = getTextContent(doc, "//TargetFrameworkVersion//text()");

        if (targetFramework != null) {
            return new NetFrameworkProjectProcessor(targetFramework);
        }

        return null;
    }

    private static String getTextContent(Document doc, String xpathStr) throws XPathExpressionException {
        // Create XPath object
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        XPathExpression expr = xpath.compile(xpathStr);
        //evaluate expression result on XML document
        String textContent = expr.evaluate(doc);

        return textContent;
    }}
