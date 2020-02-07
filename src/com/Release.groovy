package org.foo

/** A map that holds all constants and data members that can be override when constructing  */
releaseEnv

def construct(String name, String git_branch, String git_tag, String images, Node git_node, LinkedHashMap git_properties){
	releaseEnv = [
			'name' : name,
			'git_branch': git_branch,
			'git_tag': git_tag,
			'images': images,
			'git_node': git_node,
			'git_properties': git_properties,
			'filename': ''
	]
	if (releaseEnv.git_tag == 'null') {
		releaseEnv.put('git_tag', null)
	}
	releaseEnv.put('filename', releaseEnv.name + ".tar.gz")
	this.print_object()
}

def get_object(){
	return releaseEnv
}

def print_object() {
	println releaseEnv
}

def create_tag(){
	cmd = "cd ${git_path}; git fetch --tags"
	git_node.execute_command(cmd)
	cmd = "cd ${git_path}; git tag -f ${git_tag}"
	git_node.execute_command(cmd)
	//todo
	try {
		cmd = "cd ${git_path}; git push origin ${git_tag}"
		git_node.execute_command(cmd)
	} catch(Exception ex) {
		println("Catching the exception");
	}
}


def git_pull(){
	command = """
		cd ${git_path}
		#git pull --all
		git checkout ${git_branch}
		git pull
		"""
	git_node.execute_command(command)
}

def clean_old_builds(){
	command = """
		cd ${build_base_dir}
		if [ -e ${release_filename} ]; then rm ${release_filename}; fi
		if [ -e ${release_name} ]; then rm -r ${release_name}; fi
		"""
	git_node.execute_command(command)
}

def create_release_folder(){
	command = """
		cd ${build_base_dir}
		mkdir ${release_name}
		"""
	git_node.execute_command(command)
}

def create_installer(){
	command = """
		cd ${build_base_dir}
		cd ${release_name}
		cp -r ${installer_dir}/* .
		"""
	git_node.execute_command(command)
}

def create_workspace_rtom(){
	command = """
		cd ${build_base_dir}
		cd ${release_name}
		python wsgenerator.py ${git_path}/RTOM_WS/rtom.desc ./resources/WS/RMS_RTOM_Workspace.xml ${git_path} -H ${installer_dir}/resources/WS/workspace_header_sm.txt -W ${workspace_name}
		python wsgenerator.py ${git_path}/RTOM_WS/rtom.desc ./resources/WS/RTOM_Workspace.xml ${git_path} -W ${workspace_name} -D default
		python wsgenerator.py ${git_path}/TEST_WS/rtom_tests.desc ./resources/WS/RTOM_Test_Workspace.xml ${git_path} -W RTOM_Test_Workspace -D default
		"""
	git_node.execute_command(command)
}

def set_permissions() {
	command = """
		cd ${build_base_dir}
		cd ${release_name}
		find . -name \"*.sh\" | xargs chmod -R 740
		find . -name \"*.py\" | xargs chmod -R 740
		find . -type f | xargs dos2unix
		"""
	git_node.execute_command(command)	
}

def compress_build(){
	command = """
		cd ${build_base_dir}
		tar -cf ${release_name}.tar ${release_name}
		gzip -f ${release_name}.tar
		rm -r ${release_name}
		"""
	git_node.execute_command(command)
}


def create_release() {
	git_node = releaseEnv.git_node

	release_name = releaseEnv.name
	release_filename = releaseEnv.filename.toString()
	git_branch = releaseEnv.git_branch
	git_tag = releaseEnv.get('git_tag')
	workspace_name = release_name

	build_properties = releaseEnv.get('git_properties')
	build_base_dir = build_properties.get('BUILD_BASE_DIR').toString()
	images_base_dir = build_properties.get('PROD_INSTALL_IMAGE')
	git_path = build_properties.get('PROJECT_GIT_PATH')
	installer_dir = build_properties.get('INSTALLER_GIT_PATH')

	this.git_pull()
	this.clean_old_builds()
	this.create_release_folder()
	this.create_installer()
	this.create_workspace_rtom()
	this.set_permissions()
	this.compress_build()

	if (git_tag != null) {
		this.create_tag()
	}

	release_file = fileObject(release_filename, git_node, build_base_dir)

	return release_file

}

return this