import org.com.PropertiesNew

def call(propertiesMap) {
	return new PropertiesNew(propertiesMap)
}

Map loadPropertiesFromLibrary(String libraryName) {
	log("Loading properties from library ${libraryName}")
	jenkinsWorkspaceDir = env.JENKINS_HOME + "/" + "workspace"
	librariesDirectory =  jenkinsWorkspaceDir + "/" + "${env.JOB_NAME}@libs"
	libraryDirectory = librariesDirectory + "/" + libraryName
	resourcesFolder = libraryDirectory + "/resources"
	propertiesDirectory = resourcesFolder + "/" + "properties"
	propertiesFiles = this.listPropertiesFiles(propertiesDirectory)
	return this.generatePropertiesMap(propertiesFiles)
}

List listPropertiesFiles(propertiesFilesPath) {
	def command = "cd ${propertiesFilesPath}; find . -type f"
	commandResult = bash.executeLocalCommand(command)
	properties_files = commandResult[ 1 ]
	list = [  ]
	properties_files.tokenize("\n").each { fileRelativePath ->
		//remove ./ from the bash's output
		fileRelativePath = fileRelativePath.drop("./".length())
		list.add(fileRelativePath)
	}
	return list
}

//this functions generates the propertiesMap following the properties folder structure
Map generatePropertiesMap(propertiesFiles) {
	propertiesMap = [ : ]
	
	propertiesFiles.each { fileRelativePath ->
		fileDir = "properties"
		baseMap = propertiesMap
		slashChar = fileRelativePath.indexOf("/")
		lastItKey = null

		while (slashChar != -1) {
			folder = fileRelativePath[ 0..(slashChar - 1) ]
			key = folder.toUpperCase()
			fileDir = fileDir + "/" + folder
			if (!lastItKey) {
				if (!propertiesMap.get(key)) {
					propertiesMap.put(key, [:])
				}
				baseMap = propertiesMap.get(key)
			} else {
				baseMap = propertiesMap.get(lastItKey)
				if (!baseMap.get(key)) {
					baseMap.put(key, [:])
				}
				baseMap = baseMap.get(key)
			}
			fileRelativePath = fileRelativePath.drop("${key}/".length())
			slashChar = fileRelativePath.indexOf("/")
			lastItKey = key
		}
		
		fileName = fileRelativePath
		log("Importing properties from file ${fileDir}/${fileName}")
		dotChar = fileName.indexOf(".")
		key = fileName[ 0..(dotChar - 1) ].toUpperCase()
		writeFile(file: "${fileName}", text: libraryResource("${fileDir}/${fileName}"), encoding: "UTF-8")
		yamlMap = readYaml file: "${fileName}"
		if (!yamlMap.get(key)) {
			log.raiseError "Properties file ${fileDir}/${fileName} must contain the same name as its first key"
		}
		baseMap.put(key, yamlMap.get(key))
		bash.executeLocalCommandWithNoTrace("rm ${fileName}")
	}
	return propertiesMap
}

def generate_properties_map(properties_files_dir){
	properties_files = sh (returnStdout: true, script: "cd ${properties_files_dir}; find . -type f")
	properties_map = [ : ]

	properties_files.tokenize("\n").each { fileRelativePath ->
		file_dir = properties_files_dir.drop("resources/".length())
		base_map = properties_map

		fileRelativePath = fileRelativePath.drop("./".length())
		slash_char = fileRelativePath.indexOf("/")
		last_it_key = null

		while (slash_char != -1) {
			folder = fileRelativePath[ 0..(slash_char - 1) ]
			key = folder.toUpperCase()
			file_dir = file_dir + "/" + folder
			if (!last_it_key) {
				if (!properties_map.get(key)) {
					properties_map.put(key, [:])
				}
				base_map = properties_map.get(key)
			} else {
				base_map = properties_map.get(last_it_key)
				if (!base_map.get(key)) {
					base_map.put(key, [:])
				}
				base_map = base_map.get(key)
			}
			fileRelativePath = fileRelativePath.drop("${key}/".length())
			slash_char = fileRelativePath.indexOf("/")
			last_it_key = key
		}
		fileName = fileRelativePath
		dot_char = fileName.indexOf(".")
		key = fileName[ 0..(dot_char - 1) ].toUpperCase()
		writeFile(file: "${fileName}", text: libraryResource("${file_dir}/${fileName}"), encoding: "UTF-8")
		yaml_map = readYaml file: "${fileRelativePath}"
		if (!yaml_map.get(key)) {
			log.raiseError "Properties file ${fileRelativePath} must contain the same name as its first key"
		}
		base_map.put(key, yaml_map.get(key))
	}
	return properties_map
}