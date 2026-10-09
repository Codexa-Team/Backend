pipeline {
    agent any
    tools {
       maven 'MAVEN_3_9'
       jdk 'JDK_21'
    }

    stages {
       stage ('Compile Stage') {
          steps {
             withMaven(maven: 'MAVEN_3_9') {
                bat 'mvn clean compile -Dcheckstyle.skip=true'
             }
          }
       }

       stage ('Testing Stage') {
          steps {
             withMaven(maven : 'MAVEN_3_9') {
                bat 'mvn test -Dcheckstyle.skip=true'
             }
          }
       }

       stage ('package Stage') {
          steps {
             withMaven(maven : 'MAVEN_3_9') {
                bat 'mvn package -Dcheckstyle.skip=true'
             }
          }
       }
    }
}
