pipeline {
    agent any
    stages {
        stage('Deploy') {
            when {
                expression { params.DEPLOY }
            }
            steps {
                sshagent(['docker-vm']) {
                    sh "ssh -o StrictHostKeyChecking=no -l azureuser acit3855docker.westus3.cloudapp.azure.com 'git -C acit3855/ pull'"
                    sh "ssh -o StrictHostKeyChecking=no -l azureuser acit3855docker.westus3.cloudapp.azure.com 'docker pull dislocatedleg/audit_log && docker pull dislocatedleg/processing && docker pull dislocatedleg/receiver && docker pull dislocatedleg/storage'"
                    sh "ssh -o StrictHostKeyChecking=no -l azureuser acit3855docker.westus3.cloudapp.azure.com 'docker compose -f acit3855/deployment/docker-compose.yml up -d'"
                }
            }
        }
    }
}