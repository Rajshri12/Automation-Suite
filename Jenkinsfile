pipeline {
    agent any

    tools {
        maven 'Maven 3.9'
        jdk 'JDK 11'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Rajshri12/Automation-Suite.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -q'
            }
        }

        stage('Run Test Suite') {
            steps {
                sh 'mvn test -DsuiteXmlFile=src/test/resources/testng.xml'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Publish Results') {
            steps {
                publishHTML(target: [
                    reportDir  : 'target/surefire-reports',
                    reportFiles: 'index.html',
                    reportName : 'TestNG Report'
                ])
            }
        }
    }

    post {
        success {
            echo '✅ All tests passed.'
        }
        failure {
            echo '❌ Tests failed — check reports and screenshots.'
            archiveArtifacts artifacts: 'screenshots/**', allowEmptyArchive: true
        }
    }
}

// v2: archive screenshots on failure
