pipeline {
	agent any
	tools {
		maven 'Maven_3.9.16'
		jdk 'Java21'
	}

	stages {
		stage ('Compile Stage') {
			steps {
				withMaven(maven: 'Maven_3.9.16') {
					bat 'mvn clean compile -Dcheckstyle.skip=true'
				}
			}
		}

		stage ('Testing Stage') {
			steps {
				withMaven(maven : 'Maven_3.9.16') {
					bat 'mvn test -Dcheckstyle.skip=true'
				}
			}
		}

		stage ('package Stage') {
			steps {
				withMaven(maven : 'Maven_3.9.16') {
					bat 'mvn package -Dcheckstyle.skip=true'
				}
			}
		}
	}
}
