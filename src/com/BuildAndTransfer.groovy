package org.com

buildAndTransferEnv

//this class uses the release and node objects
//this class is responsible for generating the release and sending it to the nodes

def construct(Release release, Map node_objects, Map jump_server_objects){
	buildAndTransferEnv = [
			release : release,
			node_objects: node_objects,
			jump_server_objects: jump_server_objects,
			node_objects_list: null,
			jump_server_objects_list: null,
	]
	node_objects_list = []
	jump_server_objects_list = []
	node_objects.each{ k, v -> node_objects_list.add(v) }
	jump_server_objects.each{ k, v -> jump_server_objects_list.add(v)}
	buildAndTransferEnv = overrideObjects(buildAndTransferEnv, 'node_objects_list', node_objects_list)
	buildAndTransferEnv = overrideObjects(buildAndTransferEnv, 'jump_server_objects_list', jump_server_objects_list)
	this.print_object()
}

def get_object() {
	return buildAndTransferEnv
}

def print_object() {
	println buildAndTransferEnv
}

def get_jump_server_objects() {
	jump_server_map = [:]
	buildAndTransferEnv.node_object_list.eachWithIndex { node, index ->
		curr_node_obj_attributes = node.get_object()
		jump_server_node_object = curr_node_obj_attributes.jump_server
		if (jump_server_node_object != null) {
			jump_server_map.put(jump_server_node_object, null)
		}
	}
	return jump_server_map.keySet() as List
}

def build_and_transfer() {
	release = buildAndTransferEnv.release
	node_objects_list = buildAndTransferEnv.node_objects_list
	jump_server_objects_list = buildAndTransferEnv.jump_server_objects_list
	stage('Create Installer') {
		release_file = release.create_release()
	}
	stage('Transfer to Jump Server') {
		jump_server_objects_list = buildAndTransferEnv.jump_server_objects_list
		if (jump_server_objects_list.size() > 0) {
			def branches = [:]
			jump_server_objects_list.eachWithIndex { jp_node, index ->
				jp_node_attr = jp_node.get_object()
				hostname = jp_node_attr.hostname.toString()
				branches["Remote ${hostname}"] = { jp_node.copy_release(release, release_file) }
			}
			parallel branches
		}
	}
	stage('Transfer to node') {
		def branches = [:]
		node_objects_list.eachWithIndex { node, index ->
			node_attr = node.get_object()
			hostname = node_attr.hostname.toString()
			branches["Remote ${hostname}"] = { node.copy_release(release, release_file) }
		}
		parallel branches
	}
}

return this