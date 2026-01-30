pipeline {
    agent { label 'slave' }
    stages {
        stage('git-pull-stage') {
            steps {
                git branch: 'main', url: 'https://github.com/AnuragKawale14/student-ui-app.git'
            }
        }
         stage('build-stage') {
            steps {
                sh '/opt/maven/bin/mvn clean package'
            }
        }
         stage('tesr-stage') {
            steps {
                withSonarQubeEnv(installationName: 'sonar',credentialsId: 'sonar-cred') {
                 sh ' /opt/maven/bin/mvn clean verify sonar:sonar '
            }
                // sh '''  /opt/maven/bin/mvn clean verify sonar:sonar \\
                //         -Dsonar.projectKey=new-studentapp \\
                //         -Dsonar.host.url=http://172.31.21.29:9000 \\
                //         -Dsonar.login=1bdbff4bf01b412d86dd2e9aaa23cff101b5c927'''
            }
        }
         stage('Quality_Gate') {
            steps {
                timeout(3) {
   
            }
                waitForQualityGate true
            }
        }
        //    stage('Artifatory-stage') {
        //       steps {
        //          sh 'aws s3 cp target/studentapp-2.2-SNAPSHOT.war  s3://amz-01-buck/'
        //     }
        // }

         stage('deploy-stage') {
            steps {
               deploy adapters: [tomcat9(alternativeDeploymentContext: '', credentialsId: 'tomcat-cred', path: '', url: 'http://16.171.10.83:8080/')], contextPath: '/', war: '**/*.war'
            }
        }
    }
}

