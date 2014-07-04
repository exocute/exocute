package com.exocute.pipeline.tools;

import java.util.ArrayList;
import java.util.List;

public class PipelineRep {

	private String name;
	private List activities = new ArrayList();
	private String importName;
	private String exportName;
	
		
	public PipelineRep()
		{ super(); }

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List getActivities() {
		return activities;
	}

	public void addActivity(ActivityRep activity) {
		activities.add(activity);
	}
	
	public String getExportName() {
		return exportName;
	}

	public void setExportName(String exportName) {
		this.exportName = exportName;
	}

	public String getImportName() {
		return importName;
	}

	public void setImportName(String importName) {
		this.importName = importName;
	}
	
	
}
