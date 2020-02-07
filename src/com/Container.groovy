package org.foo

containerEnv

def print_object() {
	println containerEnv
}

def get_object() {
	return containerEnv
}

def docker_exec(command){
	return "docker exec -d ${containerEnv.name} /bin/bash -c ${command}"
}

def docker_exec_bkp(command){
	return "docker exec ${containerEnv.name} ${command}"
}

def docker_cp_to_container(file, destination_dir){
	return "docker cp ${file} ${containerEnv.name}:${destination_dir}"
}

def docker_cp_from_container(file, destination_dir){
	return "docker cp ${containerEnv.name}:${file} ${destination_dir}"
}


def construct(String name, Node node, String from_image, String base_dir){
	containerEnv = [
			name : name,
			node: node,
			from_image: from_image,
			base_dir: base_dir
	]
	this.print_object()
}


def execute(command) {
	node = containerEnv.node
	command = docker_exec(command)
	node.execute_command(command)
}


def copy_file_to(File file, String destination_dir) {
	node = containerEnv.node
	node_attr = node.get_object()
	file_ = file.get_object()
	node.copy_file_to_node_new(file, node_attr.base_dir)
	new_file = fileObject(file_.name, node, node_attr.base_dir)
	new_file_ = new_file.get_object()
	command = docker_cp_to_container(new_file_.file_full_path, destination_dir)
	node.execute_command(command)
}







return this



