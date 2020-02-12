package org.com

import groovy.transform.InheritConstructors

@InheritConstructors
class GitNode extends NodeNew{
	String gitPath
	String releaseBaseDir
}

