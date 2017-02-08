package com.eisenvault.repo.actions;

import java.util.List;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;

public class DeleteVersionsAction extends ActionExecuterAbstractBase{

	public static final String PARAM_VERSION_TO_DELETE = "version";
	
	/**
     * The Alfresco Service Registry that gives access to all public content services in Alfresco.
     */
    private ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
    
    private VersionService versionService;
    
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		
		String version = (String) action.getParameterValue(PARAM_VERSION_TO_DELETE);
		
		NodeRef versionedNodeRef = actionedUponNodeRef;
		versionService = serviceRegistry.getVersionService();
		VersionHistory versionHistory = this.versionService.getVersionHistory(versionedNodeRef);
		
		Version versionToBeDeleted = null;
		versionToBeDeleted = versionHistory.getVersion(version);
		serviceRegistry.getVersionService().deleteVersion(actionedUponNodeRef, versionToBeDeleted);
		
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		String version = PARAM_VERSION_TO_DELETE;
        paramList.add(new ParameterDefinitionImpl(version, DataTypeDefinition.TEXT, true, getParamDisplayLabel(version)));
	}

}
