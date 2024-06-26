pipeline {
    agent any
    tools {
        jdk 'JDK'
    }
    environment {
        SPRING_PROFILES_ACTIVE = 'prod'
        AWS_METADATA_DISABLED = 'true'
        JASYPT_PASSWORD = "${JASYPT_PASSWORD}"
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
                    sh '''
                    echo "JASYPT_PASSWORD=${JASYPT_PASSWORD}"
                    ./gradlew clean build --info --stacktrace -Djasypt.encryptor.password=${JASYPT_PASSWORD} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -Dcom.amazonaws.sdk.disableEc2Metadata=${AWS_METADATA_DISABLED}
                    '''

            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def image = docker.build('rlarkddnr1686/evertrip-image:latest')
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
                                    ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} << EOF
                                    JASYPT_PASSWORD=${JASYPT_PASSWORD}
                                    docker pull rlarkddnr1686/evertrip-image:latest &&
                                    docker stop evertrip-container || true &&
                                    docker rm evertrip-container || true &&
                                    echo "Starting container with JASYPT_PASSWORD=\${JASYPT_PASSWORD}" &&
                                    docker run -d --network host -p 8080:8080 --name evertrip-container -e JASYPT_PASSWORD=\${JASYPT_PASSWORD} -e SPRING_PROFILES_ACTIVE=prod -e DISABLE_EC2_METADATA=true rlarkddnr1686/evertrip-image:latest
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
            echo 'Pipeline failed!!'
        }
    }
}
