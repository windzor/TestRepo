pipeline {
    parameters {
        string(name: 'BRANCH', defaultValue: 'main', description: 'Branch to build', trim: true)
    }
    agent any
    stages {
        stage('Checkout') {
            steps {
                script {
                    try {
                        env.GIT_URL = 'https://github.com/windzor/TestRepo.git'
                        def scmVars = checkout scmGit(branches: [[name: 'main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/windzor/TestRepo.git']])
                        env.GIT_COMMIT = scmVars.GIT_COMMIT
                        env.GIT_BRANCH = scmVars.GIT_BRANCH
                    } catch (err) {
                        echo "Failed: [checkout] ${err}"
                        currentBuild.result = 'FAILURE'
                        throw err
                    }
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    try {
                        sh '''
                            ls -la
                            echo "Building..."
                        '''
                    } catch (err) {
                        echo "Failed: [Build] ${err}"
                        currentBuild.result = 'FAILURE'
                        throw err
                    }
                }
            }
        }
        stage('Push') {
            steps {
                script {
                    try {
                        sh '''
                            echo "Pushing..."
                        '''
                    } catch (err) {
                        echo "Failed: [Push] ${err}"
                        currentBuild.result = 'FAILURE'
                        throw err
                    }
                }
            }
        }
        stage('Publish') {
            steps {
                script {
                    try {
                        sh '''
                            echo "Publishing... Windzor"
                            
                        '''
                    } catch (err) {
                        echo "Failed: [Publish] ${err}"
                        currentBuild.result = 'FAILURE'
                        throw err
                    }
                }
            }
        }
    }
    post {
        always {
            script {
                if (currentBuild.result == 'FAILURE') {
                    sendNotifications('fail')
                } else {                    
                    sendNotifications('pass')
                }
            }
        }
    }
}

def sendNotifications(status) {
    build(
        job: 'SendNotification',
        parameters: [
            string(name: 'BUILDS_NAME', value: "${BUILD_URL}"),
            string(name: 'COMMIT', value: "${GIT_COMMIT}"),
            string(name: 'REPO', value: "TestRepo"),
            string(name: 'TYPE', value: "GitHub"),
            string(name: 'BRANCH', value: "${GIT_BRANCH}"),
            string(name: 'TARGET_DIR', value: "TestRepo"),
            string(name: 'BUILD_URL', value: "${BUILD_URL}"),
            string(name: 'COMMIT_DATE', value: "${GIT_COMMIT}"),
            string(name: 'STATUS', value: status),
        ],
        propagate: false,
        wait: false
    )
}
