package com.prowesssoft.t2m.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {

	public static final Map<String,String> projectStatus;
	static {
		Map<String,String> status = new HashMap<String,String>();
		status.put("UPLOAD_START","Upload Started");
		status.put("UPLOAD_COMPLETE","Upload Completed");
		status.put("CONVERSION_START","Conversion Started");
		status.put("EXTRACTING_ARTIFACT_START","Extracting the artifacts");
		status.put("EXTRACTING_ARTIFACT_END","Extracted the artifacts");
		status.put("IDENTIFY_API_START","Identifying the APIs");
		status.put("IDENTIFY_API_END","Identified the APIs");
		status.put("PUBLISH_DSGN_CENTER_START","Publishing projects to Design Center");
		status.put("PUBLISH_DSGN_CENTER_END","Published projects to Design Center");
		status.put("PUBLISH_EXCHANGE_START","Publishing projects to Exchange");
		status.put("PUBLISH_EXCHANGE_END","Published projects to Exchange");
		status.put("CREATE_MULE_PROJ_START","Creating Mule Projects");
		status.put("CREATE_MULE_PROJ_END","Created Mule Projects");
		status.put("CONFIG_MULE_PROJ_START","Configuring the Mule Projects");
		status.put("CONFIG_MULE_PROJ_END","Configured the Mule Projects");
		status.put("PREPARE_DOWNLOAD_LINK","Preparing the project download link");
		status.put("READY_TO_DOWNLOAD","Ready to download the projects");
		status.put("CONVERSION_END","Conversion Completed");
		status.put("PROJECT_EXPIRED","Project Expired and Artifacts deleted");
		status.put("UPLOAD_FAILED","Upload Failed");
		status.put("CONVERSION_FAILED","Conversion Failed");
		status.put("NO_APPS_FOUND","No apps found in batch file");
		status.put("PROVERSE_FAILED","Failed to generate Proverse output");
		status.put("NO_EXCHANGE_PROJECTS","No projects to publish on exchange");
		status.put("NO_MULE_PROJ","No mule projects created");
		status.put("ANALYSIS_START","Analysis Started");
		status.put("ANALYSIS_END","Analysis Completed");
		status.put("ANALYSIS_FAILED","Analysis Failed");
		status.put("CREATE_FRAGMENTS","Creating RAML fragments");
		status.put("CREATED_FRAGMENTS","Created RAML fragments");
		projectStatus = Collections.unmodifiableMap(status);
	}
	
	public static interface ProjectConfigs {
        String INPUT_TYPE = "INPUT_TYPE";
        String CONFIG_FILE_PATH = "CONFIG_FILE_PATH";
        String RAML_NAME = "CONFIG_FILE_PATH";
        String PRESERVE_CASE = "PRESERVE_CASE";
        String PUBLISH_TO_DC = "PUBLISH_TO_DC";
    }
	
	public static interface ProjectType{
		String E2EAUTO = "1";
		String XSD2RAML = "2";
		String XSLT2DWL = "3";
		String GV2YAML = "4";
		String DMNANLS = "5";
	}
	
	public static String getProjectType(String s) {
		if(s == null) {
			return "N/A";
		}
		String type = "N/A";
		switch (s) {
		case "1":
			type = "TIBCO to Mule";
			break;
		case "2":
			type = "Generate RAML's";
			break;
		case "3":
			type = "Generate Transformations";
			break;
		case "4":
			type = "Convert Global Variables";
			break;
		case "5":
			type = "TIBCO Domain Analysis";
			break;

		default:
			break;
		}
		
		return type;
	}
	
}
