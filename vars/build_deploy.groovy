def call (image) {
    pipeline {
        agent any
        stages {
            stage('Python Lint') {
                steps {
                    sh "pylint --fail-under=5.0 --disable=E0401 ${image}/app.py"     
                }                  
            }
            stage('Security') {
                steps {
                    // install docker scout
                    sh 'curl -sSfL https://raw.githubusercontent.com/docker/scout-cli/main/install.sh | sh -s --'
                    // login to dockerhub
                    withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                        sh "docker login -u 'dislocatedleg' -p '$TOKEN' docker.io"
                    }
                    // Analyze and fail on critical or high vulnerabilities on the python image
                    // script {
                    //     def images = ['audit_log', 'dashboard-ui', 'health', 'processing', 'receiver', 'storage'] as String[]
                    //     for (image in images) {
                    //         sh "docker scout cves dislocatedleg/${image}"
                    //     }
                    // }
                    sh "docker scout cves python --exit-code --only-severity critical,high"
                }
            }
            stage('Package') {
                steps {
                    withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                            sh "docker build -t dislocatedleg/${image} ${image}/"
                            sh "docker push dislocatedleg/${image}"
                    }
                }
            }
        }
    }
}