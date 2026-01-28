pipeline {
    agent { label 'slave' }

    stages {

        stage('git-pull-stage') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Anilbamnote/student-ui-app.git'
            }
        }

        stage('build-stage') {
            steps {
                sh '/opt/maven/bin/mvn clean package'
            }
        }

        stage('test-stage') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh '/opt/maven/bin/mvn clean verify sonar:sonar'
                }
            }
        }

        stage('Quality_Gate') {
            steps {
                timeout(10) {
            }
                waitForQualityGate true
            }
        }

        stage('Artifactory-stage') {
            steps {
                sh 'aws s3 cp target/studentapp-2.2-SNAPSHOT.war s3://amz-01-buck/'
            }
        }

        stage('deploy-stage') {
            steps {
                echo 'Code deployed successfully'
            }
        }
    }
}

