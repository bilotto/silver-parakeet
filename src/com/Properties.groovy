package org.com

//the class reads the properties file through its methods

def get_object() {
	return propertiesEnv
}

def print_object() {
	println propertiesEnv
}

def construct(Map properties_map) {
	propertiesEnv = [:]
	properties_map.each { key, value ->
		propertiesEnv.put(key, value)
	}
	this.print_object()
}


def get_git_properties(String repository){
	if (!propertiesEnv.GIT_PROPERTIES.get(repository)) {
		error "Properties for GIT not found"
	}
	return propertiesEnv.GIT_PROPERTIES.get(repository)
}

def get_node_properties(String node_id) {
	for (entry in propertiesEnv.NODES) {
		for (node in entry.value) {
			nodeId = node.key
			node_properties = node.value
			if (nodeId == node_id) {
				return node_properties
			}

		}
	}
	error "Node ${node_id} not found"

}

def get_resources(){
	return propertiesEnv.get('RESOURCES')
}

def get_install_properties(String node_id) {
	install_properties_id = this.get_node_properties(node_id).get('INSTALL_PROPERTIES')
	install_properties = propertiesEnv.get('INSTALL_PROPERTIES')
	if (install_properties_id) {
		return install_properties.get(install_properties_id)
	}
	return null
}

def get_list_of_nodes(String pm_send_to_env) {
	node_list_map = mergeMaps(propertiesEnv.get('NODE_LIST'), propertiesEnv.get('PROD_NODE_LIST'))
	nodeListKey = pm_send_to_env.toString()
	node_list = []
	if (node_list_map.get(nodeListKey)) {
		node_list = node_list_map.get(nodeListKey)
	} else {
		//it's a standalone node
		//todo: check if the node exists
		node_list.add(nodeListKey)
	}

	return node_list
}

def mapKeys(map) {
	map.keySet() as List
}


def get_environment_parameter_list(Boolean include_production) {
	//get nodes list to be put as parameter
	parameter_node_list = [ 'Select environment' ]
	parameter_node_list = parameter_node_list.plus(this.mapKeys(propertiesEnv.NODE_LIST))
	parameter_node_list = parameter_node_list.plus(this.mapKeys(propertiesEnv.NODES.MAQUETA_NODES))
	if (include_production) {
		parameter_node_list = parameter_node_list.plus(this.mapKeys(propertiesEnv.PROD_NODE_LIST))
		parameter_node_list = parameter_node_list.plus(this.mapKeys(propertiesEnv.NODES.PROD_NODES))
	}
	return parameter_node_list
}


return this