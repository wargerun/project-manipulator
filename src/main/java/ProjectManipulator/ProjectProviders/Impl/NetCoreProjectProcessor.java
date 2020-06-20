package ProjectManipulator.ProjectProviders.Impl;

import ProjectManipulator.Helpers.FrameworkType;

public class NetCoreProjectProcessor extends NetStandardProjectProcessor {
    @Override
    protected FrameworkType getFrameworkType() {
        return FrameworkType.NetCore;
    }

    public NetCoreProjectProcessor(String version) {
        super(version);
    }
}
