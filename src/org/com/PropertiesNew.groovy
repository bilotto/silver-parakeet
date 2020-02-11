package org.com

class PropertiesNew {
	Map propertiesMap
	PropertiesNew(propertiesMap) {
		this.propertiesMap = propertiesMap
	}
	
	
	Map getGitProperties(String projectName) {
		if (!propertiesMap.GIT_PROPERTIES.get(projectName)) {
			error "Git project properties not found"
		}
		return propertiesMap.GIT_PROPERTIES.get(projectName)                               
	}
	
	
	Map getNodeProperties(String nodeId) {
		for (entry in propertiesMap.NODES) {
			for (node in entry.value) {
				if (nodeId == node.key) {
					return node.value
				}
			}
		}
		error "Node ${nodeId} not found"
	}	
}
