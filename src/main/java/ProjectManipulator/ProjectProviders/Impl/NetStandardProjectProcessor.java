package ProjectManipulator.ProjectProviders.Impl;

import ProjectManipulator.Helpers.FrameworkType;
import ProjectManipulator.Helpers.TargetFramework;
import ProjectManipulator.Helpers.Version;
import ProjectManipulator.ProjectProviders.IProjectProcessor;
import org.w3c.dom.Document;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class NetStandardProjectProcessor implements IProjectProcessor {

    protected FrameworkType getFrameworkType() {
        return FrameworkType.NetStandard;
    }

    private TargetFramework targetFramework = new TargetFramework(getFrameworkType());;

    public NetStandardProjectProcessor(String version) {
        targetFramework.setVersion(version);
    }

    @Override
    public TargetFramework GetTargetFramework() {
        return targetFramework;
    }

    @Override
    public Version GetProjectVersion(File file) {
        Version version;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("//FileVersion/text()");
            String versionStr = expr.evaluate(doc);

            // Если узел </FileVersion> отсутствует в проектах .net core, то по умолчанию используется начальная версия проекта
            if (versionStr != null && !versionStr.isEmpty()){
                version = new Version(versionStr);
            }
            else {
                version = new Version("1.0.0.0");
            }
        }
        catch (Exception ex) {
            version = null;
        }

        return version;
    }

    @Override
    public void SetProjectVersion(File file, Version version) {
        throw new NotImplementedException();
    }
}
