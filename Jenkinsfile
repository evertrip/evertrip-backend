pipeline {
    agent any
    tools {
        jdk 'JDK'
    }
    environment {
        // 각 자격 증명을 Jenkinsfile에서 참조합니다.
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub-jenkins') // Docker Hub 자격 증명 ID
        EC2_SSH_KEY = credentials('ec2-ssh-key-id') // EC2 SSH 키 자격 증명 ID
        EC2_HOST = credentials('ec2-host') // EC2 서버 호스트 이름 또는 IP
        SPRING_PROFILES_ACTIVE = 'prod'
        AWS_METADATA_DISABLED = 'true'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/evertrip/evertrip-backend.git'
            }
        }
        stage('Set Permissions') {
            steps {
                sh 'chmod +x gradlew'
            }
        }
        stage('Redis Check') {
            steps {
                sh '''
                redis-cli -a redis1234 ping
                '''
            }
        }
        stage('Build and Test') {
            steps {
                withCredentials([string(credentialsId: 'jasypt-password-id', variable: 'JASYPT_PASSWORD')]) {
                    sh '''
                    ./gradlew clean build -Djasypt.encryptor.password=${JASYPT_PASSWORD} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -Dcom.amazonaws.sdk.disableEc2Metadata=${AWS_METADATA_DISABLED}
                    '''
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build('evertrip-image')
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'DOCKER_HUB_CREDENTIALS') {
                        docker.image('evertrip-image').push('latest')
                    }
                }
            }
        }
        stage('Deploy to EC2') {
            steps {
                script {
                    sshagent(['EC2_SSH_KEY']) {
                        sh '''
                        ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} '
                        docker pull rlarkddnr1686/evertrip-image:latest &&
                        docker stop evertrip-container || true &&
                        docker rm evertrip-container || true &&
                        docker run -d --name evertrip-container -e JASYPT_PASSWORD=${JASYPT_PASSWORD} -e SPRING_PROFILES_ACTIVE=prod -e DISABLE_EC2_METADATA=true rlarkddnr1686/evertrip-image:latest
                        '
                        '''
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
