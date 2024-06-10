pipeline {
    agent any
    tools {
        jdk 'JDK'
    }
    environment {
        // 각 자격 증명을 Jenkinsfile에서 참조합니다.
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub-jenkins') // Docker Hub 자격 증명 ID
        EC2_SSH_KEY = credentials('ec2-ssh-key-id') // EC2 SSH 키 자격 증명 ID
        SPRING_PROFILES_ACTIVE = 'local'
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
                withEnv([
                    "JASYPT_PASSWORD=${env.JASYPT_PASSWORD}"
                ]) {
                    sh '''
                    echo "JASYPT_PASSWORD=${JASYPT_PASSWORD}"
                    ./gradlew clean build --info --stacktrace -Djasypt.encryptor.password=${JASYPT_PASSWORD} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -Dcom.amazonaws.sdk.disableEc2Metadata=${AWS_METADATA_DISABLED}
                    '''
                    }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    def image = docker.build('rlarkddnr1686/evertrip-image:latest')
                    image.tag('latest')
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-jenkins', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-jenkins') {
                         sh '''
                             docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD
                             docker tag rlarkddnr1686/evertrip-image:latest rlarkddnr1686/evertrip-image:latest
                             docker push rlarkddnr1686/evertrip-image:latest
                            '''
                    }
                }
                }
            }
        }
        stage('Deploy to EC2') {
            steps {
            withCredentials([sshUserPrivateKey(credentialsId: 'ec2-ssh-key-id', keyFileVariable: 'SSH_KEY')]) {
                script {
                    sshagent(['ec2-ssh-key-id']) {
                                    sh '''
                                    #!/bin/bash
                                    ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} '
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
